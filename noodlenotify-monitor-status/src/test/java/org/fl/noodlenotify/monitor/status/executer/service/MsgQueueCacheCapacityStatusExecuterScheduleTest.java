package org.fl.noodlenotify.monitor.status.executer.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:org/fl/noodlenotify/monitor/status/executer/service/noodlenotify-monitor-schedule-status-msgqueuecache-capacity.xml" 
})

public class MsgQueueCacheCapacityStatusExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ExecuterService executerService;
	
	@Test
	public void test() throws Exception {
		executerService.execute();
	}
}