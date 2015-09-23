package org.fl.noodlenotify.core.connect.net.rmi.server;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/rmi/server/noodlenotify-core-connect-net-server-rmi-test.xml"
})

public class RmiNetConnectServerTest extends AbstractJUnit4SpringContextTests {
	
	@Test
	public final void testServer() throws Exception {
		
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		Thread.sleep(1000);
	}
}
