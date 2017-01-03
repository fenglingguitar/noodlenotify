package org.fl.noodlenotify.core.connect.cache.queue.cluster;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.cluster.AbstractConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectNoAliveException;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

public class OtherConnectCluster extends AbstractConnectCluster {
	
	private final static Logger logger = LoggerFactory.getLogger(OtherConnectCluster.class);
	
	public OtherConnectCluster(Class<?> serviceInterface, ConnectDistinguish connectDistinguish, List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		ConnectManager connectManager = getConnectManager();
		ConnectNode connectNode = getConnectNode(args);
		
		if (connectNode.getConnectAgentList().isEmpty()) {
			getConnectManager().runUpdate();
			throw new ConnectNoAliveException("all connect agent is no alive");
		}
		
		ConnectAgent connectAgentMain = null;
		
		try {
			ConnectAgent connectAgent = connectNode.getConnectAgentList().get(0);
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectAgent.getProxy();
			try {
				if (queueCacheConnectAgent.isActive(connectDistinguish.getMethodKay(method, args))) {
					connectAgentMain = connectAgent;
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("GetConnectAgent -> Get First IsActive -> " + e);
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			getConnectManager().runUpdate();
			throw new ConnectNoAliveException("all connect agent is no alive");
		}
		
		List<ConnectAgent> queueCacheConnectAgentList = connectManager.getConnectNode(connectDistinguish.getMethodKay(method, args)).getConnectAgentList();
		for (ConnectAgent connectAgent : queueCacheConnectAgentList) {
			if (connectAgentMain != null && connectAgent == connectAgentMain) {
				continue;
			}
			return AopUtils.invokeJoinpointUsingReflection(connectAgent.getProxy(), method, args);
		}
		
		return null;
	}
}
