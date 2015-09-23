package org.fl.noodlenotify.core.connect.cache.trace.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/trace/manager/noodlenotify-core-connect-cache-trace-manager-exchange.xml"
})

public class ExchangeTraceCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ExchangeTraceCacheConnectManager exchangeTraceCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		exchangeTraceCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		exchangeTraceCacheConnectManager.start();
		Thread.sleep(1000);
		exchangeTraceCacheConnectManager.destroy();
	}
}
