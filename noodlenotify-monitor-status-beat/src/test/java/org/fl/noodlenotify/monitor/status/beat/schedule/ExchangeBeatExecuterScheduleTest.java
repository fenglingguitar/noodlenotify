package org.fl.noodlenotify.monitor.status.beat.schedule;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.monitor.status.beat.executer.ExchangeBeatExecuter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/status/beat/schedule/noodlenotify-monitor-status-beat-schedule-exchange.xml"
})
public class ExchangeBeatExecuterScheduleTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ExchangeBeatExecuter exchangeBeatExecuter;
	
	@Test
	public void test() {

		ModuleRegister exchangeModuleRegister = new ModuleRegister();
		exchangeModuleRegister.setModuleId(1L);
		exchangeBeatExecuter.setExchangeModuleRegister(exchangeModuleRegister);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
		}
	}
}
