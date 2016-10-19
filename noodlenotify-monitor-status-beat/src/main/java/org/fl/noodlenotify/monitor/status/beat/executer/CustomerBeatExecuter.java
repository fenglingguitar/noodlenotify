package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerBeatExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(CustomerBeatExecuter.class);
	
	private ModuleRegister consumerModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	public void execute() throws Exception {
		
		if (consumerModuleRegister.getModuleId() == null) {
			return;
		}
		
		try {
			consoleRemotingInvoke.saveCustomerBeat(consumerModuleRegister.getModuleId());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> consoleRemotingInvoke.serverBeat -> {} -> Exception:{}", consumerModuleRegister, e.getMessage());
			}
		}
		
		if (logger.isDebugEnabled()) {			
			logger.debug("execute -> consoleRemotingInvoke.serverBeat -> {}", consumerModuleRegister);
		}
	}

	public void setConsumerModuleRegister(ModuleRegister consumerModuleRegister) {
		this.consumerModuleRegister = consumerModuleRegister;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
