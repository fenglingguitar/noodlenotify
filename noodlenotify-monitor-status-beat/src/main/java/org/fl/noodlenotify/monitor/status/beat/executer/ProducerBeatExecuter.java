package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerBeatExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ProducerBeatExecuter.class);
	
	private ModuleRegister producerModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	public void execute() throws Exception {
		
		if (producerModuleRegister.getModuleId() == null) {
			return;
		}
		
		try {
			consoleRemotingInvoke.saveProducerBeat(producerModuleRegister.getModuleId());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> consoleRemotingInvoke.clientBeat -> {} -> Exception:{}", producerModuleRegister, e.getMessage());
			}
		}
		
		if (logger.isDebugEnabled()) {			
			logger.debug("execute -> consoleRemotingInvoke.clientBeat -> {}", producerModuleRegister);
		}
	}

	public void setProducerModuleRegister(ModuleRegister producerModuleRegister) {
		this.producerModuleRegister = producerModuleRegister;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
