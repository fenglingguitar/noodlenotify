package org.fl.noodlenotify.core.distribute.callback;

import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.domain.message.AbstractMessageCallback;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public class PushQueueCacheMessageCallback extends AbstractMessageCallback {

	private ConnectManager queueCacheConnectManager;
	
	public PushQueueCacheMessageCallback(
			MessageDm messageDm,
			ConnectManager queueCacheConnectManager
			) {
		this.messageDm = messageDm;
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	@Override
	public Object execute() throws Exception {
		removeQueue();
		return null;
	}
	
	private void removeQueue() {
		ConnectCluster queueConnectCluster = queueCacheConnectManager.getConnectCluster("ALL");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent)queueConnectCluster.getProxy();
		try {
			queueCacheConnectAgent.removePop(messageDm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
}
