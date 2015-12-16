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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.constant.TraceConstant;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionInvokeException;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionStopException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;

public class Exchange implements NetConnectReceiver {

	private final static Logger logger = LoggerFactory.getLogger(Exchange.class);
	
	private long suspendTime = 300000;
	
	private ConnectManager dbConnectManager;
	private ConnectManager bodyCacheConnectManager;
	
	private ConnectManager traceCacheConnectManager;
	
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
	
	@Autowired
	OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired
	SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	private long sizeLimit = 8192;
		
	public Exchange() {
	}
	
	public void start() throws Exception {
		
		if (exchangeName == null || 
				(exchangeName != null && exchangeName.equals("hostname"))) {
			exchangeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		moduleId = consoleRemotingInvoke.exchangerRegister(localIp, localPort, url, type, checkPort, exchangeName);
		
		MemoryStorage.moduleName = MonitorPerformanceConstant.MODULE_ID_EXCHANGE;
		MemoryStorage.moduleId = moduleId;
		
		traceCacheConnectManager.setModuleId(moduleId);
		traceCacheConnectManager.setConsoleRemotingInvoke(consoleRemotingInvoke);
		traceCacheConnectManager.start();
		
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
		
		consoleRemotingInvoke.exchangerCancel(moduleId);

		stopSign = true;
		startUpdateConnectAgent();
		stopCountDownLatch.await();
		executorService.shutdown();
		
		dbConnectManager.destroy();		
		bodyCacheConnectManager.destroy();
		traceCacheConnectManager.destroy();
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
							+ ", Is_Trace: " + queueExchangerVo.getIs_Trace()
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
								+ ", Is_Trace: " + queueExchangerVo.getIs_Trace()
								);
					}
				} else {
					QueueExchangerVo queueExchangerVoOld = queueExchangerVoMap.get(queueExchangerVo.getQueue_Nm());
					if (queueExchangerVoOld.getIs_Trace() != queueExchangerVo.getIs_Trace()) {
						queueExchangerVoMap.remove(queueExchangerVoOld.getQueue_Nm());
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Remove Queue -> " 
									+ "QueueName: " + queueExchangerVoOld.getQueue_Nm() 
									+ ", Is_Trace: " + queueExchangerVoOld.getIs_Trace()
									+ ", Queue Change"
									);
						}
						queueExchangerVoMap.put(queueExchangerVo.getQueue_Nm(), queueExchangerVo);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Add Queue -> " 
									+ "QueueName: " + queueExchangerVo.getQueue_Nm() 
									+ ", Is_Trace: " + queueExchangerVo.getIs_Trace()
									+ ", Queue Change"
									);
						}
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
								+ ", Is_Trace: " + queueExchangerVoOld.getIs_Trace()
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
			if (logger.isDebugEnabled()) {
				logger.error("Receive -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", Limit: " + sizeLimit
						+ ", Message Body Bigger Then Max Limit");
			}
			successPerformanceExecuterService.result(
					MonitorPerformanceConstant.MODULE_ID_EXCHANGE, 
					moduleId,
					messageDm.getQueueName(),
					MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE,
					false);
			trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_EXCHANGE, moduleId); 
			throw new ConnectionInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		overtimePerformanceExecuterService.before(
				MonitorPerformanceConstant.MODULE_ID_EXCHANGE,
				moduleId,
				messageDm.getQueueName(),
				MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE);
		
		Long queueCustomerGroupNum = queueCustomerGroupNumMap.get(messageDm.getQueueName());
		if (queueCustomerGroupNum != null) {
			messageDm.setExecuteQueue(queueCustomerGroupNum);
			messageDm.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			if (logger.isDebugEnabled()) {
				logger.error("Receive -> "
						+ "Queue: " + messageDm.getQueueName()
						+ ", UUID: " + messageDm.getUuid()
						+ ", Get Queue Customer Group Num -> Null");
			}
			successPerformanceExecuterService.result(
					MonitorPerformanceConstant.MODULE_ID_EXCHANGE, 
					moduleId,
					messageDm.getQueueName(),
					MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE,
					false);
			trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_EXCHANGE, moduleId); 
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
						if (logger.isDebugEnabled()) {
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
							if (logger.isDebugEnabled()) {
								logger.error("Receive -> "
										+ "Queue: " + messageDm.getQueueName()
										+ ", UUID: " + messageDm.getUuid()
										+ ", Get Body Cache Agent -> Null");
							}
						}
					} while (bodyCacheConnectAgentOther != null);
				}
			} else {
				if (logger.isDebugEnabled()) {
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
						trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_DB_INSERT, TraceConstant.RESULT_TYPE_SUCCESS, TraceConstant.MODULE_TYPE_DB, ((ConnectAgent)dbConnectAgent).getConnectId()); 
						break;
					} catch (ConnectionUnableException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_DB_INSERT, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DB, ((ConnectAgent)dbConnectAgent).getConnectId()); 
						continue;
					} catch (ConnectionRefusedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_DB_INSERT, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DB, ((ConnectAgent)dbConnectAgent).getConnectId()); 
						continue;
					} catch (ConnectionResetException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_DB_INSERT, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DB, ((ConnectAgent)dbConnectAgent).getConnectId()); 
						continue;
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("Receive -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
									+ ", Insert DB -> " + e);
						}
						trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_DB_INSERT, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_DB, ((ConnectAgent)dbConnectAgent).getConnectId()); 
						continue;
					}
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("Receive -> "
								+ "Queue: " + messageDm.getQueueName()
								+ ", UUID: " + messageDm.getUuid()
								+ ", Get DB Agent -> Null");
					}
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_EXCHANGE, 
							moduleId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE,
							false);
					trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_EXCHANGE, moduleId); 
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
			successPerformanceExecuterService.result(
					MonitorPerformanceConstant.MODULE_ID_EXCHANGE, 
					moduleId,
					messageDm.getQueueName(),
					MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE,
					false);
			trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, TraceConstant.RESULT_TYPE_FAIL, TraceConstant.MODULE_TYPE_EXCHANGE, moduleId); 
			dbConnectManager.startUpdateConnectAgent();
			throw new ConnectionInvokeException("Db connect agent insert message error, can not get db queue agent");
		}
		
		overtimePerformanceExecuterService.after(
				MonitorPerformanceConstant.MODULE_ID_EXCHANGE,
				moduleId,
				messageDm.getQueueName(),
				MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE);
		
		successPerformanceExecuterService.result(
				MonitorPerformanceConstant.MODULE_ID_EXCHANGE, 
				moduleId,
				messageDm.getQueueName(),
				MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE,
				true);
		
		trace(messageDm, TraceConstant.ACTION_TYPE_EXCHANGE_RECEIVE, TraceConstant.RESULT_TYPE_SUCCESS, TraceConstant.MODULE_TYPE_EXCHANGE, moduleId); 
	}
	
	private void trace(MessageDm messageDm, int action, byte result, byte traceModuleType, long traceModuleId) {
		QueueExchangerVo queueExchangerVo = queueExchangerVoMap.get(messageDm.getQueueName());
		if (queueExchangerVo != null && queueExchangerVo.getIs_Trace() == TraceConstant.IS_TRACE_YES) {
			QueueAgent queueAgentTrace = traceCacheConnectManager.getQueueAgent(messageDm.getQueueName());
			if (queueAgentTrace != null) {
				TraceCacheConnectAgent traceCacheConnectAgent = null;			
				do {
					traceCacheConnectAgent = (TraceCacheConnectAgent) queueAgentTrace.getConnectAgent();
					if (traceCacheConnectAgent != null) {
						try {
							traceCacheConnectAgent.set(new TraceVo(
															messageDm.getUuid(), 
															action, 
															System.currentTimeMillis(), 
															result, 
															traceModuleType,
															traceModuleId,
															TraceConstant.MODULE_TYPE_EXCHANGE,
															moduleId
															));
							break;
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("Trace -> "
										+ "Queue: " + messageDm.getQueueName()
										+ ", UUID: " + messageDm.getUuid()
										+ ", TraceCache: " + ((ConnectAgent)traceCacheConnectAgent).getConnectId()
										+ ", Set Trace Cache -> " + e);
							}
							continue;
						}
					} else {
						bodyCacheConnectManager.startUpdateConnectAgent();
						if (logger.isDebugEnabled()) {
							logger.error("Trace -> "
									+ "Queue: " + messageDm.getQueueName()
									+ ", UUID: " + messageDm.getUuid()
									+ ", Get Trace Cache Agent -> Null");
						}
					}
				} while (traceCacheConnectAgent != null);
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("Trace -> "
							+ "Queue: " + messageDm.getQueueName()
							+ ", UUID: " + messageDm.getUuid()
							+ ", Get Trace Queue Agent -> Null");
				}
			}
		}
	}
	
	public void setDbConnectManager(ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}

	public void setBodyCacheConnectManager(ConnectManager bodyCacheConnectManager) {
		this.bodyCacheConnectManager = bodyCacheConnectManager;
	}

	public void setTraceCacheConnectManager(ConnectManager traceCacheConnectManager) {
		this.traceCacheConnectManager = traceCacheConnectManager;
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
