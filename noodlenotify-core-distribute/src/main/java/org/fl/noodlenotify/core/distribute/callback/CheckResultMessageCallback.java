package org.fl.noodlenotify.core.distribute.callback;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.common.pojo.db.AbstractMessageCallback;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;

public class CheckResultMessageCallback extends AbstractMessageCallback {

	private ConnectManager queueCacheConnectManager;
	private ConnectManager bodyCacheConnectManager;
	
	public CheckResultMessageCallback(
			MessageDb messageDb,
			ConnectManager queueCacheConnectManager,
			ConnectManager bodyCacheConnectManager
			) {
		this.messageDb = messageDb;
		this.bodyCacheConnectManager = bodyCacheConnectManager;
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	@Override
	public Object execute() throws Exception {
		checkUpdateResult();
		return null;
	}
	
	private void checkUpdateResult() {
		if (messageDb.getResult() == false) {
			removeQueue();
		}
		if (messageDb.getStatus() == MessageConstant.MESSAGE_STATUS_FINISH) {
			removeBody();
		} else {
			removeQueue();
		}
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
	
	private void removeBody() {
		ConnectCluster bodyConnectCluster = bodyCacheConnectManager.getConnectCluster("PARTALL");
		BodyCacheConnectAgent bodyCacheConnectAgentOne = (BodyCacheConnectAgent) bodyConnectCluster.getProxy();
		ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDb);
		try {
			bodyCacheConnectAgentOne.remove(messageDb);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
		}
	}

	public void setQueueCacheConnectManager(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public void setBodyCacheConnectManager(ConnectManager bodyCacheConnectManager) {
		this.bodyCacheConnectManager = bodyCacheConnectManager;
	}	
}
