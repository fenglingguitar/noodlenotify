package org.fl.noodlenotify.core.connect.net.netty.client;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.AbstractNetConnectAgent;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodle.common.connect.exception.ConnectSerializeException;
import org.fl.noodle.common.connect.exception.ConnectStopException;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodle.common.net.socket.SocketConnect;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyConnectionException;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class NettyNetConnectAgent extends AbstractNetConnectAgent implements NetConnectAgent, NetStatusChecker  {

	//private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectAgent.class);

	private NettyNetConnectPool nettyNetConnectPool;
	
	private NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam;
	
	public NettyNetConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList, 
			NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		super(
			connectId, ip, port, url, ConnectAgentType.NET_NETTY.getCode(),
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}

	@Override
	public void connectActual() throws Exception {

		nettyNetConnectPool = new NettyNetConnectPool(ip, port, connectTimeout, readTimeout, encoding, nettyNetConnectPoolConfParam);
		
		try {
			SocketConnect socketConnect = nettyNetConnectPool.getResource();
			nettyNetConnectPool.returnResource(socketConnect);
		} catch (NettyConnectionException e) {
			e.printStackTrace();
			nettyNetConnectPool.destroy();
			throw new ConnectRefusedException("Connection refused for create net netty connect agent");
		} 
	}

	@Override
	public void reconnectActual() throws Exception {

		try {
			SocketConnect socketConnect = nettyNetConnectPool.getResource();
			nettyNetConnectPool.returnResource(socketConnect);
		} catch (NettyConnectionException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create net netty connect agent");
		} 
	}

	@Override
	public void closeActual() {
		nettyNetConnectPool.destroy();
	}

	@Override
	public String send(Message message) throws Exception {
		return send(message, readTimeout);
	}
	
	@Override
	public String send(Message message, int readTimeout) throws Exception {

		String serializationString = null;
		try {
			serializationString = connectSerialize.serializationToString(message);
		} catch (Exception e) { 
			e.printStackTrace();
			throw new ConnectSerializeException("object serialization to string fail");
		}
		
		SocketConnect socketConnect = getConnect();
		
		String deserializationString = null;
		
		try {
			deserializationString = socketConnect.send("notify", serializationString, readTimeout);
		} catch (NettyConnectionException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectTimeoutException("Connection timeout for send by net netty connect agent");
		} catch (ConnectStopException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (Exception e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw e;
		}
		
		nettyNetConnectPool.returnResource(socketConnect);
		
		MessageResult messageResult = connectSerialize.deserializationFromString(deserializationString, MessageResult.class);
		if (messageResult.getResult() == false) {
			throw new ConnectInvokeException(messageResult.getError());
		}
		
		try {
			return connectSerialize.deserializationFromString(deserializationString, MessageResult.class).getUuid();
		} catch (Exception e) { 
			throw new ConnectSerializeException("object deserialization from string fail");
		}
	}

	@Override
	public void checkHealth() throws Exception {
		
		SocketConnect socketConnect = getConnect();

		try {
			socketConnect.send("health", "CheckHealth", readTimeout);
		} catch (NettyConnectionException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectTimeoutException("Connection timeout for send by net netty connect agent");
		} catch (ConnectStopException e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		} catch (Exception e) { 
			e.printStackTrace();
			nettyNetConnectPool.returnBrokenResource(socketConnect);
			throw e;
		}
		
		nettyNetConnectPool.returnResource(socketConnect);
	}
	
	private SocketConnect getConnect() throws Exception {
		try {
			SocketConnect socketConnect = nettyNetConnectPool.getResource();
			return socketConnect;
		} catch (NettyConnectionException e) {
			e.printStackTrace();
			throw new ConnectResetException("Connection reset for send by net netty connect agent");
		}
	}
	
	public void setNettyNetConnectPoolConfParam(NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return NetConnectAgent.class;
	}
}
