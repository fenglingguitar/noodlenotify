package org.fl.noodlenotify.core.connect;

public abstract class ConnectAgentFactoryAbstract implements ConnectAgentFactory {

	@Override
	public ConnectAgent createConnectAgent(String ip, int port, String url,
			long connectId) {
		return createConnectAgent(ip, port, connectId);
	}

	@Override
	public ConnectAgent createConnectAgent(String ip, int port, String url,
			long connectId, int timeout) {
		return createConnectAgent(ip, port, connectId);
	}
}
