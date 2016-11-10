package org.fl.noodlenotify.core.connect.net.cluster;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.AbstractConnectCluster;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.springframework.aop.support.AopUtils;

public class LayerConnectCluster extends AbstractConnectCluster {
	
	private ConnectCluster connectCluster;
	
	public LayerConnectCluster(Class<?> serviceInterface,
			ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList) {
		super(serviceInterface, connectDistinguish, methodInterceptorList);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		ConnectNode connectNode = getConnectNode(args);
		
		MessageDm messageDm = (MessageDm) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
		
		long executeQueue = messageDm.getExecuteQueue();
		long resultQueue = messageDm.getResultQueue();
		long executeQueueNum = 1;
		while (executeQueue > 0) {
			if ((executeQueue & 1) > 0 && (resultQueue & 1) == 0) {
				ConnectNode netGroupConnectNode = connectNode.getChildConnectNode(executeQueueNum);
				if (netGroupConnectNode != null) {
					ConnectThreadLocalStorage.put(LocalStorageType.LAYER_CONNECT_CLUSTER.getCode(), netGroupConnectNode);
					try {
						AopUtils.invokeJoinpointUsingReflection(connectCluster.getProxy(), method, args);
					} catch (Throwable e) {
						executeQueue >>= 1;
						resultQueue >>= 1;
						executeQueueNum <<= 1;
						continue;
					} finally {
						ConnectThreadLocalStorage.remove(LocalStorageType.LAYER_CONNECT_CLUSTER.getCode());
					}
					messageDm.setResultQueue(messageDm.getResultQueue() | executeQueueNum);
				}
			}
			executeQueue >>= 1;
			resultQueue >>= 1;
			executeQueueNum <<= 1;
		}
		
		return null;
	}
	
	public void setConnectCluster(ConnectCluster connectCluster) {
		this.connectCluster = connectCluster;
	}
}
