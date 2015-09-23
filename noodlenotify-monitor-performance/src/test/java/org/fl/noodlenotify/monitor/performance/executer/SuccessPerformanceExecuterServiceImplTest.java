package org.fl.noodlenotify.monitor.performance.executer;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/monitor/performance/executer/noodlenotify-monitor-performance-executer-success.xml"
})

public class SuccessPerformanceExecuterServiceImplTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	@Test
	public void test() throws Exception {
		
		while (true) {
			
			MemoryStorage.moduleName = "moduleName";
			MemoryStorage.moduleId = 1;
			
			Thread.sleep(Math.round(Math.random() * 500));
			if (Math.round(Math.random() * 10) > 5) {				
				successPerformanceExecuterService.result("monitorModuleName", 1,"queueName", "monitorName1", true);
			} else {
				successPerformanceExecuterService.result("monitorModuleName", 1,"queueName", "monitorName1", false);
			}
			Thread.sleep(Math.round(Math.random() * 500));
			if (Math.round(Math.random() * 10) > 5) {				
				successPerformanceExecuterService.result("monitorModuleName", 1,"queueName", "monitorName2", true);
			}else {
				successPerformanceExecuterService.result("monitorModuleName", 1,"queueName", "monitorName2", false);
			}
			Thread.sleep(Math.round(Math.random() * 100));
		}
	}
}
