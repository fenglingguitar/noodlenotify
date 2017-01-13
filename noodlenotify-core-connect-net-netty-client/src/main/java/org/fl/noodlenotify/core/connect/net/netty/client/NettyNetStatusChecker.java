package org.fl.noodlenotify.core.connect.net.netty.client;

import java.io.IOException;

import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodle.common.connect.exception.ConnectStopException;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodle.common.net.socket.SocketConnect;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyConnectionException;
import org.fl.noodlenotify.core.status.AbstractStatusChecker;

public class NettyNetStatusChecker extends AbstractStatusChecker implements NetStatusChecker  {

	//private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectAgent.class);
	
	private SocketConnect socketConnect;
	
	public NettyNetStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding) {
		super(
			connectId, ip, port, url, ConnectAgentType.NET_NETTY.getCode(),
			connectTimeout, readTimeout, encoding);
	}

	@Override
	public void connect() throws Exception {
		try {
			socketConnect = new SocketConnect(ip, port, connectTimeout, readTimeout, encoding);
		} catch (NettyConnectionException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create net netty connect agent");
		} 
	}

	@Override
	public void close() {
		try {
			socketConnect.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void checkHealth() throws Exception {
		try {
			socketConnect.send("/check/health", "CheckHealth", readTimeout);
		} catch (NettyConnectionException e) { 
			e.printStackTrace();
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketException e) { 
			e.printStackTrace();
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			e.printStackTrace();
			throw new ConnectTimeoutException("Connection timeout for send by net netty connect agent");
		} catch (ConnectStopException e) { 
			e.printStackTrace();
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (Exception e) { 
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return NetStatusChecker.class;
	}
}
