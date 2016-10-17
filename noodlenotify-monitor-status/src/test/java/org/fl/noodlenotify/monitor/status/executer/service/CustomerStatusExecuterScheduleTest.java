package org.fl.noodlenotify.monitor.status.executer.service;

import org.fl.noodle.common.monitor.executer.Executer;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = { 
		"classpath:org/fl/noodlenotify/monitor/status/executer/service/noodlenotify-monitor-schedule-status-customer.xml" 
})

public class CustomerStatusExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	Executer executer;
	
	@Test
	public void test() throws Exception {
		executer.execute();
	}
}
