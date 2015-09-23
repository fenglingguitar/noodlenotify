package org.fl.noodlenotify.core.connect.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.core.connect.ConnectAgentAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public abstract class CacheConnectAgentAbstract extends ConnectAgentAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(CacheConnectAgentAbstract.class);
	
	protected CacheConnectAgentConfParam cacheConnectAgentConfParam;
	
	protected BlockingQueue<MessageDm> setBlockingQueue;
	protected BlockingQueue<MessageDm> removeBlockingQueue;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;
	
	private CountDownLatch stopCountDownLatch;
	private AtomicInteger  stopCountDownLatchCount;

	public CacheConnectAgentAbstract(String ip, int port, long connectId) {
		super(ip, port, connectId);
		cacheConnectAgentConfParam = new CacheConnectAgentConfParam();
	}
	
	public CacheConnectAgentAbstract(String ip, int port, long connectId,
				CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		super(ip, port, connectId);
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}
	
	@Override
	protected void connectActual() throws Exception {
		
		connectCacheActual();
		
		setBlockingQueue = new LinkedBlockingQueue<MessageDm>(cacheConnectAgentConfParam.getSetCapacity());			
		removeBlockingQueue = new LinkedBlockingQueue<MessageDm>(cacheConnectAgentConfParam.getRemoveCapacity());
		
		int allThreadCount = cacheConnectAgentConfParam.getSetThreadCount() + 
								cacheConnectAgentConfParam.getRemoveThreadCount();
		stopCountDownLatch = new CountDownLatch(allThreadCount);
		if (logger.isDebugEnabled()) {
			stopCountDownLatchCount = new AtomicInteger(allThreadCount);
			logger.debug("ConnectActual -> New StopCountDownLatchCount -> " 
					+ "ConnectId: " + connectId
					+ ", Ip: " + ip
					+ ", Port: " + port
					+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.get()
					);
		}

		for (int i=0; i<cacheConnectAgentConfParam.getSetThreadCount(); i++) {
			
			Thread connectAgentSetThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<MessageDm> messageDmList = new ArrayList<MessageDm>(cacheConnectAgentConfParam.getSetBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDm messageDm = setBlockingQueue.poll(cacheConnectAgentConfParam.getSetWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDm != null) {
									messageDmList.add(messageDm);
									if (messageDmList.size() == cacheConnectAgentConfParam.getSetBatchSize()) {
										break;
									}
								} else {
									if (messageDmList.size() > 0) {
										break;
									} else {
										if (stopSign) {
											stopCountDownLatch.countDown();
											if (logger.isDebugEnabled()) {
												logger.debug("SetRunnable -> StopCountDownLatchCount CountDown -> " 
														+ "ConnectId: " + connectId
														+ ", Ip: " + ip
														+ ", Port: " + port
														+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
														);
											}
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("ConnectActual -> " 
											+ "ConnectId: " + connectId
											+ ", Ip: " + ip
											+ ", Port: " + port
											+ ", Start Set Threads -> " + e);
								}
							}
						}
						setActual(messageDmList);
						cancelCountDownLatchList(messageDmList);
						messageDmList.clear();
					}
				}
			});
			
			connectAgentSetThread.setPriority(cacheConnectAgentConfParam.getSetThreadPriority());
			executorService.execute(connectAgentSetThread);
		}
		
		for (int i=0; i<cacheConnectAgentConfParam.getRemoveThreadCount(); i++) {
			
			Thread connectAgentRemoveThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<MessageDm> messageDmList = new ArrayList<MessageDm>(cacheConnectAgentConfParam.getRemoveBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDm messageDm = removeBlockingQueue.poll(cacheConnectAgentConfParam.getRemoveWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDm != null) {
									messageDmList.add(messageDm);
									if (messageDmList.size() == cacheConnectAgentConfParam.getRemoveBatchSize()) {
										break;
									}
								} else {
									if (messageDmList.size() > 0) {
										break;
									} else {
										if (stopSign) {
											stopCountDownLatch.countDown();
											if (logger.isDebugEnabled()) {
												logger.debug("RemoveRunnable -> StopCountDownLatchCount CountDown -> " 
														+ "ConnectId: " + connectId
														+ ", Ip: " + ip
														+ ", Port: " + port
														+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
														);
											}
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("ConnectActual -> " 
											+ "ConnectId: " + connectId
											+ ", Ip: " + ip
											+ ", Port: " + port
											+ ", Start Remove Threads -> " + e);
								}
							}
						}
						try {
							startSleep(cacheConnectAgentConfParam.getRemoveDelay());
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("ConnectActual -> " 
										+ "ConnectId: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", StartSleep RemoveDelay -> " + e);
							}
						}
						removeActual(messageDmList);
						cancelCountDownLatchList(messageDmList);
						messageDmList.clear();
					}
				}
			});
			
			connectAgentRemoveThread.setPriority(cacheConnectAgentConfParam.getSetThreadPriority());
			executorService.execute(connectAgentRemoveThread);
		}
	}

	@Override
	protected void reconnectActual() throws Exception {
		reconnectCacheActual();
	}

	@Override
	protected void closeActual() {
		
		stopSign = true;
		
		notifySleep();
		
		try {
			stopCountDownLatch.await();
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("CloseActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port	
						+ ", CountDownLatch Await -> " + e
						);
			}
		}
		
		closeCacheActual();
	}
	
	protected abstract void connectCacheActual() throws Exception;
	protected abstract void reconnectCacheActual() throws Exception;
	protected abstract void closeCacheActual();
	
	protected abstract void setActual(List<MessageDm> messageDmList);
	protected abstract void removeActual(List<MessageDm> messageDmList);

	private synchronized void startSleep(long suspendTime) throws InterruptedException {
		if (!stopSign && suspendTime > 0) {
			wait(suspendTime);
		}
	}
	
	private synchronized void notifySleep() {
		notifyAll();
	}
	
	private void cancelCountDownLatchList(List<MessageDm> messageDmList) {
		for (MessageDm messageDm : messageDmList) {			
			cancelCountDownLatch(messageDm);
		}
	}
	
	private void cancelCountDownLatch(MessageDm messageDm) {
		if (messageDm.getObjectOne() != null) {					
			CountDownLatch countDownLatch = (CountDownLatch) messageDm.getObjectOne();
			countDownLatch.countDown();
		}
	}
	
	public void setCacheConnectAgentConfParam(
			CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}
}
