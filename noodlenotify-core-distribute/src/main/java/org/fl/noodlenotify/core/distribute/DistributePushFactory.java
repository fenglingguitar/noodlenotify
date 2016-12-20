package org.fl.noodlenotify.core.distribute;

import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;

public class DistributePushFactory {

	private ConnectManager queueCacheConnectManager;
	private ConnectManager netConnectManager;
		
	private DistributeConfParam distributeConfParam;
	
	public DistributePush createDistributePush(QueueDistributerVo queueDistributerVo) {
		return new DistributePush(
				queueCacheConnectManager,
				netConnectManager,
				distributeConfParam,
				queueDistributerVo);
	}

	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}

	public void setNetConnectManager(ConnectManager netConnectManager) {
		this.netConnectManager = netConnectManager;
	}

	public void setDistributeConfParam(DistributeConfParam distributeConfParam) {
		this.distributeConfParam = distributeConfParam;
	}
}
