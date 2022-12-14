package org.fl.noodlenotify.core.connect.cache.body.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class PartConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new PartConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.PART.getCode());
	}
}
