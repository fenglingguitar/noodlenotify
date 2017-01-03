package org.fl.noodlenotify.core.connect.cache.body.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class PartAllConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new PartAllConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.PARTALL.getCode());
	}
}
