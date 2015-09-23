package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.manager.console.ConsumerConsoleNetConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("customerStatusExecuterService")
public class CustomerStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(CustomerStatusExecuterServiceImpl.class);
	
	@Autowired
	private CustomerService customerService;

	@Autowired
	ConsumerConsoleNetConnectManager customerConsoleNetConnectManager;

	@Override
	public void execute() throws Exception {

		List<CustomerVo> customerVoList = customerService.queryCheckCustomerList();
		for (CustomerVo customerVo : customerVoList) {
			byte systemStatus = customerVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			NetStatusChecker netStatusChecker = (NetStatusChecker) customerConsoleNetConnectManager.getConnectAgent(customerVo.getCustomer_Id());
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
				CustomerVo currentCustomerVo = new CustomerVo();
				currentCustomerVo.setCustomer_Id(customerVo.getCustomer_Id());
				currentCustomerVo.setSystem_Status(currentSysTemStatus);
				customerService.updateCustomerSystemStatus(currentCustomerVo);
			}
		}
	}
}
