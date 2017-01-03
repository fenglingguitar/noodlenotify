package org.fl.noodlenotify.core.connect.net.cluster;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.FailoverConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;

public class LayerFailoverConnectCluster extends FailoverConnectCluster {
	
	public LayerFailoverConnectCluster(Class<?> serviceInterface,
			ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}
	
	protected ConnectNode getConnectNode(Object[] args) throws ConnectInvokeException {
		if (!ConnectThreadLocalStorage.contain(LocalStorageType.LAYER_CONNECT_CLUSTER.getCode())) {
			throw new ConnectInvokeException("no this connect node");
		}
		return (ConnectNode) ConnectThreadLocalStorage.get(LocalStorageType.LAYER_CONNECT_CLUSTER.getCode());
	}
}
