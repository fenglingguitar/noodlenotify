package org.fl.noodlenotify.core.connect.net.rmi.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import org.fl.noodlenotify.core.connect.ConnectAgentAbstract;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.exception.ConnectionTimeoutException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectConstant;
import org.fl.noodlenotify.core.connect.net.NetStatusChecker;
import org.fl.noodlenotify.core.connect.net.rmi.server.RmiNetConnectServer;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class RmiNetConnectAgent extends ConnectAgentAbstract implements NetConnectAgent, NetStatusChecker {
	
	private final static Logger logger = LoggerFactory.getLogger(RmiNetConnectAgent.class);

	private RmiNetConnectServer rmiNetConnectServer;
	
	private RmiProxyFactoryBean factory;
	
	public RmiNetConnectAgent(String ip, int port, long connectId) {
		super(ip, port, connectId, NetConnectConstant.NETCONNECT_TYPE_RMI);
	}

	@Override
	public void connectActual() throws Exception {
		
		factory = new RmiProxyFactoryBean();  
		
		try {
			factory.setServiceInterface(RmiNetConnectServer.class);  
	        factory.setServiceUrl("rmi://" + ip + ":" + String.valueOf(port) + "/rmiNetConnectServer");  
	        factory.setRefreshStubOnConnectFailure(false);
	        factory.setLookupStubOnStartup(true);
	        factory.afterPropertiesSet();  
		} catch (RemoteLookupFailureException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", AfterPropertiesSet -> " + e
						);
			}
			throw new ConnectionRefusedException("Rmi Net ConnectAgent ConnectActual -> Connection refused for create rmi net connect agent");
		}
        
		rmiNetConnectServer = (RmiNetConnectServer) factory.getObject();
	}

	@Override
	public void reconnectActual() throws Exception {
		
		try {
	        factory.afterPropertiesSet();  
		} catch (RemoteLookupFailureException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", AfterPropertiesSet -> " + e
						);
			}
			throw new ConnectionRefusedException("Rmi Net ConnectAgent ReconnectActual -> Connection refused for create rmi net connect agent");
		}
		
		rmiNetConnectServer = (RmiNetConnectServer) factory.getObject();
	}
	
	@Override
	public void closeActual() {
	}
	
	@Override
	public String send(Message message) throws Exception {
		return send(message, 0);
	}
	
	@Override
	public String send(Message message, int readTimeout) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the net rmi connect agent");
		}
		
		String uuid = null;
		
		try {
			uuid = rmiNetConnectServer.send(message);
		} catch (java.rmi.ConnectException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> "
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net rmi connect agent");
		} catch (RemoteLookupFailureException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection reset for send by net rmi connect agent");
		} catch (java.rmi.UnmarshalException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Send -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionTimeoutException("Connection timeout for send by net rmi connect agent");
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
			throw new ConnectionResetException("Connection refused for send by net rmi connect agent");
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
			throw new ConnectionUnableException("Connection disable for the net rmi connect agent");
		}
		
		try {
			rmiNetConnectServer.checkHealth();
		} catch (java.rmi.ConnectException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("checkHealth -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection refused for send by net rmi connect agent");
		} catch (RemoteLookupFailureException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("checkHealth -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionResetException("Connection reset for send by net rmi connect agent");
		} catch (java.rmi.UnmarshalException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("checkHealth -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Send -> " + e
						);
			}
			throw new ConnectionTimeoutException("Connection timeout for send by net rmi connect agent");
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
			throw new ConnectionResetException("Connection refused for send by net rmi connect agent");
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
}
