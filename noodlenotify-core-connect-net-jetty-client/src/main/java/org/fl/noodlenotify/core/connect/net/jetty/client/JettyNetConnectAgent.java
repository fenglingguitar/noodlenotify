package org.fl.noodlenotify.core.connect.net.jetty.client;

import java.io.IOException;
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
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyNetConnectAgent extends AbstractConnectAgent implements NetConnectAgent, NetStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectAgent.class);
	
	JettyNetConnect jettyNetConnect;
	
	public JettyNetConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList) {
		super(
			connectId, ip, port, url, NetConnectAgentType.HTTP.getCode(),
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
	}

	@Override
	public void connectActual() throws Exception {
		
		jettyNetConnect = new JettyNetConnect(ip, port, url, connectTimeout);
		
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
		return send(message, readTimeout);
	}
	
	@Override
	public String send(Message message, int readTimeout) throws Exception {
		
		String uuid = null;
		
		try {
			uuid = jettyNetConnect.send(message, readTimeout);
		} catch (java.net.ConnectException e) { 
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
		
		try {
			jettyNetConnect.send("CheckHealth", readTimeout);
		} catch (java.net.ConnectException e) { 
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
	
	@Override
	protected Class<?> getServiceInterfaces() {
		return NetConnectAgent.class;
	}
}
