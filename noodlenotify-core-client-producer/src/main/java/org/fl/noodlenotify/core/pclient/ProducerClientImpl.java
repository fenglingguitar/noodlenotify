package org.fl.noodlenotify.core.pclient;

import java.util.List;
import java.util.UUID;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.trace.TraceInterceptor;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

public class ProducerClientImpl implements ProducerClient, FactoryBean<Object>, MethodInterceptor {
	
	protected ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String producerClientName;
	private long moduleId;
	private String localIp;
	
	protected ModuleRegister producerModuleRegister;
	
	protected ConnectManagerPool connectManagerPool;
	
	private Object serviceProxy;	
	private List<MethodInterceptor> methodInterceptorList;
	
	public void start() throws Exception {
		
		if (producerClientName == null || 
				(producerClientName != null && producerClientName.equals("hostname"))) {
			producerClientName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.saveProducerRegister(localIp, producerClientName);		

		producerModuleRegister.setModuleId(moduleId);

		connectManagerPool.start();
		
		ProxyFactory proxyFactory = new ProxyFactory(ProducerClient.class, this);
		if (methodInterceptorList != null && methodInterceptorList.size() > 0) {
			for (Object object : methodInterceptorList) {
				proxyFactory.addAdvice((Advice)object);
			}
		}
		proxyFactory.setTarget(this);
		this.serviceProxy = proxyFactory.getProxy();
	}

	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveProducerCancel(moduleId);
		connectManagerPool.destroy();
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		return invocation.proceed();
	}

	@Override
	public Object getObject() throws Exception {
		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return ProducerClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public String send(String queueName, String content) throws Exception {
		
		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setQueueName(queueName);
		messageRequest.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
		messageRequest.setContent(content);
		messageRequest.setTraceKey(TraceInterceptor.getTraceKey());
		messageRequest.setParentInvoke(TraceInterceptor.getInvoke());
		messageRequest.setParentStackKey(TraceInterceptor.getStackKey());
		
		ConnectCluster connectCluster = connectManagerPool.getConnectManager(ConnectManagerType.NET.getCode()).getConnectCluster("DEFALT");
		NetConnectAgent netConnectAgent = (NetConnectAgent) connectCluster.getProxy();
		
		return netConnectAgent.send(messageRequest);
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

	public List<MethodInterceptor> getMethodInterceptorList() {
		return methodInterceptorList;
	}

	public void setMethodInterceptorList(List<MethodInterceptor> methodInterceptorList) {
		this.methodInterceptorList = methodInterceptorList;
	}
}
