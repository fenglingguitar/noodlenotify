package org.fl.noodlenotify.core.connect.cache.trace.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/trace/manager/noodlenotify-core-connect-cache-trace-manager-distribute.xml"
})

public class DistributeTraceCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	DistributeTraceCacheConnectManager distributeTraceCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		distributeTraceCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		distributeTraceCacheConnectManager.start();
		Thread.sleep(1000);
		distributeTraceCacheConnectManager.destroy();
	}
}
