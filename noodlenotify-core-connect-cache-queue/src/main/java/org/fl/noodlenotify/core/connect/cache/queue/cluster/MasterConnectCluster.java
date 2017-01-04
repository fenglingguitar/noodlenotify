package org.fl.noodlenotify.core.connect.cache.queue.cluster;

import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.cluster.AbstractConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectNoAliveException;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

public class MasterConnectCluster extends AbstractConnectCluster {
	
	private final static Logger logger = LoggerFactory.getLogger(MasterConnectCluster.class);
	
	public MasterConnectCluster(Class<?> serviceInterface, ConnectDistinguish connectDistinguish, List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		ConnectNode connectNode = getConnectNode(args);
		
		if (connectNode.getHealthyConnectAgentList().isEmpty()) {
			getConnectManager().runUpdate();
			throw new ConnectNoAliveException("all connect agent is no alive");
		}
		
		ConnectAgent connectAgentMain = null;
		
		try {
			ConnectAgent connectAgent = connectNode.getHealthyConnectAgentList().get(0);
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
		
		if (connectAgentMain == null) {
			for (ConnectAgent connectAgent : connectNode.getHealthyConnectAgentList()) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectAgent.getProxy();
				try {
					if (queueCacheConnectAgent.isActive(connectDistinguish.getMethodKay(method, args))) {
						connectAgentMain = connectAgent;
						connectNode.getHealthyConnectAgentList().remove(connectAgent);
						connectNode.getHealthyConnectAgentList().add(0, connectAgent);
						break;
					} 
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("GetConnectAgent -> Iteration IsActive: " + e);
					}
				}
			}
		}
		
		if (connectAgentMain != null) {
			try {
				return AopUtils.invokeJoinpointUsingReflection(connectAgentMain.getProxy(), method, args);
			} catch (Throwable e) {
				logger.error("doInvoke -> method.invoke -> Exception:{}", e.getMessage());
				throw e;
			}
		}
		
		return null;
	}
}
