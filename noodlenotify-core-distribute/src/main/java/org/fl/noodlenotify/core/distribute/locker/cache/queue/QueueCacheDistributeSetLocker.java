package org.fl.noodlenotify.core.distribute.locker.cache.queue;

import java.util.ArrayList;
import java.util.List;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.distribute.locker.DistributeSetLockerAbstract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueueCacheDistributeSetLocker extends DistributeSetLockerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueCacheDistributeSetLocker.class);

	private ConnectManager queueCacheConnectManager;
	
	public QueueCacheDistributeSetLocker(String queueName, long distributeId, ConnectManager queueCacheConnectManager) {
		super(queueName, distributeId);
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public QueueCacheDistributeSetLocker(String queueName, long distributeId, long sleepTime, long delayTime, ConnectManager queueCacheConnectManager) {
		super(queueName, distributeId, sleepTime, delayTime);
		this.queueCacheConnectManager = queueCacheConnectManager;
	}

	@Override
	protected long getDiffTime() {
		return 0;
	}

	@Override
	protected boolean getAlive() {
		long intervalTime = sleepTime + delayTime;
		boolean isAlive = false;
		ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null) {
			List<ConnectAgent> queueCacheConnectAgentList = (List<ConnectAgent>) connectNode.getConnectAgentList();
			List<QueueCacheConnectAgent> queueCacheConnectAgentBackList = new ArrayList<QueueCacheConnectAgent>(queueCacheConnectAgentList.size());
			for (ConnectAgent ConnectAgent : queueCacheConnectAgentList) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) ConnectAgent;
				try {
					isAlive = queueCacheConnectAgent.getAlive(queueName, distributeId, diffTime, intervalTime);
					if (isAlive == false) {
						for (QueueCacheConnectAgent queueCacheConnectAgentBack : queueCacheConnectAgentBackList) {
							try {
								queueCacheConnectAgentBack.releaseAlive(queueName, distributeId);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("GetAlive -> "
											+ "QUEUE: " + queueName
											+ ", DistributeId: " + distributeId
											+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
											+ ", Queue Redis Get Alive False Release Alive -> " + e);
								}
							}
						}
						break;
					} else {
						queueCacheConnectAgentBackList.add(queueCacheConnectAgent);						
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("GetAlive -> "
								+ "QUEUE: " + queueName
								+ ", DistributeId: " + distributeId
								+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
								+ ", IntervalTime: " + intervalTime
								+ ", Queue Redis Get Alive -> " + e);
					}
					for (QueueCacheConnectAgent queueCacheConnectAgentBack : queueCacheConnectAgentBackList) {
						try {
							queueCacheConnectAgentBack.releaseAlive(queueName, distributeId);
						} catch (Exception e1) {
							if (logger.isErrorEnabled()) {
								logger.error("GetAlive -> "
										+ "QUEUE: " + queueName
										+ ", DistributeId: " + distributeId
										+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
										+ ", Queue Redis Get Alive Error Release Alive -> " + e1);
							}
						}
					}
					break;
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> "
						+ "QUEUE: " + queueName 
						+ ", DistributeId: " + distributeId
						+ ", Queue Redis Get Alive -> Get Queue Agent -> Null");
			}
		}
		
		if (isAlive == true) {
			if (logger.isDebugEnabled()) {
				logger.debug("GetAlive -> " 
							+ "Queue: " + queueName
							+ ", DistributeId: " + distributeId
							+ ", Become Queue Active Master");
			}
		}
		return isAlive;
	}

	@Override
	protected boolean keepAlive() {
		long intervalTime = sleepTime + delayTime;
		boolean isAlive = false;
		ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null) {
			List<ConnectAgent> queueCacheConnectAgentList = (List<ConnectAgent>) connectNode.getConnectAgentList();
			List<QueueCacheConnectAgent> queueCacheConnectAgentBackList = new ArrayList<QueueCacheConnectAgent>(queueCacheConnectAgentList.size());
			for (ConnectAgent ConnectAgent : queueCacheConnectAgentList) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) ConnectAgent;
				try {
					isAlive = queueCacheConnectAgent.keepAlive(queueName, distributeId, diffTime, intervalTime);
					if (isAlive == false) {
						for (QueueCacheConnectAgent queueCacheConnectAgentBack : queueCacheConnectAgentBackList) {
							try {
								queueCacheConnectAgentBack.releaseAlive(queueName, distributeId);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("KeepAlive -> "
											+ "QUEUE: " + queueName
											+ ", DistributeId: " + distributeId
											+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
											+ ", Queue Redis Keep Alive False Release Alive -> " + e);
								}
							}
						}
						break;
					} else {
						queueCacheConnectAgentBackList.add(queueCacheConnectAgent);						
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("KeepAlive -> "
								+ "QUEUE: " + queueName
								+ ", DistributeId: " + distributeId
								+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
								+ ", IntervalTime: " + intervalTime
								+ ", Queue Redis Keep Alive -> " + e);
					}
					for (QueueCacheConnectAgent queueCacheConnectAgentBack : queueCacheConnectAgentBackList) {
						try {
							queueCacheConnectAgentBack.releaseAlive(queueName, distributeId);
						} catch (Exception e1) {
							if (logger.isErrorEnabled()) {
								logger.error("KeepAlive -> "
										+ "QUEUE: " + queueName
										+ ", DistributeId: " + distributeId
										+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
										+ ", Queue Redis Keep Alive Error Release Alive -> " + e1);
							}
						}
					}
					break;
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> "
						+ "QUEUE: " + queueName 
						+ ", DistributeId: " + distributeId
						+ ", Queue Redis Keep Alive -> Get Queue Agent -> Null");
			}
		}
		
		return isAlive;
	}

	@Override
	protected void releaseAlive() {
		ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null) {
			List<ConnectAgent> queueCacheConnectAgentList = (List<ConnectAgent>) connectNode.getConnectAgentList();
			for (ConnectAgent ConnectAgent : queueCacheConnectAgentList) {
				QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) ConnectAgent;
				try {
					queueCacheConnectAgent.releaseAlive(queueName, distributeId);
				} catch (Exception e1) {
					if (logger.isErrorEnabled()) {
						logger.error("KeepAlive -> "
								+ "QUEUE: " + queueName
								+ ", DistributeId: " + distributeId
								+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
								+ ", Queue Redis Release Alive -> " + e1);
					}
				}
			}
		}
	}
}
