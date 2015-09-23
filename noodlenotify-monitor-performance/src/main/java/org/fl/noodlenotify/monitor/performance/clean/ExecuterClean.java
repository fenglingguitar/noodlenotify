package org.fl.noodlenotify.monitor.performance.clean;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.monitor.performance.persistence.RedisPersistenceTemplate;

public class ExecuterClean {
	
	private final static Logger logger = LoggerFactory.getLogger(ExecuterClean.class);
	
	final long TIME_SPAN_UNIT = 1000 * 60 * 60 * 24;
	final long INTERVAL_UNIT = 1000 * 60;
	
	@Autowired
	RedisPersistenceTemplate redisPersistenceTemplate;
	
	private ExecutorService executorServiceDeal;
	private volatile boolean stopSign = false;
	private CountDownLatch stopCountDownLatch;
	
	private int threadCountDeal = 1;
	private long timeSpan = TIME_SPAN_UNIT * 3;
	private long interval = INTERVAL_UNIT * 60;
	
	public void start() {
		
		stopCountDownLatch = new CountDownLatch(threadCountDeal);
		
		executorServiceDeal = Executors.newFixedThreadPool(threadCountDeal);
		
		for (int i=0; i<threadCountDeal; i++) {
			executorServiceDeal.execute(new Runnable() {
				@Override
				public void run() {
					while (true) {
						
						Set<String> keysSet = null;
						
						try {
							keysSet = redisPersistenceTemplate.getKeys();
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("ExecutorServiceDeal -> GetKeys -> "  + e);
							}
						}
						
						if (keysSet != null) {
							long nowTime = System.currentTimeMillis();
							
							for (String key : keysSet) {
								try {
									redisPersistenceTemplate.deletes(key, 0, nowTime - timeSpan);
									if (logger.isDebugEnabled()) {
										logger.debug("ExecutorServiceDeal -> "
												+ "Key: " + key
												+ ", Deletes");
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("ExecutorServiceDeal -> "
												+ "Key: " + key
												+ ", Deletes -> "  + e);
									}
								}
							}
						}
						
						try {
							startSleep(interval);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("ExecutorServiceDeal -> Sleep -> "  + e);
							}
						}
						
						if (stopSign) {
							stopCountDownLatch.countDown();
							break;
						}
					}
				}
			});
		}
	}
	
	public void destroy() throws Exception {
		stopSign = true;
		notifySleep();
		stopCountDownLatch.await();
		executorServiceDeal.shutdown();
	}
	
	private synchronized void startSleep(long suspendTime) throws InterruptedException {
		if (!stopSign && suspendTime > 0) {
			wait(suspendTime);
		}
	}
	
	private synchronized void notifySleep() {
		notifyAll();
	}
	
	public void setThreadCountDeal(int threadCountDeal) {
		this.threadCountDeal = threadCountDeal;
	}
	
	public void setTimeSpan(long timeSpan) {
		this.timeSpan = TIME_SPAN_UNIT * timeSpan;
	}

	public void setInterval(long interval) {
		this.interval = INTERVAL_UNIT * interval;
	}
}
