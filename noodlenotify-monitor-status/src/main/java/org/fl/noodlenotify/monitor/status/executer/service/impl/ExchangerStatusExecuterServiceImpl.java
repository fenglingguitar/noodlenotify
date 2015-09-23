package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.manager.console.ExchangeConsoleNetConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("exchangerStatusExecuterService")
public class ExchangerStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(ExchangerStatusExecuterServiceImpl.class);
	
	@Autowired
	private ExchangerService exchangerService;

	@Autowired
	ExchangeConsoleNetConnectManager exchangerConsoleNetConnectManager;

	@Override
	public void execute() throws Exception {
		
		List<ExchangerVo> exchangerVoList = exchangerService.queryCheckExchangerList();
		for (ExchangerVo exchangerVo : exchangerVoList) {
			byte systemStatus = exchangerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			NetStatusChecker netStatusChecker = (NetStatusChecker) exchangerConsoleNetConnectManager.getConnectAgent(exchangerVo.getExchanger_Id());
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
				ExchangerVo currentExchangerVo = new ExchangerVo();
				currentExchangerVo.setExchanger_Id(exchangerVo.getExchanger_Id());
				currentExchangerVo.setSystem_Status(currentSysTemStatus);
				exchangerService.updateExchangerSystemStatus(currentExchangerVo);
			}
		}
	}
}
