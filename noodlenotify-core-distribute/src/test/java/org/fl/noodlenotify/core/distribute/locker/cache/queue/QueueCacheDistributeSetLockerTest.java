package org.fl.noodlenotify.core.distribute.locker.cache.queue;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/cache/queue/manager/noodlenotify-core-connect-cache-queue-manager-distribute.xml",
		"classpath:org/fl/noodlenotify/core/connect/cache/body/manager/noodlenotify-core-connect-cache-body-manager-distribute.xml",
		"classpath:org/fl/noodlenotify/core/connect/db/manager/noodlenotify-core-connect-db-manager-distribute.xml"
})

public class QueueCacheDistributeSetLockerTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(QueueCacheDistributeSetLockerTest.class);
	
	@Autowired
	ConnectManager distributeQueueCacheConnectManager;
	
	@Autowired
	ModuleRegister distributeModuleRegister;
	
	public static QueueCacheDistributeSetLocker distributeSetLocker1;
	public static QueueCacheDistributeSetLocker distributeSetLocker2;
	
	private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	
	@Test
	public final void testGetStatus() throws Exception {
		
		logger.info("QueueCacheConnectManager Start...");	
		
		distributeModuleRegister.setModuleId(1L);
		distributeQueueCacheConnectManager.runUpdateNow();
		
		
		distributeSetLocker1 = new QueueCacheDistributeSetLocker("TestQueue1", 1, distributeQueueCacheConnectManager);
		distributeSetLocker1.setSleepTimeGetAlive(3000);
		distributeSetLocker1.start();
		logger.info("DistributeSetLocker1 Start...");
		
		distributeSetLocker2 = new QueueCacheDistributeSetLocker("TestQueue1", 2, distributeQueueCacheConnectManager);
		distributeSetLocker2.setSleepTimeGetAlive(3000);
		distributeSetLocker2.start();
		logger.info("DistributeSetLocker2 Start...");
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				if (distributeSetLocker1.getStatus() == true) {
					logger.info("Scheduler distributeSetLocker1 ReleaseLocker...");
					distributeSetLocker1.releaseLocker();
				} else if (distributeSetLocker2.getStatus() == true) {
					logger.info("Scheduler distributeSetLocker2 ReleaseLocker...");
					distributeSetLocker2.releaseLocker();
				}
			}
		}, 8, 8, TimeUnit.SECONDS);
		logger.info("Scheduler Start...");
		
		logger.info("distributeSetLocker1 WaitLocker...");
		distributeSetLocker1.waitLocker();
		logger.info("distributeSetLocker1 Status: " + distributeSetLocker1.getStatus());
		
		logger.info("distributeSetLocker2 WaitLocker...");
		distributeSetLocker2.waitLocker();		
		logger.info("distributeSetLocker2 Status: " + distributeSetLocker2.getStatus());
	}

	@Test
	public final void testWaitLocker() {
		logger.info("distributeSetLocker1 WaitLocker...");
		distributeSetLocker1.waitLocker();
		logger.info("distributeSetLocker1 Status: " + distributeSetLocker1.getStatus());
		logger.info("distributeSetLocker2 WaitLocker...");
		distributeSetLocker2.waitLocker();
		logger.info("distributeSetLocker2 Status: " + distributeSetLocker2.getStatus());
	}

	@Test
	public final void testReleaseLocker() {
		logger.info("distributeSetLocker1 ReleaseLocker...");
		distributeSetLocker1.releaseLocker();
		logger.info("distributeSetLocker2 ReleaseLocker...");
		distributeSetLocker2.releaseLocker();
		distributeSetLocker1.destroy();
		distributeSetLocker2.destroy();
	}
}
