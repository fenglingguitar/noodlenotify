package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.DistributerService;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DistributerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(DistributerStatusExecuter.class);
	
	@Autowired
	private DistributerService distributerService;

	@Autowired
	private Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	@Override
	public void execute() throws Exception {

		DistributerVo distributerVoParam = new DistributerVo();
		distributerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoParam);
		for (DistributerVo distributerVo : distributerVoList) {
			byte systemStatus = distributerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get("HTTP");
			if (connectAgentFactory != null) {
				ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(distributerVo.getDistributer_Id(), distributerVo.getIp(), distributerVo.getCheck_Port(), "/noodlenotify");
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
					DistributerVo currentDistributerVo = new DistributerVo();
					currentDistributerVo.setDistributer_Id(distributerVo.getDistributer_Id());
					currentDistributerVo.setSystem_Status(currentSysTemStatus);
					distributerService.updateDistributerSystemStatus(currentDistributerVo);
				}
			}
		}
	}
	
	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}
}
