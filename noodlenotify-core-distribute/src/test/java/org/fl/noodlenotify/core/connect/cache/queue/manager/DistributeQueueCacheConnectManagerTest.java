package org.fl.noodlenotify.core.connect.cache.queue.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/queue/manager/noodlenotify-core-connect-cache-queue-manager-distribute.xml"
})

public class DistributeQueueCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	DistributeQueueCacheConnectManager distributeQueueCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		distributeQueueCacheConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		distributeQueueCacheConnectManager.start();
		Thread.sleep(1000);
		distributeQueueCacheConnectManager.destroy();
	}
}
