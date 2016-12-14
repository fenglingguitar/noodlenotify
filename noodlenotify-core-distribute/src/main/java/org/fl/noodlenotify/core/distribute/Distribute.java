package org.fl.noodlenotify.core.distribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.distributedlock.api.DistributedLock;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Distribute {
	
	private final static Logger logger = LoggerFactory.getLogger(Distribute.class);

	private long suspendTime = 300000;
	
	private DistributeConfParam distributeConfParam = new DistributeConfParam();
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private ConnectManager bodyCacheConnectManager;
	private ConnectManager netConnectManager;
		
	private ExecutorService executorService = Executors.newSingleThreadExecutor();	
	
	private volatile boolean stopSign = false;
	
	private ConcurrentMap<String, DistributePush> distributeGetMap = new ConcurrentHashMap<String, DistributePush>();
	private ConcurrentMap<String, ConcurrentMap<Long, DistributePull>> distributeSetQueueMap = new ConcurrentHashMap<String, ConcurrentMap<Long, DistributePull>>();
	ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private CountDownLatch stopCountDownLatch;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, DistributedLock> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, DistributedLock>();
	private ConcurrentMap<String, Boolean> queueCacheDistributeSetStopSignMap = new ConcurrentHashMap<String, Boolean>();
	private ExecutorService queueCacheDistributeSetLockerExecutorService = Executors.newCachedThreadPool();
	
	private AtomicInteger  stopCountDownLatchCount = new AtomicInteger();
	
	private String distributeName;
	private long moduleId;
	private String localIp;
	private int checkPort;
	
	private ModuleRegister distributeModuleRegister;
	
	public void start() throws Exception {
		
		if (distributeName == null || 
				(distributeName != null && distributeName.equals("hostname"))) {
			distributeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp(): localIp;
		moduleId = consoleRemotingInvoke.saveDistributerRegister(localIp, checkPort, distributeName);
		
		//MemoryStorage.moduleName = MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE;
		//MemoryStorage.moduleId = moduleId;
		
		distributeModuleRegister.setModuleId(moduleId);

		//netConnectManager.setModuleId(moduleId);
		//netConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//netConnectManager.start();
		netConnectManager.runUpdateNow();
		
		//bodyCacheConnectManager.setModuleId(moduleId);
		//bodyCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//bodyCacheConnectManager.start();
		bodyCacheConnectManager.runUpdateNow();

		//queueCacheConnectManager.setModuleId(moduleId);
		//queueCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//queueCacheConnectManager.start();
		queueCacheConnectManager.runUpdateNow();

		//dbConnectManager.setModuleId(moduleId);
		//dbConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//dbConnectManager.start();
		dbConnectManager.runUpdateNow();
		
		updateConnectAgent();
		
		stopCountDownLatch = new CountDownLatch(1);
		if (logger.isDebugEnabled()) {
			stopCountDownLatchCount = new AtomicInteger(1);
			logger.debug("UpdateConnectAgentRunnable -> New StopCountDownLatchCount -> "
					+ "StopCountDownLatchCount: " + stopCountDownLatchCount.get()
					);
		}
		
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				
				while (true) {
					suspendUpdateConnectAgent();
					if (stopSign) {
						destroyConnectAgent();
						break;
					}
					updateConnectAgent();
				}
				
				stopCountDownLatch.countDown();
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgentRunnable -> StopCountDownLatchCount CountDown -> "
							+ "StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
							);
				}
			}
		});		
	}
	
	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveDistributerCancel(moduleId);

		stopSign = true;
		startUpdateConnectAgent();
		stopCountDownLatch.await();
		executorService.shutdown();
		
		//dbConnectManager.destroy();
		//queueCacheConnectManager.destroy();
		//bodyCacheConnectManager.destroy();
		//netConnectManager.destroy();		
	}
	
	private synchronized void suspendUpdateConnectAgent() {
		try {
			wait(suspendTime);
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("SuspendUpdateConnectAgent -> " + e);
			}
		}
	}
	
	private synchronized void startUpdateConnectAgent() {
		notifyAll();
	}
	
	private void updateConnectAgent() {
		
		List<QueueDistributerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.distributerGetQueues(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Distributer Get Queues -> " + e);
			}
		}
				
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (QueueDistributerVo queueDistributerVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> DistributerGetQueues -> " 
							+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
							+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
							+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
							+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
							+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
							+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
							+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
							+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
							+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
							+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
							);
				}
			}
			
			Set<String> queueNameSet = new HashSet<String>();
			
			for (QueueDistributerVo queueDistributerVo : consoleInfoList) {
				queueNameSet.add(queueDistributerVo.getQueue_Nm());
				if (!queueDistributerVoMap.containsKey(queueDistributerVo.getQueue_Nm())) {
					queueDistributerVoMap.put(queueDistributerVo.getQueue_Nm(), queueDistributerVo);
					
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(queueDistributerVo.getQueue_Nm(), moduleId, queueCacheConnectManager);
					try {
						queueCacheDistributeSetLocker.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					queueCacheDistributeSetLockerMap.put(queueDistributerVo.getQueue_Nm(), queueCacheDistributeSetLocker);
					queueCacheDistributeSetStopSignMap.put(queueDistributerVo.getQueue_Nm(), false);
					queueCacheDistributeSetLockerExecutorService.execute(new CheckActiveRunnable(queueDistributerVo.getQueue_Nm(), queueCacheDistributeSetLocker));
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Start QueueCache DistributePull Locker -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								);
					}
					
					DistributePush distributePush = 
							new DistributePush(
								queueDistributerVo.getQueue_Nm(),
								queueCacheConnectManager,
								netConnectManager,
								distributeConfParam,
								queueDistributerVo);
					distributePush.start();
					distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add DistributePush -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
								+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
								+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
								+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
								+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
								+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
								+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
								+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
								+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
								);
					}
				} else {
					QueueDistributerVo queueDistributerVoOld = queueDistributerVoMap.get(queueDistributerVo.getQueue_Nm());
					if (!queueDistributerVoOld.getIs_Repeat().equals(queueDistributerVo.getIs_Repeat())
							|| !queueDistributerVoOld.getExpire_Time().equals(queueDistributerVo.getExpire_Time())
								|| !queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
									|| !queueDistributerVoOld.getDph_Delay_Time().equals(queueDistributerVo.getDph_Delay_Time())
										|| !queueDistributerVoOld.getDph_Timeout().equals(queueDistributerVo.getDph_Timeout())
											|| !queueDistributerVoOld.getNew_Pop_ThreadNum().equals(queueDistributerVo.getNew_Pop_ThreadNum())
												|| !queueDistributerVoOld.getNew_Exe_ThreadNum().equals(queueDistributerVo.getNew_Exe_ThreadNum())
													|| !queueDistributerVoOld.getPortion_Pop_ThreadNum().equals(queueDistributerVo.getPortion_Pop_ThreadNum())
														|| !queueDistributerVoOld.getPortion_Exe_ThreadNum().equals(queueDistributerVo.getPortion_Exe_ThreadNum())
					) {
						queueDistributerVoMap.put(queueDistributerVo.getQueue_Nm(), queueDistributerVo);
						
						if (!queueDistributerVoOld.getNew_Pop_ThreadNum().equals(queueDistributerVo.getNew_Pop_ThreadNum())
								|| !queueDistributerVoOld.getNew_Exe_ThreadNum().equals(queueDistributerVo.getNew_Exe_ThreadNum())
										|| !queueDistributerVoOld.getPortion_Pop_ThreadNum().equals(queueDistributerVo.getPortion_Pop_ThreadNum())
												|| !queueDistributerVoOld.getPortion_Exe_ThreadNum().equals(queueDistributerVo.getPortion_Exe_ThreadNum())
						) {
							
							DistributePush distributePush = distributeGetMap.get(queueDistributerVo.getQueue_Nm());
							distributePush.destroy();
							distributeGetMap.remove(queueDistributerVo.getQueue_Nm());
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove DistributePush -> " 
										+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
										+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
										+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
										+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
										+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
										+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
										+ ", Queue Change"
										);
							}
							distributePush = new DistributePush(
										queueDistributerVo.getQueue_Nm(),
										queueCacheConnectManager,
										netConnectManager,
										distributeConfParam,
										queueDistributerVo);
							distributePush.start();
							distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Add DistributePush -> " 
										+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
										+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
										+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
										+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
										+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
										+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
										+ ", Queue Change"
										);
							}
						} else {
							if (!queueDistributerVoOld.getIs_Repeat().equals(queueDistributerVo.getIs_Repeat())
									|| !queueDistributerVoOld.getExpire_Time().equals(queueDistributerVo.getExpire_Time())
											|| !queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
							) {
								DistributePush distributePush = distributeGetMap.get(queueDistributerVo.getQueue_Nm());
								distributePush.setQueueDistributerVo(queueDistributerVo);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Change DistributePush From -> " 
											+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
											+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
											+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
											+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
											+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
											+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
											+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
											+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
											+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
											+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
											+ ", Queue Change"
											);
									logger.debug("UpdateConnectAgent -> Change DistributePush To -> " 
											+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
											+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
											+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
											+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
											+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
											+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
											+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
											+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
											+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
											+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
											+ ", Queue Change"
											);
								}
							}
								
							if (!queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
									|| !queueDistributerVoOld.getDph_Delay_Time().equals(queueDistributerVo.getDph_Delay_Time())
							) {
								ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueDistributerVo.getQueue_Nm());
								for (Long dbId : distributeSetDbMap.keySet()) {
									DistributePull distributePull = distributeSetDbMap.get(dbId);
									distributePull.setQueueDistributerVo(queueDistributerVo);
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Change DistributePull From -> " 
												+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
												+ ", DbId: " + dbId
												+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
												+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
												+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
												+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
												+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
												+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
												+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
												+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
												+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
												+ ", Queue Change"
												);
										logger.debug("UpdateConnectAgent -> Change DistributePull To -> " 
												+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
												+ ", DbId: " + dbId
												+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
												+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
												+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
												+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
												+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
												+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
												+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
												+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
												+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
												+ ", Queue Change"
												);
									}
								}
							}
						}
					}
				}
				
				ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueDistributerVo.getQueue_Nm());
				if (distributeSetDbMap == null) {
					distributeSetDbMap = new ConcurrentHashMap<Long, DistributePull>();
					distributeSetQueueMap.put(queueDistributerVo.getQueue_Nm(), distributeSetDbMap);
				}
				Set<Long> dbIdSet = new HashSet<Long>();
				ConnectNode connectNode = dbConnectManager.getConnectNode(queueDistributerVo.getQueue_Nm());
				if (connectNode != null) {
					List<org.fl.noodle.common.connect.agent.ConnectAgent> connectAgentList = connectNode.getConnectAgentList();
					for (org.fl.noodle.common.connect.agent.ConnectAgent connectAgent : connectAgentList) {
						dbIdSet.add(connectAgent.getConnectId());
						if (!distributeSetDbMap.containsKey(connectAgent.getConnectId())) {
							DistributePull distributePull = 
									new DistributePull(
										queueDistributerVo.getQueue_Nm(),
										moduleId,
										dbConnectManager,
										queueCacheConnectManager,
										distributeConfParam,
										queueDistributerVo,
										connectAgent.getConnectId());
							distributePull.start();
							distributeSetDbMap.put(connectAgent.getConnectId(), distributePull);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Add DistributePull -> " 
										+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
										+ ", DbId: " + connectAgent.getConnectId()
										+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
										+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
										+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
										+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
										+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
										);
							} 
						} 
					}
				}
				for (Long dbId : distributeSetDbMap.keySet()) {
					if (!dbIdSet.contains(dbId)) {
						DistributePull distributePull = distributeSetDbMap.get(dbId);
						distributePull.destroy();
						distributeSetDbMap.remove(dbId);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove DistributePull -> " 
									+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
									+ ", DbId: " + dbId
									+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
									+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
									+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
									+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
									+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
									+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
									+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
									+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
									+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
									);
						}
					}
				}
			}
			
			Set<String> keySetGet = queueDistributerVoMap.keySet();
			for (String queueName : keySetGet) {
				if (!queueNameSet.contains(queueName)) {
					QueueDistributerVo queueDistributerVo = queueDistributerVoMap.get(queueName);
					
					queueDistributerVoMap.remove(queueName);

					queueCacheDistributeSetStopSignMap.put(queueName, true);
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = (QueueCacheDistributeSetLocker) queueCacheDistributeSetLockerMap.get(queueName);
					queueCacheDistributeSetLocker.destroy();
					queueCacheDistributeSetLockerMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Destroy QueueCache DistributePull Locker -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								);
					}
					
					DistributePush distributePush = distributeGetMap.get(queueName);
					distributePush.destroy();
					distributeGetMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove DistributePush -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
								+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
								+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
								+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
								+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
								+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
								+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
								+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
								+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
								);
					}
					
					ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueName);
					for (Long dbId : distributeSetDbMap.keySet()) {
						DistributePull distributePull = distributeSetDbMap.get(dbId);
						distributePull.destroy();
						distributeSetDbMap.remove(dbId);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove DistributePull -> " 
									+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
									+ ", DbId: " + dbId
									+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
									+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
									+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
									+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
									+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
									+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
									+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
									+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
									+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
									);
						}
					}
					distributeSetQueueMap.remove(queueName);
				}
			}
		}
	}
	
	private void destroyConnectAgent() {
		
		Set<String> keySetGet = queueDistributerVoMap.keySet();
		for (String queueName : keySetGet) {
			QueueDistributerVo queueDistributerVo = queueDistributerVoMap.get(queueName);
			
			queueDistributerVoMap.remove(queueName);
			
			queueCacheDistributeSetStopSignMap.put(queueName, true);
			QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = (QueueCacheDistributeSetLocker) queueCacheDistributeSetLockerMap.get(queueName);
			queueCacheDistributeSetLocker.destroy();
			queueCacheDistributeSetLockerMap.remove(queueName);
			if (logger.isDebugEnabled()) {
				logger.debug("UpdateConnectAgent -> Destroy QueueCache DistributePull Locker -> " 
						+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
						);
			}
			
			DistributePush distributePush = distributeGetMap.get(queueName);
			distributePush.destroy();
			distributeGetMap.remove(queueName);
			if (logger.isDebugEnabled()) {
				logger.debug("UpdateConnectAgent -> Remove DistributePush -> " 
						+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
						+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
						+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
						+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
						+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
						+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
						+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
						+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
						+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
						+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
						);
			}
			
			ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueName);
			for (Long dbId : distributeSetDbMap.keySet()) {
				DistributePull distributePull = distributeSetDbMap.get(dbId);
				distributePull.destroy();
				distributeSetDbMap.remove(dbId);
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Remove DistributePull -> " 
							+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
							+ ", DbId: " + dbId
							+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
							+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
							+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
							+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
							+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
							+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
							+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
							+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
							+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
							);
				}
			}
			distributeSetQueueMap.remove(queueName);
		}
		
		queueCacheDistributeSetLockerExecutorService.shutdown();
	}
	
	private class CheckActiveRunnable implements Runnable {
		
		private String queueName;
		private DistributedLock queueCacheDistributeSetLocker;
		
		public CheckActiveRunnable(String queueName, DistributedLock queueCacheDistributeSetLocker) {
			this.queueName = queueName;
			this.queueCacheDistributeSetLocker = queueCacheDistributeSetLocker;
		}
		
		@Override
		public void run() {
			
			while (true) {
				
				queueCacheDistributeSetLocker.waitLocker();
				
				if (queueCacheDistributeSetStopSignMap.get(queueName)) {
					break;
				}
				
				setActive(queueName, queueCacheConnectManager);
				
				try {
					Thread.sleep(distributeConfParam.getCheckActiveTimeInterval());
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error("CheckActiveRunnable -> " 
								+ "Queue: " + queueName
								+ ", CheckActive Time Interval -> " + e);
					}
				}
			}
		}
	}
	
	private void setActive(String queueName, ConnectManager queueCacheConnectManager) {
		
		ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null && connectNode.getConnectAgentList().size() > 0) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectNode.getConnectAgentList().get(0);
			try {
				if (queueCacheConnectAgent != null && !queueCacheConnectAgent.isActive(queueName)) {
					try {
						queueCacheConnectAgent.setActive(queueName, true);
						if (logger.isInfoEnabled()) {
							logger.info("CheckActiveRunnable -> SetActive -> "
									+ "QUEUE: " + queueName
									//+ ", ConnectId: " + ((ConnectAgent) queueCacheConnectAgentOther).getConnectId()
									+ ", Set Cache Queue Active");
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("CheckActiveRunnable -> SetActive -> "
									+ "QUEUE: " + queueName
									//+ ", ConnectId: " + ((ConnectAgent) queueCacheConnectAgentOther).getConnectId()
									+ ", Set Active -> Set Cache Queue Active -> " + e);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} /*else {
				List<ConnectAgent> connectAgentList = queueAgent.getConnectAgentAll();
				for (ConnectAgent connectAgent : connectAgentList) {
					QueueCacheConnectAgent queueCacheConnectAgentOther = (QueueCacheConnectAgent) connectAgent;
					if (queueCacheConnectAgentOther != queueCacheConnectAgent) {
						try {
							queueCacheConnectAgentOther.setActive(queueName, false);
						} catch (Exception e) {
							if (logger.isInfoEnabled()) {
								logger.info("CheckActiveRunnable -> SetActive -> "
										+ "QUEUE: " + queueName
										+ ", ConnectId: " + connectAgent.getConnectId()
										+ ", Set Active False");
							}
						}
					}
				}
			}*/
		} /*else {
			if (logger.isErrorEnabled()) {
				logger.error("CheckActiveRunnable -> SetActive -> "
						+ "QUEUE: " + queueName 
						+ ", Set Active -> Get Queue Agent -> Null");
			}
		}*/
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
	
	public void setSuspendTime(long suspendTime) {
		this.suspendTime = suspendTime;
	}
	
	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setDistributeName(String distributeName) {
		this.distributeName = distributeName;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	
	public void setCheckPort(int checkPort) {
		this.checkPort = checkPort;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}
}
