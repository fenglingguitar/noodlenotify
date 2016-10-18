package org.fl.noodlenotify.monitor.status.console.schedule;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/console/schedule/noodlenotify-monitor-schedule-status.xml"
})

public class StatusExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() throws Exception {
		Thread.sleep(Long.MAX_VALUE);
	}
}
