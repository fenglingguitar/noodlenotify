package org.fl.noodlenotify.core.distribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributeGet {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeGet.class);

	private String queueName;
	
	private ConnectManager queueCacheConnectManager;
	private ConnectManager bodyCacheConnectManager;
	private ConnectManager netConnectManager;
		
	private DistributeConfParam distributeConfParam;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private BlockingQueue<MessageDm> executeBlockingQueueNew;
	private BlockingQueue<MessageDm> executeBlockingQueuePortion;
	private BlockingQueue<MessageDm> executeBlockingQueueBatch;
	private List<MessageDm> executeBatchOverflowList = new ArrayList<MessageDm>();
	private AtomicInteger updatingCount = new AtomicInteger(0);
	
	private volatile boolean stopSign = false;
	
	private CountDownLatch stopCountDownLatch;
	private AtomicInteger  stopCountDownLatchCount;
	
	private QueueDistributerVo queueDistributerVo;
	
	public DistributeGet(String queueName,
							ConnectManager queueCacheConnectManager,
							ConnectManager bodyCacheConnectManager,
							ConnectManager netConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo) {
		this.queueName = queueName;
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.bodyCacheConnectManager = bodyCacheConnectManager;
		this.netConnectManager = netConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
	}
	
	public void start() {
		
		executeBlockingQueueNew = new LinkedBlockingQueue<MessageDm>(distributeConfParam.getExecuteCapacityNew());
		executeBlockingQueuePortion = new LinkedBlockingQueue<MessageDm>(distributeConfParam.getExecuteCapacityPortion());
		executeBlockingQueueBatch = new LinkedBlockingQueue<MessageDm>(
				distributeConfParam.getExecuteCapacityBatch() * 
					(queueDistributerVo.getNew_Exe_ThreadNum() 
							+ queueDistributerVo.getPortion_Exe_ThreadNum()));
		
		int allThreadCount = queueDistributerVo.getNew_Pop_ThreadNum() +
							queueDistributerVo.getNew_Exe_ThreadNum() +
							queueDistributerVo.getPortion_Pop_ThreadNum() +
							queueDistributerVo.getPortion_Exe_ThreadNum() + 
							1;
		stopCountDownLatch = new CountDownLatch(allThreadCount);
		if (logger.isDebugEnabled()) {
			stopCountDownLatchCount = new AtomicInteger(allThreadCount);
			logger.debug("Start -> New StopCountDownLatchCount -> " 
					+ "QueueName: " + queueName 
					+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.get()
					);
		}
		
		for (int i=0; i<queueDistributerVo.getNew_Pop_ThreadNum(); i++) {
			executorService.execute(new DistributeGetRunnable("New DistributeGetRunnablePop", executeBlockingQueueNew, true));
		}
		for (int i=0; i<queueDistributerVo.getNew_Exe_ThreadNum(); i++) {
			executorService.execute(new DistributeExecuteRunnable("New DistributeGetRunnableExe", executeBlockingQueueNew, true));
		}
		
		for (int i=0; i<queueDistributerVo.getPortion_Pop_ThreadNum(); i++) {
			executorService.execute(new DistributeGetRunnable("Portion DistributeGetRunnablePop", executeBlockingQueuePortion, false));
		}
		for (int i=0; i<queueDistributerVo.getPortion_Exe_ThreadNum(); i++) {
			executorService.execute(new DistributeExecuteRunnable("Portion DistributeGetRunnableExe", executeBlockingQueuePortion, false));
		}
		
		executorService.execute(new DistributeCheckRunnable());
	}
	
	public void destroy() {
		
		stopSign = true;
		
		notifySleep();
		
		try {
			stopCountDownLatch.await();
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Destroy -> "
						+ "Queue: " + queueName 
						+ ", StopCountDownLatch Await -> " + e);
			}
		}
		
		executorService.shutdown();
		
		int allMessageDmCount = executeBlockingQueueNew.size() + executeBlockingQueuePortion.size();
		if (logger.isDebugEnabled()) {
			logger.debug("Destroy -> Clean ExecuteBlockingQueue -> " + 
						"AllMessageDmCount: " + allMessageDmCount +
						", ExecuteBlockingQueueNew: " + executeBlockingQueueNew.size() +
						", ExecuteBlockingQueuePortion: " + executeBlockingQueuePortion.size()
						);
		}
		if (allMessageDmCount > 0) {
			Iterator<MessageDm> messageDmItNew = executeBlockingQueueNew.iterator();
			while (messageDmItNew.hasNext()) {
				MessageDm messageDmNext = messageDmItNew.next();
				removeQueue("Destroy", messageDmNext);
			}
			
			Iterator<MessageDm> messageDmItPortion = executeBlockingQueuePortion.iterator();
			while (messageDmItPortion.hasNext()) {
				MessageDm messageDmNext = messageDmItPortion.next();
				removeQueue("Destroy", messageDmNext);
			}
		}
		
		if (executeBlockingQueueBatch.size() > 0) {
			List<MessageDm> messageDmList = new ArrayList<MessageDm>(executeBlockingQueueBatch);
			checkUpdateResult(messageDmList);
		}
		
		if (executeBatchOverflowList.size() > 0) {
			checkUpdateResult(executeBatchOverflowList);
		}
	}
	
	private class DistributeGetRunnable implements Runnable {
		
		private String queueCacheName;
		private BlockingQueue<MessageDm> executeBlockingQueue;
		private boolean queueType;
		
		public DistributeGetRunnable(String queueCacheName,
				BlockingQueue<MessageDm> executeBlockingQueue,
				boolean queueType) {
			this.queueCacheName = queueCacheName;
			this.executeBlockingQueue = executeBlockingQueue;
			this.queueType = queueType;
		}
		
		@Override
		public void run() {
			
			while (true) {
				
				if (stopSign) {
					break;
				}
				
				MessageDm messageDm = null;
				ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
				try {
					messageDm = queueCacheConnectAgent.pop(queueName, queueType);
				} catch (Exception e) {
					e.printStackTrace();
					removeQueue(queueCacheName, messageDm);
					continue;
				}
				
				if (messageDm == null) {
					continue;
				}
				
				try {
					while (!executeBlockingQueue.offer(messageDm, 1000, TimeUnit.MILLISECONDS)) {
						startSleep(Math.round(distributeConfParam.getExecuteOfferTimeoutWait() * executeBlockingQueue.size()));
						if (stopSign) {
							if (logger.isDebugEnabled()) {
								logger.debug("Runnable -> " + queueCacheName + " -> Clean ExecuteBlockingQueue Offer -> " + 
											"QueueType: " + queueType 
											);
							}
							removeQueue(queueCacheName, messageDm);
							break;
						}
					}
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> " 
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", Offer To ExecuteBlockingQueue -> " + e);
					}
				}
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug(queueCacheName + " -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private class DistributeExecuteRunnable implements Runnable {
		
		private String queueCacheName;
		private BlockingQueue<MessageDm> executeBlockingQueue;
		private boolean queueType;
		
		public DistributeExecuteRunnable(String queueCacheName,
				BlockingQueue<MessageDm> executeBlockingQueue,
				boolean queueType) {
			this.queueCacheName = queueCacheName;
			this.executeBlockingQueue = executeBlockingQueue;
			this.queueType = queueType;
		}
		
		@Override
		public void run() {
			
			while (true) {
				
				if (stopSign) {
					break;
				}			
				
				MessageDm messageDm = null;
				try {
					messageDm = executeBlockingQueue.poll(1000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName
								+ ", Poll From ExecuteBlockingQueue -> " + e);
					}
					continue;
				}
				if (messageDm == null) {
					continue;
				}			
				
				messageDm.setObjectFive(updatingCount);
				messageDm.setObjectFour(executeBlockingQueueBatch);
				messageDm.setObjectTwo(executeBatchOverflowList);
				messageDm.setResult(false);
				messageDm.setBool(queueType);
				
				ConnectCluster connectCluster = netConnectManager.getConnectCluster("DEFALT");
				NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
				
				ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDm);
				ConnectThreadLocalStorage.put(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode(), queueDistributerVo);
				try {
					netConnectAgent.send(new Message(
							messageDm.getQueueName(), 
							messageDm.getUuid(), 
							new String(messageDm.getContent(), "UTF-8")
							));
				} catch (Exception e) {
					e.printStackTrace();
					removeQueue(queueCacheName, messageDm);
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
					ConnectThreadLocalStorage.remove(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode());
				}
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug(queueCacheName + " -> StopCountDownLatchCount CountDown -> " 
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private class DistributeCheckRunnable implements Runnable {
		
		@Override
		public void run() {
			
			List<MessageDm> messageDmList = new ArrayList<MessageDm>(distributeConfParam.getExecuteBatchNum());
			
			while (true) {
				while (true) {
					try {
						MessageDm messageDm = executeBlockingQueueBatch.poll(distributeConfParam.getExecuteBatchWaitTime(), TimeUnit.MILLISECONDS);
						if (messageDm != null) {
							messageDmList.add(messageDm);
							if (messageDmList.size() == distributeConfParam.getExecuteBatchNum()) {
								break;
							}
						} else {
							if (messageDmList.size() > 0) {
								break;
							} else {
								if (stopSign && (updatingCount.get() - executeBlockingQueueBatch.size() - executeBatchOverflowList.size()) == 0) {
									stopCountDownLatch.countDown();
									if (logger.isDebugEnabled()) {
										logger.debug("DistributeCheckRunnable -> StopCountDownLatchCount CountDown -> " 
												+ "StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
												);
									}
									return;
								}
							}
						}
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeCheckRunnable -> " 
									+ "Poll -> " + e);
						}
					}
				}
				checkUpdateResult(messageDmList);
				messageDmList.clear();
			}
		}
	}
	
	private void checkUpdateResult(List<MessageDm> messageDmList) {
		for (MessageDm messageDm : messageDmList) {
			messageDm.setObjectOne(null);
			messageDm.setObjectTwo(null);
			updatingCount.decrementAndGet();
			messageDm.setObjectThree(null);
			if (messageDm.getResult() == false) {
				if (logger.isErrorEnabled()) {
					logger.error("DistributeCheckRunnable -> CheckUpdateResult -> "
							+ "UUID: " + messageDm.getUuid()
							+ ", DB: " + messageDm.getDb()
							+ ", Execute Update -> " + messageDm.getException());
				}
				removeQueue("DistributeCheckRunnable -> CheckUpdateResult -> ", messageDm);
				continue;
			}
			
			if (messageDm.getStatus() == MessageConstant.MESSAGE_STATUS_FINISH) {
				removeBody("DistributeCheckRunnable -> CheckUpdateResult -> ", messageDm);
			} else {
				removeQueue("DistributeCheckRunnable -> CheckUpdateResult -> ", messageDm);
			}
		}
	}
	
	private void removeQueue(String queueCacheName, MessageDm messageDm) {

		ConnectCluster queueConnectCluster = queueCacheConnectManager.getConnectCluster("ALL");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent)queueConnectCluster.getProxy();
		try {
			queueCacheConnectAgent.removePop(messageDm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	private void removeBody(String queueCacheName, MessageDm messageDm) {

		ConnectCluster bodyConnectCluster = bodyCacheConnectManager.getConnectCluster("PARTALL");
		BodyCacheConnectAgent bodyCacheConnectAgentOne = (BodyCacheConnectAgent) bodyConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDm);
		try {
			bodyCacheConnectAgentOne.remove(messageDm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
		}
	}
	
	private synchronized void startSleep(long suspendTime) throws InterruptedException {
		if (!stopSign && suspendTime > 0) {
			wait(suspendTime);
		}
	}
	
	private synchronized void notifySleep() {
		notifyAll();
	}
	
	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public void setBodyCacheConnectManager(ConnectManager bodyCacheConnectManager) {
		this.bodyCacheConnectManager = bodyCacheConnectManager;
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
