package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;
import java.util.Map;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExchangerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ExchangerStatusExecuter.class);
	
	@Autowired
	private ExchangerService exchangerService;

	@Autowired
	private Map<String, ConnectAgentFactory> connectAgentFactoryMap;

	@Override
	public void execute() throws Exception {
		
		ExchangerVo exchangerVoParam = new ExchangerVo();
		exchangerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ExchangerVo> exchangerVoList = exchangerService.queryExchangerList(exchangerVoParam);
		for (ExchangerVo exchangerVo : exchangerVoList) {
			byte systemStatus = exchangerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get("HTTP");
			if (connectAgentFactory != null) {
				ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(exchangerVo.getIp(), exchangerVo.getCheck_Port(), exchangerVo.getExchanger_Id());
				try {
					connectAgent.connect();
					((NetStatusChecker)connectAgent).checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckHealth -> " + e);
					}
				} finally {
					connectAgent.close();
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
	
	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}
}
