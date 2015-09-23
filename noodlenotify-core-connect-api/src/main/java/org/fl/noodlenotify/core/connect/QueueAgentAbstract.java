package org.fl.noodlenotify.core.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class QueueAgentAbstract implements QueueAgent {
	
	private final static Logger logger = LoggerFactory.getLogger(QueueAgentAbstract.class);
	
	protected String queueName; 

	protected ConcurrentMap<Long, QueueAgent> queueAgentMap = new ConcurrentHashMap<Long, QueueAgent>();
	protected CopyOnWriteArrayList<ConnectAgent> queueAgentArrayList = new CopyOnWriteArrayList<ConnectAgent>();
	
	public QueueAgentAbstract(String queueName) {
		this.queueName = queueName;
	}
	
	@Override
	public ConnectAgent getConnectAgent() {
		
		ConnectAgent connectAgent = null;
		
		int queueAgentArrayListSize = 0;
		
		while ((queueAgentArrayListSize = queueAgentArrayList.size()) > 0) {
			
			int index = (int) Math.round(Math.random() * (queueAgentArrayListSize - 1));
			try {
				connectAgent = (ConnectAgentAbstract) queueAgentArrayList.get(index);
				if (connectAgent.getConnectStatus() == false) {
					queueAgentArrayList.remove(connectAgent);
					connectAgent = null;
				} else {
					break;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}
		
		return connectAgent;
	}
	
	@Override
	public ConnectAgent getConnectAgentOther(ConnectAgent connectAgent) {
		
		ConnectAgent connectAgentOther = null;
		int queueAgentArrayListSize = 0;
		
		while ((queueAgentArrayListSize = queueAgentArrayList.size()) > 0) {
			
			int index = (int) Math.round(Math.random() * (queueAgentArrayListSize - 1));
			try {
				connectAgentOther = (ConnectAgentAbstract) queueAgentArrayList.get(index);
				if (connectAgentOther.getConnectStatus() == false) {
					queueAgentArrayList.remove(connectAgentOther);
					connectAgentOther = null;
				} else {
					if (connectAgentOther == connectAgent) {
						if (queueAgentArrayListSize == 1) {
							connectAgentOther = null;
							break;
						} else {
							index = (index + 1) % queueAgentArrayListSize;
							connectAgentOther = (ConnectAgentAbstract) queueAgentArrayList.get(index);
							if (connectAgentOther.getConnectStatus() == false) {
								queueAgentArrayList.remove(connectAgentOther);
								connectAgentOther = null;
							} else {
								break;
							}
						}
					} else {						
						break;
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				continue;
			}
		}
		
		return connectAgentOther;
	}
	
	@Override
	public List<ConnectAgent> getConnectAgentAll() {
		return queueAgentArrayList;
	}
	
	@Override
	public List<ConnectAgent> updateConnectAgents(List<ConnectAgent> connectAgentList) {
		
		List<ConnectAgent> connectAgentListNew = new ArrayList<ConnectAgent>(connectAgentList.size());
		
		for (ConnectAgent connectAgent : connectAgentList) {
			if (!queueAgentArrayList.contains(connectAgent)) {
				connectAgentListNew.add(connectAgent);
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgents -> Add Connect -> " 
							+ "QueueName: " + queueName
							+ ", ConnectId: " + connectAgent.getConnectId()
							+ ", Ip: " + connectAgent.getIp()
							+ ", Port: " + connectAgent.getPort()
							);
				}
			}
		}
		
		queueAgentArrayList.addAllAbsent(connectAgentList);
		for (ConnectAgent connectAgent : queueAgentArrayList) {
			if (!connectAgentList.contains(connectAgent)) {
				queueAgentArrayList.remove(connectAgent);
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgents -> Remove Connect -> " 
							+ "QueueName: " + queueName
							+ ", ConnectId: " + connectAgent.getConnectId()
							+ ", Ip: " + connectAgent.getIp()
							+ ", Port: " + connectAgent.getPort()
							);
				}
			}
		}
		
		return connectAgentListNew;
	}
	
	@Override
	public void addChildQueueAgent(long key, QueueAgent queueAgent) {
		queueAgentMap.put(key, queueAgent);
	}
	
	@Override
	public QueueAgent getChildQueueAgent(long key) {
		return queueAgentMap.get(key);
	}
	
	@Override
	public ConcurrentMap<Long, QueueAgent> getChildQueueAgentMap() {
		return queueAgentMap;
	}
	
	@Override
	public String getQueueName() {
		return this.queueName;
	}
}
