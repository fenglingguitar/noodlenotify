package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.body.manager.console.ConsoleBodyCacheConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("msgBodyCacheStatusExecuterService")
public class MsgBodyCacheStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgBodyCacheStatusExecuterServiceImpl.class);
	
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	ConsoleBodyCacheConnectManager consoleBodyCacheConnectManager;

	@Override
	public void execute() throws Exception {
		
		List<MsgBodyCacheVo> msgBodyCacheVoList = msgBodyCacheService.queryCheckMsgBodyCacheList();
		for (MsgBodyCacheVo msgBodyCacheVo : msgBodyCacheVoList) {
			byte systemStatus = msgBodyCacheVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			BodyCacheStatusChecker queueCacheStatusChecker = (BodyCacheStatusChecker) consoleBodyCacheConnectManager.getConnectAgent(msgBodyCacheVo.getMsgBodyCache_Id());
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
				MsgBodyCacheVo currentmsgBodyCacheVo = new MsgBodyCacheVo();
				currentmsgBodyCacheVo.setMsgBodyCache_Id(msgBodyCacheVo.getMsgBodyCache_Id());
				currentmsgBodyCacheVo.setSystem_Status(currentSysTemStatus);
				msgBodyCacheService.updatesMsgBodyCacheSystemStatus(currentmsgBodyCacheVo);
			}
		}
	}
}
