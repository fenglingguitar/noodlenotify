package org.fl.noodlenotify.core.distribute.aop;

import java.util.concurrent.atomic.AtomicInteger;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.net.aop.LocalStorageType;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushLayerResultMethodInterceptor implements MethodInterceptor {
	
	private ConnectDistinguish connectDistinguish;
	
	private final static Logger logger = LoggerFactory.getLogger(PushLayerResultMethodInterceptor.class);
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		ConnectManager connectManager = connectDistinguish.getConnectManager();
		if (connectManager == null) {
			logger.error("invoke -> connectDistinguish.getConnectManager return null");
			throw new ConnectInvokeException("no this connect manager");
		}
		
		MessageDm messageDm = (MessageDm) ConnectThreadLocalStorage.get(LocalStorageType.MESSAGE_DM.getCode());
		QueueDistributerVo queueDistributerVo = (QueueDistributerVo) ConnectThreadLocalStorage.get(LocalStorageType.QUEUE_DISTRIBUTER_VO.getCode());
		
		DbConnectAgent dbConnectAgent = (DbConnectAgent)((ConnectAgent)connectManager.getConnectAgent(messageDm.getDb())).getProxy();
		if (dbConnectAgent == null) {
			if (logger.isErrorEnabled()) {
				logger.error("invoke -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", DB: " + messageDm.getDb()
						+ ", Get Db ConnectAgent -> Null");
			}
			throw new ConnectInvokeException("get db connect agent return null");
		}
		
		try {
			return invocation.proceed();
		} catch (Throwable e) {
			throw e;
		} finally {
			if (messageDm.getResultQueue() == messageDm.getExecuteQueue()) {
				messageDm.setStatus(MessageConstant.MESSAGE_STATUS_FINISH);
			} else {
				if (queueDistributerVo.getIs_Repeat() == MessageConstant.MESSAGE_IS_REPEAT_No) {
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
			((AtomicInteger) messageDm.getObjectFive()).incrementAndGet();
			try {
				dbConnectAgent.update(messageDm);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("invoke ->"
							+ "UUID: " + messageDm.getUuid()
							+ ", DB: " + messageDm.getDb()
							+ ", Execute Update -> " + e);
				}
				((AtomicInteger) messageDm.getObjectFive()).decrementAndGet();
			}
		}
	}
	
	public void setConnectDistinguish(ConnectDistinguish connectDistinguish) {
		this.connectDistinguish = connectDistinguish;
	}
}
