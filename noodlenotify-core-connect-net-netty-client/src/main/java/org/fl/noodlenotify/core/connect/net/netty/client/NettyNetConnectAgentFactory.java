package org.fl.noodlenotify.core.connect.net.netty.client;

import org.fl.noodle.common.connect.agent.AbstractConnectAgentFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;

public class NettyNetConnectAgentFactory extends AbstractConnectAgentFactory {

	private NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam = new NettyNetConnectPoolConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		return new NettyNetConnectAgent(
				connectId, ip, port, url,
				connectTimeout, readTimeout, encoding, invalidLimitNum,
				connectDistinguish, methodInterceptorList, 
				nettyNetConnectPoolConfParam);
	}	

	public void setNettyNetConnectPoolConfParam(NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}
}
