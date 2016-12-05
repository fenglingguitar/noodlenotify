package org.fl.noodlenotify.core.connect.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;

public class IDConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new IDConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList);
	}
}
