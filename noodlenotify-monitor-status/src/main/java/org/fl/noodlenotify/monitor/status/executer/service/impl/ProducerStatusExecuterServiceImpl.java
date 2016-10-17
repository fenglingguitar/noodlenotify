package org.fl.noodlenotify.monitor.status.executer.service.impl;

import org.fl.noodlenotify.console.service.ProducerService;
//import org.fl.noodlenotify.core.connect.net.manager.console.ProducerConsoleNetConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;

//@Service("producerStatusExecuter")
public class ProducerStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	//private final static Logger logger = LoggerFactory.getLogger(ProducerStatusExecuterServiceImpl.class);
	
	@Autowired
	private ProducerService producerService;

	@Autowired
	//ProducerConsoleNetConnectManager producerConsoleNetConnectManager;

	@Override
	public void execute() throws Exception {
		
		/*ProducerVo producerVoParam = new ProducerVo();
		producerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ProducerVo> producerVoList = producerService.queryProducerList(producerVoParam);
		for (ProducerVo producerVo : producerVoList) {
			byte systemStatus = producerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			NetStatusChecker netStatusChecker = (NetStatusChecker) producerConsoleNetConnectManager.getConnectAgent(producerVo.getProducer_Id());
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
				ProducerVo currentProducer = new ProducerVo();
				currentProducer.setProducer_Id(producerVo.getProducer_Id());
				currentProducer.setSystem_Status(currentSysTemStatus);
				producerService.updateProducerSystemStatus(currentProducer);
			}
		}*/
	}
}
