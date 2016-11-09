package org.fl.noodlenotify.core.distribute;

import java.io.UnsupportedEncodingException;
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
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributeGet {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeGet.class);

	private String queueName;
	
	private org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private ConnectManager bodyCacheConnectManager;
	private org.fl.noodle.common.connect.manager.ConnectManager netConnectManager;
		
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
							org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager,
							ConnectManager queueCacheConnectManager,
							ConnectManager bodyCacheConnectManager,
							org.fl.noodle.common.connect.manager.ConnectManager netConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo) {
		this.queueName = queueName;
		this.dbConnectManager = dbConnectManager;
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
			
			CountDownLatch messageDmCountDownLatch = new CountDownLatch(allMessageDmCount);
			
			Iterator<MessageDm> messageDmItNew = executeBlockingQueueNew.iterator();
			while (messageDmItNew.hasNext()) {
				MessageDm messageDmNext = messageDmItNew.next();
				messageDmNext.setObjectOne(messageDmCountDownLatch);
				removeQueueSelf("Destroy", messageDmNext);
			}
			
			Iterator<MessageDm> messageDmItPortion = executeBlockingQueuePortion.iterator();
			while (messageDmItPortion.hasNext()) {
				MessageDm messageDmNext = messageDmItPortion.next();
				messageDmNext.setObjectOne(messageDmCountDownLatch);
				removeQueueSelf("Destroy", messageDmNext);
			}
			
			try {
				messageDmCountDownLatch.await();
			} catch (InterruptedException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Destroy -> "
							+ "Queue: " + queueName 
							+ ", RemoveQueueSelf -> MessageDmCountDownLatch Await -> " + e);
				}
			}
			
			messageDmCountDownLatch = new CountDownLatch(allMessageDmCount);
			
			MessageDm messageDm = null;
			
			while ((messageDm = executeBlockingQueueNew.poll()) != null) {
				messageDm.setObjectOne(messageDmCountDownLatch);
				messageDm.setBool(true);
				pushQueue("Destroy", messageDm);
			}
			
			while ((messageDm = executeBlockingQueuePortion.poll()) != null) {
				messageDm.setObjectOne(messageDmCountDownLatch);
				messageDm.setBool(false);
				pushQueue("Destroy", messageDm);
			}
			
			try {
				messageDmCountDownLatch.await();
			} catch (InterruptedException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Destroy -> "
							+ "Queue: " + queueName 
							+ ", PushQueue -> MessageDmCountDownLatch Await -> " + e);
				}
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
				
				QueueAgent queueCacheQueueAgent = queueCacheConnectManager.getQueueAgent(queueName);
				if (queueCacheQueueAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName 
								+ ", Get Queue QueueAgent -> Null");
					}
					queueCacheConnectManager.startUpdateConnectAgent();
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error(queueCacheName + " -> "
									+ "Queue: " + queueName 
									+ ", Get Queue QueueAgent Sleep -> " + e);
						}
					}
					continue;
				}
				
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) queueCacheQueueAgent.getConnectAgent();
				if (queueCacheConnectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName 
								+ ", Get Queue Cache ConnectAgent -> Null");
					}
					queueCacheConnectManager.startUpdateConnectAgent();
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error(queueCacheName + " -> "
									+ "Queue: " + queueName 
									+ ", Get Queue Cache ConnectAgent Sleep -> " + e);
						}
					}
					continue;
				}
				
				MessageDm messageDm = null;
				try {
					messageDm = queueCacheConnectAgent.pop(queueName, queueType);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> " 
								+ "Queue: " + queueName
								+ ", QueueCache: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
								+ ", Ip: " + ((ConnectAgent)queueCacheConnectAgent).getIp()
								+ ", Port: " + ((ConnectAgent)queueCacheConnectAgent).getPort()
								+ ", Pop Massage -> " + e);
					}
					continue;
				}
				if (messageDm == null) {
					continue;
				}
				
				List<ConnectAgent> queueCacheConnectAgentList = queueCacheQueueAgent.getConnectAgentAll();
				for (ConnectAgent connectAgent : queueCacheConnectAgentList) {
					QueueCacheConnectAgent queueCacheConnectAgentAll = (QueueCacheConnectAgent) connectAgent;
					if (queueCacheConnectAgentAll == queueCacheConnectAgent) {
						continue;
					}
					try {
						queueCacheConnectAgentAll.setPop(messageDm);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error(queueCacheName + " -> "
									+ "Queue: " + queueName
									+ ", UUID: " + messageDm.getUuid()
									+ ", QueueCache: " + connectAgent.getConnectId()
									+ ", Ip: " + connectAgent.getIp()
									+ ", Port: " + connectAgent.getPort()
									+ ", SetPop -> " + e);
						}
					}
				}
				
				if (messageDm.getContent() == null) {
					if (messageDm.getRedisOne() > 0) {					
						BodyCacheConnectAgent bodyCacheConnectAgentOne = (BodyCacheConnectAgent) 
									bodyCacheConnectManager.getConnectAgent(messageDm.getRedisOne());
						if (bodyCacheConnectAgentOne != null) {
							try {
								bodyCacheConnectAgentOne.get(messageDm);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error(queueCacheName + " -> "
											+ "Queue: " + queueName
											+ ", UUID: " + messageDm.getUuid()
											+ ", BodyCacheOne: " + messageDm.getRedisOne()
											+ ", Ip: " + ((ConnectAgent)bodyCacheConnectAgentOne).getIp()
											+ ", Port: " + ((ConnectAgent)bodyCacheConnectAgentOne).getPort()
											+ ", Get Body One -> " + e);
								}
							}
						}
					} 					
				}

				if (messageDm.getContent() == null) {
					if (messageDm.getRedisTwo() > 0) {	
						BodyCacheConnectAgent bodyCacheConnectAgentTwo = (BodyCacheConnectAgent) 
									bodyCacheConnectManager.getConnectAgent(messageDm.getRedisTwo());
						if (bodyCacheConnectAgentTwo != null) {
							try {
								bodyCacheConnectAgentTwo.get(messageDm);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error(queueCacheName + " -> "
											+ "Queue: " + queueName
											+ ", UUID: " + messageDm.getUuid()
											+ ", BodyCacheTwo: " + messageDm.getRedisTwo()
											+ ", Ip: " + ((ConnectAgent)bodyCacheConnectAgentTwo).getIp()
											+ ", Port: " + ((ConnectAgent)bodyCacheConnectAgentTwo).getPort()
											+ ", Get Body Two -> " + e);
								}
							}
						}
					}
				}
				
				if (messageDm.getContent() == null) {
					DbConnectAgent dbConnectAgent = (DbConnectAgent)((org.fl.noodle.common.connect.agent.ConnectAgent)dbConnectManager.getConnectAgent(messageDm.getDb())).getProxy();
					if (dbConnectAgent == null) {
						if (logger.isErrorEnabled()) {
							logger.error(queueCacheName + " -> "
									+ "Queue: " + queueName
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + messageDm.getDb()
									+ ", Get Db ConnectAgent -> Null");
						}
						removeQueue(queueCacheName, messageDm);
						continue;
					}
					try {
						MessageDm messageDmTemp = dbConnectAgent.selectById(queueName, messageDm.getContentId());
						if (messageDmTemp != null) {
							messageDm.setContent(messageDmTemp.getContent());
						} else {
							if (logger.isErrorEnabled()) {
								logger.error(queueCacheName
										+ " -> " + "Queue: " + queueName
										+ ", UUID: " + messageDm.getUuid()
										+ ", ID: " + messageDm.getId()
										+ ", DB: " + messageDm.getDb()
										+ ", Ip: " + ((ConnectAgent)dbConnectAgent).getIp()
										+ ", Port: " + ((ConnectAgent)dbConnectAgent).getPort()
										+ ", Get Body From DB -> Null");
							}
							removeQueue(queueCacheName, messageDm);
							continue;
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Runnable -> " + queueCacheName + " -> " 
									+ "Queue: " + queueName
									+ ", UUID: " + messageDm.getUuid()
									+ ", ID: " + messageDm.getId()
									+ ", DB: " + messageDm.getDb()
									+ ", Ip: " + ((ConnectAgent)dbConnectAgent).getIp()
									+ ", Port: " + ((ConnectAgent)dbConnectAgent).getPort()
									+ ", Get Body From DB -> " + e);
						}
						removeQueue(queueCacheName, messageDm);
						continue;
					}
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
							CountDownLatch messageDmCountDownLatch = new CountDownLatch(1);
							messageDm.setObjectOne(messageDmCountDownLatch);
							removeQueueSelf(queueCacheName, messageDm);
							try {
								messageDmCountDownLatch.await();
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("Runnable -> " + queueCacheName + " -> " 
											+ "Queue: " + queueName 
											+ ", RemoveQueueSelf -> MessageDmCountDownLatch Await -> " + e);
								}
							}
							messageDmCountDownLatch = new CountDownLatch(1);
							messageDm.setObjectOne(messageDmCountDownLatch);
							messageDm.setBool(queueType);
							pushQueue(queueCacheName, messageDm);
							try {
								messageDmCountDownLatch.await();
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("Runnable -> " + queueCacheName + " -> " 
											+ "Queue: " + queueName 
											+ ", PushQueue -> MessageDmCountDownLatch Await -> " + e);
								}
							}
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
				
				ConnectCluster connectCluster = netConnectManager.getConnectCluster(queueName);
				if (connectCluster == null) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", DB: " + messageDm.getDb()
								+ ", Get ConnectCluster -> Null");
					}
					removeQueue(queueCacheName, messageDm);
					continue;
				}
				
				NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
				
				ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDm);
				ConnectThreadLocalStorage.put(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode(), queueDistributerVo);
				try {
					netConnectAgent.send(new Message(
							messageDm.getQueueName(), 
							messageDm.getUuid(), 
							new String(messageDm.getContent(), "UTF-8")
							));
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
					removeQueue(queueCacheName, messageDm);
				} catch (Exception e1) {
					e1.printStackTrace();
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

		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			List<ConnectAgent> queueCacheConnectAgentList = queueAgent.getConnectAgentAll();
			for (ConnectAgent connectAgent : queueCacheConnectAgentList) {
				QueueCacheConnectAgent queueCacheConnectAgentAll = (QueueCacheConnectAgent) connectAgent;
				try {
					queueCacheConnectAgentAll.removePop(messageDm);
				} catch (ConnectionUnableException e) {
					queueCacheConnectManager.startUpdateConnectAgent();
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> RemoveQueue -> "
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", QueueCache: " + connectAgent.getConnectId()
								+ ", RemovePop -> " + e);
					}
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(queueCacheName + " -> RemoveQueue -> "
						+ "QUEUE: " + queueName 
						+ ", UUID: " + messageDm.getUuid()
						+ ", Remove Pop -> Get Queue Agent -> Null");
			}
		}
	}
	
	private void removeQueueSelf(String queueCacheName, MessageDm messageDm) {

		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			ConnectAgent connectAgent = queueAgent.getConnectAgent();
			if (connectAgent != null) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectAgent;
				try {
					queueCacheConnectAgent.removePop(messageDm);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> RemoveQueueSelf -> "
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", QueueCache: " + connectAgent.getConnectId()
								+ ", RemovePop -> " + e);
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> RemoveQueueSelf -> "
							+ "QUEUE: " + queueName 
							+ ", UUID: " + messageDm.getUuid()
							+ ", Remove Pop -> Get Connect Agent -> Null");
				}
				cancelCountDownLatch(messageDm);
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(queueCacheName + " -> RemoveQueueSelf -> "
						+ "QUEUE: " + queueName 
						+ ", UUID: " + messageDm.getUuid()
						+ ", Remove Pop -> Get Queue Agent -> Null");
			}
			cancelCountDownLatch(messageDm);
		}
	}
	
	private void pushQueue(String queueCacheName, MessageDm messageDm) {

		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			ConnectAgent connectAgent = queueAgent.getConnectAgent();
			if (connectAgent != null) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectAgent;
				try {
					queueCacheConnectAgent.push(messageDm);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> PushQueue -> "
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", QueueCache: " + connectAgent.getConnectId()
								+ ", Push -> " + e);
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> PushQueue -> "
							+ "QUEUE: " + queueName 
							+ ", UUID: " + messageDm.getUuid()
							+ ", Push Pop -> Get Connect Agent -> Null");
				}
				cancelCountDownLatch(messageDm);
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(queueCacheName + " -> PushQueue -> "
						+ "QUEUE: " + queueName 
						+ ", UUID: " + messageDm.getUuid()
						+ ", Push Pop -> Get Queue Agent -> Null");
			}
			cancelCountDownLatch(messageDm);
		}
	}
	
	private void removeBody(String queueCacheName, MessageDm messageDm) {

		BodyCacheConnectAgent bodyCacheConnectAgentOne = (BodyCacheConnectAgent) 
				bodyCacheConnectManager.getConnectAgent(messageDm.getRedisOne());
		BodyCacheConnectAgent bodyCacheConnectAgentTwo = (BodyCacheConnectAgent) 
				bodyCacheConnectManager.getConnectAgent(messageDm.getRedisTwo());
		if (bodyCacheConnectAgentOne != null) {
			try {
				bodyCacheConnectAgentOne.remove(messageDm);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> RmoveBody -> "
							+ "UUID: " + messageDm.getUuid()
							+ ", BodyCacheOne: " + messageDm.getRedisOne()
							+ ", Remove Body Redis One -> " + e);
				}
			}
		}
		if (bodyCacheConnectAgentTwo != null) {
			try {
				bodyCacheConnectAgentTwo.remove(messageDm);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> RmoveBody -> "
							+ "UUID: " + messageDm.getUuid()
							+ ", BodyCacheTwo: " + messageDm.getRedisTwo()
							+ ", Remove Body Redis Two -> " + e);
				}
			}
		}
	}
	
	private void cancelCountDownLatch(MessageDm messageDm) {
		if (messageDm.getObjectOne() != null) {					
			CountDownLatch countDownLatch = (CountDownLatch) messageDm.getObjectOne();
			countDownLatch.countDown();
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
	
	public void setDbConnectManager(org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}
	
	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public void setBodyCacheConnectManager(ConnectManager bodyCacheConnectManager) {
		this.bodyCacheConnectManager = bodyCacheConnectManager;
	}

	public void setNetConnectManager(org.fl.noodle.common.connect.manager.ConnectManager netConnectManager) {
		this.netConnectManager = netConnectManager;
	}
	
	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}

	public void setQueueDistributerVo(QueueDistributerVo queueDistributerVo) {
		this.queueDistributerVo = queueDistributerVo;
	}
}
