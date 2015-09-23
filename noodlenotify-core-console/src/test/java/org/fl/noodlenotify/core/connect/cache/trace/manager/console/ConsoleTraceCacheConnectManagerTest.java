package org.fl.noodlenotify.core.connect.cache.trace.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/trace/manager/console/noodlenotify-core-connect-cache-trace-manager-console.xml"
})

public class ConsoleTraceCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsoleTraceCacheConnectManager consoleTraceCacheConnectManager;
	
	@Test
	public final void testStart() throws Exception {
		
		consoleTraceCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		consoleTraceCacheConnectManager.start();
		Thread.sleep(1000);
		consoleTraceCacheConnectManager.destroy();
	}
}
