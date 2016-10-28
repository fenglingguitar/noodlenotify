package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.ConsumerService;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ConsumerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ConsumerStatusExecuter.class);
	
	@Autowired
	private ConsumerService consumerService;

	@Autowired
	private Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
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
			for (ConsumerVo ConsumerVoToOnline : consumerVoToOnlineList) {
				ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(ConsumerVoToOnline.getCheck_Type());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(ConsumerVoToOnline.getConsumer_Id(), ConsumerVoToOnline.getIp(), ConsumerVoToOnline.getCheck_Port(), ConsumerVoToOnline.getUrl());
					try {
						connectAgent.connect();
						((NetStatusChecker)connectAgent).checkHealth();
						ConsumerVo currentConsumerVo = new ConsumerVo();
						currentConsumerVo.setConsumer_Id(ConsumerVoToOnline.getConsumer_Id());
						currentConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
						consumerService.updateConsumerSystemStatus(currentConsumerVo);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("CheckHealth -> " + e);
						}
					} finally {
						connectAgent.close();
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
				ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(consumerVoToOffline.getCheck_Type());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(consumerVoToOffline.getConsumer_Id(), consumerVoToOffline.getIp(), consumerVoToOffline.getCheck_Port(), consumerVoToOffline.getCheck_Url());
					try {
						connectAgent.connect();
						((NetStatusChecker)connectAgent).checkHealth();
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("CheckHealth -> " + e);
						}
						ConsumerVo currentConsumerVo = new ConsumerVo();
						currentConsumerVo.setConsumer_Id(consumerVoToOffline.getConsumer_Id());
						currentConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
						consumerService.updateConsumerSystemStatus(currentConsumerVo);
					} finally {
						connectAgent.close();
					}
				}
			}
		}
	}

	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}
}
