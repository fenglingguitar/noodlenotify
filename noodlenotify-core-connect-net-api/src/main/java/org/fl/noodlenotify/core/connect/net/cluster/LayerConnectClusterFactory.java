package org.fl.noodlenotify.core.connect.net.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class LayerConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new LayerConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.LAYER.getCode());
	}
}
