package org.fl.noodlenotify.core.connect.cache.body.cluster;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.AbstractConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectNoAliveException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodle.common.connect.exception.ConnectUnableException;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

public class EitherConnectCluster extends AbstractConnectCluster {
	
	private final static Logger logger = LoggerFactory.getLogger(EitherConnectCluster.class);
	
	public EitherConnectCluster(Class<?> serviceInterface, ConnectDistinguish connectDistinguish, List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		ConnectNode connectNode = getConnectNode(args);
		ConnectRoute connectRoute = getConnectRoute(args);
		
		if (connectNode.getConnectAgentList().isEmpty()) {
			getConnectManager().runUpdate();
			throw new ConnectNoAliveException("all connect agent is no alive");
		}
		
		Object result = null;
		
		MessageDm messageDm = (MessageDm) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
		
		List<ConnectAgent> connectAgentListPartAll = new LinkedList<ConnectAgent>();
		if (messageDm.getRedisOne() > 0) {
			ConnectAgent connectAgent = getConnectManager().getConnectAgent(messageDm.getRedisOne());
			if (connectAgent != null) {
				connectAgentListPartAll.add(connectAgent);
			}
		}
		if (messageDm.getRedisTwo() > 0) {
			ConnectAgent connectAgent = getConnectManager().getConnectAgent(messageDm.getRedisTwo());
			if (connectAgent != null) {
				connectAgentListPartAll.add(connectAgent);
			}
		}
		
		List<ConnectAgent> connectAgentListSelected = new LinkedList<ConnectAgent>();
		ConnectAgent connectAgent = null;		
		do {
			connectAgent = connectRoute.selectConnect(connectNode.getConnectAgentList(), connectAgentListSelected, connectDistinguish.getMethodKay(method, args));
			if (connectAgent != null) {
				connectAgentListSelected.add(connectAgent);
				try {
					result = AopUtils.invokeJoinpointUsingReflection(connectAgent.getProxy(), method, args);
				} catch (Throwable e) {
					logger.error("doInvoke -> method.invoke -> Exception:{}", e.getMessage());
					if (e instanceof ConnectUnableException
							|| e instanceof ConnectResetException
								|| e instanceof ConnectTimeoutException) {
						continue;
					} else {
						throw e;
					}
				}
			} 
		} while (connectAgent != null);
		
		return result;
	}
}
