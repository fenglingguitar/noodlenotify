package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;

public class JettyNetConnectAgentFactory extends ConnectAgentFactoryAbstract {

	private int timeout = 3000;
	
	private String url = "/noodlenotify";
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		return createConnectAgent(ip, port, url, connectId);
	}

	@Override
	public ConnectAgent createConnectAgent(String ip, int port, String url,
			long connectId) {
		return new JettyNetConnectAgent(ip, port, url, connectId, timeout);
	}
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, String url,
			long connectId, int timeout) {
		this.timeout = timeout > 0 ? timeout : this.timeout;
		return new JettyNetConnectAgent(ip, port, url, connectId, this.timeout);
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
}
