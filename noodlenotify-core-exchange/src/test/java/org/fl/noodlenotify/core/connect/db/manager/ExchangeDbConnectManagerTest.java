package org.fl.noodlenotify.core.connect.db.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/db/manager/noodlenotify-core-connect-db-manager-exchange.xml"
})

public class ExchangeDbConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ExchangeDbConnectManager exchangeDbConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		exchangeDbConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		exchangeDbConnectManager.start();
		Thread.sleep(1000);
		exchangeDbConnectManager.destroy();
	}
}
