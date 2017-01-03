package org.fl.noodlenotify.core.connect.net.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class LayerFailoverConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new LayerFailoverConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.LAYERFAILOVER.getCode());
	}
}
