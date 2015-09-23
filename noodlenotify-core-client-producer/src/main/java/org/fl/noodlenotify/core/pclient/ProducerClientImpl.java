package org.fl.noodlenotify.core.pclient;

import java.net.InetAddress;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionNothingQueueException;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionTimeoutException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.manager.ProducerNetConnectManager;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class ProducerClientImpl implements ProducerClient {
	
	private final static Logger logger = LoggerFactory.getLogger(ProducerClientImpl.class);

	protected ConnectManager netConnectManager;
	
	protected ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String producerClientName;
	private long moduleId;
	private String localIp;
	private int checkPort;
	private String checkUrl;
	private String checkType;
	
	public ProducerClientImpl() {
	}
	
	public ProducerClientImpl(ConnectAgentFactory connectAgentFactory) {
		netConnectManager = new ProducerNetConnectManager(connectAgentFactory);
	}
	
	public void start() throws Exception {
		
		if (producerClientName == null || 
				(producerClientName != null && producerClientName.equals("hostname"))) {
			producerClientName = InetAddress.getLocalHost().getHostName();
		}
		localIp = localIp == null ? InetAddress.getLocalHost().getHostAddress() : localIp;
		moduleId = consoleRemotingInvoke.producerRegister(localIp, checkPort, checkUrl, checkType, producerClientName);		

		netConnectManager.setModuleId(moduleId);
		netConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		netConnectManager.start();
	}

	public void destroy() throws Exception {
		
		consoleRemotingInvoke.producerCancel(moduleId);
		netConnectManager.destroy();
	}
	
	@Override
	public String send(String queueName, String content) throws Exception {
		
		Message message = new Message();
		message.setQueueName(queueName);
		String uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
		message.setUuid(uuid);
		message.setContent(content);
		
		QueueAgent queueAgent = netConnectManager.getQueueAgent(queueName);
		if (queueAgent == null) {
			logger.error("Send -> "
					+ "Queue: " + message.getQueueName()
					+ ", UUID: " + message.getUuid()
					+ ", Get QueueAgent -> Null");
			throw new ConnectionNothingQueueException("Producer Client Send -> Not have this queue");
		}
		
		NetConnectAgent netConnectAgent = null;		
		do {
			netConnectAgent = (NetConnectAgent) queueAgent.getConnectAgent();
			if (netConnectAgent != null) {
				try {
					netConnectAgent.send(message);
					break;
				} catch (ConnectionUnableException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Send -> "
								+ "Queue: " + message.getQueueName()
								+ ", UUID: " + message.getUuid()
								+ ", Connect: " + ((ConnectAgent)netConnectAgent).getConnectId()
								+ ", Send Message -> " + e);
					}
					continue;
				} catch (ConnectionResetException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Send -> "
								+ "Queue: " + message.getQueueName()
								+ ", UUID: " + message.getUuid()
								+ ", Connect: " + ((ConnectAgent)netConnectAgent).getConnectId()
								+ ", Send Message -> " + e);
					}
					continue;
				} catch (ConnectionTimeoutException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Send -> "
								+ "Queue: " + message.getQueueName()
								+ ", UUID: " + message.getUuid()
								+ ", Connect: " + ((ConnectAgent)netConnectAgent).getConnectId()
								+ ", Send Message -> " + e);
					}
					continue;
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("Send -> "
								+ "Queue: " + message.getQueueName()
								+ ", UUID: " + message.getUuid()
								+ ", Connect: " + ((ConnectAgent)netConnectAgent).getConnectId()
								+ ", Send Message -> " + e);
					}
					throw e;
				}
			} else {
				netConnectManager.startUpdateConnectAgent();
				if (logger.isErrorEnabled()) {
					logger.error("NoodleNotify have lose Message -> "
								+ "Queue: " + message.getQueueName()
								+ ", Content: " + message.getContent()
								+ ", UUID: " + message.getUuid()
								+ " " );
				}
				throw new ConnectionRefusedException("Producer Client Send -> Connection refused by all the net connect agent");
			}
		} while (netConnectAgent != null);
		
		return uuid;
	}

	public void setNetConnectManager(ConnectManager netConnectManager) {
		this.netConnectManager = netConnectManager;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setProducerClientName(String producerClientName) {
		this.producerClientName = producerClientName;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public void setCheckPort(int checkPort) {
		this.checkPort = checkPort;
	}

	public void setCheckUrl(String checkUrl) {
		this.checkUrl = checkUrl;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
}
