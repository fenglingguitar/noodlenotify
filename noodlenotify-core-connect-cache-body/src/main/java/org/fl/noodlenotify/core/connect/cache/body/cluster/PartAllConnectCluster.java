package org.fl.noodlenotify.core.connect.cache.body.cluster;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.AbstractConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

public class PartAllConnectCluster extends AbstractConnectCluster {
	
	private final static Logger logger = LoggerFactory.getLogger(PartAllConnectCluster.class);
	
	public PartAllConnectCluster(Class<?> serviceInterface, ConnectDistinguish connectDistinguish, List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		Object result = null;
		Throwable resultThrowable = null;
		
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
		
		for (ConnectAgent ConnectAgentIt : connectAgentListPartAll) {
			try {
				result = AopUtils.invokeJoinpointUsingReflection(ConnectAgentIt.getProxy(), method, args);
			} catch (Throwable e) {
				logger.error("doInvoke -> method.invoke -> Exception:{}", e.getMessage());
				resultThrowable = e;
			}
		}
		
		if (resultThrowable != null) {
			throw resultThrowable;
		}
		
		return result;
	}
}
