package org.fl.noodlenotify.core.distribute.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.distribute.DistributePull;
import org.fl.noodlenotify.core.distribute.DistributePullFactory;
import org.fl.noodlenotify.core.distribute.DistributePush;
import org.fl.noodlenotify.core.distribute.DistributePushFactory;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.CheckActiveLockChangeHandler;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;

public class DistributeConnectManager extends AbstractConnectManager {

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private DistributePullFactory distributePullFactory;
	private DistributePushFactory distributePushFactory;

	private ConnectDistinguish queueCacheConnectDistinguish;
	
	private ConcurrentMap<String, DistributePush> distributePushMap = new ConcurrentHashMap<String, DistributePush>();
	private ConcurrentMap<String, ConcurrentMap<Long, DistributePull>> distributePullMap = new ConcurrentHashMap<String, ConcurrentMap<Long, DistributePull>>();
	private ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private ConcurrentMap<String, QueueCacheDistributeSetLocker> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, QueueCacheDistributeSetLocker>();
	
	private Map<QueueDistributerVo, List<QueueMsgStorageVo>> queueDistributerInfoMap = null;
	
	private List<QueueDistributerVo> addPushList = null;
	private List<QueueDistributerVo> addLockerList = null;
	private Map<QueueDistributerVo, List<Long>> addPullMap = null;
	
	private List<String> reducePushList = null;
	private List<String> reduceLockerList = null;
	private Map<String, List<Long>> reducePullMap = null;
	
	private List<QueueDistributerVo> updatePushList = null;
	private List<QueueDistributerVo> updatePullList = null;
	
	@Override
	protected synchronized void updateConnectAgent() {
		
		queueDistributerInfoMap = null;
		
		queryInfo();
		
		if (queueDistributerInfoMap != null && !queueDistributerInfoMap.isEmpty()) {
			getAddPush();
			addPush();
			getAddLocker();
			addLocker();
			getAddPull();
			addPull();
			
			getReducePush();
			reducePush();
			getReduceLocker();
			reduceLocker();
			getReducePull();
			reducePull();
			
			getUpdatePush();
			updatePush();
			getUpdatePull();
			updatePull();
		}
				
		/*if (queueDistributerList != null && !queueDistributerList.isEmpty()) {
			
			Set<String> queueNameSet = new HashSet<String>();
			
			for (QueueDistributerVo queueDistributerVo : queueDistributerList) {
				queueNameSet.add(queueDistributerVo.getQueue_Nm());
				if (!queueDistributerVoMap.containsKey(queueDistributerVo.getQueue_Nm())) {
					queueDistributerVoMap.put(queueDistributerVo.getQueue_Nm(), queueDistributerVo);
					
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(queueDistributerVo.getQueue_Nm(), moduleId, queueCacheConnectDistinguish.getConnectManager());
					queueCacheDistributeSetLocker.setLockChangeHandler(new CheckActiveLockChangeHandler(queueDistributerVo.getQueue_Nm(), queueCacheConnectDistinguish));
					try {
						queueCacheDistributeSetLocker.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					queueCacheDistributeSetLockerMap.put(queueDistributerVo.getQueue_Nm(), queueCacheDistributeSetLocker);
					
					DistributePush distributePush = distributePushFactory.createDistributePush(queueDistributerVo);
					distributePush.start();
					distributePushMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
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
							
							DistributePush distributePush = distributePushMap.get(queueDistributerVo.getQueue_Nm());
							distributePush.destroy();
							distributePushMap.remove(queueDistributerVo.getQueue_Nm());
							distributePush = distributePushFactory.createDistributePush(queueDistributerVo);
							distributePush.start();
							distributePushMap.put(queueDistributerVo.getQueue_Nm(), distributePush);
						} else {
							if (!queueDistributerVoOld.getIs_Repeat().equals(queueDistributerVo.getIs_Repeat())
									|| !queueDistributerVoOld.getExpire_Time().equals(queueDistributerVo.getExpire_Time())
											|| !queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
							) {
								DistributePush distributePush = distributePushMap.get(queueDistributerVo.getQueue_Nm());
								distributePush.setQueueDistributerVo(queueDistributerVo);
							}
								
							if (!queueDistributerVoOld.getInterval_Time().equals(queueDistributerVo.getInterval_Time())
									|| !queueDistributerVoOld.getDph_Delay_Time().equals(queueDistributerVo.getDph_Delay_Time())
							) {
								ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributePullMap.get(queueDistributerVo.getQueue_Nm());
								for (Long dbId : distributeSetDbMap.keySet()) {
									DistributePull distributePull = distributeSetDbMap.get(dbId);
									distributePull.setQueueDistributerVo(queueDistributerVo);
								}
							}
						}
					}
				}
				
				ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributePullMap.get(queueDistributerVo.getQueue_Nm());
				if (distributeSetDbMap == null) {
					distributeSetDbMap = new ConcurrentHashMap<Long, DistributePull>();
					distributePullMap.put(queueDistributerVo.getQueue_Nm(), distributeSetDbMap);
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
					
					QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = (QueueCacheDistributeSetLocker) queueCacheDistributeSetLockerMap.get(queueName);
					queueCacheDistributeSetLocker.destroy();
					queueCacheDistributeSetLockerMap.remove(queueName);
					
					DistributePush distributePush = distributePushMap.get(queueName);
					distributePush.destroy();
					distributePushMap.remove(queueName);
					
					ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributePullMap.get(queueName);
					for (Long dbId : distributeSetDbMap.keySet()) {
						DistributePull distributePull = distributeSetDbMap.get(dbId);
						distributePull.destroy();
						distributeSetDbMap.remove(dbId);
					}
					distributePullMap.remove(queueName);
				}
			}
		}*/
		
		
		
	}

