package org.fl.noodlenotify.monitor.status.beat.schedule;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/schedule/noodlenotify-monitor-status-schedule-beat.xml"
})
public class BeatExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Test
	public void test() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}
	}
}
