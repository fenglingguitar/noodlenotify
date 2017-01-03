package org.fl.noodlenotify.core.connect.cache.queue.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class MasterConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new MasterConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.MASTER.getCode());
	}
}
