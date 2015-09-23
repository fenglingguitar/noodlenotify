package org.fl.noodlenotify.monitor.performance.clean;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/performance/clean/noodlenotify-monitor-performance-clean.xml"
})

public class ExecuterCleanTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() throws Exception {
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testDestroy() throws Exception {
		Thread.sleep(1000);
	}
}
