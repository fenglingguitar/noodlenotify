package org.fl.noodlenotify.core.exchange;

import java.util.List;
import java.util.UUID;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.server.ConnectServer;
import org.fl.noodle.common.trace.TraceInterceptor;
import org.fl.noodle.common.trace.operation.performance.TracePerformancePrint;
import org.fl.noodle.common.trace.util.TimeSynchron;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.common.util.ConsoleConstant;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.exchange.manager.ExchangeConnectManager;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;

public class Exchange implements NetConnectReceiver, FactoryBean<Object>, MethodInterceptor {
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String exchangeName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type;
	
	private long sizeLimit = 8192;
	
	private ModuleRegister exchangeModuleRegister;
	
	private ConnectManagerPool connectManagerPool;
	
	private ConnectServer connectServer;
	
	private Object serviceProxy;	
	private List<MethodInterceptor> methodInterceptorList;
	
	public void start() throws Exception {
		
		if (exchangeName == null || 
				(exchangeName != null && exchangeName.equals("hostname"))) {
			exchangeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		
		connectServer.start();
		
		moduleId = consoleRemotingInvoke.saveExchangerRegister(localIp, localPort, url, type, exchangeName);
		exchangeModuleRegister.setModuleId(moduleId);

		connectManagerPool.start();
		
		ProxyFactory proxyFactory = new ProxyFactory(NetConnectReceiver.class, this);
		if (methodInterceptorList != null && methodInterceptorList.size() > 0) {
			for (Object object : methodInterceptorList) {
				proxyFactory.addAdvice((Advice)object);
			}
		}
		proxyFactory.setTarget(this);
		this.serviceProxy = proxyFactory.getProxy();
	}
	
	public void destroy() throws Exception {
		consoleRemotingInvoke.saveExchangerCancel(moduleId);
		connectServer.destroy();
		connectManagerPool.destroy();
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		long startTime = TimeSynchron.currentTimeMillis();
		boolean isError = false;
		try {
			if (invocation.getArguments().length > 0 && invocation.getArguments()[0] instanceof MessageRequest) {
				MessageRequest messageRequest = (MessageRequest)invocation.getArguments()[0];
				if (messageRequest.getTraceKey() == null || messageRequest.getTraceKey().isEmpty()) {
					TraceInterceptor.setTraceKey(UUID.randomUUID().toString().replaceAll("-", ""));
				} else {
					TraceInterceptor.setTraceKey(messageRequest.getUuid());
					//TraceInterceptor.setInvoke(messageRequest.getParentInvoke());
					//TraceInterceptor.setStackKey(messageRequest.getParentStackKey());
				}
			}
			TraceInterceptor.setInvoke(ConsoleConstant.PRODUCER_METHOD);
			TraceInterceptor.setStackKey(ConsoleConstant.PRODUCER_KEY);
			TraceInterceptor.setInvoke(ConsoleConstant.EXCHANGE_METHOD);
			TraceInterceptor.setStackKey(ConsoleConstant.EXCHANGE_KEY);
			return invocation.proceed();
		} catch (Throwable t){
			isError = true;
			throw t;
		} finally {
			long endTime = TimeSynchron.currentTimeMillis();
			TracePerformancePrint.printTraceLog(ConsoleConstant.EXCHANGE_METHOD, TraceInterceptor.getParentInvoke(), startTime, endTime, isError, ConsoleConstant.EXCHANGE_KEY, TraceInterceptor.getParentStackKey());
			TraceInterceptor.popInvoke();
			TraceInterceptor.popStackKey();
			TraceInterceptor.popInvoke();
			TraceInterceptor.popStackKey();
			
			TraceInterceptor.setTraceKey(null);
		}
	}

	@Override
	public Object getObject() throws Exception {
		return this.serviceProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return NetConnectReceiver.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void receive(MessageRequest message) throws Exception {
				
		MessageDb messageDb = new MessageDb(
				message.getQueueName(), 
				message.getUuid(), 
				message.getContent().getBytes("UTF-8"),
				message.getTraceKey(),
				message.getParentStackKey()
				);
		
		if (messageDb.getContent().length > sizeLimit) {
			throw new ConnectInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		Long queueConsumerGroupNum = ((ExchangeConnectManager) connectManagerPool.getConnectManager(ConnectManagerType.EXCHANGE.getCode())).getQueueConsumerGroupNumMap().get(messageDb.getQueueName());
		if (queueConsumerGroupNum != null) {
			messageDb.setExecuteQueue(queueConsumerGroupNum);
			messageDb.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			connectManagerPool.getConnectManager(ConnectManagerType.EXCHANGE.getCode()).runUpdate();
			throw new ConnectInvokeException("Set execute queue error, can not get queue consumer group num");
		}
		
		ConnectCluster connectCluster = connectManagerPool.getConnectManager(ConnectManagerType.DB.getCode()).getConnectCluster("DEFALT");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) connectCluster.getProxy();
		
		messageDb.setBeginTime(System.currentTimeMillis());
		ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDb);
		try {
			dbConnectAgent.insert(messageDb);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
		}
		
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
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

	public void setSizeLimit(long sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}

	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}

	public void setConnectServer(ConnectServer connectServer) {
		this.connectServer = connectServer;
	}

	public List<MethodInterceptor> getMethodInterceptorList() {
		return methodInterceptorList;
	}

	public void setMethodInterceptorList(List<MethodInterceptor> methodInterceptorList) {
		this.methodInterceptorList = methodInterceptorList;
	}
}
