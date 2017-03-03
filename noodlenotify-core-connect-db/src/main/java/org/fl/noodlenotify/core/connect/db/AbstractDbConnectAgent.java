package org.fl.noodlenotify.core.connect.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.AbstractConnectAgent;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;

public abstract class AbstractDbConnectAgent extends AbstractConnectAgent implements DbConnectAgent {
	
	//private final static Logger logger = LoggerFactory.getLogger(AbstractDbConnectAgent.class);
	
	protected DbConnectAgentConfParam dbConnectAgentConfParam;

	private BlockingQueue<MessageDb> insertBlockingQueue;
	private BlockingQueue<MessageDb> updateBlockingQueue;
	private BlockingQueue<MessageDb> deleteBlockingQueue;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;
		
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
		
		insertBlockingQueue = new LinkedBlockingQueue<MessageDb>(dbConnectAgentConfParam.getInsertCapacity());
		updateBlockingQueue = new LinkedBlockingQueue<MessageDb>(dbConnectAgentConfParam.getUpdateCapacity());
		deleteBlockingQueue = new LinkedBlockingQueue<MessageDb>(dbConnectAgentConfParam.getDeleteCapacity());

		for (int i=0; i<dbConnectAgentConfParam.getInsertThreadCount(); i++) {
			
			Thread dbConnectAgentInsertThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					List<MessageDb> messageDbList = new ArrayList<MessageDb>(dbConnectAgentConfParam.getInsertBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDb messageDb = insertBlockingQueue.poll(dbConnectAgentConfParam.getInsertWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDb != null) {
									messageDbList.add(messageDb);
									if (messageDbList.size() == dbConnectAgentConfParam.getInsertBatchSize()) {
										break;
									}
								} else {
									if (messageDbList.size() > 0) {
										break;
									} else {
										if (stopSign && insertBlockingQueue.size() == 0) {
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						insertActual(messageDbList);
						for (MessageDb messageDb : messageDbList) {
							synchronized(messageDb) {								
								messageDb.notifyAll();
							}
						}
						messageDbList.clear();
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
					List<MessageDb> messageDbList = new ArrayList<MessageDb>(dbConnectAgentConfParam.getUpdateBatchSize());
					while (true) {
						while (true) {
							try {
								MessageDb messageDb = updateBlockingQueue.poll(dbConnectAgentConfParam.getUpdateWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDb != null) {
									messageDbList.add(messageDb);
									if (messageDbList.size() == dbConnectAgentConfParam.getUpdateBatchSize()) {
										break;
									}
								} else {
									if (messageDbList.size() > 0) {
										break;
									} else {
										if (stopSign && updateBlockingQueue.size() == 0) {
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						updateActual(messageDbList);
						for (MessageDb messageDb : messageDbList) {
							messageDb.executeMessageCallback();
						}
						messageDbList.clear();
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
					
					List<MessageDb> messageDbList = new ArrayList<MessageDb>(dbConnectAgentConfParam.getDeleteBatchSize());
					
					while (true) {
						while (true) {
							try {
								MessageDb messageDb = deleteBlockingQueue.poll(dbConnectAgentConfParam.getDeleteWaitTime(), TimeUnit.MILLISECONDS);
								if (messageDb != null) {
									messageDbList.add(messageDb);
									if (messageDbList.size() == dbConnectAgentConfParam.getDeleteBatchSize()) {
										break;
									}
								} else {
									if (messageDbList.size() > 0) {
										break;
									} else {
										if (stopSign && deleteBlockingQueue.size() == 0) {
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						deleteActual(messageDbList);
						for (MessageDb messageDb : messageDbList) {
							messageDb.executeMessageCallback();
						}
						messageDbList.clear();
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
		executorService.shutdownNow();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		closeDbActual();
	}
	
	protected abstract void connectDbActual() throws Exception;
	protected abstract void reconnectDbActual() throws Exception;
	protected abstract void closeDbActual();
	
	@Override
	public void insert(MessageDb messageDb) throws Exception {
		
		synchronized(messageDb) {
			messageDb.setResult(false);
			if (insertBlockingQueue.offer(messageDb, dbConnectAgentConfParam.getInsertTimeout(), TimeUnit.MILLISECONDS)) {
				try {
					messageDb.wait();
				} catch (java.lang.InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				throw new ConnectTimeoutException("Db connect agent insert timeout");
			}
		}
		
		if (messageDb.getResult() == false) {
			throw messageDb.getException();
		}
	}

	@Override
	public void update(MessageDb messageDb) throws Exception {
		if (!updateBlockingQueue.offer(messageDb, dbConnectAgentConfParam.getUpdateTimeout(), TimeUnit.MILLISECONDS)) {				
			throw new ConnectTimeoutException("Db connect agent update timeout");
		}
	}
	
	@Override
	public void delete(MessageDb messageDb) throws Exception {
		if (!deleteBlockingQueue.offer(messageDb, dbConnectAgentConfParam.getDeleteTimeout(), TimeUnit.MILLISECONDS)) {
			throw new ConnectTimeoutException("Db connect agent update timeout");
		}
	}
	
	protected abstract void insertActual(List<MessageDb> messageDbList);
	protected abstract void updateActual(List<MessageDb> messageDbList);
	protected abstract void deleteActual(List<MessageDb> messageDbList);

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}
