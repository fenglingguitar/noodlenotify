package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentAbstract;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
//import org.fl.noodlenotify.core.distribute.locker.DistributeSetLocker;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.core.domain.message.MessageQueueDm;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;

public class RedisQueueCacheConnectAgent extends CacheConnectAgentAbstract implements QueueCacheConnectAgent, QueueCacheStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(RedisQueueCacheConnectAgent.class);
	
	private String queuePostfix = "-Queue";
	
	private String hashPostfix = queuePostfix + "-Hash";
	
	private String typeNewPostfix = "-New";
	private String typePortionPostfix = "-Portion";
	
	private final String activePostfix = "-IsActive";
	
	private final String diffTimeFullName = "DiffTime";
	private final String lockerPostfix = "-Locker";
	private final int lockerOvertime = 5;
	
	private JedisPool jedisPool;
	
	private QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam;
	
	private ConcurrentMap<String, Boolean> queueIsActiveMap = new ConcurrentHashMap<String, Boolean>();
	
	private ConcurrentMap<String, String> queueNewFullMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> hashNewFullMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> queuePortionFullMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> hashPortionFullMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> activeFullMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> lockerFullMap = new ConcurrentHashMap<String, String>();
	
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	private SuccessPerformanceExecuterService successPerformanceExecuterService;

	public RedisQueueCacheConnectAgent(String ip, int port, long connectId) {
		super(ip, port, connectId);
		this.queueCacheConnectAgentConfParam = new QueueCacheConnectAgentConfParam();
	}
	
	public RedisQueueCacheConnectAgent(String ip, int port, long connectId,
			CacheConnectAgentConfParam cacheConnectAgentConfParam,
			QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		super(ip, port, connectId, cacheConnectAgentConfParam);
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}
	
	@Override
	protected void connectCacheActual() throws Exception {
		
		jedisPool = new JedisPool(cacheConnectAgentConfParam, ip, port, cacheConnectAgentConfParam.getTimeout());
		
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			jedisPool.destroy();
			throw new ConnectionRefusedException("Connection refused for queue redis connect agent");
		}
	}

	@Override
	protected void reconnectCacheActual() throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectionRefusedException("Connection refused for queue redis connect agent");
		}
	}

	@Override
	protected void closeCacheActual() {
		jedisPool.destroy();
	}
	
	protected void setActual(List<MessageDm> messageDmList) {
		
		MessageQueueDm messageQueueDm = new MessageQueueDm();
		
		Jedis jedis;
		
		try {
			jedis = getConnect();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Connect -> " + e);
			}
			return;
		}
		
		for (MessageDm messageDm : messageDmList) {
			
			String queueFullName = null;
			String hashFullName = null;
			
			if (messageDm.getBool()) {				
				queueFullName = queueNewFullMap.get(messageDm.getQueueName());
				if (queueFullName == null) {
					queueFullName = messageDm.getQueueName() + queuePostfix + typeNewPostfix;
					queueNewFullMap.putIfAbsent(messageDm.getQueueName(), queueFullName);
				}
				hashFullName = hashNewFullMap.get(messageDm.getQueueName());
				if (hashFullName == null) {
					hashFullName = messageDm.getQueueName() + hashPostfix + typeNewPostfix;
					hashNewFullMap.putIfAbsent(messageDm.getQueueName(), hashFullName);
				}
			} else {
				queueFullName = queuePortionFullMap.get(messageDm.getQueueName());
				if (queueFullName == null) {
					queueFullName = messageDm.getQueueName() + queuePostfix + typePortionPostfix;
					queuePortionFullMap.putIfAbsent(messageDm.getQueueName(), queueFullName);
				}
				hashFullName = hashPortionFullMap.get(messageDm.getQueueName());
				if (hashFullName == null) {
					hashFullName = messageDm.getQueueName() + hashPostfix + typePortionPostfix;
					hashPortionFullMap.putIfAbsent(messageDm.getQueueName(), hashFullName);
				}
			}
			String activeFullName = activeFullMap.get(messageDm.getQueueName());
			if (activeFullName == null) {
				activeFullName = messageDm.getQueueName() + activePostfix;
				activeFullMap.putIfAbsent(messageDm.getQueueName(), activeFullName);
			}
			
			if (messageDm.getBool()) {
				overtimePerformanceExecuterService.before(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET);
			} else {
				overtimePerformanceExecuterService.before(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET);
			}
			
			try {
				if (!jedis.exists(activeFullName)) {
					queueIsActiveMap.put(messageDm.getQueueName(), false);
					if (messageDm.getBool()) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
								false);
					}
					continue;
				}
				
				if (jedis.exists(messageDm.getUuid())) {
					if (messageDm.getBool()) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
								false);
					}
					continue;
				}
				
				long nowTime = System.currentTimeMillis();
				messageQueueDm.setCacheTimestamp(nowTime);
				if (jedis.hsetnx(hashFullName, messageDm.getUuid(), String.valueOf(nowTime)) == 0) {
					String value = jedis.hget(hashFullName, messageDm.getUuid());
					if (value != null) {
						if (System.currentTimeMillis() - Long.valueOf(value) >= queueCacheConnectAgentConfParam.getHashExpire()) {
							jedis.hdel(hashFullName, messageDm.getUuid());
						}
					}
					if (messageDm.getBool()) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
								false);
					}
					continue;
				}
				
				if (jedis.exists(messageDm.getUuid())) {
					jedis.hdel(hashFullName, messageDm.getUuid());
					if (messageDm.getBool()) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
								false);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								messageDm.getQueueName(),
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
								false);
					}
					continue;
				}
				
				messageQueueDm.setQueueName(messageDm.getQueueName());
				messageQueueDm.setUuid(messageDm.getUuid());
				messageQueueDm.setContentId(messageDm.getContentId());
				messageQueueDm.setDb(messageDm.getDb());
				messageQueueDm.setId(messageDm.getId());
				messageQueueDm.setExecuteQueue(messageDm.getExecuteQueue());
				messageQueueDm.setResultQueue(messageDm.getResultQueue());
				messageQueueDm.setStatus(messageDm.getStatus());
				messageQueueDm.setRedisOne(messageDm.getRedisOne());
				messageQueueDm.setRedisTwo(messageDm.getRedisTwo());
				messageQueueDm.setBeginTime(messageDm.getBeginTime());
				messageQueueDm.setFinishTime(messageDm.getFinishTime());
				
				jedis.rpush(queueFullName, JsonTranslator.toString(messageQueueDm));
				
				if (messageDm.getObjectThree() != null) {
					AtomicLong countDown = (AtomicLong) messageDm.getObjectThree();
					countDown.decrementAndGet();
				}
				
			} catch (JedisConnectionException e) {
				connectStatus.set(false);
				if (logger.isErrorEnabled()) {
					logger.error("SetActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Set -> " + e);
				}
				jedisPool.returnBrokenResource(jedis);
				if (messageDm.getBool()) {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
							false);
				} else {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
							false);
				}
				return;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("SetActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Message: " + messageDm.getUuid()
							+ ", Set -> " + e);
				}
				if (messageDm.getBool()) {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
							false);
				} else {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							messageDm.getQueueName(),
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
							false);
				}
				continue;
			} 
			
			if (messageDm.getBool()) {
				overtimePerformanceExecuterService.after(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET);
			} else {
				overtimePerformanceExecuterService.after(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET);
			}
			
			if (messageDm.getBool()) {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET,
						true);
			} else {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						messageDm.getQueueName(),
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET,
						true);
			}
		}
		
		jedisPool.returnResource(jedis);
	}
	
	@Override
	protected void removeActual(List<MessageDm> messageDmList) {
		
		Jedis jedis;
		
		try {
			jedis = getConnect();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("RemoveActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Connect -> " + e);
			}
			return;
		}
		
		for (MessageDm messageDm : messageDmList) {
			try {
				if (messageDm.getDelayTime() > 0) {
					long extendTime = System.currentTimeMillis() - messageDm.getFinishTime();
					if (messageDm.getDelayTime() - extendTime > 1000) {
						jedis.expire(messageDm.getUuid(), (int)((messageDm.getDelayTime() - extendTime) / 1000));						
					} else {
						jedis.del(messageDm.getUuid());
					}
				} else {					
					jedis.del(messageDm.getUuid());
				}
			} catch (JedisConnectionException e) {
				connectStatus.set(false);
				if (logger.isErrorEnabled()) {
					logger.error("RemoveActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Remove -> " + e);
				}
				jedisPool.returnBrokenResource(jedis);
				return;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("RemoveActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Message: " + messageDm.getUuid()
							+ ", Remove -> " + e);
				}
				continue;
			}
		}
		
		jedisPool.returnResource(jedis);
	}
	
	@Override
	public void push(MessageDm messageDm) throws Exception {
		
		if (connectStatus.get() == false) {
			cancelCountDownLatch(messageDm);
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		if (!setBlockingQueue.offer(messageDm, cacheConnectAgentConfParam.getSetTimeout(), TimeUnit.MILLISECONDS)) {
			cancelCountDownLatch(messageDm);
		}
	}

	@Override
	public MessageDm pop(String queueName, boolean queueType) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String queueFullName = null;
		String hashFullName = null;
		
		if (queueType) {				
			queueFullName = queueNewFullMap.get(queueName);
			if (queueFullName == null) {
				queueFullName = queueName + queuePostfix + typeNewPostfix;
				queueNewFullMap.putIfAbsent(queueName, queueFullName);
			}
			hashFullName = hashNewFullMap.get(queueName);
			if (hashFullName == null) {
				hashFullName = queueName + hashPostfix + typeNewPostfix;
				hashNewFullMap.putIfAbsent(queueName, hashFullName);
			}
		} else {
			queueFullName = queuePortionFullMap.get(queueName);
			if (queueFullName == null) {
				queueFullName = queueName + queuePostfix + typePortionPostfix;
				queuePortionFullMap.putIfAbsent(queueName, queueFullName);
			}
			hashFullName = hashPortionFullMap.get(queueName);
			if (hashFullName == null) {
				hashFullName = queueName + hashPostfix + typePortionPostfix;
				hashPortionFullMap.putIfAbsent(queueName, hashFullName);
			}
		}
		String activeFullName = activeFullMap.get(queueName);
		if (activeFullName == null) {
			activeFullName = queueName + activePostfix;
			activeFullMap.putIfAbsent(queueName, activeFullName);
		}
		
		if (queueType) {
			overtimePerformanceExecuterService.before(
					MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
					connectId,
					queueName,
					MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP);
		} else {
			overtimePerformanceExecuterService.before(
					MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
					connectId,
					queueName,
					MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP);
		}
		
		try {
			
			if (!jedis.exists(activeFullName)) {
				queueIsActiveMap.put(queueName, false);
				jedisPool.returnResource(jedis);
				if (queueType) {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							queueName,
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP,
							false);
				} else {
					successPerformanceExecuterService.result(
							MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
							connectId,
							queueName,
							MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP,
							false);
				}
				return null;
			}
			
			List<String> messageDmStrList = jedis.blpop(queueCacheConnectAgentConfParam.getPopTimeout(), queueFullName);
			if (messageDmStrList != null && messageDmStrList.size() >= 2) {
				
				MessageDm messageDm = (MessageDm) JsonTranslator.fromString(messageDmStrList.get(1), MessageDm.class);
				
				String value = jedis.hget(hashFullName, messageDm.getUuid());
				if (value != null && Long.valueOf(value) == messageDm.getCacheTimestamp()) {
					
					jedis.multi();
					Transaction transaction = new Transaction(jedis.getClient());
					transaction.set(messageDm.getUuid(), "");
					transaction.expire(messageDm.getUuid(), queueCacheConnectAgentConfParam.getExpire());
					transaction.hdel(hashFullName, messageDm.getUuid());
					transaction.exec();
					
					jedisPool.returnResource(jedis);
					if (queueType) {
						overtimePerformanceExecuterService.after(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								queueName,
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP);
					} else {
						overtimePerformanceExecuterService.after(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								queueName,
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP);
					}
					
					if (queueType) {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								queueName,
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP,
								true);
					} else {
						successPerformanceExecuterService.result(
								MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
								connectId,
								queueName,
								MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP,
								true);
					}
					return messageDm;
				}
			}
			
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Pop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			if (queueType) {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						queueName,
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP,
						false);
			} else {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						queueName,
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP,
						false);
			}
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Pop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			if (queueType) {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						queueName,
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP,
						false);
			} else {
				successPerformanceExecuterService.result(
						MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
						connectId,
						queueName,
						MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP,
						false);
			}
			throw e;
		}
		
		if (queueType) {
			successPerformanceExecuterService.result(
					MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
					connectId,
					queueName,
					MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP,
					false);
		} else {
			successPerformanceExecuterService.result(
					MonitorPerformanceConstant.MODULE_ID_QUEUECACHE,
					connectId,
					queueName,
					MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP,
					false);
		}
		
		jedisPool.returnResource(jedis);
		
		return null;
	}

	@Override
	public boolean havePop(MessageDm messageDm) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		boolean bool = false;
		
		try {
			bool = jedis.exists(messageDm.getUuid());
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("HavePop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Have Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("HavePop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Have Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return bool;
	}
	
	@Override
	public void setPop(MessageDm messageDm) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		try {
			jedis.multi();
			Transaction transaction = new Transaction(jedis.getClient());
			transaction.set(messageDm.getUuid(), "");
			transaction.expire(messageDm.getUuid(), queueCacheConnectAgentConfParam.getExpire());
			transaction.exec();
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("SetPop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetPop -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set Pop -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}

		jedisPool.returnResource(jedis);
	}
	
	@Override
	public void removePop(MessageDm messageDm) throws Exception {
		
		if (connectStatus.get() == false) {
			cancelCountDownLatch(messageDm);
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		if (!removeBlockingQueue.offer(messageDm)) {
			cancelCountDownLatch(messageDm);
		}
	}

	@Override
	public void setActive(String queueName, boolean bool) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String queueNewFullName = queueNewFullMap.get(queueName);
		if (queueNewFullName == null) {
			queueNewFullName = queueName + queuePostfix + typeNewPostfix;
			queueNewFullMap.putIfAbsent(queueName, queueNewFullName);
		}
		String hashNewFullName = hashNewFullMap.get(queueName);
		if (hashNewFullName == null) {
			hashNewFullName = queueName + hashPostfix + typeNewPostfix;
			hashNewFullMap.putIfAbsent(queueName, hashNewFullName);
		}
		String queuePortionFullName = queuePortionFullMap.get(queueName);
		if (queuePortionFullName == null) {
			queuePortionFullName = queueName + queuePostfix + typePortionPostfix;;
			queuePortionFullMap.putIfAbsent(queueName, queuePortionFullName);
		}
		String hashPortionFullName = hashPortionFullMap.get(queueName);
		if (hashPortionFullName == null) {
			hashPortionFullName = queueName + hashPostfix + typePortionPostfix;;
			hashPortionFullMap.putIfAbsent(queueName, hashPortionFullName);
		}
		String activeFullName = activeFullMap.get(queueName);
		if (activeFullName == null) {
			activeFullName = queueName + activePostfix;
			activeFullMap.putIfAbsent(queueName, activeFullName);
		}
		
		try {
			jedis.del(queueNewFullName, 
					hashNewFullName, 
					queuePortionFullName,
					hashPortionFullName);
			
			if (bool) {
				jedis.setnx(activeFullName, "");
				queueIsActiveMap.put(queueName, true);
			} else {
				jedis.del(activeFullName);
				queueIsActiveMap.put(queueName, false);
			}
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("SetActive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set Active -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetActive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set Active -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
	}

	@Override
	public boolean isActive(String queueName) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Boolean isActive = queueIsActiveMap.get(queueName);
		if (isActive != null && isActive.equals(true)) {
			return true;
		}
		
		Jedis jedis = getConnect();

		String activeFullName = activeFullMap.get(queueName);
		if (activeFullName == null) {
			activeFullName = queueName + activePostfix;
			activeFullMap.putIfAbsent(queueName, activeFullName);
		}
		
		boolean bool = false;
		
		try {
			bool = jedis.exists(activeFullName);
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("IsActive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Is Active -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("IsActive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Is Active -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		queueIsActiveMap.put(queueName, bool);
		
		jedisPool.returnResource(jedis);
		
		return bool;
	}
	

	@Override
	public long len(String queueName, boolean queueType) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String queueFullName = null;
		
		if (queueType) {				
			queueFullName = queueNewFullMap.get(queueName);
			if (queueFullName == null) {
				queueFullName = queueName + queuePostfix + typeNewPostfix;
				queueNewFullMap.putIfAbsent(queueName, queueFullName);
			}
		} else {
			queueFullName = queuePortionFullMap.get(queueName);
			if (queueFullName == null) {
				queueFullName = queueName + queuePostfix + typePortionPostfix;
				queuePortionFullMap.putIfAbsent(queueName, queueFullName);
			}
		}
		String activeFullName = activeFullMap.get(queueName);
		if (activeFullName == null) {
			activeFullName = queueName + activePostfix;
			activeFullMap.putIfAbsent(queueName, activeFullName);
		}
		
		long len = 0;
		
		try {
			if (!jedis.exists(activeFullName)) {
				queueIsActiveMap.put(queueName, false);
				jedisPool.returnResource(jedis);
				return len;
			}
			len = jedis.llen(queueFullName);
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Len -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Len -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Len -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Len -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return len;
	}

	@Override
	public long getDiffTime() throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		long diffTime = 0;
		
		try {
			jedis.set(diffTimeFullName, "");
			jedis.expireAt(diffTimeFullName, Integer.MAX_VALUE);
			diffTime = System.currentTimeMillis() - (Integer.MAX_VALUE - jedis.ttl(diffTimeFullName)) * 1000;
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("GetDiffTime -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get DiffTime -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetDiffTime -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get DiffTime -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return diffTime;
	}

	@Override
	public boolean getAlive(String queueName, long id, long diffTime,
			long intervalTime) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String lockerFullName = lockerFullMap.get(queueName);
		if (lockerFullName == null) {
			lockerFullName = queueName + lockerPostfix;
			lockerFullMap.putIfAbsent(queueName, lockerFullName);
		}
		
		boolean isAlive = false;
		
		try {
			if(jedis.setnx(lockerFullName, String.valueOf(id)) != 0) {
				if (jedis.expire(lockerFullName, (int)(intervalTime / 1000)) != 0) {
					isAlive = true;
				}
			} else {
				if (jedis.ttl(lockerFullName) == -1) {
					jedis.expire(lockerFullName, (int)(intervalTime / 1000));
				}
			}
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return isAlive;
	}

	@Override
	public boolean keepAlive(String queueName, long id, long diffTime,
			long intervalTime) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String lockerFullName = lockerFullMap.get(queueName);
		if (lockerFullName == null) {
			lockerFullName = queueName + lockerPostfix;
			lockerFullMap.putIfAbsent(queueName, lockerFullName);
		}
		
		boolean isAlive = false;
		
		try {
			String strId = jedis.get(lockerFullName);
			if (strId != null) {
				if (Long.valueOf(strId) == id) {
					if (jedis.expire(lockerFullName, (int)(intervalTime / 1000)) != 0) {
						strId = jedis.get(lockerFullName);
						if (strId != null) {
							if (Long.valueOf(strId) == id) {
								isAlive = true;
							}
						}
					}
				}
			}
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Keep Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Keep Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return isAlive;
	}

	@Override
	public void releaseAlive(String queueName, long id) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		String lockerFullName = lockerFullMap.get(queueName);
		if (lockerFullName == null) {
			lockerFullName = queueName + lockerPostfix;
			lockerFullMap.putIfAbsent(queueName, lockerFullName);
		}
		
		try {
			String strId = jedis.get(lockerFullName);
			if (strId != null) {
				if (Long.valueOf(strId) == id) {
					if (jedis.expire(lockerFullName, lockerOvertime) != 0) {
						strId = jedis.get(lockerFullName);
						if (strId != null) {
							if (Long.valueOf(strId) == id) {
								jedis.del(lockerFullName);
							}
						}
					}
				}
			}
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("ReleaseAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Release Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReleaseAlive -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Release Alive -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the queue redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		try {
			jedis.exists("CheckHealth");
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Check Health -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Check Health -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
	}
	
	private Jedis getConnect() throws Exception {
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis;
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("GetConnect -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", GetConnect -> " + e);
			}
			throw new ConnectionResetException("Connection reset for queue redis connect agent");
		}
	}
	
	private void cancelCountDownLatch(MessageDm messageDm) {
		if (messageDm.getObjectOne() != null) {					
			CountDownLatch countDownLatch = (CountDownLatch) messageDm.getObjectOne();
			countDownLatch.countDown();
		}
	}
	
	public void setQueuePostfix(String queuePostfix) {
		this.queuePostfix = queuePostfix;
		this.hashPostfix = queuePostfix + "-Hash";
	}

	public void setQueueCacheConnectAgentConfParam(
			QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}

	@Override
	public boolean checkIsActive(String queueName) throws Exception {
		return isActive(queueName);
	}

	@Override
	public long checkNewLen(String queueName) throws Exception {
		return len(queueName, true);
	}

	@Override
	public long checkPortionLen(String queueName) throws Exception {
		return len(queueName, false);
	}
	
	public void setOvertimePerformanceExecuterService(
			OvertimePerformanceExecuterService overtimePerformanceExecuterService) {
		this.overtimePerformanceExecuterService = overtimePerformanceExecuterService;
	}

	public void setSuccessPerformanceExecuterService(
			SuccessPerformanceExecuterService successPerformanceExecuterService) {
		this.successPerformanceExecuterService = successPerformanceExecuterService;
	}
}
