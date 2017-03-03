package org.fl.noodlenotify.core.distribute.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;

public class SetPopMethodInterceptor implements MethodInterceptor {

	private ConnectDistinguish connectDistinguish;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		ConnectManager queueCacheConnectManager = connectDistinguish.getConnectManager();
		
		Object object = null;
		
		try {
			object = invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			if (invocation.getMethod().getName().equals("pop") && object != null) {
				ConnectCluster otherConnectCluster = queueCacheConnectManager.getConnectCluster("OTHER");
				QueueCacheConnectAgent otherQueueCacheConnectAgent = (QueueCacheConnectAgent) otherConnectCluster.getProxy();
				try {
					otherQueueCacheConnectAgent.setPop((MessageDb)object);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return object;
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
