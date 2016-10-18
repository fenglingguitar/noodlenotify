package org.fl.noodlenotify.monitor.status.beat.executer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.monitor.status.beat.executer.ProducerBeatExecuter;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/executer/noodlenotify-monitor-status-executer-beat-producer.xml"
})
public class ProducerBeatExecuterTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ProducerBeatExecuter producerBeatExecuter;
	
	@Test
	public void testExecute() throws Exception {
		ModuleRegister moduleRegister = new ModuleRegister();
		moduleRegister.setModuleId(1L);
		producerBeatExecuter.setProducerModuleRegister(moduleRegister);
		producerBeatExecuter.execute();
	}
}
