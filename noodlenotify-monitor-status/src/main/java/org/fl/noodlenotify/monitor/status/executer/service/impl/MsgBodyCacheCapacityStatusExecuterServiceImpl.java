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


@Service("msgBodyCacheCapacityStatusExecuterService")
public class MsgBodyCacheCapacityStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgBodyCacheCapacityStatusExecuterServiceImpl.class);
	
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	ConsoleBodyCacheConnectManager consoleBodyCacheConnectManager;

	@Override
	public void execute() throws Exception {
		
		MsgBodyCacheVo msgBodyCacheVoParam = new MsgBodyCacheVo();
		msgBodyCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgBodyCacheVo> msgBodyCaches = msgBodyCacheService.queryMsgBodyCacheList(msgBodyCacheVoParam);
		for (MsgBodyCacheVo msgBodyCache : msgBodyCaches) {
			BodyCacheStatusChecker queueCacheStatusChecker = (BodyCacheStatusChecker) consoleBodyCacheConnectManager.getConnectAgent(msgBodyCache.getMsgBodyCache_Id());
			if (queueCacheStatusChecker != null) {
				try {
					long size = queueCacheStatusChecker.checkSize();
					MsgBodyCacheVo currentmsgBodyCacheVo = new MsgBodyCacheVo();
					currentmsgBodyCacheVo.setMsgBodyCache_Id(msgBodyCache.getMsgBodyCache_Id());
					currentmsgBodyCacheVo.setSize(size);
					msgBodyCacheService.updatesMsgBodyCacheSize(currentmsgBodyCacheVo);
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckSize -> " + e);
					}
				}
			}
		}
	}

}
