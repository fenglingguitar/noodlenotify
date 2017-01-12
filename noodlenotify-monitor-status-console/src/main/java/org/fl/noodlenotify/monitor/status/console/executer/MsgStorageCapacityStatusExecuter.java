package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgStorageCapacityStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(MsgStorageCapacityStatusExecuter.class);
	
	@Autowired
	private QueueMsgStorageService queueMsgStorageService;

	@Autowired
	private StatusCheckerFactory mysqlDbStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueMsgStorageVo> queueMsgStorages = queueMsgStorageService.queryQueueMsgStorageIncludeList(queueMsgStorageVo);
		for (QueueMsgStorageVo queueMsgStorageVoIt : queueMsgStorages) {
			DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(queueMsgStorageVoIt.getMsgStorage_Id(), queueMsgStorageVoIt.getIp(), queueMsgStorageVoIt.getPort(), null).getProxy();
			try {
				queueMsgStorageVoIt.setNew_Len(dbStatusChecker.checkNewLen(queueMsgStorageVoIt.getQueue_Nm()));
				queueMsgStorageVoIt.setPortion_Len(dbStatusChecker.checkPortionLen(queueMsgStorageVoIt.getQueue_Nm()));
				queueMsgStorageService.updateQueueMsgStorageSimple(queueMsgStorageVoIt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
