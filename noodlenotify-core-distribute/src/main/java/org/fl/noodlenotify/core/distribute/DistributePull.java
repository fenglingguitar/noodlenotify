package org.fl.noodlenotify.core.distribute;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.distributedlock.api.LockChangeHandler;
import org.fl.noodle.common.distributedlock.db.DbDistributedLock;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.distribute.callback.RemovePopMessageCallback;
import org.fl.noodlenotify.core.domain.message.MessageDm;

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

	public DistributePull(String queueName,
							long moduleId,
							ConnectManager dbConnectManager,
							ConnectManager queueCacheConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo,
							long dbId) {
		this.queueName = queueName;
		this.moduleId = moduleId;
		this.dbConnectManager = dbConnectManager;
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
		this.dbId = dbId;
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
		
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReleaseLock() {
		
		stopSign = true;
		
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		dbDistributedLock.setTableName("MSG_" + queueName + "_LK");
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
	
	private class DistributeSetFreshRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				try {
					
					if (stopSign) {
						break;
					}
					
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
						
						List<MessageDm> messageDmList = select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						
						if (messageDmList != null && messageDmList.size() > 0) {
							for (MessageDm messageDm : messageDmList) {
								try {
									if (!pushNew(messageDm)) {
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							messageDmList.clear();
						}
						middleIdFresh.set(end); 					
					}
					
					try {
						Thread.sleep(distributeConfParam.getSelectTimeIntervalFresh());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
		}
	}
	
	private class DistributeSetNewRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				try {
					
					if (stopSign) {
						break;
					}
					
					long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_NEW);
					long max = middleIdFresh.get();
					
					for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {

						long start = index;
						long end = index + distributeConfParam.getSelectByIdIntervalNew() <= max ? index + distributeConfParam.getSelectByIdIntervalNew() : max;
											
						if (getNewQueueCacheLen(queueName) > distributeConfParam.getQueueCacheCapacityNew()) {
							break;
						}
						
						List<MessageDm> messageDmList = select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						
						if (messageDmList != null && messageDmList.size() > 0) {
							for (MessageDm messageDm : messageDmList) {
								try {
									if (!pushNew(messageDm)) {
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							messageDmList.clear();
						}
					}
					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
		}
	}
	
	private class DistributeSetPortionRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				try {
					
					if (stopSign) {
						break;
					}
					
					long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_PORTION);
					long max = middleIdFresh.get();
					
					for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {
						
						long start = index;
						long end = index + distributeConfParam.getSelectByIdIntervalPortion() <= max ? index + distributeConfParam.getSelectByIdIntervalPortion() : max;
						
						if (getPortionQueueCacheLen(queueName) > distributeConfParam.getQueueCacheCapacityPortion()) {
							break;
						}

						List<MessageDm> messageDmList = selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_PORTION, System.currentTimeMillis() - queueDistributerVo.getInterval_Time());
						
						if (messageDmList != null && messageDmList.size() > 0) {
							for (MessageDm messageDm : messageDmList) {
								try {
									if (!pushPortion(messageDm)) {
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							messageDmList.clear();
						}		
					}
					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}		
			}
		}
	}
	
	private class DistributeSetDeleteTimeoutRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				try {
					
					if (stopSign) {
						break;
					}
					
					long min = minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_FINISH);
					long max = middleIdFresh.get();
					
					for (long index = min; max > 0 && index <= max && !stopSign; index += distributeConfParam.getSelectByIdIntervalNew() + 1) {
						
						long start = index;
						long end = index + distributeConfParam.getSelectByIdIntervalDeleteTimeout() <= max ? index + distributeConfParam.getSelectByIdIntervalDeleteTimeout() : max;
						
						List<MessageDm> messageDmList = selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - distributeConfParam.getSelectDeleteTimeout());
						
						if (messageDmList != null && messageDmList.size() > 0) {
							for (MessageDm messageDm : messageDmList) {
								messageDm.addMessageCallback(new RemovePopMessageCallback(messageDm, queueCacheConnectManager));
								try {
									delete(messageDm);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							messageDmList.clear();
						}
					}
					
					try {
						Thread.sleep(distributeConfParam.getSelectEmptyTimeIntervalDeleteTimeout());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
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
	
	private List<MessageDm> select(String queueName, long start, long end, byte status) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.select(queueName, start, end, status);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private boolean pushNew(MessageDm messageDm) throws Exception {
		messageDm.setBool(true);
		ConnectCluster pushConnectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent pushQueueCacheConnectAgent = (QueueCacheConnectAgent) pushConnectCluster.getProxy();
		return pushQueueCacheConnectAgent.push(messageDm);
	}
	
	private boolean pushPortion(MessageDm messageDm) throws Exception {
		messageDm.setBool(false);
		ConnectCluster pushConnectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent pushQueueCacheConnectAgent = (QueueCacheConnectAgent) pushConnectCluster.getProxy();
		return pushQueueCacheConnectAgent.push(messageDm);
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
	
	private List<MessageDm> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			return dbConnectAgent.selectTimeout(queueName, start, end, status, timeout);
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	private void delete(MessageDm messageDm) throws Exception {
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			dbConnectAgent.delete(messageDm);
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
}
