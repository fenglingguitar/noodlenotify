package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgStorageStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(MsgStorageStatusExecuter.class);
	
	@Autowired
	private MsgStorageService msgStorageService;
	
	@Autowired
	private StatusCheckerFactory mysqlDbStatusCheckerFactory;

	@Override
	public void execute() throws Exception {
		
		MsgStorageVo msgStorageVoParam = new MsgStorageVo();
		msgStorageVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgStorageVo> msgStorageVoList = msgStorageService.queryMsgStorageList(msgStorageVoParam);
		for (MsgStorageVo msgStorageVo : msgStorageVoList) {
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(msgStorageVo.getMsgStorage_Id(), msgStorageVo.getIp(), msgStorageVo.getPort(), null).getProxy();
			try {
				dbStatusChecker.checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (currentSysTemStatus != msgStorageVo.getSystem_Status()) {
				MsgStorageVo currentmsgStorageVo = new MsgStorageVo();
				currentmsgStorageVo.setMsgStorage_Id(msgStorageVo.getMsgStorage_Id());
				currentmsgStorageVo.setSystem_Status(currentSysTemStatus);
				msgStorageService.updatesMsgStorageSystemStatus(currentmsgStorageVo);
			}
		}
	}
}
