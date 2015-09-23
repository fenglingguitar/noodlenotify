package org.fl.noodlenotify.core.connect.net.manager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/manager/noodlenotify-core-connect-net-manager-producer.xml"
})

public class ProducerNetConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ProducerNetConnectManager producerNetConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		
		producerNetConnectManager.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		producerNetConnectManager.start();
		Thread.sleep(1000);
		producerNetConnectManager.destroy();
	}
}
