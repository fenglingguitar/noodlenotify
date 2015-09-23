package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.queue.manager.console.ConsoleQueueCacheConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("msgQueueCacheStatusExecuterService")
public class MsgQueueCacheStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgQueueCacheStatusExecuterServiceImpl.class);
	
	@Autowired
	private MsgQueueCacheService msgQueueCacheService;

	@Autowired
	ConsoleQueueCacheConnectManager consoleQueueCacheConnectManager;

	@Override
	public void execute() throws Exception {
		
		List<MsgQueueCacheVo> msgQueueCacheVoList = msgQueueCacheService.queryCheckMsgQueueCacheList();
		for (MsgQueueCacheVo msgQueueCacheVo : msgQueueCacheVoList) {
			byte systemStatus = msgQueueCacheVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) consoleQueueCacheConnectManager.getConnectAgent(msgQueueCacheVo.getMsgQueueCache_Id());
			if (queueCacheStatusChecker != null) {
				try {
					queueCacheStatusChecker.checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckHealth -> " + e);
					}
				}
			}
			if (systemStatus != currentSysTemStatus) {
				MsgQueueCacheVo currentmsgQueueCacheVo = new MsgQueueCacheVo();
				currentmsgQueueCacheVo.setMsgQueueCache_Id(msgQueueCacheVo.getMsgQueueCache_Id());
				currentmsgQueueCacheVo.setSystem_Status(currentSysTemStatus);
				msgQueueCacheService.updatesMsgQueueCacheSystemStatus(currentmsgQueueCacheVo);
			}
		}
	}
}
