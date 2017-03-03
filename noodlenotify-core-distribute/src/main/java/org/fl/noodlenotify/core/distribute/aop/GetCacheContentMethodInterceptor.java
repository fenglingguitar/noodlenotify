package org.fl.noodlenotify.core.distribute.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;

public class GetCacheContentMethodInterceptor implements MethodInterceptor {

	private ConnectDistinguish connectDistinguish;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		ConnectManager bodyCacheConnectManager = connectDistinguish.getConnectManager();
		
		Object object = null;
		
		try {
			object = invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			if (invocation.getMethod().getName().equals("pop") && object != null) {
				
				MessageDb messageDb = (MessageDb)object;
				
				ConnectCluster bodyConnectCluster = bodyCacheConnectManager.getConnectCluster("EITHER");
				BodyCacheConnectAgent bodyCacheConnectAgentOne = (BodyCacheConnectAgent) bodyConnectCluster.getProxy();
				
				ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDb);
				try {
					bodyCacheConnectAgentOne.get(messageDb);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
				}
			}
		}
		
		return object;
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
