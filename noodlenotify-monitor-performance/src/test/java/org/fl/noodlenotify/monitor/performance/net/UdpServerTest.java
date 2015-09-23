package org.fl.noodlenotify.monitor.performance.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class UdpServerTest {

	private final static Logger logger = LoggerFactory.getLogger(UdpServerTest.class);
	
	@Test
	public final void test() throws Exception {
		UdpServer udpServer = new UdpServer(12345);
		udpServer.setGetInterval(10000);
		udpServer.start();
		logger.info("Recv: " + udpServer.get());
		udpServer.destroy();
	}
}
