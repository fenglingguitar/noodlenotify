package org.fl.noodlenotify.monitor.performance.receiver;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/performance/receiver/noodlenotify-monitor-performance-receiver.xml"
})

public class ExecuterReceiverTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() throws Exception {
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testDestroy() throws Exception {
		Thread.sleep(1000);
	}
}
