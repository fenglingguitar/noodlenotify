package org.fl.noodlenotify.core.connect.cluster;

import org.fl.noodle.common.connect.cluster.AbstractConnectClusterFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodlenotify.core.connect.constent.ConnectClusterExtendType;

public class IDConnectClusterFactory extends AbstractConnectClusterFactory {

	@Override
	public ConnectCluster createConnectCluster(Class<?> serviceInterface) {
		return new IDConnectCluster(serviceInterface, connectDistinguish, methodInterceptorList, ConnectClusterExtendType.ID.getCode());
	}
}
