package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.DistributerService;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.manager.console.DistributeConsoleNetConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("distributerStatusExecuterService")
public class DistributerStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(DistributerStatusExecuterServiceImpl.class);
	
	@Autowired
	private DistributerService distributerService;

	@Autowired
	DistributeConsoleNetConnectManager distributerConsoleNetConnectManager;

	@Override
	public void execute() throws Exception {

		DistributerVo distributerVoParam = new DistributerVo();
		distributerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoParam);
		for (DistributerVo distributerVo : distributerVoList) {
			byte systemStatus = distributerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			NetStatusChecker netStatusChecker = (NetStatusChecker) distributerConsoleNetConnectManager.getConnectAgent(distributerVo.getDistributer_Id());
			if (netStatusChecker != null) {
				try {
					netStatusChecker.checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckHealth -> " + e);
					}
				}
			}
			if (systemStatus != currentSysTemStatus) {
				DistributerVo currentDistributerVo = new DistributerVo();
				currentDistributerVo.setDistributer_Id(distributerVo.getDistributer_Id());
				currentDistributerVo.setSystem_Status(currentSysTemStatus);
				distributerService.updateDistributerSystemStatus(currentDistributerVo);
			}
		}

	}
}
