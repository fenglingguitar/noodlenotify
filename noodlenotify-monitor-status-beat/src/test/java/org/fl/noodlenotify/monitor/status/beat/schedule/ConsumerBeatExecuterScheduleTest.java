package org.fl.noodlenotify.monitor.status.beat.schedule;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.monitor.status.beat.executer.ConsumerBeatExecuter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/schedule/noodlenotify-monitor-status-beat-schedule-consumer.xml"
})
public class ConsumerBeatExecuterScheduleTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ConsumerBeatExecuter consumerBeatExecuter;
	
	@Test
	public void test() {

		ModuleRegister consumerModuleRegister = new ModuleRegister();
		consumerModuleRegister.setModuleId(1L);
		consumerBeatExecuter.setConsumerModuleRegister(consumerModuleRegister);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
	}
}
