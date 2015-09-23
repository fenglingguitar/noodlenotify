package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.junit.Test;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class JettyNetConnectTest {

	@Test
	public void testSend() throws Exception {

		JettyNetConnect jettyNetConnect = new JettyNetConnect("127.0.0.1", 20001, "/noodlenotify", 30000);
		jettyNetConnect.connect();
		for (int i=0; i<10; i++) {			
			jettyNetConnect.send(new Message("TestQueue1", "1234567890", "你好"));
			Thread.sleep(1000);
		}
		jettyNetConnect.close();
	}
}
