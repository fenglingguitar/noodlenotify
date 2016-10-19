package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MsgStorageStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(MsgStorageStatusExecuter.class);
	
	@Autowired
	private MsgStorageService msgStorageService;
	
	@Autowired
	private ConnectAgentFactory dbConnectAgentFactory;

	@Override
	public void execute() throws Exception {
		
		MsgStorageVo msgStorageVoParam = new MsgStorageVo();
		msgStorageVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgStorageVo> msgStorageVoList = msgStorageService.queryMsgStorageList(msgStorageVoParam);
		for (MsgStorageVo msgStorageVo : msgStorageVoList) {
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			ConnectAgent connectAgent = dbConnectAgentFactory.createConnectAgent(msgStorageVo.getIp(), msgStorageVo.getPort(), msgStorageVo.getMsgStorage_Id());
			try {
				connectAgent.connect();
				((DbStatusChecker) connectAgent).checkHealth();
				currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.error("execute -> " + e);
				}
			} finally {
				connectAgent.close();
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
