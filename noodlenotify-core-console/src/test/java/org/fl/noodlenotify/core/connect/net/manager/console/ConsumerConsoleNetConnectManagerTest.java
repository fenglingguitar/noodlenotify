package org.fl.noodlenotify.core.connect.net.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.net.manager.console.ConsumerConsoleNetConnectManager;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/manager/console/noodlenotify-core-connect-net-manager-console.xml"
})

public class ConsumerConsoleNetConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsumerConsoleNetConnectManager consumerConsoleNetConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		consumerConsoleNetConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		consumerConsoleNetConnectManager.start();
		Thread.sleep(1000);
		consumerConsoleNetConnectManager.destroy();
	}
}
