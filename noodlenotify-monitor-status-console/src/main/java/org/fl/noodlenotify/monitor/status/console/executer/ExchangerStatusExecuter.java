package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.List;
import java.util.Map;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExchangerStatusExecuter extends AbstractExecuter {

	//private final static Logger logger = LoggerFactory.getLogger(ExchangerStatusExecuter.class);
	
	@Autowired
	private ExchangerService exchangerService;

	@Autowired
	private Map<String, StatusCheckerFactory> statusCheckerFactoryMap;

	@Override
	public void execute() throws Exception {
		
		ExchangerVo exchangerVoParam = new ExchangerVo();
		exchangerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ExchangerVo> exchangerVoList = exchangerService.queryExchangerList(exchangerVoParam);
		for (ExchangerVo exchangerVo : exchangerVoList) {
			byte systemStatus = exchangerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			StatusCheckerFactory statusCheckerFactory = statusCheckerFactoryMap.get("HTTP");
			if (statusCheckerFactory != null) {
				NetStatusChecker netStatusChecker = (NetStatusChecker) statusCheckerFactory.createStatusChecker(exchangerVo.getExchanger_Id(), exchangerVo.getIp(), exchangerVo.getCheck_Port(), "/noodlenotify").getProxy();
				try {
					netStatusChecker.checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					e.printStackTrace();
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
	
	public void setStatusCheckerFactoryMap(Map<String, StatusCheckerFactory> statusCheckerFactoryMap) {
		this.statusCheckerFactoryMap = statusCheckerFactoryMap;
	}
}
