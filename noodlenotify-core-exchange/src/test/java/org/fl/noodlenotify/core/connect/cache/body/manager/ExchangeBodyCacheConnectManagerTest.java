package org.fl.noodlenotify.core.connect.cache.body.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/body/manager/noodlenotify-core-connect-cache-body-manager-exchange.xml"
})

public class ExchangeBodyCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ExchangeBodyCacheConnectManager exchangeBodyCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		exchangeBodyCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		exchangeBodyCacheConnectManager.start();
		Thread.sleep(1000);
		exchangeBodyCacheConnectManager.destroy();
	}
}
