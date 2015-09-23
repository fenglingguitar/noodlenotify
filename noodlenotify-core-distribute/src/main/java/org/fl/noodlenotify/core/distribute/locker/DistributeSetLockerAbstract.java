package org.fl.noodlenotify.core.distribute.locker;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DistributeSetLockerAbstract implements DistributeSetLocker {

	private final static Logger logger = LoggerFactory.getLogger(DistributeSetLockerAbstract.class);
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private volatile boolean stopSign = false;
	
	private AtomicBoolean status = new AtomicBoolean(false);
	
	private CountDownLatch stopCountDownLatch;
	
	protected String queueName;
	protected long distributeId;
	
	protected long diffTime = 0;
	protected long sleepTime = 10000;
	protected long delayTime = 3000;
	
	private Object sleepObject = new Object();
	
	public DistributeSetLockerAbstract(String queueName, long distributeId) {
		this.distributeId = distributeId;
		this.queueName = queueName;
	}
	
	public DistributeSetLockerAbstract(String queueName, long distributeId, long sleepTime, long delayTime) {
		this.queueName = queueName;
		this.distributeId = distributeId;
		this.sleepTime = sleepTime;
		this.delayTime = delayTime;
	}
	
	@Override
	public void start() {
		
		diffTime = getDiffTime();
		
		stopCountDownLatch = new CountDownLatch(1);
		
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				
				while (true) {
					if (!keepAlive()) {
						status.set(false);
						while(!getAlive()) {
							try {
								startSleep(sleepTime);
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("Start -> " 
											+ "QueueName: " + queueName
											+ ", DistributeId: " + distributeId
											+ ", GetAlive Sleep -> " + e);
								}
							}
							if (stopSign) {
								stopCountDownLatch.countDown();
								return;
							}
						}
						if (stopSign) {
							stopCountDownLatch.countDown();
							return;
						}
					}
					status.set(true);
					notifyAllForLocker();
					try {
						startSleep(sleepTime);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Start -> " 
									+ "QueueName: " + queueName
									+ ", DistributeId: " + distributeId
									+ ", KeepAlive Sleep -> " + e);
						}
					}
					if (stopSign) {
						stopCountDownLatch.countDown();
						return;
					}
				}
				
				
			}
		});
	}
	
	@Override
	public void destroy() {
		
		stopSign = true;
		
		notifySleep();
		
		try {
			stopCountDownLatch.await();
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Destroy -> "
						+ "Queue: " + queueName 
						+ ", DistributeId: " + distributeId
						+ ", CountDownLatch Await -> " + e);
			}
		}
		
		executorService.shutdown();
		
		releaseLocker();
		
		notifyAllForLocker();
	}
	
	@Override
	public boolean getStatus() {
		return status.get();
	}

	@Override
	public boolean waitLocker() {
		if (status.get() == false && !stopSign) {
			waitForLocker();
			return true;
		}
		return false;
	}
	
	private synchronized void waitForLocker() {
		if (!stopSign) {
			try {
				wait();
			} catch (InterruptedException e) {
				if (logger.isErrorEnabled()) {
					logger.error("WaitForLocker -> " 
								+ "QueueName: " + queueName
								+ ", DistributeId: " + distributeId
								+ ", Wait -> " + e);
				}
			}
		}
	}
	
	private synchronized void notifyAllForLocker() {
		notifyAll();
	}

	@Override
	public void releaseLocker() {
		status.set(false);
		releaseAlive();
	}
	
	private void startSleep(long suspendTime) throws InterruptedException {
		if (!stopSign && suspendTime > 0) {			
			synchronized(sleepObject) {
				sleepObject.wait(suspendTime);
			}
		}
	}
	
	private synchronized void notifySleep() {
		synchronized(sleepObject) {
			sleepObject.notifyAll();
		}
	}
	
	protected abstract long getDiffTime();
	protected abstract boolean getAlive();
	protected abstract boolean keepAlive();
	protected abstract void releaseAlive();
	
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}
}
