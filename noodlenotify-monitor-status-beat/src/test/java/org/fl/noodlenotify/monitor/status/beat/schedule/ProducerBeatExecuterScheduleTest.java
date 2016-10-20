package org.fl.noodlenotify.monitor.status.beat.schedule;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.monitor.status.beat.executer.ProducerBeatExecuter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/schedule/noodlenotify-monitor-status-beat-schedule-producer.xml"
})
public class ProducerBeatExecuterScheduleTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ProducerBeatExecuter producerBeatExecuter;
	
	@Test
	public void test() {
		
		ModuleRegister producerModuleRegister = new ModuleRegister();
		producerModuleRegister.setModuleId(1L);
		producerBeatExecuter.setProducerModuleRegister(producerModuleRegister);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
	}
}
