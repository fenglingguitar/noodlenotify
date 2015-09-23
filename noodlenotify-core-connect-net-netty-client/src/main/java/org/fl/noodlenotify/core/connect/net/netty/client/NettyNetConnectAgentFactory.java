package org.fl.noodlenotify.core.connect.net.netty.client;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;

public class NettyNetConnectAgentFactory extends ConnectAgentFactoryAbstract {

	private NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam 
				= new NettyNetConnectPoolConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		return new NettyNetConnectAgent(ip, port, connectId, nettyNetConnectPoolConfParam);
	}

	public void setNettyNetConnectPoolConfParam(
			NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}
}
