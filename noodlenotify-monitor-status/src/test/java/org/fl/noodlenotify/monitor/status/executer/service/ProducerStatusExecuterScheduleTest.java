package org.fl.noodlenotify.monitor.status.executer.service;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:org/fl/noodlenotify/monitor/status/executer/service/noodlenotify-monitor-schedule-status-producer.xml" 
})

public class ProducerStatusExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() throws Exception {
		Thread.sleep(Long.MAX_VALUE);
	}
}
