package org.fl.noodlenotify.core.cclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.server.ConnectServer;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class ConsumerClient implements NetConnectReceiver {
	
	private String consumerGroupName;
	
	private Map<String, ConsumerReceiver> consumerReceiverMap
						= new HashMap<String, ConsumerReceiver>();
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	private String consumerClientName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type;
	
	private ModuleRegister consumerModuleRegister;
	
	private ConnectServer connectServer;
	
	public void start() throws Exception {
		
		if (consumerClientName == null || 
				(consumerClientName != null && consumerClientName.equals("hostname"))) {
			consumerClientName = NetAddressUtil.getLocalHostName();
		}		
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		
		connectServer.start();
		
		moduleId = consoleRemotingInvoke.saveConsumerRegister(localIp, localPort, url, type, consumerClientName, consumerGroupName, new ArrayList<String>(consumerReceiverMap.keySet()));
		consumerModuleRegister.setModuleId(moduleId);
	}
	
	public void destroy() throws Exception {
		consoleRemotingInvoke.saveConsumerCancel(moduleId);
		connectServer.destroy();
	}
	
	@Override
	public void receive(Message message) throws Exception {
		ConsumerReceiver consumerReceiver = consumerReceiverMap.get(message.getQueueName());
		if (consumerReceiver != null) {
			if(!consumerReceiver.receive(message)) {
				throw new ConnectInvokeException("Consumer receiver consume message return false");
			}
		} else {
			throw new ConnectInvokeException("Not have this queue consumer receiver");
		}
	}
	
	public void setConsumerReceiverMap(Map<String, ConsumerReceiver> consumerReceiverMap) {
		this.consumerReceiverMap = consumerReceiverMap;
	}
	
	public void setConsumerGroupName(String consumerGroupName) {
		this.consumerGroupName = consumerGroupName;
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

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public void setConsumerModuleRegister(ModuleRegister consumerModuleRegister) {
		this.consumerModuleRegister = consumerModuleRegister;
	}
	
	public void setConnectServer(ConnectServer connectServer) {
		this.connectServer = connectServer;
	}
}
