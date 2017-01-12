package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgQueueCacheStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(MsgQueueCacheStatusExecuter.class);
	
	@Autowired
	private MsgQueueCacheService msgQueueCacheService;

	@Autowired
	private StatusCheckerFactory redisQueueCacheStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		MsgQueueCacheVo msgQueueCacheVoParam = new MsgQueueCacheVo();
		msgQueueCacheVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgQueueCacheVo> msgQueueCacheVoList = msgQueueCacheService.queryMsgQueueCacheList(msgQueueCacheVoParam);
		for (MsgQueueCacheVo msgQueueCacheVo : msgQueueCacheVoList) {
			byte systemStatus = msgQueueCacheVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			QueueCacheStatusChecker queueCacheStatusChecker = (QueueCacheStatusChecker) redisQueueCacheStatusCheckerFactory.createStatusChecker(msgQueueCacheVo.getMsgQueueCache_Id(), msgQueueCacheVo.getIp(), msgQueueCacheVo.getPort(), null).getProxy();
			try {
				queueCacheStatusChecker.checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				e.printStackTrace();
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
