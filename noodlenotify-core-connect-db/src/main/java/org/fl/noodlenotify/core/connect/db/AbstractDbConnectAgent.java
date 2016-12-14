package org.fl.noodlenotify.core.connect.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.AbstractConnectAgent;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDbConnectAgent extends AbstractConnectAgent implements DbConnectAgent, DbStatusChecker {
	
	private final static Logger logger = LoggerFactory.getLogger(AbstractDbConnectAgent.class);
	
	protected DbConnectAgentConfParam dbConnectAgentConfParam;

	private BlockingQueue<MessageDm> insertBlockingQueue;
	private BlockingQueue<MessageDm> updateBlockingQueue;
	private BlockingQueue<MessageDm> deleteBlockingQueue;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;
	
	private CountDownLatch stopCountDownLatch;
	private AtomicInteger  stopCountDownLatchCount;
		
	public AbstractDbConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList, 
			DbConnectAgentConfParam dbConnectAgentConfParam) {
		super(
			connectId, ip, port, url, ConnectAgentType.DB.getCode(),
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
	
	@Override
	public void connectActual() throws Exception {
		
		connectDbActual();
		
		insertBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getInsertCapacity());
		updateBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getUpdateCapacity());
		deleteBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getDeleteCapacity());
		
		int allThreadCount = dbConnectAgentConfParam.getInsertThreadCount() + 
								dbConnectAgentConfParam.getUpdateThreadCount() + 
								dbConnectAgentConfParam.getDeleteThreadCount();
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

		for (int i=0; i<dbConnectAgentConfParam.getInsertThreadCount(); i++) {
			
			Thread dbConnectAgentInsertThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<MessageDm> messageDmList = new ArrayList<MessageDm>(dbConnectAgentConfParam.getInsertBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDm messageDm = insertBlockingQueue.poll(dbConnectAgentConfParam.getInsertWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDm != null) {
									messageDmList.add(messageDm);
									if (messageDmList.size() == dbConnectAgentConfParam.getInsertBatchSize()) {
										break;
									}
								} else {
									if (messageDmList.size() > 0) {
										break;
									} else {
										if (stopSign && insertBlockingQueue.size() == 0) {
											stopCountDownLatch.countDown();
											if (logger.isDebugEnabled()) {
												logger.debug("InsertRunnable -> StopCountDownLatchCount CountDown -> " 
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
									logger.error("InsertRunnable -> " 
											+ "DB: " + connectId
											+ ", Ip: " + ip
											+ ", Port: " + port
											+ ", Start Insert Threads -> " + e);
								}
							}
						}
						insertActual(messageDmList);
						for (MessageDm messageDm : messageDmList) {
							synchronized(messageDm) {								
								messageDm.notifyAll();
							}
						}
						messageDmList.clear();
					}
				}
			});
			
			dbConnectAgentInsertThread.setPriority(dbConnectAgentConfParam.getInsertThreadPriority());
			executorService.execute(dbConnectAgentInsertThread);
		}
		
		for (int i=0; i<dbConnectAgentConfParam.getUpdateThreadCount(); i++) {
			
			Thread dbConnectAgentUpdateThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<MessageDm> messageDmList = new ArrayList<MessageDm>(dbConnectAgentConfParam.getUpdateBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDm messageDm = updateBlockingQueue.poll(dbConnectAgentConfParam.getUpdateWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDm != null) {
									messageDmList.add(messageDm);
									if (messageDmList.size() == dbConnectAgentConfParam.getUpdateBatchSize()) {
										break;
									}
								} else {
									if (messageDmList.size() > 0) {
										break;
									} else {
										if (stopSign && updateBlockingQueue.size() == 0) {
											stopCountDownLatch.countDown();
											if (logger.isDebugEnabled()) {
												logger.debug("UpdateRunnable -> StopCountDownLatchCount CountDown -> " 
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
									logger.error("UpdateRunnable -> " 
											+ "DB: " + connectId
											+ ", Ip: " + ip
											+ ", Port: " + port
											+ ", Start Update Threads -> " + e);
								}
							}
						}
						updateActual(messageDmList);
						//offerExecuteBatchList(messageDmList);
						for (MessageDm messageDm : messageDmList) {
							messageDm.executeMessageCallback();
						}
						messageDmList.clear();
					}
				}
			});
			
			dbConnectAgentUpdateThread.setPriority(dbConnectAgentConfParam.getUpdateThreadPriority());
			executorService.execute(dbConnectAgentUpdateThread);
		}
		
		for (int i=0; i<dbConnectAgentConfParam.getDeleteThreadCount(); i++) {
			
			Thread dbConnectAgentDeleteThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					List<MessageDm> messageDmList = new ArrayList<MessageDm>(dbConnectAgentConfParam.getDeleteBatchSize());
					
					while (true) {
						while (true) {
							try {
								MessageDm messageDm = deleteBlockingQueue.poll(dbConnectAgentConfParam.getDeleteWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDm != null) {
									messageDmList.add(messageDm);
									if (messageDmList.size() == dbConnectAgentConfParam.getDeleteBatchSize()) {
										break;
									}
								} else {
									if (messageDmList.size() > 0) {
										break;
									} else {
										if (stopSign && deleteBlockingQueue.size() == 0) {
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
											+ "DB: " + connectId
											+ ", Ip: " + ip
											+ ", Port: " + port
											+ ", Start Delete Threads -> " + e);
								}
							}
						}
						
						deleteActual(messageDmList);
						cancelCountDownLatchList(messageDmList);
						messageDmList.clear();
					}
				}
			});
			
			dbConnectAgentDeleteThread.setPriority(dbConnectAgentConfParam.getDeleteThreadPriority());
			executorService.execute(dbConnectAgentDeleteThread);
		}
	}
	
	@Override
	public void reconnectActual() throws Exception {
		reconnectDbActual();
	}
		
	@Override
	public void closeActual() {
		
		stopSign = true;
		
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
		
		executorService.shutdown();
		
		closeDbActual();
	}
	
	protected abstract void connectDbActual() throws Exception;
	protected abstract void reconnectDbActual() throws Exception;
	protected abstract void closeDbActual();
	
	@Override
	public void insert(MessageDm messageDm) throws Exception {
		
		synchronized(messageDm) {
			messageDm.setResult(false);
			if (insertBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getInsertTimeout(), TimeUnit.MILLISECONDS)) {
				try {
					messageDm.wait();
				} catch (java.lang.InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Insert -> wait -> " 
								+ "Queue: " + messageDm.getQueueName()
								+ ", UUID: " + messageDm.getUuid()
								+ ", InterruptedException -> " + e);
					}
				}
			} else {
				throw new ConnectTimeoutException("Db connect agent insert timeout");
			}
		}
		
		if (messageDm.getResult() == false) {
			throw messageDm.getException();
		}
	}

	@Override
	public void update(MessageDm messageDm) throws Exception {
		
		/*if (connectStatus.get() == false) {
			offerExecuteBatch(messageDm);
			throw new ConnectionUnableException("Connection disable for the db connect agent");
		}*/
		
		if (!updateBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getUpdateTimeout(), TimeUnit.MILLISECONDS)) {				
			throw new ConnectTimeoutException("Db connect agent update timeout");
		}
	}
	
	@Override
	public void delete(MessageDm messageDm) throws Exception {
		
		/*if (connectStatus.get() == false) {
			cancelCountDownLatch(messageDm);
			throw new ConnectionUnableException("Connection disable for the db connect agent");
		}*/
		
		if (!deleteBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getDeleteTimeout(), TimeUnit.MILLISECONDS)) {
			cancelCountDownLatch(messageDm);
		}
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
	
	protected abstract void insertActual(List<MessageDm> messageDmList);
	protected abstract void updateActual(List<MessageDm> messageDmList);
	protected abstract void deleteActual(List<MessageDm> messageDmList);
	
	public long getConnectId() {
		return connectId;
	}

	public void setConnectId(long connectId) {
		this.connectId = connectId;
	}

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}