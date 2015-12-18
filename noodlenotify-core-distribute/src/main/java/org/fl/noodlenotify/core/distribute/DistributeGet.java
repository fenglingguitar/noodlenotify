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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.constant.TraceConstant;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;

public class DistributeGet {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeGet.class);

	private String queueName;
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private ConnectManager bodyCacheConnectManager;
	private ConnectManager netConnectManager;
	
	private ConnectManager traceCacheConnectManager;
	
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
	
	private long moduleId;
	
	@Autowired
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired
	private SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	public DistributeGet(String queueName,
							ConnectManager dbConnectManager,
							ConnectManager queueCacheConnectManager,
							ConnectManager bodyCacheConnectManager,
							ConnectManager customerNetConnectManager,
							ConnectManager traceCacheConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo,
							long moduleId,
							OvertimePerformanceExecuterService overtimePerformanceExecuterService,
							SuccessPerformanceExecuterService successPerformanceExecuterService) {
		this.queueName = queueName;
		this.dbConnectManager = dbConnectManager;
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.bodyCacheConnectManager = bodyCacheConnectManager;
		this.netConnectManager = customerNetConnectManager;
		this.traceCacheConnectManager = traceCacheConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
		this.moduleId = moduleId;
		this.overtimePerformanceExecuterService = overtimePerformanceExecuterService;
		this.successPerformanceExecuterService = successPerformanceExecuterService;
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
					DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(messageDm.getDb());
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
			
			BlockingQueue<Integer> executeBlockingQueueBatchCount = 
					new LinkedBlockingQueue<Integer>(distributeConfParam.getExecuteBatchNum());
			
			while (true) {
				
				try {
					while (!executeBlockingQueueBatchCount.offer(1, 1000, TimeUnit.MILLISECONDS)) {
						startSleep(executeBlockingQueueBatchCount.size() > 0 ? executeBlockingQueueBatchCount.size() : 1000);
						if (stopSign) {
							break;
						}
					}
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName+ " -> Offer To ExecuteBlockingQueueBatchCount -> " + e);
					}
				}
				
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
					executeBlockingQueueBatchCount.poll();
					continue;
				}
				if (messageDm == null) {
					executeBlockingQueueBatchCount.poll();
					continue;
				}
				
				updatingCount.incrementAndGet();
				
				if (queueType) {
					overtimePerformanceExecuterService.before(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE,
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH);
				} else {
					overtimePerformanceExecuterService.before(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE,
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH);		
				}				
				
				DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(messageDm.getDb());
				if (dbConnectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName
								+ ", UUID: " + messageDm.getUuid()
								+ ", DB: " + messageDm.getDb()
								+ ", Get Db ConnectAgent -> Null");
					}
					removeQueue(queueCacheName, messageDm);
					executeBlockingQueueBatchCount.poll();
					updatingCount.decrementAndGet();
					if (queueType) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
								moduleId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
								moduleId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH,
								false);
					}
					if (queueType) {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
					} else {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
					}
					continue;
				}
				
				QueueAgent netQueueAgent = netConnectManager.getQueueAgent(queueName);
				if (netQueueAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "Queue: " + queueName 
								+ ", UUID: " + messageDm.getUuid()
								+ ", Get Net QueueAgent -> Null");
					}
					removeQueue(queueCacheName, messageDm);
					executeBlockingQueueBatchCount.poll();
					updatingCount.decrementAndGet();
					if (queueType) {
						successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH,
							false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
								moduleId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH,
								false);
					}
					if (queueType) {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
					} else {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
					}
					continue;
				}
				
				long executeQueue = messageDm.getExecuteQueue();
				long resultQueue = messageDm.getResultQueue();
				long executeQueueNum = 1;
				long testNum = 1;
				while (executeQueue > 0) {
					if ((executeQueue & testNum) > 0 && (resultQueue & testNum) == 0) {
						QueueAgent netGroupQueueAgent = netQueueAgent.getChildQueueAgent(executeQueueNum);
						if (netGroupQueueAgent != null) {
							try {
								ConnectAgent connectAgent = null;
								do {
									connectAgent = netGroupQueueAgent.getConnectAgent();
									if (connectAgent != null) {
										NetConnectAgent netConnectAgent = (NetConnectAgent) connectAgent;
										try {
											overtimePerformanceExecuterService.before(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND);
											netConnectAgent.send(new Message(
																		messageDm.getQueueName(), 
																		messageDm.getUuid(), 
																		new String(messageDm.getContent(), "UTF-8")
																		), (int)queueDistributerVo.getDph_Timeout());
											overtimePerformanceExecuterService.after(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND);
											successPerformanceExecuterService.result(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND,
													true);
											trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_SUCCESS, TraceConstant.MODULE_TYPE_CUSTOMER, ((ConnectAgent)netConnectAgent).getConnectId());
											break;
										} catch (ConnectionUnableException e) {
											if (logger.isErrorEnabled()) {
												logger.error(queueCacheName + " -> "
														+ "UUID: "+ messageDm.getUuid()
														+ ", Group: " + executeQueueNum
														+ ", Connect: " + connectAgent.getConnectId()
														+ ", Execute -> " + e);
											}
											successPerformanceExecuterService.result(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND,
													false);
											trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_CUSTOMER, ((ConnectAgent)netConnectAgent).getConnectId());
											continue;
										} catch (ConnectionResetException e) {
											if (logger.isErrorEnabled()) {
												logger.error(queueCacheName + " -> "
														+ "UUID: "+ messageDm.getUuid()
														+ ", Group: " + executeQueueNum
														+ ", Connec: " + connectAgent.getConnectId()
														+ ", Execute -> " + e);
											}
											successPerformanceExecuterService.result(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND,
													false);
											trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_CUSTOMER, ((ConnectAgent)netConnectAgent).getConnectId());
											continue;
										} catch (Exception e) {
											successPerformanceExecuterService.result(
													MonitorPerformanceConstant.MODULE_ID_CUSTOMER,
													((ConnectAgent)netConnectAgent).getConnectId(),
													messageDm.getQueueName(),
													MonitorPerformanceConstant.MONITOR_ID_CUSTOMER_SEND,
													false);
											trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_CUSTOMER, ((ConnectAgent)netConnectAgent).getConnectId());
											throw e;
										}
									} else {
										netConnectManager.startUpdateConnectAgent();
										throw new ConnectionRefusedException("Distribute Send -> Connection refused by all the net connect agent");
									}
								} while (connectAgent != null);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error(queueCacheName + " -> "
											+ "UUID: "+ messageDm.getUuid()
											+ ", Group: " + executeQueueNum
											+ ", Execute -> " + e);
								}
								trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_CUSTOMER_GROUP, executeQueueNum);
								executeQueue >>= 1;
								resultQueue >>= 1;
								executeQueueNum <<= 1;
								continue;
							}
							trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_SUCCESS, TraceConstant.MODULE_TYPE_CUSTOMER_GROUP, executeQueueNum);
							long resultQueueNew = messageDm.getResultQueue();
							resultQueueNew |= executeQueueNum;
							messageDm.setResultQueue(resultQueueNew);
						} else {
							if (logger.isErrorEnabled()) {
								logger.error(queueCacheName + " -> "
										+ "Queue: " + queueName 
										+ ", UUID: " + messageDm.getUuid()
										+ ", Group: " + executeQueueNum
										+ ", Get Net Group QueueAgent -> Null");
							}
							trace(messageDm, TraceConstant.ACTION_TYPE_CUSTOMER_SEND, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_CUSTOMER_GROUP, executeQueueNum);
						}
					}
					executeQueue >>= 1;
					resultQueue >>= 1;
					executeQueueNum <<= 1;
				}
				
				if (messageDm.getResultQueue() == messageDm.getExecuteQueue()) {
					messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
				} else {
					if (queueDistributerVo.getIs_Repeat() == MessageConstant.MESSAGE_IS_REPEAT_No) {
						messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
					} else {
						if (queueDistributerVo.getExpire_Time() > 0 
								&& System.currentTimeMillis() - messageDm.getBeginTime() > queueDistributerVo.getExpire_Time()) {
							messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
							successPerformanceExecuterService.result(
									MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
									moduleId,
									messageDm.getQueueName(),
									MonitorPerformanceConstant.MONITOR_ID_QUEUE_EXPIRE,
									false);
							if (queueType) {
								trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, TraceConstant.RESULT_TYPE_EXPIRE, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
							} else {
								trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, TraceConstant.RESULT_TYPE_EXPIRE, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
							}
						} else {
							messageDm.setStatus(MessageConstant.MESSAGE_STATUS_PORTION);
							messageDm.setDelayTime(queueDistributerVo.getInterval_Time());
						}
					}
				}
				messageDm.setFinishTime(System.currentTimeMillis());
				
				messageDm.setObjectOne(executeBlockingQueueBatch);
				messageDm.setObjectTwo(executeBatchOverflowList);
				messageDm.setObjectThree(executeBlockingQueueBatchCount);
				messageDm.setResult(false);
				messageDm.setBool(queueType);
				try {
					dbConnectAgent.update(messageDm);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> "
								+ "UUID: " + messageDm.getUuid()
								+ ", DB: " + messageDm.getDb()
								+ ", Execute Update -> " + e);
					}
					removeQueue(queueCacheName, messageDm);
					executeBlockingQueueBatchCount.poll();
					updatingCount.decrementAndGet();
					if (queueType) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
								moduleId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
								moduleId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH,
								false);
					}
					if (queueType) {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, TraceConstant.RESULT_TYPE_DB_UPDATE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
					} else {
						trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, TraceConstant.RESULT_TYPE_DB_UPDATE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
					}
					continue;
				}
				
				if (queueType) {
					overtimePerformanceExecuterService.after(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE,
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH);
				} else {
					overtimePerformanceExecuterService.after(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE,
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH);
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
			@SuppressWarnings("unchecked")
			BlockingQueue<Integer> executeBlockingQueueBatchCount = (BlockingQueue<Integer>) messageDm.getObjectThree();
			executeBlockingQueueBatchCount.poll();
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
				if (messageDm.getBool()) {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH,
							false);
				} else {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH,
							false);
				}
				if (messageDm.getBool()) {
					trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, TraceConstant.RESULT_TYPE_DB_UPDATE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
				} else {
					trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, TraceConstant.RESULT_TYPE_DB_UPDATE_FAIL, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
				}
				continue;
			}
			
			if (messageDm.getStatus() == MessageConstant.MESSAGE_STATUS_FINISH) {
				removeBody("DistributeCheckRunnable -> CheckUpdateResult -> ", messageDm);
			} else {
				removeQueue("DistributeCheckRunnable -> CheckUpdateResult -> ", messageDm);
			}
			
			boolean success = messageDm.getResultQueue() == messageDm.getExecuteQueue() ? true : false;
			if (messageDm.getBool()) {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
						moduleId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH,
						success);
			} else {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, 
						moduleId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH,
						success);
			}
			byte result = TraceConstant.RESULT_TYPE_SUCCESS;
			if (messageDm.getResultQueue() != messageDm.getExecuteQueue()) {
				if (messageDm.getResultQueue() == 0) {
					result = TraceConstant.RESULT_TYPE_FAIL;
				} else {						
					result = TraceConstant.RESULT_TYPE_PORTION_SUCCESS;
				}
			}
			if (messageDm.getBool()) {
				trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_DISPATCH, result, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);
			} else {
				trace(messageDm, TraceConstant.ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH, result, TraceConstant.MODULE_TYPE_DISTRIBUTE, moduleId);						
			}		
			if (result == TraceConstant.RESULT_TYPE_SUCCESS) {
				removeTrace(messageDm);
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
	
	private void trace(MessageDm messageDm, int action, byte result, byte traceModuleType, long traceModuleId) {
		if (queueDistributerVo.getIs_Trace() == TraceConstant.IS_TRACE_YES) {
			QueueAgent queueAgentTrace = traceCacheConnectManager.getQueueAgent(messageDm.getQueueName());
			if (queueAgentTrace != null) {
				TraceCacheConnectAgent traceCacheConnectAgent = null;			
				do {
					traceCacheConnectAgent = (TraceCacheConnectAgent) queueAgentTrace.getConnectAgent();
					if (traceCacheConnectAgent != null) {
						try {
							traceCacheConnectAgent.set(new TraceVo(
															messageDm.getUuid(), 
															action, 
															System.currentTimeMillis(), 
															result, 
															traceModuleType,
															traceModuleId,
															TraceConstant.MODULE_TYPE_DISTRIBUTE,
															moduleId
															));
							break;
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("Trace -> "
										+ "Queue: " + messageDm.getQueueName()
										+ ", UUID: " + messageDm.getUuid()
										+ ", TraceCache: " + ((ConnectAgent)traceCacheConnectAgent).getConnectId()
										+ ", Set Trace Cache -> " + e);
							}
							continue;
						}
					} else {
						bodyCacheConnectManager.startUpdateConnectAgent();
						if (logger.isDebugEnabled()) {
							logger.error("Trace -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", Get Trace Cache Agent -> Null");
						}
					}
				} while (traceCacheConnectAgent != null);
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("Trace -> "
							+ "Queue: " + messageDm.getQueueName()
							+ ", UUID: " + messageDm.getUuid()
							+ ", Get Trace Queue Agent -> Null");
				}
			}
		}
	}
	
	private void removeTrace(MessageDm messageDm) {
		if (queueDistributerVo.getIs_Trace() == TraceConstant.IS_TRACE_YES) {
			QueueAgent queueAgentTrace = traceCacheConnectManager.getQueueAgent(messageDm.getQueueName());
			if (queueAgentTrace != null) {
				List<ConnectAgent> connectAgentList = queueAgentTrace.getConnectAgentAll();
				for (ConnectAgent connectAgent : connectAgentList) {
					TraceCacheConnectAgent traceCacheConnectAgent = (TraceCacheConnectAgent) connectAgent;
					try {
						traceCacheConnectAgent.remove(messageDm.getUuid());
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("RemoveTrace -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", TraceCache: " + connectAgent.getConnectId()
									+ ", Remove Trace Cache -> " + e);
						}
						continue;
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("RemoveTrace -> "
							+ "Queue: " + messageDm.getQueueName()
							+ ", UUID: " + messageDm.getUuid()
							+ ", Get Trace Queue Agent -> Null");
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
	
	public void setDbConnectManager(ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
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

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setOvertimePerformanceExecuterService(
			OvertimePerformanceExecuterService overtimePerformanceExecuterService) {
		this.overtimePerformanceExecuterService = overtimePerformanceExecuterService;
	}

	public void setSuccessPerformanceExecuterService(
			SuccessPerformanceExecuterService successPerformanceExecuterService) {
		this.successPerformanceExecuterService = successPerformanceExecuterService;
	}
}
