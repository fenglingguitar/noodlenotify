package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.connect.db.manager.console.ConsoleDbConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("msgStorageStatusExecuterService")
public class MsgStorageStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgStorageStatusExecuterServiceImpl.class);
	
	@Autowired
	private MsgStorageService msgStorageService;

	@Autowired
	ConsoleDbConnectManager consoleDbConnectManager;

	@Override
	public void execute() throws Exception {
		
		List<MsgStorageVo> msgStorageVoList = msgStorageService.queryCheckMsgStorageList();
		for (MsgStorageVo msgStorageVo : msgStorageVoList) {
			byte systemStatus = msgStorageVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			DbStatusChecker dbStatusChecker = (DbStatusChecker) consoleDbConnectManager.getConnectAgent(msgStorageVo.getMsgStorage_Id());
			if (dbStatusChecker != null) {
				try {
					dbStatusChecker.checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckHealth -> " + e);
					}
				}
			}
			if (systemStatus != currentSysTemStatus) {
				MsgStorageVo currentmsgStorageVo = new MsgStorageVo();
				currentmsgStorageVo.setMsgStorage_Id(msgStorageVo.getMsgStorage_Id());
				currentmsgStorageVo.setSystem_Status(currentSysTemStatus);
				msgStorageService.updatesMsgStorageSystemStatus(currentmsgStorageVo);
			}
		}
	}
}
