package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.DbService;
import org.fl.noodlenotify.console.vo.DbVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DbStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(DbStatusExecuter.class);
	
	@Autowired
	private DbService dbService;
	
	@Autowired
	private StatusCheckerFactory mysqlDbStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		DbVo dbVoParam = new DbVo();
		dbVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DbVo> dbVoList = dbService.queryDbList(dbVoParam);
		for (DbVo dbVo : dbVoList) {
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(dbVo.getDb_Id(), dbVo.getIp(), dbVo.getPort(), null).getProxy();
			try {
				dbStatusChecker.checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (currentSysTemStatus != dbVo.getSystem_Status()) {
				DbVo currentdbVo = new DbVo();
				currentdbVo.setDb_Id(dbVo.getDb_Id());
				currentdbVo.setSystem_Status(currentSysTemStatus);
				dbService.updatesDbSystemStatus(currentdbVo);
			}
		}
	}
}
