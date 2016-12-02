package org.fl.noodlenotify.core.pclient;

import java.util.UUID;

import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class ProducerClientImpl implements ProducerClient {

	protected ConnectManager netConnectManager;
	
	protected ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String producerClientName;
	private long moduleId;
	private String localIp;
	private int checkPort;
	private String checkUrl;
	private String checkType;
	
	protected ModuleRegister producerModuleRegister;

	public ProducerClientImpl() {
	}
	
	public void start() throws Exception {
		
		if (producerClientName == null || 
				(producerClientName != null && producerClientName.equals("hostname"))) {
			producerClientName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.saveProducerRegister(localIp, checkPort, checkUrl, checkType, producerClientName);		

		producerModuleRegister.setModuleId(moduleId);
		
		//netConnectManager.setModuleId(0);
		//netConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//netConnectManager.start();
		
		netConnectManager.runUpdateNow();
	}

	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveProducerCancel(moduleId);
		//netConnectManager.destroy();
	}
	
	@Override
	public String send(String queueName, String content) throws Exception {
		
		Message message = new Message();
		message.setQueueName(queueName);
		String uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
		message.setUuid(uuid);
		message.setContent(content);
		
		ConnectCluster connectCluster = netConnectManager.getConnectCluster("DEFALT");
		NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
		
		return netConnectAgent.send(message);
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
	
	public void setProducerModuleRegister(ModuleRegister producerModuleRegister) {
		this.producerModuleRegister = producerModuleRegister;
	}
}
