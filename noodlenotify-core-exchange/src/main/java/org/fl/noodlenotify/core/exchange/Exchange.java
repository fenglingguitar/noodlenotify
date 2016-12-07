package org.fl.noodlenotify.core.exchange;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionInvokeException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Exchange implements NetConnectReceiver {

	private final static Logger logger = LoggerFactory.getLogger(Exchange.class);
	
	private long suspendTime = 300000;
	
	private ConnectManager dbConnectManager;
	private ConnectManager bodyCacheConnectManager;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();	
	
	private volatile boolean stopSign = false;
	private CountDownLatch stopCountDownLatch;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, Long> queueConsumerGroupNumMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentMap<String, QueueExchangerVo> queueExchangerVoMap = new ConcurrentHashMap<String, QueueExchangerVo>();
	
	private String exchangeName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type; 
	private int checkPort;
	
	private long sizeLimit = 8192;
	
	private ModuleRegister exchangeModuleRegister;
	
	public Exchange() {
	}
	
	public void start() throws Exception {
		
		if (exchangeName == null || 
				(exchangeName != null && exchangeName.equals("hostname"))) {
			exchangeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.saveExchangerRegister(localIp, localPort, url, type, checkPort, exchangeName);
		
		//MemoryStorage.moduleName = MonitorPerformanceConstant.MODULE_ID_EXCHANGE;
		//MemoryStorage.moduleId = moduleId;
		
		exchangeModuleRegister.setModuleId(moduleId);
		
		//bodyCacheConnectManager.setModuleId(moduleId);
		//bodyCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//bodyCacheConnectManager.start();
		bodyCacheConnectManager.runUpdateNow();
		
		//dbConnectManager.setModuleId(moduleId);
		//dbConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		//dbConnectManager.start();
		dbConnectManager.runUpdateNow();
		
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
		
		consoleRemotingInvoke.saveExchangerCancel(moduleId);

		stopSign = true;
		startUpdateConnectAgent();
		stopCountDownLatch.await();
		executorService.shutdown();
		
		//dbConnectManager.destroy();		
		//bodyCacheConnectManager.destroy();
	}
	
	private synchronized void suspendUpdateConnectAgent() {
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
	
	private synchronized void startUpdateConnectAgent() {
		notifyAll();
	}
	
	private void updateConnectAgent() {
		
		Map<String, Long> consoleInfoMapGroupNum = null;
		
		try {
			consoleInfoMapGroupNum = consoleRemotingInvoke.exchangerGetQueueConsumerGroupNum(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Exchanger Get QueueConsumerGroupNum -> " + e);
			}
		}
		
		if (consoleInfoMapGroupNum != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMapGroupNum.keySet();
				for (String queueName : set) {
					logger.debug("UpdateConnectAgent -> ExchangerGetQueueConsumerGroupNum -> " 
							+ "QueueName: " + queueName 
							+ ", QueueConsumerGroupNum: " + consoleInfoMapGroupNum.get(queueName)
							);
				}
			}
			
			for (String queueName : consoleInfoMapGroupNum.keySet()) {
				if (!queueConsumerGroupNumMap.containsKey(queueName)) {
					queueConsumerGroupNumMap.putIfAbsent(queueName, consoleInfoMapGroupNum.get(queueName));
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add QueueConsumerGroupNum -> " 
								+ "QueueName: " + queueName 
								+ ", QueueConsumerGroupNum: " + consoleInfoMapGroupNum.get(queueName)
								);
					}
				} else {
					Long queueConsumerGroupNumOld = queueConsumerGroupNumMap.get(queueName);
					Long queueConsumerGroupNumNew = consoleInfoMapGroupNum.get(queueName);
					if (!queueConsumerGroupNumOld.equals(queueConsumerGroupNumNew)) {
						queueConsumerGroupNumMap.remove(queueName);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove QueueConsumerGroupNum -> " 
									+ "QueueName: " + queueName 
									+ ", QueueConsumerGroupNum: " + queueConsumerGroupNumOld
									+ ", QueueConsumerGroupNum Change"
									);
						}
						queueConsumerGroupNumMap.put(queueName, queueConsumerGroupNumNew);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Add QueueConsumerGroupNum -> " 
									+ "QueueName: " + queueName 
									+ ", QueueConsumerGroupNum: " + queueConsumerGroupNumNew
									+ ", QueueConsumerGroupNum Change"
									);
						}
					}
				}
			}
			
			for (String queueName : queueConsumerGroupNumMap.keySet()) {
				Long queueConsumerGroupNumOld = queueConsumerGroupNumMap.get(queueName);
				if (!consoleInfoMapGroupNum.containsKey(queueName)) {
					queueConsumerGroupNumMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove QueueConsumerGroupNum -> " 
								+ "QueueName: " + queueName 
								+ ", QueueConsumerGroupNum: " + queueConsumerGroupNumOld
								);
					}
				}
			}
		}
		
		List<QueueExchangerVo> consoleInfoMapQueues = null;
		
		try {
			consoleInfoMapQueues = consoleRemotingInvoke.exchangerGetQueues(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Exchanger Get Queues -> " + e);
			}
		}
		
		if (consoleInfoMapQueues != null) {
			
			if (logger.isDebugEnabled()) {
				for (QueueExchangerVo queueExchangerVo : consoleInfoMapQueues) {
					logger.debug("UpdateConnectAgent -> ExchangerGetQueues -> " 
							+ "QueueName: " + queueExchangerVo.getQueue_Nm() 
							);
				}
			}
			
			Set<String> queueNameSet = new HashSet<String>();
			
			for (QueueExchangerVo queueExchangerVo : consoleInfoMapQueues) {
				queueNameSet.add(queueExchangerVo.getQueue_Nm());
				if (!queueExchangerVoMap.containsKey(queueExchangerVo.getQueue_Nm())) {
					queueExchangerVoMap.put(queueExchangerVo.getQueue_Nm(), queueExchangerVo);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add Queue -> " 
								+ "QueueName: " + queueExchangerVo.getQueue_Nm() 
								);
					}
				}
			}
			
			for (String queueName : queueExchangerVoMap.keySet()) {
				if (!queueNameSet.contains(queueName)) {
					QueueExchangerVo queueExchangerVoOld = queueExchangerVoMap.get(queueName);
					queueExchangerVoMap.remove(queueExchangerVoOld.getQueue_Nm());
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Queue -> " 
								+ "QueueName: " + queueExchangerVoOld.getQueue_Nm() 
								);
					}
				} 
			}
		}
	}
	
	private void destroyConnectAgent() {
		queueConsumerGroupNumMap.clear();
	}
	
	@Override
	public void receive(Message message) throws Exception {
		
		if (stopSign) {
			throw new ConnectionStopException("Exchange is stopping");
		}
		
		MessageDm messageDm = new MessageDm(
				message.getQueueName(), 
				message.getUuid(), 
				message.getContent().getBytes("UTF-8")
				);
		
		if (messageDm.getContent().length > sizeLimit) {
			throw new ConnectionInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		Long queueConsumerGroupNum = queueConsumerGroupNumMap.get(messageDm.getQueueName());
		if (queueConsumerGroupNum != null) {
			messageDm.setExecuteQueue(queueConsumerGroupNum);
			messageDm.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			startUpdateConnectAgent();
			throw new ConnectionInvokeException("Set execute queue error, can not get queue consumer group num");
		}
		
		ConnectCluster connectCluster = dbConnectManager.getConnectCluster("DEFALT");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) connectCluster.getProxy();
		
		messageDm.setBeginTime(System.currentTimeMillis());
		ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDm);
		try {
			dbConnectAgent.insert(messageDm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
		}
		
	}	
	
	public void setDbConnectManager(org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}

	public void setBodyCacheConnectManager(ConnectManager bodyCacheConnectManager) {
		this.bodyCacheConnectManager = bodyCacheConnectManager;
	}

	public void setSuspendTime(long suspendTime) {
		this.suspendTime = suspendTime;
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
	
	public void setCheckPort(int checkPort) {
		this.checkPort = checkPort;
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
}
