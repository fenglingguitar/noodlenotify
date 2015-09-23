package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.connect.db.manager.console.ConsoleDbConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("msgStorageCapacityStatusExecuterService")
public class MsgStorageCapacityStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(MsgStorageCapacityStatusExecuterServiceImpl.class);
	
	@Autowired
	private QueueMsgStorageService queueMsgStorageService;

	@Autowired
	ConsoleDbConnectManager consoleDbConnectManager;

	@Override
	public void execute() throws Exception {
		
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
		List<QueueMsgStorageVo> queueMsgStorages = queueMsgStorageService.queryMsgStoragesByQueueExclude(queueMsgStorageVo);
		for (QueueMsgStorageVo queueMsgStorage : queueMsgStorages) {
			DbStatusChecker dbStatusChecker = (DbStatusChecker) consoleDbConnectManager.getConnectAgent(queueMsgStorage.getMsgStorage_Id());
			if (dbStatusChecker != null) {
				String queueNm = queueMsgStorage.getQueue_Nm();
				try {
					long newLen = dbStatusChecker.checkNewLen(queueNm);
					long portionLen = dbStatusChecker.checkPortionLen(queueNm);
					queueMsgStorage.setNew_Len(newLen);
					queueMsgStorage.setPortion_Len(portionLen);
					queueMsgStorageService.updateQueueMsgStorageSimple(queueMsgStorage);
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckNewLen And CheckPortionLen -> " + e);
					}
				}
			}
		}
	}
}
