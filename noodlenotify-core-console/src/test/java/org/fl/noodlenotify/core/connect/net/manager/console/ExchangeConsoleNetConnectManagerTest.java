package org.fl.noodlenotify.core.connect.net.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.net.manager.console.ExchangeConsoleNetConnectManager;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/manager/console/noodlenotify-core-connect-net-manager-console.xml"
})

public class ExchangeConsoleNetConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ExchangeConsoleNetConnectManager exchangeConsoleNetConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		exchangeConsoleNetConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		exchangeConsoleNetConnectManager.start();
		Thread.sleep(1000);
		exchangeConsoleNetConnectManager.destroy();
	}
}
