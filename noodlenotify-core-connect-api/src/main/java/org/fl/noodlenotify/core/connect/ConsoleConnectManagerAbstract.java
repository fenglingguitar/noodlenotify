package org.fl.noodlenotify.core.connect;

import java.util.concurrent.CountDownLatch;

public abstract class ConsoleConnectManagerAbstract extends ConnectManagerAbstract {
	
	public ConsoleConnectManagerAbstract() {
		super();
	}
	
	public ConsoleConnectManagerAbstract(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}
	
	public void start() throws Exception {
		
		stopCountDownLatch = new CountDownLatch(1);
		
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					updateConnectAgent();
					suspendUpdateConnectAgent();
					if (stopSign) {
						destroyConnectAgent();
						break;
					}
				}
				stopCountDownLatch.countDown();
			}
		});		
	}
}
