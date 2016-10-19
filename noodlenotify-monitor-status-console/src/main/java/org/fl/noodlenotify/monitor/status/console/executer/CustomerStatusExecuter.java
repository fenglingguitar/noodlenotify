package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.Date;
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

public class CustomerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(CustomerStatusExecuter.class);
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	private Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	private long maxInterval = 10 * 1000;

	@Override
	public void execute() throws Exception {
		
		CustomerVo customerVoParam = new CustomerVo();
		customerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		
		List<CustomerVo> customerVoToOnlineList = null;
		try {
			customerVoToOnlineList = customerService.queryCustomerToOnlineList(customerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", customerVoParam, e.getMessage());
			}
		}
		if (customerVoToOnlineList != null) {
			for (CustomerVo CustomerVoToOnline : customerVoToOnlineList) {
				ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(CustomerVoToOnline.getCheck_Type());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(CustomerVoToOnline.getIp(), CustomerVoToOnline.getCheck_Port(), CustomerVoToOnline.getCustomer_Id());
					try {
						connectAgent.connect();
						((NetStatusChecker)connectAgent).checkHealth();
						CustomerVo currentCustomerVo = new CustomerVo();
						currentCustomerVo.setCustomer_Id(CustomerVoToOnline.getCustomer_Id());
						currentCustomerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
						customerService.updateCustomerSystemStatus(currentCustomerVo);
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
		
		customerVoParam.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		List<CustomerVo> customerVoToOfflineList = null;
		try {
			customerVoToOfflineList = customerService.queryCustomerToOfflineList(customerVoParam);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> serverService.queryServerOnlineLis -> {} -> Exception:{}", customerVoParam, e.getMessage());
			}
		}
		if (customerVoToOfflineList != null) {
			for (CustomerVo customerVoToOffline : customerVoToOfflineList) {
				ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(customerVoToOffline.getCheck_Type());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentFactory.createConnectAgent(customerVoToOffline.getIp(), customerVoToOffline.getCheck_Port(), customerVoToOffline.getCustomer_Id());
					try {
						connectAgent.connect();
						((NetStatusChecker)connectAgent).checkHealth();
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("CheckHealth -> " + e);
						}
						CustomerVo currentCustomerVo = new CustomerVo();
						currentCustomerVo.setCustomer_Id(customerVoToOffline.getCustomer_Id());
						currentCustomerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
						customerService.updateCustomerSystemStatus(currentCustomerVo);
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
