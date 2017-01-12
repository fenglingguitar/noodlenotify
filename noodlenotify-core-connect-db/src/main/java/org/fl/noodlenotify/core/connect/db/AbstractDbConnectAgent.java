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
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public abstract class AbstractDbConnectAgent extends AbstractConnectAgent implements DbConnectAgent {
	
	//private final static Logger logger = LoggerFactory.getLogger(AbstractDbConnectAgent.class);
	
	protected DbConnectAgentConfParam dbConnectAgentConfParam;

	private BlockingQueue<MessageDm> insertBlockingQueue;
	private BlockingQueue<MessageDm> updateBlockingQueue;
	private BlockingQueue<MessageDm> deleteBlockingQueue;
	
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
		
		insertBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getInsertCapacity());
		updateBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getUpdateCapacity());
		deleteBlockingQueue = new LinkedBlockingQueue<MessageDm>(dbConnectAgentConfParam.getDeleteCapacity());

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
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
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
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						updateActual(messageDmList);
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
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						deleteActual(messageDmList);
						for (MessageDm messageDm : messageDmList) {
							messageDm.executeMessageCallback();
						}
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
	public void insert(MessageDm messageDm) throws Exception {
		
		synchronized(messageDm) {
			messageDm.setResult(false);
			if (insertBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getInsertTimeout(), TimeUnit.MILLISECONDS)) {
				try {
					messageDm.wait();
				} catch (java.lang.InterruptedException e) {
					e.printStackTrace();
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
		if (!updateBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getUpdateTimeout(), TimeUnit.MILLISECONDS)) {				
			throw new ConnectTimeoutException("Db connect agent update timeout");
		}
	}
	
	@Override
	public void delete(MessageDm messageDm) throws Exception {
		if (!deleteBlockingQueue.offer(messageDm, dbConnectAgentConfParam.getDeleteTimeout(), TimeUnit.MILLISECONDS)) {
			throw new ConnectTimeoutException("Db connect agent update timeout");
		}
	}
	
	protected abstract void insertActual(List<MessageDm> messageDmList);
	protected abstract void updateActual(List<MessageDm> messageDmList);
	protected abstract void deleteActual(List<MessageDm> messageDmList);

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}
