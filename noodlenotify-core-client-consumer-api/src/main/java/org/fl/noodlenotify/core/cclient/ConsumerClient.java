package org.fl.noodlenotify.core.cclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.core.connect.exception.ConnectionInvokeException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;

public class ConsumerClient implements NetConnectReceiver {
	
	private String customerGroupName;
	
	private Map<String, ConsumerReceiver> consumerReceiverMap
						= new HashMap<String, ConsumerReceiver>();
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	private String consumerClientName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type;
	private int checkPort;
	private String checkUrl;
	private String checkType;
	
	private volatile boolean stopSign = false;
	
	public void start() throws Exception {
		
		if (consumerClientName == null || 
				(consumerClientName != null && consumerClientName.equals("hostname"))) {
			consumerClientName = NetAddressUtil.getLocalHostName();
		}		
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.customerRegister(localIp, localPort, url, type, checkPort,
					checkUrl, checkType, consumerClientName, customerGroupName, 
					new ArrayList<String>(consumerReceiverMap.keySet()));
		
	}
	
	public void destroy() throws Exception {
		consoleRemotingInvoke.customerCancel(moduleId);
		stopSign = true;
	}
	
	@Override
	public void receive(Message message) throws Exception {
		
		if (stopSign) {
			throw new ConnectionStopException("Exchange is stopping");
		}
		
		ConsumerReceiver consumerReceiver = consumerReceiverMap.get(message.getQueueName());
		if (consumerReceiver != null) {
			if(!consumerReceiver.receive(message)) {
				throw new ConnectionInvokeException("Consumer receiver consume message return false");
			}
		} else {
			throw new ConnectionInvokeException("Not have this queue consumer receiver");
		}
	}
	
	public void setConsumerReceiverMap(Map<String, ConsumerReceiver> consumerReceiverMap) {
		this.consumerReceiverMap = consumerReceiverMap;
	}
	
	public void setCustomerGroupName(String customerGroupName) {
		this.customerGroupName = customerGroupName;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setConsumerClientName(String consumerClientName) {
		this.consumerClientName = consumerClientName;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(String type) {
		this.type = type;
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
