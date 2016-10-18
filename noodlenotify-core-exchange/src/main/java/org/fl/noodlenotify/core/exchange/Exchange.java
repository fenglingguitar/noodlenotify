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

import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionInvokeException;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
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
	
	private ConcurrentMap<String, Long> queueCustomerGroupNumMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentMap<String, QueueExchangerVo> queueExchangerVoMap = new ConcurrentHashMap<String, QueueExchangerVo>();
	
	private String exchangeName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type; 
	private int checkPort;
	
	private long sizeLimit = 8192;
		
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
		
		bodyCacheConnectManager.setModuleId(moduleId);
		bodyCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		bodyCacheConnectManager.start();
		
		dbConnectManager.setModuleId(moduleId);
		dbConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		dbConnectManager.start();
		
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
		
		dbConnectManager.destroy();		
		bodyCacheConnectManager.destroy();
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
			consoleInfoMapGroupNum = consoleRemotingInvoke.exchangerGetQueueCustomerGroupNum(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Exchanger Get QueueCustomerGroupNum -> " + e);
			}
		}
		
		if (consoleInfoMapGroupNum != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMapGroupNum.keySet();
				for (String queueName : set) {
					logger.debug("UpdateConnectAgent -> ExchangerGetQueueCustomerGroupNum -> " 
							+ "QueueName: " + queueName 
							+ ", QueueCustomerGroupNum: " + consoleInfoMapGroupNum.get(queueName)
							);
				}
			}
			
			for (String queueName : consoleInfoMapGroupNum.keySet()) {
				if (!queueCustomerGroupNumMap.containsKey(queueName)) {
					queueCustomerGroupNumMap.putIfAbsent(queueName, consoleInfoMapGroupNum.get(queueName));
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add QueueCustomerGroupNum -> " 
								+ "QueueName: " + queueName 
								+ ", QueueCustomerGroupNum: " + consoleInfoMapGroupNum.get(queueName)
								);
					}
				} else {
					Long queueCustomerGroupNumOld = queueCustomerGroupNumMap.get(queueName);
					Long queueCustomerGroupNumNew = consoleInfoMapGroupNum.get(queueName);
					if (!queueCustomerGroupNumOld.equals(queueCustomerGroupNumNew)) {
						queueCustomerGroupNumMap.remove(queueName);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove QueueCustomerGroupNum -> " 
									+ "QueueName: " + queueName 
									+ ", QueueCustomerGroupNum: " + queueCustomerGroupNumOld
									+ ", QueueCustomerGroupNum Change"
									);
						}
						queueCustomerGroupNumMap.put(queueName, queueCustomerGroupNumNew);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Add QueueCustomerGroupNum -> " 
									+ "QueueName: " + queueName 
									+ ", QueueCustomerGroupNum: " + queueCustomerGroupNumNew
									+ ", QueueCustomerGroupNum Change"
									);
						}
					}
				}
			}
			
			for (String queueName : queueCustomerGroupNumMap.keySet()) {
				Long queueCustomerGroupNumOld = queueCustomerGroupNumMap.get(queueName);
				if (!consoleInfoMapGroupNum.containsKey(queueName)) {
					queueCustomerGroupNumMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove QueueCustomerGroupNum -> " 
								+ "QueueName: " + queueName 
								+ ", QueueCustomerGroupNum: " + queueCustomerGroupNumOld
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
		queueCustomerGroupNumMap.clear();
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
			if (logger.isErrorEnabled()) {
				logger.error("Receive -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", Limit: " + sizeLimit
						+ ", Message Body Bigger Then Max Limit");
			}
			throw new ConnectionInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		Long queueCustomerGroupNum = queueCustomerGroupNumMap.get(messageDm.getQueueName());
		if (queueCustomerGroupNum != null) {
			messageDm.setExecuteQueue(queueCustomerGroupNum);
			messageDm.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Receive -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", Get Queue Customer Group Num -> Null");
			}
			startUpdateConnectAgent();
			throw new ConnectionInvokeException("Set execute queue error, can not get queue customer group num");
		}
		
		if (bodyCacheConnectManager != null) {
			QueueAgent queueAgentBody = bodyCacheConnectManager.getQueueAgent(messageDm.getQueueName());
			if (queueAgentBody != null) {
				BodyCacheConnectAgent bodyCacheConnectAgent = null;			
				do {
					bodyCacheConnectAgent = (BodyCacheConnectAgent) queueAgentBody.getConnectAgent();
					if (bodyCacheConnectAgent != null) {
						try {
							bodyCacheConnectAgent.set(messageDm);
							messageDm.setRedisOne(((ConnectAgent)bodyCacheConnectAgent).getConnectId());
							break;
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("Receive -> "
										+ "Queue: " + messageDm.getQueueName()
										+ ", UUID: " + messageDm.getUuid()
										+ ", BodyCacheOne: " + ((ConnectAgent)bodyCacheConnectAgent).getConnectId()
										+ ", Set Body Cache One -> " + e);
							}
							continue;
						}
					} else {
						bodyCacheConnectManager.startUpdateConnectAgent();
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", Get Body Cache Agent -> Null");
						}
					}
				} while (bodyCacheConnectAgent != null);
				
				if (bodyCacheConnectAgent != null) {
					BodyCacheConnectAgent bodyCacheConnectAgentOther = null;
					do {
						bodyCacheConnectAgentOther = (BodyCacheConnectAgent) queueAgentBody.getConnectAgentOther((ConnectAgent) bodyCacheConnectAgent);
						if (bodyCacheConnectAgentOther != null) {
							try {
								bodyCacheConnectAgentOther.set(messageDm);
								messageDm.setRedisTwo(((ConnectAgent)bodyCacheConnectAgentOther).getConnectId());
								break;
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("Receive -> "
											+ "Queue: " + messageDm.getQueueName()
											+ ", UUID: " + messageDm.getUuid()
											+ ", BodyCacheTwo: " + ((ConnectAgent)bodyCacheConnectAgent).getConnectId()
											+ ", Set Body Cache Two -> " + e);

								}
								continue;
							}
						} else {
							bodyCacheConnectManager.startUpdateConnectAgent();
							if (logger.isErrorEnabled()) {
								logger.error("Receive -> "
										+ "Queue: " + messageDm.getQueueName()
										+ ", UUID: " + messageDm.getUuid()
										+ ", Get Body Cache Agent -> Null");
							}
						}
					} while (bodyCacheConnectAgentOther != null);
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("Receive -> "
							+ "Queue: " + messageDm.getQueueName()
							+ ", UUID: " + messageDm.getUuid()
							+ ", Get Body Cache Queue Agent -> Null");
				}
				bodyCacheConnectManager.startUpdateConnectAgent();
			}
		}
		
		QueueAgent queueAgentDb = dbConnectManager.getQueueAgent(messageDm.getQueueName());
		if (queueAgentDb != null) {
			DbConnectAgent dbConnectAgent = null;
			do {
				dbConnectAgent = (DbConnectAgent) queueAgentDb.getConnectAgent();
				if (dbConnectAgent != null) {
					try {
						messageDm.setBeginTime(System.currentTimeMillis());
						dbConnectAgent.insert(messageDm);
						break;
					} catch (ConnectionUnableException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						continue;
					} catch (ConnectionRefusedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						continue;
					} catch (ConnectionResetException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						continue;
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("Receive -> "
								+ "Queue: " + messageDm.getQueueName()
								+ ", UUID: " + messageDm.getUuid()
								+ ", Get DB Agent -> Null");
					}
					dbConnectManager.startUpdateConnectAgent();
					throw new ConnectionInvokeException("Db connect agent insert message error, can not get db connect agent");
				}
			} while (dbConnectAgent != null);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("Receive -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", Get DB Queue Agent -> Null");
			}
			dbConnectManager.startUpdateConnectAgent();
			throw new ConnectionInvokeException("Db connect agent insert message error, can not get db queue agent");
		}
	}	
	
	public void setDbConnectManager(ConnectManager dbConnectManager) {
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
}
