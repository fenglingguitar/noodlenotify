package org.fl.noodlenotify.core.connect.cache.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.QueueAgentAbstract;

public class QueueCacheQueueAgent extends QueueAgentAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueCacheQueueAgent.class);
	
	public QueueCacheQueueAgent(String queueName) {
		super(queueName);
	}

	@Override
	public ConnectAgent getConnectAgent() {
		
		if (queueAgentArrayList.size() > 0) {
			try {
				ConnectAgent connectAgent = queueAgentArrayList.get(0);
				QueueCacheConnectAgent queueCacheConnectAgent
						= (QueueCacheConnectAgent) connectAgent;
				try {
					if (connectAgent.getConnectStatus() == true) {
						if (queueCacheConnectAgent.isActive(queueName)) {
							return connectAgent;
						}
					} 
				} catch (Exception e) {
					queueAgentArrayList.remove(connectAgent);
					if (logger.isErrorEnabled()) {
						logger.error("GetConnectAgent -> Get First IsActive -> " + e);
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			}
		}
		
		ConnectAgent connectAgentMain = null;
		
		for (ConnectAgent connectAgent : queueAgentArrayList) {
			QueueCacheConnectAgent queueCacheConnectAgent
					= (QueueCacheConnectAgent) connectAgent;
			try {
				if (connectAgent.getConnectStatus() == true) {
					if (queueCacheConnectAgent.isActive(queueName)) {
						connectAgentMain = connectAgent;
						queueAgentArrayList.remove(connectAgent);
						queueAgentArrayList.add(0, connectAgent);
						break;
					} 
				} else {
					queueAgentArrayList.remove(connectAgent);
				}
			} catch (Exception e) {
				queueAgentArrayList.remove(connectAgent);
				if (logger.isErrorEnabled()) {
					logger.error("GetConnectAgent -> Iteration IsActive: " + e);
				}
			}
		}
		
		return connectAgentMain;
	}
}
