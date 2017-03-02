package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ConsumerService;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.status.StatusCheckerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConsumerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ConsumerStatusExecuter.class);
	
	@Autowired
	private ConsumerService consumerService;

	@Autowired
	private Map<String, StatusCheckerFactory> statusCheckerFactoryMap;
	
	private long maxInterval = 10 * 1000;

	@Override
	public void execute() throws Exception {
		
		ConsumerVo consumerVoParam = new ConsumerVo();
		consumerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		
		List<ConsumerVo> consumerVoToOnlineList = null;
		try {
			consumerVoToOnlineList = consumerService.queryConsumerToOnlineList(consumerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", consumerVoParam, e.getMessage());
			}
		}
		if (consumerVoToOnlineList != null) {
			for (ConsumerVo consumerVoToOnline : consumerVoToOnlineList) {
				StatusCheckerFactory statusCheckerFactory = statusCheckerFactoryMap.get(consumerVoToOnline.getType());
				if (statusCheckerFactory != null) {
					NetStatusChecker netStatusChecker = (NetStatusChecker) statusCheckerFactory.createStatusChecker(consumerVoToOnline.getConsumer_Id(), consumerVoToOnline.getIp(), consumerVoToOnline.getPort(), consumerVoToOnline.getUrl()).getProxy();
					try {
						netStatusChecker.checkHealth();
						ConsumerVo currentConsumerVo = new ConsumerVo();
						currentConsumerVo.setConsumer_Id(consumerVoToOnline.getConsumer_Id());
						currentConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
						consumerService.updateConsumerSystemStatus(currentConsumerVo);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		consumerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		List<ConsumerVo> consumerVoToOfflineList = null;
		try {
			consumerVoToOfflineList = consumerService.queryConsumerToOfflineList(consumerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", consumerVoParam, e.getMessage());
			}
		}
		if (consumerVoToOfflineList != null) {
			for (ConsumerVo consumerVoToOffline : consumerVoToOfflineList) {
				StatusCheckerFactory statusCheckerFactory = statusCheckerFactoryMap.get(consumerVoToOffline.getType());
				if (statusCheckerFactory != null) {
					NetStatusChecker netStatusChecker = (NetStatusChecker) statusCheckerFactory.createStatusChecker(consumerVoToOffline.getConsumer_Id(), consumerVoToOffline.getIp(), consumerVoToOffline.getPort(), consumerVoToOffline.getUrl()).getProxy();
					try {
						netStatusChecker.checkHealth();
					} catch (Exception e) {
						e.printStackTrace();
						ConsumerVo currentConsumerVo = new ConsumerVo();
						currentConsumerVo.setConsumer_Id(consumerVoToOffline.getConsumer_Id());
						currentConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
						consumerService.updateConsumerSystemStatus(currentConsumerVo);
					}
				}
			}
		}
	}

	public void setStatusCheckerFactoryMap(Map<String, StatusCheckerFactory> statusCheckerFactoryMap) {
		this.statusCheckerFactoryMap = statusCheckerFactoryMap;
	}
}
