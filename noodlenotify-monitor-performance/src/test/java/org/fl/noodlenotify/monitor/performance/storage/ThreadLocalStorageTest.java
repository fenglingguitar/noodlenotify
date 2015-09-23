package org.fl.noodlenotify.monitor.performance.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class ThreadLocalStorageTest {

	private final static Logger logger = LoggerFactory.getLogger(ThreadLocalStorageTest.class);
	
	@Test
	public final void testGet() throws Exception {
		TestVo testVo = ThreadLocalStorage.get("executerName", "monitorModuleName", 1, "queueName", "monitorName", TestVo.class);
		testVo.testStr = "Hello";
		testVo = ThreadLocalStorage.get("executerName", "monitorModuleName", 1, "queueName", "monitorName", TestVo.class);
		logger.info(testVo.testStr);
	} 
	
	static class TestVo {
		public String testStr;
	}
}

