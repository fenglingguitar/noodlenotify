package org.fl.noodlenotify.core.distribute.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.distribute.DistributePull;
import org.fl.noodlenotify.core.distribute.DistributePullFactory;
import org.fl.noodlenotify.core.distribute.DistributePush;
import org.fl.noodlenotify.core.distribute.DistributePushFactory;
import org.fl.noodlenotify.core.distribute.locker.QueueCacheDistributeSetLocker;

public class DistributeConnectManager extends AbstractConnectManager {

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private DistributePullFactory distributePullFactory;
	private DistributePushFactory distributePushFactory;
	
	private ConcurrentMap<String, DistributePush> distributePushMap = new ConcurrentHashMap<String, DistributePush>();
	private ConcurrentMap<String, ConcurrentMap<Long, DistributePull>> distributePullMap = new ConcurrentHashMap<String, ConcurrentMap<Long, DistributePull>>();
	private ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private ConcurrentMap<String, QueueCacheDistributeSetLocker> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, QueueCacheDistributeSetLocker>();
	
	private Map<QueueDistributerVo, List<QueueDbVo>> queueDistributerInfoMap = null;
	
	private List<QueueDistributerVo> addPushList = null;
	private Map<QueueDistributerVo, List<Long>> addPullMap = null;
	
	private List<String> reducePushList = null;
	private Map<String, List<Long>> reducePullMap = null;
	
	private List<QueueDistributerVo> updatePushList = null;
	private List<QueueDistributerVo> updatePullList = null;

	@Override
	protected void destroyConnectAgent() {
		
		Set<String> keySetGet = queueDistributerVoMap.keySet();
		for (String queueName : keySetGet) {
			
			queueDistributerVoMap.remove(queueName);
			distributePushMap.remove(queueName).destroy();
			queueCacheDistributeSetLockerMap.remove(queueName).destroy();
			
			ConcurrentMap<Long, DistributePull> distributeSetDbMap = distributePullMap.get(queueName);
			for (long dbId : distributePullMap.get(queueName).keySet()) {
				distributeSetDbMap.remove(dbId).destroy();
			}
			distributePullMap.remove(queueName);
		}
	}
	
	@Override
	public void runUpdateAddComponent() {	
		cleanComponent();
		queryInfo();
		addComponent();
		updateComponent();
	}
	
	@Override
	public void runUpdateReduceComponent() {
		cleanComponent();
		queryInfo();
		reduceComponent();
	}
	
	protected void cleanComponent() {
		queueDistributerInfoMap = null;
	}
	
	protected void addComponent() {
		if (queueDistributerInfoMap != null) {
			getAddPush();
			addPush();
			getAddPull();
			addPull();
		}
	}
	
	protected void reduceComponent() {
		if (queueDistributerInfoMap != null) {
			getReducePull();
			reducePull();
			getReducePush();
			reducePush();
		}
	}
	
	protected void updateComponent() {
		if (queueDistributerInfoMap != null) {			
			getUpdatePush();
			updatePush();
			getUpdatePull();
			updatePull();
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
										|| !queueDistributerVoOld.getPortion_Exe_ThreadNum().equals(queueDistributerVoIt.getPortion_Exe_ThreadNum())) {
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
	
	private void getAddPull() {
		addPullMap = new HashMap<QueueDistributerVo, List<Long>>();
		for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
			ConcurrentMap<Long, DistributePull> distributePullDbMap = distributePullMap.get(queueDistributerVoIt.getQueue_Nm());
			if (distributePullDbMap == null) {
				distributePullDbMap = new ConcurrentHashMap<Long, DistributePull>();
				distributePullMap.put(queueDistributerVoIt.getQueue_Nm(), distributePullDbMap);
			}
			for(QueueDbVo queueDbVoIt : queueDistributerInfoMap.get(queueDistributerVoIt)) {
				if (!distributePullDbMap.containsKey(queueDbVoIt.getDb_Id())) {
					if (!addPullMap.containsKey(queueDistributerVoIt)) {
						addPullMap.put(queueDistributerVoIt, new ArrayList<Long>());
					}
					addPullMap.get(queueDistributerVoIt).add(queueDbVoIt.getDb_Id());
				}
			}
		}
	}
	
	private void addPull() {
		for (QueueDistributerVo queueDistributerVoIt : addPullMap.keySet()) {
			for (long id : addPullMap.get(queueDistributerVoIt)) {
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
	
	private void getReducePull() {
		reducePullMap = new HashMap<String, List<Long>>();
		for (String name : distributePullMap.keySet()) {
			for (long id : distributePullMap.get(name).keySet()) {
				boolean isHave = false;
				for (QueueDistributerVo queueDistributerVoIt : queueDistributerInfoMap.keySet()) {
					if (name.equals(queueDistributerVoIt.getQueue_Nm())) {
						for (QueueDbVo queueDbVoIt : queueDistributerInfoMap.get(queueDistributerVoIt)) {
							if (id == queueDbVoIt.getDb_Id()) {
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
			for (long id : reducePullMap.get(name)) {
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
			for (long id : distributePullMap.get(queueDistributerVoIt.getQueue_Nm()).keySet()) {
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
}
