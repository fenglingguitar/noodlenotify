package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("msgQueueCacheStatusExecuter")
public class MsgQueueCacheStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgQueueCacheStatusExecuterServiceImpl.class);
	
	@Autowired
	private MsgQueueCacheService msgQueueCacheService;

	@Autowired
	private ConnectAgentFactory redisQueueCacheConnectAgentFactory;

	@Override
	public void execute() throws Exception {
		
		MsgQueueCacheVo msgQueueCacheVoParam = new MsgQueueCacheVo();
		msgQueueCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgQueueCacheVo> msgQueueCacheVoList = msgQueueCacheService.queryMsgQueueCacheList(msgQueueCacheVoParam);
		for (MsgQueueCacheVo msgQueueCacheVo : msgQueueCacheVoList) {
			byte systemStatus = msgQueueCacheVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			ConnectAgent connectAgent = redisQueueCacheConnectAgentFactory.createConnectAgent(msgQueueCacheVo.getIp(), msgQueueCacheVo.getPort(), msgQueueCacheVo.getMsgQueueCache_Id());
			try {
				connectAgent.connect();
				((QueueCacheStatusChecker)connectAgent).checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.error("CheckHealth -> " + e);
				}
			} finally {
				connectAgent.close();
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
