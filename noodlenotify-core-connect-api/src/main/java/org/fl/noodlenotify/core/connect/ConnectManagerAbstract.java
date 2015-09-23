package org.fl.noodlenotify.core.connect;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;

public abstract class ConnectManagerAbstract implements ConnectManager {
	
	private final static Logger logger = LoggerFactory.getLogger(ConnectManagerAbstract.class);
	
	protected ConnectAgentFactory connectAgentFactory;
	
	private long suspendTime = 300000;
	
	protected ConcurrentMap<String, QueueAgent> queueAgentMap = new ConcurrentHashMap<String, QueueAgent>();
	protected ConcurrentMap<Long, ConnectAgent> connectAgentMap = new ConcurrentHashMap<Long, ConnectAgent>();
	
	protected ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	protected volatile boolean stopSign = false;
	protected CountDownLatch stopCountDownLatch;
	
	protected long moduleId;
	protected ConsoleRemotingInvoke consoleRemotingInvoke;
	
	public ConnectManagerAbstract() {
	}
	
	public ConnectManagerAbstract(ConnectAgentFactory connectAgentFactory) {
		this.connectAgentFactory = connectAgentFactory;
	}
	
	public void start() throws Exception {
		
		updateConnectAgent();
		
		stopCountDownLatch = new CountDownLatch(1);
		
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					suspendUpdateConnectAgent();
					if (stopSign) {
						destroyConnectAgent();
						break;
					}
					updateConnectAgent();
				}
				stopCountDownLatch.countDown();
			}
		});		
	}
	
	public void destroy() throws Exception {
		stopSign = true;
		startUpdateConnectAgent();
		stopCountDownLatch.await();
		executorService.shutdown();
	}
	
	protected synchronized void suspendUpdateConnectAgent() {
		try {
			wait(suspendTime);
		} catch (InterruptedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("SuspendUpdateConnectAgent -> Wait -> "
							+ "ModuleId: " + moduleId
							+ ", Wait Interrupted -> " + e);
			}
		}
	}
	
	@Override
	public synchronized void startUpdateConnectAgent() {
		notifyAll();
	}
	
	@Override
	public QueueAgent getQueueAgent(String queueName) {
		return queueAgentMap.get(queueName);
	}

	@Override
	public ConnectAgent getConnectAgent(long ConnectId) {
		return connectAgentMap.get(ConnectId);
	}
	
	protected abstract void updateConnectAgent();
	protected abstract void destroyConnectAgent();
	
	public void setSuspendTime(long suspendTime) {
		this.suspendTime = suspendTime;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
