package org.fl.noodlenotify.core.distribute.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.core.distribute.callback.CheckResultMessageCallback;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public class SetCallbackMethodInterceptor implements MethodInterceptor {

	private ConnectDistinguish queueCacheConnectDistinguish;
	private ConnectDistinguish bodyCacheConnectDistinguish;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		ConnectManager queueCacheConnectManager = queueCacheConnectDistinguish.getConnectManager();
		ConnectManager bodyCacheConnectManager = bodyCacheConnectDistinguish.getConnectManager();
		
		Object object = null;
		
		try {
			object = invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			if (invocation.getMethod().getName().equals("pop") && object != null) {
				MessageDm messageDm = (MessageDm)object;
				messageDm.addMessageCallback(new CheckResultMessageCallback(messageDm, queueCacheConnectManager, bodyCacheConnectManager));
			}
		}
		
		return object;
	}

	public void setQueueCacheConnectDistinguish(ConnectDistinguish queueCacheConnectDistinguish) {
		this.queueCacheConnectDistinguish = queueCacheConnectDistinguish;
	}

	public void setBodyCacheConnectDistinguish(ConnectDistinguish bodyCacheConnectDistinguish) {
		this.bodyCacheConnectDistinguish = bodyCacheConnectDistinguish;
	}
}
