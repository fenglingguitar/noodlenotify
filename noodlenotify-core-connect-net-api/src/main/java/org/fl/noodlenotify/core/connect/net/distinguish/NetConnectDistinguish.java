package org.fl.noodlenotify.core.connect.net.distinguish;

import java.lang.reflect.Method;

import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodlenotify.core.connect.net.constent.NetConnectManagerType;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class NetConnectDistinguish implements ConnectDistinguish {

	@Override
	public ConnectManager getConnectManager() {
		return ConnectManagerPool.getConnectManager(NetConnectManagerType.NET.getCode());
	}

	@Override
	public String getNodeName(Object[] args) {		
		return ((Message) args[0]).getQueueName();
	}

	@Override
	public String getRouteName(Object[] args) {
		return NetConnectManagerType.NET.getCode();
	}

	@Override
	public String getMethodKay(Method method, Object[] args) {
		return ((Message) args[0]).getQueueName();
	}

	@Override
	public String getModuleName(Object[] args) {
		return NetConnectManagerType.NET.getCode();
	}
}
