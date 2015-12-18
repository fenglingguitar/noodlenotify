package org.fl.noodlenotify.core.connect.net.jetty.client;

import java.io.IOException;

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
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class JettyNetConnectAgent extends ConnectAgentAbstract implements NetConnectAgent, NetStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectAgent.class);
	
	JettyNetConnect jettyNetConnect;

	private int timeout;
	
	public JettyNetConnectAgent(String ip, int port, String url, long connectId, int timeout) {
		super(ip, port, connectId, url, NetConnectConstant.NETCONNECT_TYPE_HTTP);
		this.timeout = timeout;
	}

	@Override
	public void connectActual() throws Exception {
		
		jettyNetConnect = new JettyNetConnect(ip, port, url, timeout);
		
		try {
			jettyNetConnect.connect();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectionRefusedException("Connection refused for create net jetty connect agent");
		} 
	}

	@Override
	public void reconnectActual() throws Exception {
		
		try {
			jettyNetConnect.connect();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectionRefusedException("Connection refused for create net jetty connect agent");
		} 
	}

	@Override
	public void closeActual() {
		jettyNetConnect.close();
	}

	@Override
	public String send(Message message) throws Exception {
		return send(message, timeout);
	}
	
	@Override
	public String send(Message message, int readTimeout) throws Exception {

		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the net jetty connect agent");
		}
		
		String uuid = null;
		
		try {
			uuid = jettyNetConnect.send(message, readTimeout);
		} catch (java.net.ConnectException e) { 
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
		} catch (java.io.FileNotFoundException e) { 
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
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
			throw new ConnectionResetException("Connection reset for send by net jetty connect agent");
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
			throw new ConnectionTimeoutException("Connection timeout for send by net jetty connect agent");
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
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
		} catch (Exception e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw e;
		}
		
		return uuid;
	}

	@Override
	public void checkHealth() throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the net jetty connect agent");
		}
		
		try {
			jettyNetConnect.send("CheckHealth", timeout);
		} catch (java.net.ConnectException e) { 
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
		} catch (java.io.FileNotFoundException e) { 
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
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
			throw new ConnectionResetException("Connection reset for send by net jetty connect agent");
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
			throw new ConnectionTimeoutException("Connection timeout for send by net jetty connect agent");
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
			throw new ConnectionResetException("Connection refused for send by net jetty connect agent");
		} catch (Exception e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw e;
		}
	}
	
	public int getTimeout() {
		return timeout;
	}
}
