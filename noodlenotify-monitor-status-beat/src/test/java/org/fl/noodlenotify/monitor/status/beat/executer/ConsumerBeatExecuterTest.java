package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/executer/noodlenotify-monitor-status-beat-executer-consumer.xml"
})
public class ConsumerBeatExecuterTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ConsumerBeatExecuter consumerBeatExecuter;
	
	@Test
	public void testExecute() throws Exception {
		ModuleRegister moduleRegister = new ModuleRegister();
		moduleRegister.setModuleId(1L);
		consumerBeatExecuter.setConsumerModuleRegister(moduleRegister);
		consumerBeatExecuter.execute();
	}
}
