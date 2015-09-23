package org.fl.noodlenotify.core.connect.cache.body.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/body/manager/console/noodlenotify-core-connect-cache-body-manager-console.xml"
})

public class ConsoleBodyCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsoleBodyCacheConnectManager consoleBodyCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		consoleBodyCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		consoleBodyCacheConnectManager.start();
		Thread.sleep(1000);
		consoleBodyCacheConnectManager.destroy();
	}
}
