package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;
import java.util.Map;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("customerStatusExecuterService")
public class CustomerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(CustomerStatusExecuter.class);
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	private Map<String, ConnectAgentFactory> connectAgentFactoryMap;

	@Override
	public void execute() throws Exception {

		CustomerVo customerVoParam = new CustomerVo();
		customerVoParam.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<CustomerVo> customerVoList = customerService.queryCustomerList(customerVoParam);
		for (CustomerVo customerVo : customerVoList) {
			byte systemStatus = customerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(customerVo.getCheck_Type());
			if (connectAgentFactory != null) {
				ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(customerVo.getIp(), customerVo.getCheck_Port(), customerVo.getCustomer_Id());
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
					CustomerVo currentCustomerVo = new CustomerVo();
					currentCustomerVo.setCustomer_Id(customerVo.getCustomer_Id());
					currentCustomerVo.setSystem_Status(currentSysTemStatus);
					customerService.updateCustomerSystemStatus(currentCustomerVo);
				}
			}
		}
	}

	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}
}
