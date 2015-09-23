package org.fl.noodlenotify.core.connect.cache.queue.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/queue/manager/console/noodlenotify-core-connect-cache-queue-manager-console.xml"
})

public class ConsoleQueueCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsoleQueueCacheConnectManager consoleQueueCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		consoleQueueCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		consoleQueueCacheConnectManager.start();
		Thread.sleep(1000);
		consoleQueueCacheConnectManager.destroy();
	}
}
