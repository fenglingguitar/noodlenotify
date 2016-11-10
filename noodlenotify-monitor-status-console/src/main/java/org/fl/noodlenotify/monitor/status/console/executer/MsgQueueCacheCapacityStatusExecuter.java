package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgQueueCacheCapacityStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(MsgQueueCacheCapacityStatusExecuter.class);
	
	@Autowired
	private QueueMsgQueueCacheService queueMsgQueueCacheService;

	@Autowired
	private ConnectAgentFactory redisQueueCacheConnectAgentFactory;

	@Override
	public void execute() throws Exception {
		
		QueueMsgQueueCacheVo queueMsgQueueCacheVoParam = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueMsgQueueCacheVo> queueMsgQueueCacheList = queueMsgQueueCacheService.queryQueueMsgQueueCacheByQueue(queueMsgQueueCacheVoParam);
		for (QueueMsgQueueCacheVo queueMsgQueueCacheVo : queueMsgQueueCacheList) {
			ConnectAgent connectAgent = redisQueueCacheConnectAgentFactory.createConnectAgent(queueMsgQueueCacheVo.getMsgQueueCache_Id(), queueMsgQueueCacheVo.getIp(), queueMsgQueueCacheVo.getPort(), null);
			try {
				connectAgent.connect();
				boolean isActive = ((QueueCacheStatusChecker)connectAgent).checkIsActive(queueMsgQueueCacheVo.getQueue_Nm());
				if (isActive) {
					queueMsgQueueCacheVo.setIs_Active(ConsoleConstants.IS_TRUE);
					queueMsgQueueCacheVo.setNew_Len(((QueueCacheStatusChecker)connectAgent).checkNewLen(queueMsgQueueCacheVo.getQueue_Nm()));
					queueMsgQueueCacheVo.setPortion_Len(((QueueCacheStatusChecker)connectAgent).checkPortionLen(queueMsgQueueCacheVo.getQueue_Nm()));
					queueMsgQueueCacheService.updateQueueMsgQueueCacheSimple(queueMsgQueueCacheVo);
				} else if (queueMsgQueueCacheVo.getIs_Active() == ConsoleConstants.IS_TRUE) {
					queueMsgQueueCacheVo.setIs_Active(ConsoleConstants.IS_FALSE);
					queueMsgQueueCacheVo.setNew_Len(0L);
					queueMsgQueueCacheVo.setPortion_Len(0L);
					queueMsgQueueCacheService.updateQueueMsgQueueCacheSimple(queueMsgQueueCacheVo);
				}
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.error("CheckIsActive And CheckNewLen And CheckPortionLen -> " + e);
				}
			} finally {
				connectAgent.close();
			}
		}
	}
}
