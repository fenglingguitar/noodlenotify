package org.fl.noodlenotify.core.distribute;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;

public class DistributePullFactory {

	private ModuleRegister distributeModuleRegister;
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private DistributeConfParam distributeConfParam;
	
	private List<MethodInterceptor> methodInterceptorList;
	
	public DistributePull createDistributePull(QueueDistributerVo queueDistributerVo, long dbId) {
		return new DistributePull(
				distributeModuleRegister.getModuleId(),
				dbConnectManager,
				queueCacheConnectManager,
				distributeConfParam,
				queueDistributerVo,
				dbId,
				methodInterceptorList);
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

	public void setMethodInterceptorList(List<MethodInterceptor> methodInterceptorList) {
		this.methodInterceptorList = methodInterceptorList;
	}
}
