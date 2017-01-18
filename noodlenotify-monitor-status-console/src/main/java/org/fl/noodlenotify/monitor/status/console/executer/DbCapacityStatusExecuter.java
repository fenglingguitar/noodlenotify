package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueDbService;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DbCapacityStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(DbCapacityStatusExecuter.class);
	
	@Autowired
	private QueueDbService queueDbService;

	@Autowired
	private StatusCheckerFactory mysqlDbStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		QueueDbVo queueDbVo = new QueueDbVo();
		queueDbVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueDbVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDbVo> queueDb = queueDbService.queryQueueDbIncludeList(queueDbVo);
		for (QueueDbVo queueDbVoIt : queueDb) {
			DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(queueDbVoIt.getDb_Id(), queueDbVoIt.getIp(), queueDbVoIt.getPort(), null).getProxy();
			try {
				queueDbVoIt.setNew_Len(dbStatusChecker.checkNewLen(queueDbVoIt.getQueue_Nm()));
				queueDbVoIt.setPortion_Len(dbStatusChecker.checkPortionLen(queueDbVoIt.getQueue_Nm()));
				queueDbService.updateQueueDbSimple(queueDbVoIt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
