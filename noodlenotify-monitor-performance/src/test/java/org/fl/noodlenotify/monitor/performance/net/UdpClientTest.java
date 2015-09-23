package org.fl.noodlenotify.monitor.performance.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class UdpClientTest {

	private final static Logger logger = LoggerFactory.getLogger(UdpClientTest.class);
	
	@Test
	public final void test() throws Exception {
		UdpClient udpNet = new UdpClient("127.0.0.1", 12345);
		udpNet.start();
		udpNet.send("Hello");
		logger.info("Send: " + "Hello");
		udpNet.destroy();
	}
}
