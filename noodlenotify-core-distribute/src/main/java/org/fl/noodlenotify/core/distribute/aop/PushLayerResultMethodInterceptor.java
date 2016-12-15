package org.fl.noodlenotify.core.distribute.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public class PushLayerResultMethodInterceptor implements MethodInterceptor {
	
	private ConnectDistinguish connectDistinguish;
	
	//private final static Logger logger = LoggerFactory.getLogger(PushLayerResultMethodInterceptor.class);
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			MessageDm messageDm = (MessageDm) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
			QueueDistributerVo queueDistributerVo = (QueueDistributerVo) ConnectThreadLocalStorage.get(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode());
			
			if (messageDm.getResultQueue() == messageDm.getExecuteQueue()) {
				messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
			} else {
				if (queueDistributerVo.getIs_Repeat() == MessageConstant.MESSAGE_IS_REPEAT_NO) {
					messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
				} else {
					if (queueDistributerVo.getExpire_Time() > 0 
							&& System.currentTimeMillis() - messageDm.getBeginTime() > queueDistributerVo.getExpire_Time()) {
						messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
					} else {
						messageDm.setStatus(MessageConstant.MESSAGE_STATUS_PORTION);
						messageDm.setDelayTime(queueDistributerVo.getInterval_Time());
					}
				}
			}
			messageDm.setFinishTime(System.currentTimeMillis());
			
			ConnectManager dbConnectManager = connectDistinguish.getConnectManager();
			ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
			DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
			ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), messageDm.getDb());
			try {
				dbConnectAgent.update(messageDm);
			} catch (Exception e) {
				throw e;
			} finally {
				ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
			}
		}
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
