package org.fl.noodlenotify.core.connect.cache;

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
import org.fl.noodlenotify.core.domain.message.MessageDm;

public abstract class AbstractCacheConnectAgent extends AbstractConnectAgent {
	
	//private final static Logger logger = LoggerFactory.getLogger(AbstractCacheConnectAgent.class);
	
	protected CacheConnectAgentConfParam cacheConnectAgentConfParam;
	
	protected BlockingQueue<MessageDm> setBlockingQueue;
	protected BlockingQueue<MessageDm> removeBlockingQueue;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;

	public AbstractCacheConnectAgent(
			long connectId, String ip, int port, String url, String type, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList, 
			CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		super(
			connectId, ip, port, url, type,
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}
	
	@Override
	protected void connectActual() throws Exception {
		
		connectCacheActual();
		
		setBlockingQueue = new LinkedBlockingQueue<MessageDm>(cacheConnectAgentConfParam.getSetCapacity());			
		removeBlockingQueue = new LinkedBlockingQueue<MessageDm>(cacheConnectAgentConfParam.getRemoveCapacity());
		
		for (int i=0; i<cacheConnectAgentConfParam.getSetThreadCount(); i++) {
			
			executorService.execute(new Runnable() {
				
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
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						setActual(messageDmList);
						for (MessageDm messageDm : messageDmList) {
							messageDm.executeMessageCallback();
						}
						messageDmList.clear();
					}
				}
			});
		}
		
		for (int i=0; i<cacheConnectAgentConfParam.getRemoveThreadCount(); i++) {
			
			executorService.execute(new Runnable() {
				
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
											return;
										}
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(cacheConnectAgentConfParam.getRemoveDelay());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						removeActual(messageDmList);
						for (MessageDm messageDm : messageDmList) {
							messageDm.executeMessageCallback();
						}
						messageDmList.clear();
					}
				}
			});
		}
	}

	@Override
	protected void reconnectActual() throws Exception {
		reconnectCacheActual();
	}

	@Override
	protected void closeActual() {
		stopSign = true;
		executorService.shutdownNow();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		closeCacheActual();
	}
	
	protected abstract void connectCacheActual() throws Exception;
	protected abstract void reconnectCacheActual() throws Exception;
	protected abstract void closeCacheActual();
	
	protected abstract void setActual(List<MessageDm> messageDmList);
	protected abstract void removeActual(List<MessageDm> messageDmList);
	
	public void setCacheConnectAgentConfParam(CacheConnectAgentConfParam cacheConnectAgentConfParam) {
		this.cacheConnectAgentConfParam = cacheConnectAgentConfParam;
	}
}
