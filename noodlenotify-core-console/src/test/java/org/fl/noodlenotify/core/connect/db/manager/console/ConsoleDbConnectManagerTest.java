package org.fl.noodlenotify.core.connect.db.manager.console;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/db/manager/console/noodlenotify-core-connect-db-manager-console.xml"
})

public class ConsoleDbConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsoleDbConnectManager consoleDbConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		consoleDbConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		consoleDbConnectManager.start();
		Thread.sleep(1000);
		consoleDbConnectManager.destroy();
	}
}
