package org.fl.noodlenotify.core.distribute.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;

public class GetDbContentMethodInterceptor implements MethodInterceptor {

	private ConnectDistinguish connectDistinguish;
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		ConnectManager dbConnectManager = connectDistinguish.getConnectManager();
		
		Object object = null;
		
		try {
			object = invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			if (invocation.getMethod().getName().equals("pop") && object != null) {
				
				MessageDb messageDb = (MessageDb)object;
				
				if (messageDb.getContent() == null) {
					ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
					DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
					
					ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), messageDb.getDb());
					try {
						MessageDb messageDbTemp = dbConnectAgent.selectById(messageDb.getQueueName(), messageDb.getContentId());
						if (messageDbTemp == null) {
							throw new Exception("can not get the message content");
						}
						messageDb.setContent(messageDbTemp.getContent());
					} catch (Exception e) {
						e.printStackTrace();
						messageDb.setResult(false);
						messageDb.executeMessageCallback();
						throw e;
					} finally {
						ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
					}
				}
			}
		}
		
		return object;
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
