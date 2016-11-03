package org.fl.noodlenotify.core.connect.net.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;

public class LayerFailoverConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new LayerFailoverConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList);
	}
}
