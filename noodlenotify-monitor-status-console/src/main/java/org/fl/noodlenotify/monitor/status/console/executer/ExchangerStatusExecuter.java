package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ExchangerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ExchangerStatusExecuter.class);
	
	@Autowired
	private ExchangerService exchangerService;

	@Autowired
	private Map<String, StatusCheckerFactory> statusCheckerFactoryMap;
	
	private long maxInterval = 10 * 1000;

	@Override
	public void execute() throws Exception {
		
		ExchangerVo exchangerVoParam = new ExchangerVo();
		exchangerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		
		List<ExchangerVo> exchangerVoToOnlineList = null;
		try {
			exchangerVoToOnlineList = exchangerService.queryExchangerToOnlineList(exchangerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", exchangerVoParam, e.getMessage());
			}
		}
		if (exchangerVoToOnlineList != null) {
			for (ExchangerVo exchangerVoToOnline : exchangerVoToOnlineList) {
				StatusCheckerFactory statusCheckerFactory = statusCheckerFactoryMap.get(exchangerVoToOnline.getType());
				if (statusCheckerFactory != null) {
					NetStatusChecker netStatusChecker = (NetStatusChecker) statusCheckerFactory.createStatusChecker(exchangerVoToOnline.getExchanger_Id(), exchangerVoToOnline.getIp(), exchangerVoToOnline.getPort(), exchangerVoToOnline.getUrl()).getProxy();
					try {
						netStatusChecker.checkHealth();
						ExchangerVo currentExchangerVo = new ExchangerVo();
						currentExchangerVo.setExchanger_Id(exchangerVoToOnline.getExchanger_Id());
						currentExchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
						exchangerService.updateExchangerSystemStatus(currentExchangerVo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		exchangerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		List<ExchangerVo> exchangerVoToOfflineList = null;
		try {
			exchangerVoToOfflineList = exchangerService.queryExchangerToOfflineList(exchangerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", exchangerVoParam, e.getMessage());
			}
		}
		if (exchangerVoToOfflineList != null) {
			for (ExchangerVo exchangerVoToOffline : exchangerVoToOfflineList) {
				StatusCheckerFactory statusCheckerFactory = statusCheckerFactoryMap.get(exchangerVoToOffline.getType());
				if (statusCheckerFactory != null) {
					NetStatusChecker netStatusChecker = (NetStatusChecker) statusCheckerFactory.createStatusChecker(exchangerVoToOffline.getExchanger_Id(), exchangerVoToOffline.getIp(), exchangerVoToOffline.getPort(), exchangerVoToOffline.getUrl());
					try {
						netStatusChecker.checkHealth();
					} catch (Exception e) {
						e.printStackTrace();
						ExchangerVo currentExchangerVo = new ExchangerVo();
						currentExchangerVo.setExchanger_Id(exchangerVoToOffline.getExchanger_Id());
						currentExchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
						exchangerService.updateExchangerSystemStatus(currentExchangerVo);
					}
				}
			}
		}
	}

	public void setStatusCheckerFactoryMap(Map<String, StatusCheckerFactory> statusCheckerFactoryMap) {
		this.statusCheckerFactoryMap = statusCheckerFactoryMap;
	}
}
