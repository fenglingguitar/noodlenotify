package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgBodyCacheCapacityStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(MsgBodyCacheCapacityStatusExecuter.class);
	
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
			BodyCacheStatusChecker bodyCacheStatusChecker = (BodyCacheStatusChecker) redisBodyCacheStatusCheckerFactory.createStatusChecker(msgBodyCacheVo.getMsgBodyCache_Id(), msgBodyCacheVo.getIp(), msgBodyCacheVo.getPort(), null).getProxy();
			try {
				MsgBodyCacheVo currentmsgBodyCacheVo = new MsgBodyCacheVo();
				currentmsgBodyCacheVo.setMsgBodyCache_Id(msgBodyCacheVo.getMsgBodyCache_Id());
				currentmsgBodyCacheVo.setSize(bodyCacheStatusChecker.checkSize());
				msgBodyCacheService.updatesMsgBodyCacheSize(currentmsgBodyCacheVo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
