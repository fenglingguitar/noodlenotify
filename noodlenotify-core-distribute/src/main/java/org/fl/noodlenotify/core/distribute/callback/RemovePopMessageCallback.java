package org.fl.noodlenotify.core.distribute.callback;

import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.AbstractMessageCallback;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;

public class RemovePopMessageCallback extends AbstractMessageCallback {

	private ConnectManager queueCacheConnectManager;
	
	public RemovePopMessageCallback(
			MessageDb messageDb,
			ConnectManager queueCacheConnectManager
			) {
		this.messageDb = messageDb;
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
			queueCacheConnectAgent.removePop(messageDb);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
}
