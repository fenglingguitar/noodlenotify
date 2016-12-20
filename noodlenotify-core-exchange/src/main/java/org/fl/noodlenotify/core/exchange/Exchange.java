package org.fl.noodlenotify.core.exchange;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.exception.ConnectStopException;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;

public class Exchange implements NetConnectReceiver {

	//private final static Logger logger = LoggerFactory.getLogger(Exchange.class);
	
	private long suspendTime = 300000;
	
	private ConnectManager dbConnectManager;
	private ConnectManager bodyCacheConnectManager;
	
	private ExecutorService executorService = Executors.newSingleThreadExecutor();	
	
	private volatile boolean stopSign = false;
	
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
		
		exchangeModuleRegister.setModuleId(moduleId);

		bodyCacheConnectManager.runUpdateNow();
		dbConnectManager.runUpdateNow();
		
		updateConnectAgent();

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
			}
		});				
	}
	
	public void destroy() throws Exception {
		
		consoleRemotingInvoke.saveExchangerCancel(moduleId);

		stopSign = true;
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(60000, TimeUnit.MILLISECONDS)) {
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private synchronized void suspendUpdateConnectAgent() {
		try {
			wait(suspendTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		if (consoleInfoMapGroupNum != null) {
			
			for (String queueName : consoleInfoMapGroupNum.keySet()) {
				if (!queueConsumerGroupNumMap.containsKey(queueName)) {
					queueConsumerGroupNumMap.putIfAbsent(queueName, consoleInfoMapGroupNum.get(queueName));
				} else {
					Long queueConsumerGroupNumOld = queueConsumerGroupNumMap.get(queueName);
					Long queueConsumerGroupNumNew = consoleInfoMapGroupNum.get(queueName);
					if (!queueConsumerGroupNumOld.equals(queueConsumerGroupNumNew)) {
						queueConsumerGroupNumMap.remove(queueName);
						queueConsumerGroupNumMap.put(queueName, queueConsumerGroupNumNew);
					}
				}
			}
			
			for (String queueName : queueConsumerGroupNumMap.keySet()) {
				if (!consoleInfoMapGroupNum.containsKey(queueName)) {
					queueConsumerGroupNumMap.remove(queueName);
				}
			}
		}
		
		List<QueueExchangerVo> consoleInfoMapQueues = null;
		
		try {
			consoleInfoMapQueues = consoleRemotingInvoke.exchangerGetQueues(moduleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (consoleInfoMapQueues != null) {
			
			Set<String> queueNameSet = new HashSet<String>();
			
			for (QueueExchangerVo queueExchangerVo : consoleInfoMapQueues) {
				queueNameSet.add(queueExchangerVo.getQueue_Nm());
				if (!queueExchangerVoMap.containsKey(queueExchangerVo.getQueue_Nm())) {
					queueExchangerVoMap.put(queueExchangerVo.getQueue_Nm(), queueExchangerVo);
				}
			}
			
			for (String queueName : queueExchangerVoMap.keySet()) {
				if (!queueNameSet.contains(queueName)) {
					QueueExchangerVo queueExchangerVoOld = queueExchangerVoMap.get(queueName);
					queueExchangerVoMap.remove(queueExchangerVoOld.getQueue_Nm());
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
			throw new ConnectStopException("Exchange is stopping");
		}
		
		MessageDm messageDm = new MessageDm(
				message.getQueueName(), 
				message.getUuid(), 
				message.getContent().getBytes("UTF-8")
				);
		
		if (messageDm.getContent().length > sizeLimit) {
			throw new ConnectInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		Long queueConsumerGroupNum = queueConsumerGroupNumMap.get(messageDm.getQueueName());
		if (queueConsumerGroupNum != null) {
			messageDm.setExecuteQueue(queueConsumerGroupNum);
			messageDm.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			startUpdateConnectAgent();
			throw new ConnectInvokeException("Set execute queue error, can not get queue consumer group num");
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
