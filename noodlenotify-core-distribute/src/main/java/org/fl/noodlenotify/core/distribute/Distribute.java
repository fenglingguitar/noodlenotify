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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.distribute.locker.DistributeSetLocker;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;

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
	
	private ConcurrentMap<String, DistributeGet> distributeGetMap = new ConcurrentHashMap<String, DistributeGet>();
	private ConcurrentMap<String, ConcurrentMap<Long, DistributeSet>> distributeSetQueueMap = new ConcurrentHashMap<String, ConcurrentMap<Long, DistributeSet>>();
	ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private CountDownLatch stopCountDownLatch;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, DistributeSetLocker> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, DistributeSetLocker>();
	private ConcurrentMap<String, Boolean> queueCacheDistributeSetStopSignMap = new ConcurrentHashMap<String, Boolean>();
	private ExecutorService queueCacheDistributeSetLockerExecutorService = Executors.newCachedThreadPool();
	
	private AtomicInteger  stopCountDownLatchCount = new AtomicInteger();
	
	private String distributeName;
	private long moduleId;
	private String localIp;
	private int checkPort;
	
	@Autowired
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired
	private SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	public void start() throws Exception {
		
		if (distributeName == null || 
				(distributeName != null && distributeName.equals("hostname"))) {
			distributeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp(): localIp;
		moduleId = consoleRemotingInvoke.distributerRegister(localIp, checkPort, distributeName);
		
		MemoryStorage.moduleName = MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE;
		MemoryStorage.moduleId = moduleId;

		netConnectManager.setModuleId(moduleId);
		netConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		netConnectManager.start();
		
		bodyCacheConnectManager.setModuleId(moduleId);
		bodyCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		bodyCacheConnectManager.start();

		queueCacheConnectManager.setModuleId(moduleId);
		queueCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		queueCacheConnectManager.start();

		dbConnectManager.setModuleId(moduleId);
		dbConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		dbConnectManager.start();
		
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
		
		consoleRemotingInvoke.distributerCancel(moduleId);

		stopSign = true;
		startUpdateConnectAgent();
		stopCountDownLatch.await();
		executorService.shutdown();
		
		dbConnectManager.destroy();
		queueCacheConnectManager.destroy();
		bodyCacheConnectManager.destroy();
		netConnectManager.destroy();		
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
							+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
					
					DistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(queueDistributerVo.getQueue_Nm(), moduleId, queueCacheConnectManager);
					queueCacheDistributeSetLocker.start();
					queueCacheDistributeSetLockerMap.put(queueDistributerVo.getQueue_Nm(), queueCacheDistributeSetLocker);
					queueCacheDistributeSetStopSignMap.put(queueDistributerVo.getQueue_Nm(), false);
					queueCacheDistributeSetLockerExecutorService.execute(new CheckActiveRunnable(queueDistributerVo.getQueue_Nm(), queueCacheDistributeSetLocker));
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Start QueueCache DistributeSet Locker -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								);
					}
					
					DistributeGet distributeGet = 
							new DistributeGet(
								queueDistributerVo.getQueue_Nm(),
								dbConnectManager,
								queueCacheConnectManager,
								bodyCacheConnectManager,
								netConnectManager,
								distributeConfParam,
								queueDistributerVo,
								moduleId,
								overtimePerformanceExecuterService,
								successPerformanceExecuterService);
					distributeGet.start();
					distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributeGet);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add DistributeGet -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
								+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
								+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
								+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
								+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
								+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
								+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
								+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
								+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
								+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
								);
					}
				} else {
					QueueDistributerVo queueDistributerVoOld = queueDistributerVoMap.get(queueDistributerVo.getQueue_Nm());
					if (queueDistributerVoOld.getIs_Repeat() != queueDistributerVo.getIs_Repeat() 
							|| queueDistributerVoOld.getExpire_Time() != queueDistributerVo.getExpire_Time()
								|| queueDistributerVoOld.getInterval_Time() != queueDistributerVo.getInterval_Time()
									|| queueDistributerVoOld.getDph_Delay_Time() != queueDistributerVo.getDph_Delay_Time()
										|| queueDistributerVoOld.getDph_Timeout() != queueDistributerVo.getDph_Timeout()
											|| queueDistributerVoOld.getIs_Trace() != queueDistributerVo.getIs_Trace()
												|| queueDistributerVoOld.getNew_Pop_ThreadNum() != queueDistributerVo.getNew_Pop_ThreadNum()
													|| queueDistributerVoOld.getNew_Exe_ThreadNum() != queueDistributerVo.getNew_Exe_ThreadNum()
														|| queueDistributerVoOld.getPortion_Pop_ThreadNum() != queueDistributerVo.getPortion_Pop_ThreadNum()
															|| queueDistributerVoOld.getPortion_Exe_ThreadNum() != queueDistributerVo.getPortion_Exe_ThreadNum()
					) {
						queueDistributerVoMap.put(queueDistributerVo.getQueue_Nm(), queueDistributerVo);
						
						if (queueDistributerVoOld.getNew_Pop_ThreadNum() != queueDistributerVo.getNew_Pop_ThreadNum()
								|| queueDistributerVoOld.getNew_Exe_ThreadNum() != queueDistributerVo.getNew_Exe_ThreadNum()
										|| queueDistributerVoOld.getPortion_Pop_ThreadNum() != queueDistributerVo.getPortion_Pop_ThreadNum()
												|| queueDistributerVoOld.getPortion_Exe_ThreadNum() != queueDistributerVo.getPortion_Exe_ThreadNum()
						) {
							
							DistributeGet distributeGet = distributeGetMap.get(queueDistributerVo.getQueue_Nm());
							distributeGet.destroy();
							distributeGetMap.remove(queueDistributerVo.getQueue_Nm());
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove DistributeGet -> " 
										+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
										+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
										+ ", Is_Trace: " + queueDistributerVoOld.getIs_Trace()
										+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
										+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
										+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
										+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
										+ ", Queue Change"
										);
							}
							distributeGet = new DistributeGet(
										queueDistributerVo.getQueue_Nm(),
										dbConnectManager,
										queueCacheConnectManager,
										bodyCacheConnectManager,
										netConnectManager,
										distributeConfParam,
										queueDistributerVo,
										moduleId,
										overtimePerformanceExecuterService,
										successPerformanceExecuterService);
							distributeGet.start();
							distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributeGet);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Add DistributeGet -> " 
										+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
										+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
										+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
										+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
										+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
										+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
										+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
										+ ", Queue Change"
										);
							}
						} else {
							if (queueDistributerVoOld.getIs_Repeat() != queueDistributerVo.getIs_Repeat() 
									|| queueDistributerVoOld.getExpire_Time() != queueDistributerVo.getExpire_Time()
										|| queueDistributerVoOld.getIs_Trace() != queueDistributerVo.getIs_Trace()
											|| queueDistributerVoOld.getInterval_Time() != queueDistributerVo.getInterval_Time()
							) {
								DistributeGet distributeGet = distributeGetMap.get(queueDistributerVo.getQueue_Nm());
								distributeGet.setQueueDistributerVo(queueDistributerVo);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Change DistributeGet From -> " 
											+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
											+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
											+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
											+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
											+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
											+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
											+ ", Is_Trace: " + queueDistributerVoOld.getIs_Trace()
											+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
											+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
											+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
											+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
											+ ", Queue Change"
											);
									logger.debug("UpdateConnectAgent -> Change DistributeGet To -> " 
											+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
											+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
											+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
											+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
											+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
											+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
											+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
											+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
											+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
											+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
											+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
											+ ", Queue Change"
											);
								}
							}
								
							if (queueDistributerVoOld.getInterval_Time() != queueDistributerVo.getInterval_Time()
									|| queueDistributerVoOld.getDph_Delay_Time() != queueDistributerVo.getDph_Delay_Time()
							) {
								ConcurrentMap<Long, DistributeSet> distributeSetDbMap = distributeSetQueueMap.get(queueDistributerVo.getQueue_Nm());
								for (Long dbId : distributeSetDbMap.keySet()) {
									DistributeSet distributeSet = distributeSetDbMap.get(dbId);
									distributeSet.setQueueDistributerVo(queueDistributerVo);
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Change DistributeSet From -> " 
												+ "QueueName: " + queueDistributerVoOld.getQueue_Nm() 
												+ ", DbId: " + dbId
												+ ", Is_Repeat: " + queueDistributerVoOld.getIs_Repeat()
												+ ", Expire_Time: " + queueDistributerVoOld.getExpire_Time()
												+ ", Interval_Time: " + queueDistributerVoOld.getInterval_Time()
												+ ", Dph_Delay_Time: " + queueDistributerVoOld.getDph_Delay_Time()
												+ ", Dph_Timeout: " + queueDistributerVoOld.getDph_Timeout()
												+ ", Is_Trace: " + queueDistributerVoOld.getIs_Trace()
												+ ", New_Pop_ThreadNum: " + queueDistributerVoOld.getNew_Pop_ThreadNum()
												+ ", New_Exe_ThreadNum: " + queueDistributerVoOld.getNew_Exe_ThreadNum()
												+ ", Portion_Pop_ThreadNum: " + queueDistributerVoOld.getPortion_Pop_ThreadNum()
												+ ", Portion_Exe_ThreadNum: " + queueDistributerVoOld.getPortion_Exe_ThreadNum()
												+ ", Queue Change"
												);
										logger.debug("UpdateConnectAgent -> Change DistributeSet To -> " 
												+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
												+ ", DbId: " + dbId
												+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
												+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
												+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
												+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
												+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
												+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
				
				ConcurrentMap<Long, DistributeSet> distributeSetDbMap = distributeSetQueueMap.get(queueDistributerVo.getQueue_Nm());
				if (distributeSetDbMap == null) {
					distributeSetDbMap = new ConcurrentHashMap<Long, DistributeSet>();
					distributeSetQueueMap.put(queueDistributerVo.getQueue_Nm(), distributeSetDbMap);
				}
				Set<Long> dbIdSet = new HashSet<Long>();
				QueueAgent queueAgent = dbConnectManager.getQueueAgent(queueDistributerVo.getQueue_Nm());
				if (queueAgent != null) {
					List<ConnectAgent> connectAgentList = queueAgent.getConnectAgentAll();
					for (ConnectAgent connectAgent : connectAgentList) {
						dbIdSet.add(connectAgent.getConnectId());
						if (!distributeSetDbMap.containsKey(connectAgent.getConnectId())) {
							DistributeSet distributeSet = 
									new DistributeSet(
										queueDistributerVo.getQueue_Nm(),
										moduleId,
										dbConnectManager,
										queueCacheConnectManager,
										distributeConfParam,
										queueDistributerVo,
										connectAgent.getConnectId());
							distributeSet.start();
							distributeSetDbMap.put(connectAgent.getConnectId(), distributeSet);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Add DistributeSet -> " 
										+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
										+ ", DbId: " + connectAgent.getConnectId()
										+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
										+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
										+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
										+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
										+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
										+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
						DistributeSet distributeSet = distributeSetDbMap.get(dbId);
						distributeSet.destroy();
						distributeSetDbMap.remove(dbId);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove DistributeSet -> " 
									+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
									+ ", DbId: " + dbId
									+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
									+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
									+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
									+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
									+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
									+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
					DistributeSetLocker queueCacheDistributeSetLocker = queueCacheDistributeSetLockerMap.get(queueName);
					queueCacheDistributeSetLocker.destroy();
					queueCacheDistributeSetLockerMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Destroy QueueCache DistributeSet Locker -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								);
					}
					
					DistributeGet distributeGet = distributeGetMap.get(queueName);
					distributeGet.destroy();
					distributeGetMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove DistributeGet -> " 
								+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
								+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
								+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
								+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
								+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
								+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
								+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
								+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
								+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
								+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
								+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
								);
					}
					
					ConcurrentMap<Long, DistributeSet> distributeSetDbMap = distributeSetQueueMap.get(queueName);
					for (Long dbId : distributeSetDbMap.keySet()) {
						DistributeSet distributeSet = distributeSetDbMap.get(dbId);
						distributeSet.destroy();
						distributeSetDbMap.remove(dbId);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove DistributeSet -> " 
									+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
									+ ", DbId: " + dbId
									+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
									+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
									+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
									+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
									+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
									+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
			DistributeSetLocker queueCacheDistributeSetLocker = queueCacheDistributeSetLockerMap.get(queueName);
			queueCacheDistributeSetLocker.destroy();
			queueCacheDistributeSetLockerMap.remove(queueName);
			if (logger.isDebugEnabled()) {
				logger.debug("UpdateConnectAgent -> Destroy QueueCache DistributeSet Locker -> " 
						+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
						);
			}
			
			DistributeGet distributeGet = distributeGetMap.get(queueName);
			distributeGet.destroy();
			distributeGetMap.remove(queueName);
			if (logger.isDebugEnabled()) {
				logger.debug("UpdateConnectAgent -> Remove DistributeGet -> " 
						+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
						+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
						+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
						+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
						+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
						+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
						+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
						+ ", New_Pop_ThreadNum: " + queueDistributerVo.getNew_Pop_ThreadNum()
						+ ", New_Exe_ThreadNum: " + queueDistributerVo.getNew_Exe_ThreadNum()
						+ ", Portion_Pop_ThreadNum: " + queueDistributerVo.getPortion_Pop_ThreadNum()
						+ ", Portion_Exe_ThreadNum: " + queueDistributerVo.getPortion_Exe_ThreadNum()
						);
			}
			
			ConcurrentMap<Long, DistributeSet> distributeSetDbMap = distributeSetQueueMap.get(queueName);
			for (Long dbId : distributeSetDbMap.keySet()) {
				DistributeSet distributeSet = distributeSetDbMap.get(dbId);
				distributeSet.destroy();
				distributeSetDbMap.remove(dbId);
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Remove DistributeSet -> " 
							+ "QueueName: " + queueDistributerVo.getQueue_Nm() 
							+ ", DbId: " + dbId
							+ ", Is_Repeat: " + queueDistributerVo.getIs_Repeat()
							+ ", Expire_Time: " + queueDistributerVo.getExpire_Time()
							+ ", Interval_Time: " + queueDistributerVo.getInterval_Time()
							+ ", Dph_Delay_Time: " + queueDistributerVo.getDph_Delay_Time()
							+ ", Dph_Timeout: " + queueDistributerVo.getDph_Timeout()
							+ ", Is_Trace: " + queueDistributerVo.getIs_Trace()
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
		private DistributeSetLocker queueCacheDistributeSetLocker;
		
		public CheckActiveRunnable(String queueName, DistributeSetLocker queueCacheDistributeSetLocker) {
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
		
		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) queueAgent.getConnectAgent();
			if (queueCacheConnectAgent == null) {
				QueueCacheConnectAgent queueCacheConnectAgentOther = (QueueCacheConnectAgent) queueAgent.getConnectAgentOther(null);
				if (queueCacheConnectAgentOther != null) {
					try {
						queueCacheConnectAgentOther.setActive(queueName, true);
						if (logger.isInfoEnabled()) {
							logger.info("CheckActiveRunnable -> SetActive -> "
									+ "QUEUE: " + queueName
									+ ", ConnectId: " + ((ConnectAgent) queueCacheConnectAgentOther).getConnectId()
									+ ", Set Cache Queue Active");
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("CheckActiveRunnable -> SetActive -> "
									+ "QUEUE: " + queueName
									+ ", ConnectId: " + ((ConnectAgent) queueCacheConnectAgentOther).getConnectId()
									+ ", Set Active -> Set Cache Queue Active -> " + e);
						}
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("CheckActiveRunnable -> SetActive -> "
								+ "QUEUE: " + queueName
								+ ", Set Active -> Get Other Cache Connect Agent -> Null");
					}
					queueCacheConnectManager.startUpdateConnectAgent();
				}
			} else {
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
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("CheckActiveRunnable -> SetActive -> "
						+ "QUEUE: " + queueName 
						+ ", Set Active -> Get Queue Agent -> Null");
			}
		}
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

	public void setOvertimePerformanceExecuterService(
			OvertimePerformanceExecuterService overtimePerformanceExecuterService) {
		this.overtimePerformanceExecuterService = overtimePerformanceExecuterService;
	}

	public void setSuccessPerformanceExecuterService(
			SuccessPerformanceExecuterService successPerformanceExecuterService) {
		this.successPerformanceExecuterService = successPerformanceExecuterService;
	}
}
