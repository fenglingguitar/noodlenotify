package org.fl.noodlenotify.core.distribute.locker.cache.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.distributedlock.api.LockChangeHandler;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;

public class CheckActiveLockChangeHandler implements LockChangeHandler {

	private String queueName;
	private ConnectManager queueCacheConnectManager;
	
	private volatile boolean stopSign = false;
	
	private ExecutorService executorService;
	
	public CheckActiveLockChangeHandler(String queueName, ConnectManager queueCacheConnectManager) {
		this.queueName = queueName;
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	@Override
	public void onMessageGetLock() {
		stopSign = false;
		executorService = Executors.newCachedThreadPool();
		executorService.execute(new CheckActiveRunnable());
	}

	@Override
	public void onMessageLossLock() {
		stopSign = true;
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReleaseLock() {
		onMessageLossLock();
	}

	@Override
	public void onMessageStart() {
	}

	@Override
	public void onMessageStop() {
	}
	
	private class CheckActiveRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				if (stopSign) {
					break;
				}
				
				setActive();
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setActive() {
		ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null && connectNode.getHealthyConnectAgentList().size() > 0) {
			try {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectNode.getHealthyConnectAgentList().get(0);
				if (queueCacheConnectAgent != null && !queueCacheConnectAgent.isActive(queueName)) {
					queueCacheConnectAgent.setActive(queueName, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	
	public boolean waitActiveReady() {
		for (int i=0; i<10; i++) {
			ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
			if (connectNode != null && connectNode.getHealthyConnectAgentList().size() > 0) {
				try {
					QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectNode.getHealthyConnectAgentList().get(0);
					if (queueCacheConnectAgent != null && queueCacheConnectAgent.isActive(queueName)) {
						return true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		return false;
	}
}
