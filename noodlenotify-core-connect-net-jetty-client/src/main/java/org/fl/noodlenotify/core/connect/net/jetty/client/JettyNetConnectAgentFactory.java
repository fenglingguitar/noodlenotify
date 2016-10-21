package org.fl.noodlenotify.core.connect.net.jetty.client;

import org.fl.noodle.common.connect.agent.AbstractConnectAgentFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;

public class JettyNetConnectAgentFactory extends AbstractConnectAgentFactory {

	@Override
	public ConnectAgent createConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		return new JettyNetConnectAgent(
				connectId, ip, port, url,
				connectTimeout, readTimeout, encoding, invalidLimitNum,
				connectDistinguish, methodInterceptorList);
	}	
}
