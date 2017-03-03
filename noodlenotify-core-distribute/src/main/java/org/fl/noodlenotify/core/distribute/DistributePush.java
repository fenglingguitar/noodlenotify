package org.fl.noodlenotify.core.distribute;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;

public class DistributePush {
	
	//private final static Logger logger = LoggerFactory.getLogger(DistributePush.class);

	private String queueName;
	
	private ConnectManager queueCacheConnectManager;
	private ConnectManager netConnectManager;
		
	private DistributeConfParam distributeConfParam;
	private QueueDistributerVo queueDistributerVo;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private BlockingQueue<MessageDb> executeBlockingQueueNew;
	private BlockingQueue<MessageDb> executeBlockingQueuePortion;
	
	private volatile boolean stopSign = false;
	
	private AtomicInteger  stopCount;
	
	public DistributePush(ConnectManager queueCacheConnectManager,
							ConnectManager netConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo) {
		this.queueName = queueDistributerVo.getQueue_Nm();
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.netConnectManager = netConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
	}
	
	public void start() {
		
		executeBlockingQueueNew = new LinkedBlockingQueue<MessageDb>(distributeConfParam.getExecuteCapacityNew());
		executeBlockingQueuePortion = new LinkedBlockingQueue<MessageDb>(distributeConfParam.getExecuteCapacityPortion());
		
		int allThreadCount = queueDistributerVo.getNew_Pop_ThreadNum() +
							queueDistributerVo.getNew_Exe_ThreadNum() +
							queueDistributerVo.getPortion_Pop_ThreadNum() +
							queueDistributerVo.getPortion_Exe_ThreadNum();
		
		stopCount = new AtomicInteger(allThreadCount);
		
		for (int i=0; i<queueDistributerVo.getNew_Pop_ThreadNum(); i++) {
			executorService.execute(new DistributeGetRunnable(executeBlockingQueueNew, true));
		}
		for (int i=0; i<queueDistributerVo.getNew_Exe_ThreadNum(); i++) {
			executorService.execute(new DistributeExecuteRunnable(executeBlockingQueueNew, true));
		}
		
		for (int i=0; i<queueDistributerVo.getPortion_Pop_ThreadNum(); i++) {
			executorService.execute(new DistributeGetRunnable(executeBlockingQueuePortion, false));
		}
		for (int i=0; i<queueDistributerVo.getPortion_Exe_ThreadNum(); i++) {
			executorService.execute(new DistributeExecuteRunnable(executeBlockingQueuePortion, false));
		}
	}
	
	public void destroy() {
		stopSign = true;
		executorService.shutdownNow();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (executeBlockingQueueNew.size() > 0) {
			Iterator<MessageDb> messageDbItNew = executeBlockingQueueNew.iterator();
			while (messageDbItNew.hasNext()) {
				MessageDb messageDbNext = messageDbItNew.next();
				messageDbNext.setResult(false);
				messageDbNext.executeMessageCallback();
			}
		}
		if (executeBlockingQueuePortion.size() > 0) {	
			Iterator<MessageDb> messageDbItPortion = executeBlockingQueuePortion.iterator();
			while (messageDbItPortion.hasNext()) {
				MessageDb messageDbNext = messageDbItPortion.next();
				messageDbNext.setResult(false);
				messageDbNext.executeMessageCallback();
			}
		}
	}
	
	private class DistributeGetRunnable implements Runnable {
		
		private BlockingQueue<MessageDb> executeBlockingQueue;
		private boolean queueType;
		
		public DistributeGetRunnable(BlockingQueue<MessageDb> executeBlockingQueue, boolean queueType) {
			this.executeBlockingQueue = executeBlockingQueue;
			this.queueType = queueType;
		}
		
		@Override
		public void run() {
			
			while (true) {
				
				if (stopSign) {
					stopCount.decrementAndGet();
					break;
				}
				
				MessageDb messageDb = null;
				ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
				try {
					messageDb = queueCacheConnectAgent.pop(queueName, queueType);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				
				if (messageDb == null) {
					continue;
				}
				
				try {
					if (!executeBlockingQueue.offer(messageDb, 60000, TimeUnit.MILLISECONDS)) {
						messageDb.setResult(false);
						messageDb.executeMessageCallback();
					}
				} catch (InterruptedException e) {
					messageDb.setResult(false);
					messageDb.executeMessageCallback();
					e.printStackTrace();
				}
			}
		}
	}
	
	private class DistributeExecuteRunnable implements Runnable {
		
		private BlockingQueue<MessageDb> executeBlockingQueue;
		private boolean queueType;
		
		public DistributeExecuteRunnable(BlockingQueue<MessageDb> executeBlockingQueue, boolean queueType) {
			this.executeBlockingQueue = executeBlockingQueue;
			this.queueType = queueType;
		}
		
		@Override
		public void run() {
			
			while (true) {
				
				if (stopSign) {
					stopCount.decrementAndGet();
					break;
				}			
				
				MessageDb messageDb = null;
				try {
					messageDb = executeBlockingQueue.poll(1000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
				if (messageDb == null) {
					continue;
				}			
				
				messageDb.setResult(false);
				messageDb.setBool(queueType);
				
				ConnectCluster connectCluster = netConnectManager.getConnectCluster("DEFALT");
				NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
				
				ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDb);
				ConnectThreadLocalStorage.put(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode(), queueDistributerVo);
				try {
					netConnectAgent.send(new MessageRequest(
							messageDb.getQueueName(), 
							messageDb.getUuid(), 
							new String(messageDb.getContent(), "UTF-8")
							));
				} catch (Exception e) {
					e.printStackTrace();
					messageDb.setResult(false);
					messageDb.executeMessageCallback();
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
					ConnectThreadLocalStorage.remove(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode());
				}
			}
		}
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}

	public void setNetConnectManager(ConnectManager netConnectManager) {
		this.netConnectManager = netConnectManager;
	}
	
	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}

	public void setQueueDistributerVo(QueueDistributerVo queueDistributerVo) {
		this.queueDistributerVo = queueDistributerVo;
	}
}
