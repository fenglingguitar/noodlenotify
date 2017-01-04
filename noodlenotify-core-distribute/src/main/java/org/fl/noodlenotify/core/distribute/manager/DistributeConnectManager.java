package org.fl.noodlenotify.core.distribute.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.distributedlock.api.DistributedLock;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.distribute.DistributeConfParam;
import org.fl.noodlenotify.core.distribute.DistributePull;
import org.fl.noodlenotify.core.distribute.DistributePullFactory;
import org.fl.noodlenotify.core.distribute.DistributePush;
import org.fl.noodlenotify.core.distribute.DistributePushFactory;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;

public class DistributeConnectManager extends AbstractConnectManager {

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private DistributePullFactory distributePullFactory;
	private DistributePushFactory distributePushFactory;

	private ConnectDistinguish queueCacheConnectDistinguish;
	private ConnectDistinguish dbConnectDistinguish;
	
	private DistributeConfParam distributeConfParam = new DistributeConfParam();
	
	private ConcurrentMap<String, DistributePush> distributeGetMap = new ConcurrentHashMap<String, DistributePush>();
	private ConcurrentMap<String, ConcurrentMap<Long, DistributePull>> distributeSetQueueMap = new ConcurrentHashMap<String, ConcurrentMap<Long, DistributePull>>();
	ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private ConcurrentMap<String, DistributedLock> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, DistributedLock>();
	private ConcurrentMap<String, Boolean> queueCacheDistributeSetStopSignMap = new ConcurrentHashMap<String, Boolean>();
	private ExecutorService queueCacheDistributeSetLockerExecutorService = Executors.newCachedThreadPool();
	
	@Override
	protected void updateConnectAgent() {
		
		long moduleId = distributeModuleRegister.getModuleId();
		
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
					
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(queueDistributerVo.getQueue_Nm(), moduleId, queueCacheConnectDistinguish.getConnectManager());
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
				ConnectNode connectNode = dbConnectDistinguish.getConnectManager().getConnectNode(queueDistributerVo.getQueue_Nm());
				if (connectNode != null) {
					List<ConnectAgent> connectAgentList = connectNode.getHealthyConnectAgentList();
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

	@Override
	protected void destroyConnectAgent() {
		
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

	@Override
	public String getManagerName() {
		return ConnectManagerType.DISTRIBUTE.getCode();
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
				
				setActive(queueName, queueCacheConnectDistinguish.getConnectManager());
				
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
		if (connectNode != null && connectNode.getHealthyConnectAgentList().size() > 0) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectNode.getHealthyConnectAgentList().get(0);
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

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	public void setDistributePullFactory(DistributePullFactory distributePullFactory) {
		this.distributePullFactory = distributePullFactory;
	}

	public void setDistributePushFactory(DistributePushFactory distributePushFactory) {
		this.distributePushFactory = distributePushFactory;
	}

	public void setQueueCacheConnectDistinguish(
			ConnectDistinguish queueCacheConnectDistinguish) {
		this.queueCacheConnectDistinguish = queueCacheConnectDistinguish;
	}

	public void setDbConnectDistinguish(ConnectDistinguish dbConnectDistinguish) {
		this.dbConnectDistinguish = dbConnectDistinguish;
	}

	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}
}
