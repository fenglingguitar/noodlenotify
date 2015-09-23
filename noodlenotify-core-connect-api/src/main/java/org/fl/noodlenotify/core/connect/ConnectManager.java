package org.fl.noodlenotify.core.connect;

import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;

public interface ConnectManager {
	public void start() throws Exception;
	public void destroy() throws Exception;
	public void startUpdateConnectAgent();
	public QueueAgent getQueueAgent(String queueName);
	public ConnectAgent getConnectAgent(long ConnectId);
	public void setModuleId(long moduleId);
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke);
}
