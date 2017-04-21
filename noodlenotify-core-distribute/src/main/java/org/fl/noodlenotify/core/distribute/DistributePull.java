package org.fl.noodlenotify.core.distribute;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.distributedlock.api.LockChangeHandler;
import org.fl.noodle.common.distributedlock.db.DbDistributedLock;
import org.fl.noodle.common.trace.TraceInterceptor;
import org.fl.noodle.common.trace.operation.performance.TracePerformancePrint;
import org.fl.noodle.common.trace.util.TimeSynchron;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.distribute.callback.RemovePopMessageCallback;
import org.springframework.aop.framework.ProxyFactory;

public class DistributePull implements LockChangeHandler {
	
	//private final static Logger logger = LoggerFactory.getLogger(DistributePull.class);

	private String queueName;
	
	private long moduleId;
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private long dbId;
	
	private DistributeConfParam distributeConfParam;
	
	private ExecutorService executorService;
	
	private volatile boolean stopSign = false;
	
	private AtomicLong middleIdFresh = new AtomicLong(0);
	
	private DbDistributedLock dbDistributedLock;
	
	private QueueDistributerVo queueDistributerVo;
	
	private List<MethodInterceptor> methodInterceptorList;

	public DistributePull(long moduleId,
							ConnectManager dbConnectManager,
							ConnectManager queueCacheConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo,
							long dbId,
							List<MethodInterceptor> methodInterceptorList) {
		this.queueName = queueDistributerVo.getQueue_Nm();
		this.moduleId = moduleId;
		this.dbConnectManager = dbConnectManager;
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
		this.dbId = dbId;
		this.methodInterceptorList = methodInterceptorList;
	}
	
