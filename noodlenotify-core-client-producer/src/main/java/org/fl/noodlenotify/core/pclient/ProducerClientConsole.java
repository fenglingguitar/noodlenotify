package org.fl.noodlenotify.core.pclient;

public class ProducerClientConsole extends ProducerClientImpl {
	
	public void start() throws Exception {
		producerModuleRegister.setModuleId(0L);
		//netConnectManager.setModuleId(0);
		//netConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//netConnectManager.start();
	}

	public void destroy() throws Exception {
		//netConnectManager.destroy();
	}
}
