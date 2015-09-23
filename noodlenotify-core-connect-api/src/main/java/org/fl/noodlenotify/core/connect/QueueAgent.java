package org.fl.noodlenotify.core.connect;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public interface QueueAgent {
	public ConnectAgent getConnectAgent();
	public ConnectAgent getConnectAgentOther(ConnectAgent connectAgent);
	public List<ConnectAgent> getConnectAgentAll();
	public List<ConnectAgent> updateConnectAgents(List<ConnectAgent> connectAgentList);
	public void addChildQueueAgent(long key, QueueAgent queueAgent);
	public QueueAgent getChildQueueAgent(long key);
	public ConcurrentMap<Long, QueueAgent> getChildQueueAgentMap();
	public String getQueueName();
}
