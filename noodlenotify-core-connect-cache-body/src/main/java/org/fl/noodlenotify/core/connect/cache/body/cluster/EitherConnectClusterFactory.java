package org.fl.noodlenotify.core.connect.cache.body.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class EitherConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new EitherConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.EITHER.getCode());
	}
}
