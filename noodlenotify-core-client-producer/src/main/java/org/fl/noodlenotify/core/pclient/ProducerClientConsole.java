package org.fl.noodlenotify.core.pclient;

public class ProducerClientConsole extends ProducerClientImpl {
	
	public void start() throws Exception {
		producerModuleRegister.setModuleId(0L);
		connectManagerPool.start();
	}

	public void destroy() throws Exception {
		connectManagerPool.destroy();
	}
}
