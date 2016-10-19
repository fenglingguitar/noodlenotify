package org.fl.noodlenotify.monitor.status.beat.schedule;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.monitor.status.beat.executer.CustomerBeatExecuter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/schedule/noodlenotify-monitor-status-schedule-beat-customer.xml"
})
public class CustomerBeatExecuterScheduleTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	CustomerBeatExecuter customerBeatExecuter;
	
	@Test
	public void test() {

		ModuleRegister customerModuleRegister = new ModuleRegister();
		customerModuleRegister.setModuleId(1L);
		customerBeatExecuter.setConsumerModuleRegister(customerModuleRegister);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
	}
}
