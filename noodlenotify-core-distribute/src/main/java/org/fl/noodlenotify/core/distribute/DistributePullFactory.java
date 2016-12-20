package org.fl.noodlenotify.core.distribute;

import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;

public class DistributePullFactory {

	private ModuleRegister distributeModuleRegister;
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private DistributeConfParam distributeConfParam;
	
	public DistributePull createDistributePull(QueueDistributerVo queueDistributerVo, long dbId) {
		return new DistributePull(
				distributeModuleRegister.getModuleId(),
				dbConnectManager,
				queueCacheConnectManager,
				distributeConfParam,
				queueDistributerVo,
				dbId);
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}
	
	public void setDbConnectManager(ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}

	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}

	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}
}
