package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeBeatExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ExchangeBeatExecuter.class);
	
	private ModuleRegister exchangeModuleRegister;

	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	public void execute() throws Exception {
		
		if (exchangeModuleRegister.getModuleId() == null) {
			return;
		}
		
		try {
			consoleRemotingInvoke.saveExchangeBeat(exchangeModuleRegister.getModuleId());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> consoleRemotingInvoke.serverBeat -> {} -> Exception:{}", exchangeModuleRegister, e.getMessage());
			}
		}
		
		if (logger.isDebugEnabled()) {			
			logger.debug("execute -> consoleRemotingInvoke.serverBeat -> {}", exchangeModuleRegister);
		}
	}

	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
