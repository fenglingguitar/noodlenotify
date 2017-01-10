package org.fl.noodlenotify.core.exchange.manager;

import java.util.ArrayList;
import java.util.List;

import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.core.exchange.manager.ExchangeBodyCacheConnectManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/exchange/manager/noodlenotify-core-manager-exchange-cache-body.xml"
})

public class ExchangeBodyCacheConnectManagerTest extends AbstractJUnit4SpringContextTests {
	
	@Autowired
	ModuleRegister exchangeModuleRegister;
	
	@Autowired
	ExchangeBodyCacheConnectManager exchangeBodyCacheConnectManager;
	
	@Test
	public final void testServer() throws Exception {
		exchangeModuleRegister.setModuleId(1L);
		ConnectManagerPool connectManagerPool = new ConnectManagerPool();
		List<ConnectManager> connectManagerList = new ArrayList<ConnectManager>();
		connectManagerList.add(exchangeBodyCacheConnectManager);
		connectManagerPool.setConnectManagerList(connectManagerList);
		connectManagerPool.start();
		Thread.sleep(Long.MAX_VALUE);
	}
	
	@Test
	public final void testClose() throws Exception {
		exchangeModuleRegister.setModuleId(1L);
		ConnectManagerPool connectManagerPool = new ConnectManagerPool();
		List<ConnectManager> connectManagerList = new ArrayList<ConnectManager>();
		connectManagerList.add(exchangeBodyCacheConnectManager);
		connectManagerPool.setConnectManagerList(connectManagerList);
		connectManagerPool.start();
		Thread.sleep(1000);
		connectManagerPool.destroy();
	}
}
