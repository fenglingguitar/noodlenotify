package org.fl.noodlenotify.core.connect.net.netty.client;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.agent.AbstractConnectAgent;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.exception.ConnectionTimeoutException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.constent.NetConnectAgentType;
import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyConnectionException;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyNetConnectAgent extends AbstractConnectAgent implements NetConnectAgent, NetStatusChecker  {

	private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectAgent.class);

	private NettyNetConnectPool nettyNetConnectPool;
	
	private NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam;
	
	public NettyNetConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList, 
			NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		super(
			connectId, ip, port, url, NetConnectAgentType.NETTY.getCode(),
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}

	@Override
	public void connectActual() throws Exception {

		nettyNetConnectPool = new NettyNetConnectPool(nettyNetConnectPoolConfParam, ip, port, nettyNetConnectPoolConfParam.getTimeout());
		
		try {
			NettyNetConnect nettyNetConnect = nettyNetConnectPool.getResource();
			nettyNetConnectPool.returnResource(nettyNetConnect);
		} catch (NettyConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			nettyNetConnectPool.destroy();
			throw new ConnectionRefusedException("Connection refused for create net netty connect agent");
		} 
	}

	@Override
	public void reconnectActual() throws Exception {

		try {
			NettyNetConnect nettyNetConnect = nettyNetConnectPool.getResource();
			nettyNetConnectPool.returnResource(nettyNetConnect);
		} catch (NettyConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectionRefusedException("Connection refused for create net netty connect agent");
		} 
	}

	@Override
	public void closeActual() {
		nettyNetConnectPool.destroy();
	}

	@Override
	public String send(Message message) throws Exception {
		return send(message, nettyNetConnectPoolConfParam.getTimeout());
	}
	
	@Override
	public String send(Message message, int readTimeout) throws Exception {

		NettyNetConnect nettyNetConnect = getConnect();
		
		String uuid = null;
		
		try {
			uuid = nettyNetConnect.send(message, readTimeout);
		} catch (NettyConnectionException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionTimeoutException("Connection timeout for send by net netty connect agent");
		} catch (ConnectionStopException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (Exception e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw e;
		}
		
		nettyNetConnectPool.returnResource(nettyNetConnect);
		
		return uuid;
	}

	@Override
	public void checkHealth() throws Exception {
		
		NettyNetConnect nettyNetConnect = getConnect();

		try {
			nettyNetConnect.send("CheckHealth", nettyNetConnectPoolConfParam.getTimeout());
		} catch (NettyConnectionException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionTimeoutException("Connection timeout for send by net netty connect agent");
		} catch (ConnectionStopException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		} catch (Exception e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			nettyNetConnectPool.returnBrokenResource(nettyNetConnect);
			throw e;
		}
		
		nettyNetConnectPool.returnResource(nettyNetConnect);
	}
	
	private NettyNetConnect getConnect() throws Exception {
		try {
			NettyNetConnect nettyNetConnect = nettyNetConnectPool.getResource();
			return nettyNetConnect;
		} catch (NettyConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetConnect -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Connect -> " + e);
			}
			throw new ConnectionResetException("Connection reset for send by net netty connect agent");
		}
	}
	
	public void setNettyNetConnectPoolConfParam(
			NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		this.nettyNetConnectPoolConfParam = nettyNetConnectPoolConfParam;
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return NetConnectAgent.class;
	}
}
