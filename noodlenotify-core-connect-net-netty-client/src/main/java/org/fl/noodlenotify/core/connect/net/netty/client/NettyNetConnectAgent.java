package org.fl.noodlenotify.core.connect.net.netty.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.core.connect.ConnectAgentAbstract;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.exception.ConnectionTimeoutException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectConstant;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.netty.client.exception.NettyConnectionException;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class NettyNetConnectAgent extends ConnectAgentAbstract implements NetConnectAgent, NetStatusChecker  {

	private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectAgent.class);

	private NettyNetConnectPool nettyNetConnectPool;
	
	private NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam;
	
	public NettyNetConnectAgent(String ip, int port, long connectId,
			NettyNetConnectPoolConfParam nettyNetConnectPoolConfParam) {
		super(ip, port, connectId, NetConnectConstant.NETCONNECT_TYPE_NETTY);
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

		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the net netty connect agent");
		}

		NettyNetConnect nettyNetConnect = getConnect();
		
		String uuid = null;
		
		try {
			uuid = nettyNetConnect.send(message);
		} catch (NettyConnectionException e) { 
			connectStatus.set(false);
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
			connectStatus.set(false);
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
			connectStatus.set(false);
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
			connectStatus.set(false);
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
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the net netty connect agent");
		}
		
		NettyNetConnect nettyNetConnect = getConnect();

		try {
			nettyNetConnect.send("CheckHealth");
		} catch (NettyConnectionException e) { 
			connectStatus.set(false);
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
			connectStatus.set(false);
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
			connectStatus.set(false);
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
			connectStatus.set(false);
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
			connectStatus.set(false);
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
}
