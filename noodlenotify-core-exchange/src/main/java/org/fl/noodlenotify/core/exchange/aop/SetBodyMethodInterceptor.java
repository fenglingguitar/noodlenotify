package org.fl.noodlenotify.core.exchange.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public class SetBodyMethodInterceptor implements MethodInterceptor {

	private ConnectDistinguish connectDistinguish;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		ConnectManager bodyCacheConnectManager = connectDistinguish.getConnectManager();
		
		Object object = null;
		
		try {
			if (invocation.getMethod().getName().equals("insert")) {
				try {
					ConnectCluster bodyCacheConnectCluster = bodyCacheConnectManager.getConnectCluster("DEFALT");		
					BodyCacheConnectAgent bodyCacheConnectAgent = (BodyCacheConnectAgent) bodyCacheConnectCluster.getProxy();
					MessageDm messageDm = (MessageDm) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
					bodyCacheConnectAgent.set(messageDm);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
				}
			}
			
			object = invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
		}
		
		return object;
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
