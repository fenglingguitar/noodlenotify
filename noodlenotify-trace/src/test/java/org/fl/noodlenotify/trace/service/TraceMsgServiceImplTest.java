package org.fl.noodlenotify.trace.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.trace.service.TraceMsgService;

@ContextConfiguration(locations = { 
		"classpath:/org/fl/noodlenotify/trace/service/noodlenotify-trace-msg-service.xml" 
})

public class TraceMsgServiceImplTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	private TraceMsgService traceMsgService;

	@Test
	public void test() throws Exception {
		traceMsgService.traceMsg("1111");
	}
}
