package org.fl.noodlenotify.core.connect.cache.body.distinguish;

import java.lang.reflect.Method;

import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;

public class BodyCacheConnectDistinguish implements ConnectDistinguish {

	private ConnectManagerPool connectManagerPool;
	
	@Override
	public ConnectManager getConnectManager() {
		return connectManagerPool.getConnectManager(ConnectManagerType.BODY_CACHE.getCode());
	}

	@Override
	public String getNodeName(Object[] args) {
		if (args[0] instanceof MessageDb) {
			return ((MessageDb) args[0]).getQueueName();
		}
		return (String) args[0];
	}

	@Override
	public String getRouteName(Object[] args) {
		return ConnectManagerType.BODY_CACHE.getCode();
	}

	@Override
	public String getMethodKay(Method method, Object[] args) {
		if (args[0] instanceof MessageDb) {
			return ((MessageDb) args[0]).getQueueName();
		}
		return (String) args[0];
	}

	@Override
	public String getModuleName(Object[] args) {
		return ConnectManagerType.BODY_CACHE.getCode();
	}
	
	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}
}
