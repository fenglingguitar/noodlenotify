package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.queue.manager.console.ConsoleQueueCacheConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("msgQueueCacheCapacityStatusExecuterService")
public class MsgQueueCacheCapacityStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgQueueCacheCapacityStatusExecuterServiceImpl.class);
	
	@Autowired
	private QueueMsgQueueCacheService queueMsgQueueCacheService;

	@Autowired
	ConsoleQueueCacheConnectManager consoleQueueCacheConnectManager;

	@Override
	public void execute() throws Exception {
		
		QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueMsgQueueCacheVo> queueMsgQueueCaches = queueMsgQueueCacheService.queryQueueMsgQueueCacheByQueue(queueMsgQueueCacheVo);
		for (QueueMsgQueueCacheVo queueMsgQueueCache : queueMsgQueueCaches) {
			QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) consoleQueueCacheConnectManager.getConnectAgent(queueMsgQueueCache.getMsgQueueCache_Id());
			if (queueCacheStatusChecker != null) {
				try {
					String queueNm = queueMsgQueueCache.getQueue_Nm();
					boolean isActive = queueCacheStatusChecker.checkIsActive(queueNm);
					if (isActive) {
						queueMsgQueueCache.setIs_Active(ConsoleConstants.IS_TRUE);
						queueMsgQueueCache.setNew_Len(queueCacheStatusChecker.checkNewLen(queueNm));
						queueMsgQueueCache.setPortion_Len(queueCacheStatusChecker.checkPortionLen(queueNm));
						queueMsgQueueCacheService.updateQueueMsgQueueCacheSimple(queueMsgQueueCache);
					} else if (queueMsgQueueCache.getIs_Active() == ConsoleConstants.IS_TRUE) {
						queueMsgQueueCache.setIs_Active(ConsoleConstants.IS_FALSE);
						queueMsgQueueCache.setNew_Len(0);
						queueMsgQueueCache.setPortion_Len(0);
						queueMsgQueueCacheService.updateQueueMsgQueueCacheSimple(queueMsgQueueCache);
					}
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckIsActive And CheckNewLen And CheckPortionLen -> " + e);
					}
				}
			}
		}
	}
}
