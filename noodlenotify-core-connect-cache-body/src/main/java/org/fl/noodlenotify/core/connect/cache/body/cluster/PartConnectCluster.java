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
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

public class PartConnectCluster extends AbstractConnectCluster {
	
	private final static Logger logger = LoggerFactory.getLogger(PartConnectCluster.class);
	
	private int maxInvokeNum = 2;
	
	public PartConnectCluster(Class<?> serviceInterface, ConnectDistinguish connectDistinguish, List<MethodInterceptor> methodInterceptorList, String type) {
		super(serviceInterface, connectDistinguish, methodInterceptorList, type);
	}

	@Override
	public Object doInvoke(Method method, Object[] args) throws Throwable {
		
		ConnectNode connectNode = getConnectNode(args);
		ConnectRoute connectRoute = getConnectRoute(args);
		
		if (connectNode.getHealthyConnectAgentList().isEmpty()) {
			getConnectManager().runUpdate();
			throw new ConnectNoAliveException("all connect agent is no alive");
		}
		
		Object result = null;
		Throwable resultThrowable = null;
		int successNum = 0;
		
		MessageDb messageDb = (MessageDb) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
		
		List<ConnectAgent> connectAgentListSelected = new LinkedList<ConnectAgent>();
		ConnectAgent connectAgent = null;		
		do {
			connectAgent = connectRoute.selectConnect(connectNode.getHealthyConnectAgentList(), connectAgentListSelected, connectDistinguish.getMethodKay(method, args));
			if (connectAgent != null) {
				connectAgentListSelected.add(connectAgent);
				try {
					result = AopUtils.invokeJoinpointUsingReflection(connectAgent.getProxy(), method, args);
				} catch (Throwable e) {
					logger.error("doInvoke -> method.invoke -> Exception:{}", e.getMessage());
					resultThrowable = e;
					continue;
				}
				successNum++;
				if (successNum == 1) {
					messageDb.setRedisOne(connectAgent.getConnectId());
				} else {
					messageDb.setRedisTwo(connectAgent.getConnectId());
				}
				
			} 
		} while (connectAgent != null && successNum < maxInvokeNum);
		
		if (successNum == 0) {
			throw resultThrowable;
		}
		
		return result;
	}

	public void setMaxInvokeNum(int maxInvokeNum) {
		this.maxInvokeNum = maxInvokeNum;
	}
}
