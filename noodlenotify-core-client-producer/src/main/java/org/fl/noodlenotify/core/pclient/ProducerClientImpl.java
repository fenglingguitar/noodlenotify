package org.fl.noodlenotify.core.pclient;

import java.util.UUID;

import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.log.Logger;
import org.fl.noodle.common.log.LoggerFactory;
import org.fl.noodle.common.trace.TraceInterceptor;
import org.fl.noodle.common.trace.operation.performance.TracePerformancePrint;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class ProducerClientImpl implements ProducerClient {
	
	private final static Logger loggerMethod = LoggerFactory.getLogger("trace.method");
	
	protected ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String producerClientName;
	private long moduleId;
	private String localIp;
	
	protected ModuleRegister producerModuleRegister;
	
	protected ConnectManagerPool connectManagerPool;
	
	public void start() throws Exception {
		
		if (producerClientName == null || 
				(producerClientName != null && producerClientName.equals("hostname"))) {
			producerClientName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.saveProducerRegister(localIp, producerClientName);		

		producerModuleRegister.setModuleId(moduleId);

		connectManagerPool.start();
	}

	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveProducerCancel(moduleId);
		connectManagerPool.destroy();
	}
	
	@Override
	public String send(String queueName, String content) throws Exception {
		
		if (TraceInterceptor.getTraceKey().isEmpty()) {
			TraceInterceptor.setTraceKey(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		TraceInterceptor.setInvoke("ProducerClientImpl.send");
		TraceInterceptor.setStackKey(UUID.randomUUID().toString().replaceAll("-", ""));
		loggerMethod.printEnter(queueName, content);
		
		Message message = new Message();
		message.setQueueName(queueName);
		message.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		message.setContent(content);
		message.setTraceKey(TraceInterceptor.getTraceKey());
		message.setParentInvoke(TraceInterceptor.getInvoke());
		message.setParentStackKey(TraceInterceptor.getStackKey());
		
		ConnectCluster connectCluster = connectManagerPool.getConnectManager(ConnectManagerType.NET.getCode()).getConnectCluster("DEFALT");
		NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
		
		try {
			return netConnectAgent.send(message);
		} finally {
			TracePerformancePrint.printTraceLog(TraceInterceptor.getInvoke(), 0, 0, false);
		}
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
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setProducerModuleRegister(ModuleRegister producerModuleRegister) {
		this.producerModuleRegister = producerModuleRegister;
	}

	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}
}