	@Override
	public void onMessageGetLock() {
		
		try {
			middleIdFresh.set(maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		executorService = Executors.newCachedThreadPool();
		executorService.execute(new DistributeSetFreshRunnable());		
		executorService.execute(new DistributeSetNewRunnable());		
		executorService.execute(new DistributeSetPortionRunnable());		
		executorService.execute(new DistributeSetDeleteTimeoutRunnable());
	}

	@Override
	public void onMessageLossLock() {
		stopSign = true;
		executorService.shutdownNow();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReleaseLock() {
		onMessageLossLock();
	}

	@Override
	public void onMessageStart() {
	}

	@Override
	public void onMessageStop() {
	}
	
	public void start() {
		
		dbDistributedLock = new DbDistributedLock();
		dbDistributedLock.setJdbcTemplate(((MysqlDbConnectAgent)dbConnectManager.getConnectAgent(dbId)).getJdbcTemplate());
		dbDistributedLock.setLockId(moduleId);
		dbDistributedLock.setTableName("MSG_" + queueName.toUpperCase() + "_LK");
		dbDistributedLock.setLockChangeHandler(this);
		try {
			dbDistributedLock.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		dbDistributedLock.destroy();
	}
	
	private interface PullRunnable {
		public void doRun() throws Exception;
		public void interval() throws Exception;
	}
	
	private abstract class AbstractPullRunnable implements Runnable, MethodInterceptor, PullRunnable {

		private PullRunnable pullRunnable;
		
		public AbstractPullRunnable() {
			ProxyFactory proxyFactory = new ProxyFactory(PullRunnable.class, this);
			if (methodInterceptorList != null && methodInterceptorList.size() > 0) {
				for (Object object : methodInterceptorList) {
					proxyFactory.addAdvice((Advice)object);
				}
			}
			proxyFactory.setTarget(this);
			pullRunnable = (PullRunnable) proxyFactory.getProxy();
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					if (stopSign) { break; }
					TraceInterceptor.setTraceKey(UUID.randomUUID().toString().replaceAll("-", ""));
					pullRunnable.doRun();
					pullRunnable.interval();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					TraceInterceptor.setTraceKey(null);
				}
			}
		}
		
		@Override
		public Object invoke(MethodInvocation invocation) throws Throwable {
			return invocation.proceed();
		}
		
		protected boolean push(MessageDb messageDb) throws Exception {
			String traceKey = TraceInterceptor.getTraceKey();
			long startTime = TimeSynchron.currentTimeMillis();
			String stackKey = UUID.randomUUID().toString().replaceAll("-", "");
			try {
				TraceInterceptor.setTraceKey(messageDb.getUuid());
				TraceInterceptor.setInvoke("DistributePull.push");
				TraceInterceptor.setStackKey(stackKey);
				return doPush(messageDb);
			} finally {
				TraceInterceptor.popInvoke();
				TraceInterceptor.popStackKey();
				TracePerformancePrint.printTraceLog("DistributePull.push", "Exchange.receive", startTime, TimeSynchron.currentTimeMillis(), false, stackKey, messageDb.getParentKey());
				TraceInterceptor.setTraceKey(traceKey);
			}
		}
		
		protected abstract boolean doPush(MessageDb messageDb) throws Exception;
	}
	
	private class DistributeSetFreshRunnable extends AbstractPullRunnable {
		
		@Override
		public void doRun() throws Exception {
			
			long min = middleIdFresh.get() > 0 ? middleIdFresh.get() + 1 : minId(queueName);
			long max = maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time());
			
			for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalFresh() + 1) {
				
				long start = index;
				long end = index + distributeConfParam.getSelectByIdIntervalFresh() <= max ? index + distributeConfParam.getSelectByIdIntervalFresh() : max;
				
				if (getNewQueueCacheLen(queueName) > distributeConfParam.getQueueCacheCapacityNew()) {
					break;
				}
				
				long expect = end - start + 1;
				
				if (selectCount(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW) < expect) {
					break;
				}
				
				List<MessageDb> messageDbList = select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
				
				if (messageDbList != null && messageDbList.size() > 0) {
					for (MessageDb messageDb : messageDbList) {
						try {
							if (!push(messageDb)) {
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					messageDbList.clear();
				}
				middleIdFresh.set(end); 					
			}
		}

		@Override
		public void interval() throws Exception {
			Thread.sleep(distributeConfParam.getSelectTimeIntervalFresh());
		}

		@Override
		protected boolean doPush(MessageDb messageDb) throws Exception {
			return pushNew(messageDb);
		}
	}
	
	private class DistributeSetNewRunnable extends AbstractPullRunnable {
		
		@Override
		public void doRun() throws Exception {
			
			long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_NEW);
			long max = middleIdFresh.get();
			
			for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {

				long start = index;
				long end = index + distributeConfParam.getSelectByIdIntervalNew() <= max ? index + distributeConfParam.getSelectByIdIntervalNew() : max;
									
				if (getNewQueueCacheLen(queueName) > distributeConfParam.getQueueCacheCapacityNew()) {
					break;
				}
				
				List<MessageDb> messageDbList = select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
				
				if (messageDbList != null && messageDbList.size() > 0) {
					for (MessageDb messageDb : messageDbList) {
						try {
							if (!push(messageDb)) {
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					messageDbList.clear();
				}
			}
		}

		@Override
		public void interval() throws Exception {
			Thread.sleep(10000);
		}

		@Override
		protected boolean doPush(MessageDb messageDb) throws Exception {
			return pushNew(messageDb);
		}
	}
	
	private class DistributeSetPortionRunnable extends AbstractPullRunnable {
		
		@Override
		public void doRun() throws Exception {
			
			long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_PORTION);
			long max = middleIdFresh.get();
			
			for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {
				
				long start = index;
				long end = index + distributeConfParam.getSelectByIdIntervalPortion() <= max ? index + distributeConfParam.getSelectByIdIntervalPortion() : max;
				
				if (getPortionQueueCacheLen(queueName) > distributeConfParam.getQueueCacheCapacityPortion()) {
					break;
				}

				List<MessageDb> messageDbList = selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_PORTION, System.currentTimeMillis() - queueDistributerVo.getInterval_Time());
				
				if (messageDbList != null && messageDbList.size() > 0) {
					for (MessageDb messageDb : messageDbList) {
						try {
							if (!pushPortion(messageDb)) {
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					messageDbList.clear();
				}		
			}
		}

		@Override
		public void interval() throws Exception {
			Thread.sleep(10000);
		}

		@Override
		protected boolean doPush(MessageDb messageDb) throws Exception {
			return pushPortion(messageDb);
		}
	}
	
	private class DistributeSetDeleteTimeoutRunnable extends AbstractPullRunnable {
		
		@Override
		public void doRun() throws Exception {
			
			long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_FINISH);
			long max = middleIdFresh.get();
			
			for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {
				
				long start = index;
				long end = index + distributeConfParam.getSelectByIdIntervalDeleteTimeout() <= max ? index + distributeConfParam.getSelectByIdIntervalDeleteTimeout() : max;
				
				List<MessageDb> messageDbList = selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - distributeConfParam.getSelectDeleteTimeout());
				
				if (messageDbList != null && messageDbList.size() > 0) {
					for (MessageDb messageDb : messageDbList) {
						messageDb.addMessageCallback(new RemovePopMessageCallback(messageDb, queueCacheConnectManager));
						try {
							push(messageDb);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					messageDbList.clear();
				}
			}
		}

		@Override
		public void interval() throws Exception {
			Thread.sleep(distributeConfParam.getSelectEmptyTimeIntervalDeleteTimeout());
		}

		@Override
		protected boolean doPush(MessageDb messageDb) throws Exception {
			delete(messageDb);
			return false;
		}
	}
	
	private long maxIdDelay(String queueName, long delay) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.maxIdDelay(queueName, delay);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private long minId(String queueName) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.minId(queueName);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private long getNewQueueCacheLen(String queueName) throws Exception {
		ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
		return queueCacheConnectAgent.len(queueName, true);
	}
	
	private long getPortionQueueCacheLen(String queueName) throws Exception {
		ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
		return queueCacheConnectAgent.len(queueName, false);
	}
	
	private long selectCount(String queueName, long start, long end, byte status) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.selectCount(queueName, start, end, status);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private List<MessageDb> select(String queueName, long start, long end, byte status) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.select(queueName, start, end, status);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private boolean pushNew(MessageDb messageDb) throws Exception {
		messageDb.setBool(true);
		ConnectCluster pushConnectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent pushQueueCacheConnectAgent = (QueueCacheConnectAgent) pushConnectCluster.getProxy();
		return pushQueueCacheConnectAgent.push(messageDb);
	}
	
	private boolean pushPortion(MessageDb messageDb) throws Exception {
		messageDb.setBool(false);
		ConnectCluster pushConnectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent pushQueueCacheConnectAgent = (QueueCacheConnectAgent) pushConnectCluster.getProxy();
		return pushQueueCacheConnectAgent.push(messageDb);
	}
	
	private long minIdByStatus(String queueName, byte status) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.minIdByStatus(queueName, status);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private List<MessageDb> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.selectTimeout(queueName, start, end, status, timeout);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private void delete(MessageDb messageDb) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			dbConnectAgent.delete(messageDb);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public void setDbConnectManager(org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}
	
	public void setQueueCacheConnectManagerNew(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public void setQueueDistributerVo(QueueDistributerVo queueDistributerVo) {
		this.queueDistributerVo = queueDistributerVo;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}

	public void setMethodInterceptorList(List<MethodInterceptor> methodInterceptorList) {
		this.methodInterceptorList = methodInterceptorList;
	}
}
