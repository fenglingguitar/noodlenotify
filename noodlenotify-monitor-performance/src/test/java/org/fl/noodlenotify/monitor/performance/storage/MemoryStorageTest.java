package org.fl.noodlenotify.monitor.performance.storage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;

public class MemoryStorageTest {

	private final static Logger logger = LoggerFactory.getLogger(MemoryStorageTest.class);
	
	@Test
	public final void testGet() throws Exception {
		MemoryStorage.moduleName = "moduleName";
		MemoryStorage.moduleId = 1;
		TestVo testVo = MemoryStorage.get("executerName", "monitorModuleName", 1, "queueName", "monitorName", TestVo.class);
		testVo.testStr = "Hello";
		testVo = MemoryStorage.get("executerName", "monitorModuleName", 1, "queueName", "monitorName", TestVo.class);
		logger.info(testVo.testStr);
	}

	@Test
	public final void testGetKeys() {
		List<KeyVo> keyVoList = MemoryStorage.getKeys("executerName");
		for (KeyVo keyVo : keyVoList) {
			logger.info(keyVo.toKeyString());
		}
	}

	static class TestVo {
		public String testStr;
	}
}
