package org.fl.noodlenotify.core.connect.db.manager;

import org.fl.noodle.common.connect.register.ModuleRegister;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/db/manager/noodlenotify-core-connect-db-manager-distribute.xml"
})

public class DistributeDbConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	DistributeDbConnectManager distributeDbConnectManager;
	
	@Autowired
	ModuleRegister distributeModuleRegister;
	
	@Test
	public final void testServer() throws Exception {
		
		distributeDbConnectManager.start();
		distributeModuleRegister.setModuleId(1L);
		distributeDbConnectManager.runUpdateNow();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		
		distributeDbConnectManager.start();
		Thread.sleep(1000);
		distributeDbConnectManager.destroy();
	}
}
