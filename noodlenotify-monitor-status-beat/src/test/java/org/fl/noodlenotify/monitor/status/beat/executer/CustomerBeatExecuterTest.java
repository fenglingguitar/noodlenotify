package org.fl.noodlenotify.monitor.status.beat.executer;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/executer/noodlenotify-monitor-status-executer-beat-customer.xml"
})
public class CustomerBeatExecuterTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	CustomerBeatExecuter customerBeatExecuter;
	
	@Test
	public void testExecute() throws Exception {
		ModuleRegister moduleRegister = new ModuleRegister();
		moduleRegister.setModuleId(1L);
		customerBeatExecuter.setCustomerModuleRegister(moduleRegister);
		customerBeatExecuter.execute();
	}
}
