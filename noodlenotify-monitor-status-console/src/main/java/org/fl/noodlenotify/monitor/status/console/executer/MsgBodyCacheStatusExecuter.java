package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgBodyCacheStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(MsgBodyCacheStatusExecuter.class);
	
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	private StatusCheckerFactory redisBodyCacheStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		MsgBodyCacheVo msgBodyCacheVoParam = new MsgBodyCacheVo();
		msgBodyCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgBodyCacheVo> msgBodyCacheVoList = msgBodyCacheService.queryMsgBodyCacheList(msgBodyCacheVoParam);
		for (MsgBodyCacheVo msgBodyCacheVo : msgBodyCacheVoList) {
			byte systemStatus = msgBodyCacheVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			BodyCacheStatusChecker bodyCacheStatusChecker = (BodyCacheStatusChecker) redisBodyCacheStatusCheckerFactory.createStatusChecker(msgBodyCacheVo.getMsgBodyCache_Id(), msgBodyCacheVo.getIp(), msgBodyCacheVo.getPort(), null).getProxy();
			try {
				bodyCacheStatusChecker.checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				e.printStackTrace();
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