	@Override
	protected void destroyConnectAgent() {
		
		Set<String> keySetGet = queueDistributerVoMap.keySet();
		for (String queueName : keySetGet) {
			
			queueDistributerVoMap.remove(queueName);
			distributePushMap.remove(queueName).destroy();
			queueCacheDistributeSetLockerMap.remove(queueName).destroy();
			
			ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributePullMap.get(queueName);
			for (Long dbId : distributePullMap.get(queueName).keySet()) {
				distributeSetDbMap.remove(dbId).destroy();
			}
			distributePullMap.remove(queueName);
		}
	}
	
	protected void queryInfo() {
		try {
			queueDistributerInfoMap = consoleRemotingInvoke.distributerGetQueues(distributeModuleRegister.getModuleId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAddPush() {
		addPushList = new ArrayList<QueueDistributerVo>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			if (!queueDistributerVoMap.containsKey(queueDistributerVoIt.getQueue_Nm())) {
				addPushList.add(queueDistributerVoIt);
			} else {
				QueueDistributerVo queueDistributerVoOld = queueDistributerVoMap.get(queueDistributerVoIt.getQueue_Nm());
				if (!queueDistributerVoOld.getNew_Pop_ThreadNum().equals(queueDistributerVoIt.getNew_Pop_ThreadNum())
						|| !queueDistributerVoOld.getNew_Exe_ThreadNum().equals(queueDistributerVoIt.getNew_Exe_ThreadNum())
								|| !queueDistributerVoOld.getPortion_Pop_ThreadNum().equals(queueDistributerVoIt.getPortion_Pop_ThreadNum())
										|| !queueDistributerVoOld.getPortion_Exe_ThreadNum().equals(queueDistributerVoIt.getPortion_Exe_ThreadNum())
				) {
					addPushList.add(queueDistributerVoIt);
				}
			}
		}
	}
	
	private void addPush() {
		for (QueueDistributerVo queueDistributerVoIt : addPushList) {
			if (queueDistributerVoMap.containsKey(queueDistributerVoIt.getQueue_Nm())) {
				distributePushMap.remove(queueDistributerVoIt.getQueue_Nm()).destroy();
			} else {
				queueDistributerVoMap.put(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt);
			}
			DistributePush distributePush = distributePushFactory.createDistributePush(queueDistributerVoIt);
			distributePush.start();
			distributePushMap.put(queueDistributerVoIt.getQueue_Nm(), distributePush);
		}
	}

	private void getAddLocker() {
		addLockerList = new ArrayList<QueueDistributerVo>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			if (!queueCacheDistributeSetLockerMap.containsKey(queueDistributerVoIt.getQueue_Nm())) {
				addLockerList.add(queueDistributerVoIt);
			}
		}
	}
	
	private void addLocker() {
		for (QueueDistributerVo queueDistributerVoIt : addLockerList) {
			QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(queueDistributerVoIt.getQueue_Nm(), distributeModuleRegister.getModuleId(), queueCacheConnectDistinguish.getConnectManager());
			queueCacheDistributeSetLocker.setLockChangeHandler(new CheckActiveLockChangeHandler(queueDistributerVoIt.getQueue_Nm(), queueCacheConnectDistinguish));
			try {
				queueCacheDistributeSetLocker.start();
				queueCacheDistributeSetLockerMap.put(queueDistributerVoIt.getQueue_Nm(), queueCacheDistributeSetLocker);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getAddPull() {
		addPullMap = new HashMap<QueueDistributerVo, List<Long>>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			ConcurrentMap<Long, DistributePull> distributePullDbMap = distributePullMap.get(queueDistributerVoIt.getQueue_Nm());
			if (distributePullDbMap == null) {
				distributePullDbMap = new ConcurrentHashMap<Long, DistributePull>();
				distributePullMap.put(queueDistributerVoIt.getQueue_Nm(), distributePullDbMap);
			}
			for(QueueMsgStorageVo queueMsgStorageVoIt : queueDistributerInfoMap.get(queueDistributerVoIt)) {
				if (!distributePullDbMap.containsKey(queueMsgStorageVoIt.getMsgStorage_Id())) {
					if (!addPullMap.containsKey(queueDistributerVoIt)) {
						addPullMap.put(queueDistributerVoIt, new ArrayList<Long>());
					}
					addPullMap.get(queueDistributerVoIt).add(queueMsgStorageVoIt.getMsgStorage_Id());
				}
			}
		}
	}
	
	private void addPull() {
		for (QueueDistributerVo queueDistributerVoIt : addPullMap.keySet()) {
			for (Long id : addPullMap.get(queueDistributerVoIt)) {
				DistributePull distributePull = distributePullFactory.createDistributePull(queueDistributerVoIt, id);
				distributePull.start();
				distributePullMap.get(queueDistributerVoIt.getQueue_Nm()).put(id, distributePull);
			}
		}
	}
	
	private void getReducePush() {
		reducePushList = new ArrayList<String>();
		for (String name : queueDistributerVoMap.keySet()) {
			boolean isHave = false;
			for(QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
				if (name.equals(queueDistributerVoIt.getQueue_Nm())) {
					isHave = true;
					break;
				}
			}
			if (!isHave) {
				reducePushList.add(name);
			}
		}
	}
	
	private void reducePush() {
		for (String name : reducePushList) {
			distributePushMap.remove(name).destroy();
			queueDistributerVoMap.remove(name);
		}
	}
	
	private void getReduceLocker() {
		reduceLockerList = new ArrayList<String>();
		for (String name : queueCacheDistributeSetLockerMap.keySet()) {
			boolean isHave = false;
			for(QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
				if (name.equals(queueDistributerVoIt.getQueue_Nm())) {
					isHave = true;
					break;
				}
			}
			if (!isHave) {
				reduceLockerList.add(name);
			}
		}
	}
	
	private void reduceLocker() {
		for (String name : reduceLockerList) {
			queueCacheDistributeSetLockerMap.remove(name).destroy();
		}
	}
	
	private void getReducePull() {
		reducePullMap = new HashMap<String, List<Long>>();
		for (String name : distributePullMap.keySet()) {
			for (Long id : distributePullMap.get(name).keySet()) {
				boolean isHave = false;
				for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
					if (name.equals(queueDistributerVoIt.getQueue_Nm())) {
						for (QueueMsgStorageVo queueMsgStorageVoIt : queueDistributerInfoMap.get(queueDistributerVoIt)) {
							if (id == queueMsgStorageVoIt.getMsgStorage_Id()) {
								isHave = true;
								break;
							}
						}
						break;
					}
				}
				if (!isHave) {
					if (!reducePullMap.containsKey(name)) {
						reducePullMap.put(name, new ArrayList<Long>());
					}
					reducePullMap.get(name).add(id);
				}
			}
		}
	}
	
	private void reducePull() {
		for (String name : reducePullMap.keySet()) {
			for (Long id : reducePullMap.get(name)) {
				distributePullMap.get(name).remove(id).destroy();
			}
			if (distributePullMap.get(name).isEmpty()) {
				distributePullMap.remove(name);
			}
		}
	}
	
	private void getUpdatePush() {
		updatePushList = new ArrayList<QueueDistributerVo>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			QueueDistributerVo queueDistributerVoOld = queueDistributerVoMap.get(queueDistributerVoIt.getQueue_Nm());
			if (!queueDistributerVoOld.getIs_Repeat().equals(queueDistributerVoIt.getIs_Repeat())
					|| !queueDistributerVoOld.getExpire_Time().equals(queueDistributerVoIt.getExpire_Time())
						|| !queueDistributerVoOld.getInterval_Time().equals(queueDistributerVoIt.getInterval_Time())
			) {
				updatePushList.add(queueDistributerVoIt);
			}
		}
	}
	
	private void updatePush() {
		for (QueueDistributerVo queueDistributerVoIt : updatePushList) {
			distributePushMap.get(queueDistributerVoIt.getQueue_Nm()).setQueueDistributerVo(queueDistributerVoIt);
		}
	}
	
	private void getUpdatePull() {
		updatePullList = new ArrayList<QueueDistributerVo>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			QueueDistributerVo queueDistributerVoOld = queueDistributerVoMap.get(queueDistributerVoIt.getQueue_Nm());
			if (!queueDistributerVoOld.getInterval_Time().equals(queueDistributerVoIt.getInterval_Time())
					|| !queueDistributerVoOld.getDph_Delay_Time().equals(queueDistributerVoIt.getDph_Delay_Time())
			) {
				updatePullList.add(queueDistributerVoIt);
			}
		}
	}
	
	private void updatePull() {
		for (QueueDistributerVo queueDistributerVoIt : updatePullList) {
			for (Long id : distributePullMap.get(queueDistributerVoIt.getQueue_Nm()).keySet()) {
				distributePullMap.get(queueDistributerVoIt.getQueue_Nm()).get(id).setQueueDistributerVo(queueDistributerVoIt);
			}
		}
	}
	
	@Override
	public String getManagerName() {
		return ConnectManagerType.DISTRIBUTE.getCode();
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

	public void setQueueCacheConnectDistinguish(ConnectDistinguish queueCacheConnectDistinguish) {
		this.queueCacheConnectDistinguish = queueCacheConnectDistinguish;
	}
}
