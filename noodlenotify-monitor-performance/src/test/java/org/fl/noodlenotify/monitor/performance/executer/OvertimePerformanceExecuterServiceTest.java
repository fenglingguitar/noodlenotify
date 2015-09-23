package org.fl.noodlenotify.monitor.performance.executer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/performance/executer/noodlenotify-monitor-performance-executer-overtime.xml"
})

public class OvertimePerformanceExecuterServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Test
	public void test() throws Exception {
		
		while (true) {
			
			MemoryStorage.moduleName = "moduleName";
			MemoryStorage.moduleId = 1;
			
			overtimePerformanceExecuterService.before("monitorModuleName", 1,"queueName", "monitorName1");
			Thread.sleep(Math.round(Math.random() * 10));
			if (Math.round(Math.random() * 10) > 5) {				
				overtimePerformanceExecuterService.after("monitorModuleName", 1,"queueName", "monitorName1");
			}
			overtimePerformanceExecuterService.before("monitorModuleName", 1,"queueName", "monitorName2");
			Thread.sleep(Math.round(Math.random() * 500));
			if (Math.round(Math.random() * 10) > 5) {				
				overtimePerformanceExecuterService.after("monitorModuleName", 1,"queueName", "monitorName2");
			}
			Thread.sleep(Math.round(Math.random() * 100));
		}
	}
}
