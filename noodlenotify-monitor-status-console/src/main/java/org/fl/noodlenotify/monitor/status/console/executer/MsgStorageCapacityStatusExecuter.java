package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("msgStorageCapacityStatusExecuter")
public class MsgStorageCapacityStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(MsgStorageCapacityStatusExecuter.class);
	
	@Autowired
	private QueueMsgStorageService queueMsgStorageService;

	@Autowired
	private ConnectAgentFactory dbConnectAgentFactory;

	@Override
	public void execute() throws Exception {
		
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueMsgStorageVo> queueMsgStorages = queueMsgStorageService.queryQueueMsgStorageIncludeList(queueMsgStorageVo);
		for (QueueMsgStorageVo queueMsgStorage : queueMsgStorages) {
			ConnectAgent connectAgent = dbConnectAgentFactory.createConnectAgent(queueMsgStorage.getIp(), queueMsgStorage.getPort(), queueMsgStorage.getMsgStorage_Id());
			try {
				connectAgent.connect();
				long newLen = ((DbStatusChecker) connectAgent).checkNewLen(queueMsgStorage.getQueue_Nm());
				long portionLen = ((DbStatusChecker) connectAgent).checkPortionLen(queueMsgStorage.getQueue_Nm());
				queueMsgStorage.setNew_Len(newLen);
				queueMsgStorage.setPortion_Len(portionLen);
				queueMsgStorageService.updateQueueMsgStorageSimple(queueMsgStorage);
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.error("execute -> " + e);
				}
			} finally {
				connectAgent.close();
			}
		}
	}
}
