package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgBodyCacheCapacityStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(MsgBodyCacheCapacityStatusExecuter.class);
	
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	private ConnectAgentFactory redisBodyCacheConnectAgentFactory;

	@Override
	public void execute() throws Exception {
		
		MsgBodyCacheVo msgBodyCacheVoParam = new MsgBodyCacheVo();
		msgBodyCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgBodyCacheVo> msgBodyCacheVoList = msgBodyCacheService.queryMsgBodyCacheList(msgBodyCacheVoParam);
		for (MsgBodyCacheVo msgBodyCacheVo : msgBodyCacheVoList) {
			ConnectAgent connectAgent = redisBodyCacheConnectAgentFactory.createConnectAgent(msgBodyCacheVo.getIp(), msgBodyCacheVo.getPort(), msgBodyCacheVo.getMsgBodyCache_Id());
			try {
				connectAgent.connect();
				long size = ((BodyCacheStatusChecker)connectAgent).checkSize();
				MsgBodyCacheVo currentmsgBodyCacheVo = new MsgBodyCacheVo();
				currentmsgBodyCacheVo.setMsgBodyCache_Id(msgBodyCacheVo.getMsgBodyCache_Id());
				currentmsgBodyCacheVo.setSize(size);
				msgBodyCacheService.updatesMsgBodyCacheSize(currentmsgBodyCacheVo);
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.error("CheckSize -> " + e);
				}
			} finally {
				connectAgent.close();
			}
		}
	}

}
