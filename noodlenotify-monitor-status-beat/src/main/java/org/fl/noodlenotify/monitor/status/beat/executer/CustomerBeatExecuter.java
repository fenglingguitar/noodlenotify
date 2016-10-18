package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerBeatExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(CustomerBeatExecuter.class);
	
	private ModuleRegister customerModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	public void execute() throws Exception {
		
		try {
			consoleRemotingInvoke.saveCustomerBeat(customerModuleRegister.getModuleId());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> consoleRemotingInvoke.serverBeat -> {} -> Exception:{}", customerModuleRegister, e.getMessage());
			}
		}
		
		if (logger.isDebugEnabled()) {			
			logger.debug("execute -> consoleRemotingInvoke.serverBeat -> {}", customerModuleRegister);
		}
	}

	public void setCustomerModuleRegister(ModuleRegister customerModuleRegister) {
		this.customerModuleRegister = customerModuleRegister;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
