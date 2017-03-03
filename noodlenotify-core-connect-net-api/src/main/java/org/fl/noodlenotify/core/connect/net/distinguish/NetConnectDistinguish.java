package org.fl.noodlenotify.core.connect.net.distinguish;

import java.lang.reflect.Method;

import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;

public class NetConnectDistinguish implements ConnectDistinguish {

	private ConnectManagerPool connectManagerPool;
	
	@Override
	public ConnectManager getConnectManager() {
		return connectManagerPool.getConnectManager(ConnectManagerType.NET.getCode());
	}

	@Override
	public String getNodeName(Object[] args) {		
		return ((MessageRequest) args[0]).getQueueName();
	}

	@Override
	public String getRouteName(Object[] args) {
		return ConnectManagerType.NET.getCode();
	}

	@Override
	public String getMethodKay(Method method, Object[] args) {
		return ((MessageRequest) args[0]).getQueueName();
	}

	@Override
	public String getModuleName(Object[] args) {
		return ConnectManagerType.NET.getCode();
	}
	
	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}
}
