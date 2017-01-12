package org.fl.noodlenotify.core.connect.net.jetty.client;

import java.io.IOException;

import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodle.common.connect.exception.ConnectStopException;
import org.fl.noodle.common.connect.exception.ConnectTimeoutException;
import org.fl.noodle.common.net.http.HttpConnect;
import org.fl.noodle.common.net.http.jdk.HttpConnectJdk;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.status.AbstractStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyNetStatusChecker extends AbstractStatusChecker implements NetStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(JettyNetStatusChecker.class);
	
	private HttpConnect httpConnect;
	
	public JettyNetStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding) {
		super(
			connectId, ip, port, url, ConnectAgentType.NET_HTTP.getCode(),
			connectTimeout, readTimeout, encoding);
	}

	@Override
	public void connect() throws Exception {
		String fullUrl = new StringBuilder("http://").append(ip).append(":").append(port).append(url).toString();
		httpConnect = new HttpConnectJdk(fullUrl, connectTimeout, readTimeout, encoding);
		try {
			httpConnect.connect();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create net jetty connect agent");
		} 
	}
	
	@Override
	public void close() {
		httpConnect.close();
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
		return NetStatusChecker.class;
	}
}
