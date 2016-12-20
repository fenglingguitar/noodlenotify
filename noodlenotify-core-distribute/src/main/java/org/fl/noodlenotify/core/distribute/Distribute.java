package org.fl.noodlenotify.core.distribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.distributedlock.api.DistributedLock;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;

public class Distribute {
	
	//private final static Logger logger = LoggerFactory.getLogger(Distribute.class);

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
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, DistributedLock> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, DistributedLock>();
	private ConcurrentMap<String, Boolean> queueCacheDistributeSetStopSignMap = new ConcurrentHashMap<String, Boolean>();
	private ExecutorService queueCacheDistributeSetLockerExecutorService = Executors.newCachedThreadPool();
	
	private String distributeName;
	private long moduleId;
	private String localIp;
	private int checkPort;
	
	private ModuleRegister distributeModuleRegister;
	
	private DistributePullFactory distributePullFactory;
	private DistributePushFactory distributePushFactory;
	
	public void start() throws Exception {
		
		if (distributeName == null || 
				(distributeName != null && distributeName.equals("hostname"))) {
			distributeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp(): localIp;
		moduleId = consoleRemotingInvoke.saveDistributerRegister(localIp, checkPort, distributeName);
		
		distributeModuleRegister.setModuleId(moduleId);

		netConnectManager.runUpdateNow();
		bodyCacheConnectManager.runUpdateNow();
		queueCacheConnectManager.runUpdateNow();
		dbConnectManager.runUpdateNow();
		
		updateConnectAgent();
		
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
			}
		});		
	}
	
	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveDistributerCancel(moduleId);

		stopSign = true;
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void suspendUpdateConnectAgent() {
		try {
			wait(suspendTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void updateConnectAgent() {
		
		List<QueueDistributerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.distributerGetQueues(moduleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		if (consoleInfoList != null) {
			
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
					
					DistributePush distributePush = distributePushFactory.createDistributePush(queueDistributerVo);
					distributePush.start();
					distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
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
							distributePush = distributePushFactory.createDistributePush(queueDistributerVo);
							distributePush.start();
							distributeGetMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
						} else {
							if (!queueDistributerVoOld.getIs_Repeat().equals(queueDistributerVo.getIs_Repeat())
									|| !queueDistributerVoOld.getExpire_Time().equals(queueDistributerVo.getExpire_Time())
											|| !queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
							) {
								DistributePush distributePush = distributeGetMap.get(queueDistributerVo.getQueue_Nm());
								distributePush.setQueueDistributerVo(queueDistributerVo);
							}
								
							if (!queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
									|| !queueDistributerVoOld.getDph_Delay_Time().equals(queueDistributerVo.getDph_Delay_Time())
							) {
								ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueDistributerVo.getQueue_Nm());
								for (Long dbId : distributeSetDbMap.keySet()) {
									DistributePull distributePull = distributeSetDbMap.get(dbId);
									distributePull.setQueueDistributerVo(queueDistributerVo);
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
					List<ConnectAgent> connectAgentList = connectNode.getConnectAgentList();
					for (ConnectAgent connectAgent : connectAgentList) {
						dbIdSet.add(connectAgent.getConnectId());
						if (!distributeSetDbMap.containsKey(connectAgent.getConnectId())) {
							DistributePull distributePull = distributePullFactory.createDistributePull(queueDistributerVo, connectAgent.getConnectId());
							distributePull.start();
							distributeSetDbMap.put(connectAgent.getConnectId(), distributePull);
						} 
					}
				}
				for (Long dbId : distributeSetDbMap.keySet()) {
					if (!dbIdSet.contains(dbId)) {
						DistributePull distributePull = distributeSetDbMap.get(dbId);
						distributePull.destroy();
						distributeSetDbMap.remove(dbId);
					}
				}
			}
			
			Set<String> keySetGet = queueDistributerVoMap.keySet();
			for (String queueName : keySetGet) {
				if (!queueNameSet.contains(queueName)) {
					
					queueDistributerVoMap.remove(queueName);

					queueCacheDistributeSetStopSignMap.put(queueName, true);
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = (QueueCacheDistributeSetLocker) queueCacheDistributeSetLockerMap.get(queueName);
					queueCacheDistributeSetLocker.destroy();
					queueCacheDistributeSetLockerMap.remove(queueName);
					
					DistributePush distributePush = distributeGetMap.get(queueName);
					distributePush.destroy();
					distributeGetMap.remove(queueName);
					
					ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueName);
					for (Long dbId : distributeSetDbMap.keySet()) {
						DistributePull distributePull = distributeSetDbMap.get(dbId);
						distributePull.destroy();
						distributeSetDbMap.remove(dbId);
					}
					distributeSetQueueMap.remove(queueName);
				}
			}
		}
	}
	
	private void destroyConnectAgent() {
		
		Set<String> keySetGet = queueDistributerVoMap.keySet();
		for (String queueName : keySetGet) {
			
			queueDistributerVoMap.remove(queueName);
			
			queueCacheDistributeSetStopSignMap.put(queueName, true);
			QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = (QueueCacheDistributeSetLocker) queueCacheDistributeSetLockerMap.get(queueName);
			queueCacheDistributeSetLocker.destroy();
			queueCacheDistributeSetLockerMap.remove(queueName);
			
			DistributePush distributePush = distributeGetMap.get(queueName);
			distributePush.destroy();
			distributeGetMap.remove(queueName);
			
			ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributeSetQueueMap.get(queueName);
			for (Long dbId : distributeSetDbMap.keySet()) {
				DistributePull distributePull = distributeSetDbMap.get(dbId);
				distributePull.destroy();
				distributeSetDbMap.remove(dbId);
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
					e.printStackTrace();
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
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setDistributePullFactory(DistributePullFactory distributePullFactory) {
		this.distributePullFactory = distributePullFactory;
	}

	public void setDistributePushFactory(DistributePushFactory distributePushFactory) {
		this.distributePushFactory = distributePushFactory;
	}
}
