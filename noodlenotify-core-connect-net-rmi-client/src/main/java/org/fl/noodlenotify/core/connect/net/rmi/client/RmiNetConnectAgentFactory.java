package org.fl.noodlenotify.core.connect.net.rmi.client;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;

public class RmiNetConnectAgentFactory extends ConnectAgentFactoryAbstract {

	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		return new RmiNetConnectAgent(ip, port, connectId);
	}
}
