package org.fl.noodlenotify.core.connect;

public interface ConnectAgentFactory {
	public ConnectAgent createConnectAgent(String ip, int port, long connectId);
	public ConnectAgent createConnectAgent(String ip, int port, String url, long connectId);
	public ConnectAgent createConnectAgent(String ip, int port, String url, long connectId, int timeout);
}
