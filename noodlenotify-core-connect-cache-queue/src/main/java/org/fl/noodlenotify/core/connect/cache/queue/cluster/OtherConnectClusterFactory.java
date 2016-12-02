package org.fl.noodlenotify.core.connect.cache.queue.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;

public class OtherConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new OtherConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList);
	}
}
