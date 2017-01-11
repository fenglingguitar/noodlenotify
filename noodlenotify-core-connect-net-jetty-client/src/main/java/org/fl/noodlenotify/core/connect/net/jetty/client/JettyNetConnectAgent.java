package org.fl.noodlenotify.core.connect.net.jetty.client;

import java.io.IOException;
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
import org.fl.noodle.common.net.http.HttpConnect;
import org.fl.noodle.common.net.http.jdk.HttpConnectJdk;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyNetConnectAgent extends AbstractNetConnectAgent implements NetConnectAgent, NetStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectAgent.class);
	
	private HttpConnect httpConnect;
	
	private String inputName;
	
	public JettyNetConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, String inputName, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList) {
		super(
			connectId, ip, port, url, ConnectAgentType.NET_HTTP.getCode(),
			connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList);
		this.inputName = inputName;
	}

	@Override
	public void connectActual() throws Exception {
		
		String fullUrl = new StringBuilder("http://").append(ip).append(":").append(port).append(url).toString();
		httpConnect = new HttpConnectJdk(fullUrl, connectTimeout, readTimeout, encoding);
		
		try {
			httpConnect.connect();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectRefusedException("Connection refused for create net jetty connect agent");
		} 
	}

	@Override
	public void reconnectActual() throws Exception {
		
		try {
			httpConnect.connect();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectRefusedException("Connection refused for create net jetty connect agent");
		} 
	}

	@Override
	public void closeActual() {
		httpConnect.close();
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
			if (logger.isErrorEnabled()) {
				logger.error("invoke -> connectSerialize.serializationToString -> {} -> {} -> Exception:{}", this, message, e.getMessage());
			}
			throw new ConnectSerializeException("object serialization to string fail");
		}
		
		String deserializationString = null;
		try {
			deserializationString = httpConnect.postString(inputName, serializationString, readTimeout);
		} catch (java.net.ConnectException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
		} catch (java.io.FileNotFoundException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
		} catch (java.net.SocketException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection reset for send by net jetty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectTimeoutException("Connection timeout for send by net jetty connect agent");
		} catch (ConnectStopException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
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
		
		MessageResult messageResult = null;
		try {
			messageResult = connectSerialize.deserializationFromString(deserializationString, MessageResult.class);
		} catch (Exception e) { 
			throw new ConnectSerializeException("object deserialization from string fail");
		}
		
		if (messageResult.getResult() == false) {
			throw new ConnectInvokeException(messageResult.getError());
		}
		
		return messageResult.getUuid();
	}

	@Override
	public void checkHealth() throws Exception {
		
		try {
			httpConnect.postString("health", "CheckHealth", readTimeout);
		} catch (java.net.ConnectException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
		} catch (java.io.FileNotFoundException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
		} catch (java.net.SocketException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection reset for send by net jetty connect agent");
		} catch (java.net.SocketTimeoutException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectTimeoutException("Connection timeout for send by net jetty connect agent");
		} catch (ConnectStopException e) { 
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectResetException("Connection refused for send by net jetty connect agent");
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
