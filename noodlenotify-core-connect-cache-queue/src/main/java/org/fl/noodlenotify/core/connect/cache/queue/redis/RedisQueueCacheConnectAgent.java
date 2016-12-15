package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.cache.AbstractCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.CachePostfix;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.redis.JedisTemplate;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.core.domain.message.MessageQueueDm;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisQueueCacheConnectAgent extends AbstractCacheConnectAgent implements QueueCacheConnectAgent, QueueCacheStatusChecker {

	//private final static Logger logger = LoggerFactory.getLogger(RedisQueueCacheConnectAgent.class);
	
	private final int lockerOvertime = 5;
	
	private JedisPool jedisPool;
	
	private QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam;
	
	private ConcurrentMap<String, Boolean> queueIsActiveMap = new ConcurrentHashMap<String, Boolean>();

	public RedisQueueCacheConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList,
			CacheConnectAgentConfParam cacheConnectAgentConfParam,
			QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		super(
			connectId, ip, port, url, ConnectAgentType.QUEUE_CACHE.getCode(), 
			connectTimeout, readTimeout, encoding,
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList, cacheConnectAgentConfParam);
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}
	
	@Override
	protected void connectCacheActual() throws Exception {
		
		jedisPool = new JedisPool(cacheConnectAgentConfParam, ip, port, cacheConnectAgentConfParam.getTimeout());
		
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			jedisPool.destroy();
			throw new ConnectRefusedException("Connection refused for queue redis connect agent");
		}
	}

	@Override
	protected void reconnectCacheActual() throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for queue redis connect agent");
		}
	}

	@Override
	protected void closeCacheActual() {
		jedisPool.destroy();
	}
	
	protected void setActual(List<MessageDm> messageDmList) {
	}
	
	@Override
	protected void removeActual(List<MessageDm> messageDmList) {
	}
	
	@Override
	public boolean push(MessageDm messageDm) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				
				MessageQueueDm messageQueueDm = new MessageQueueDm();

				String queueFullName = null;
				String hashFullName = null;
				
				if (messageDm.getBool()) {				
					queueFullName = CachePostfix.getKey(messageDm.getQueueName(), CachePostfix.KeyPostfix.QUEUE_NEW);
					hashFullName = CachePostfix.getKey(messageDm.getQueueName(), CachePostfix.KeyPostfix.QUEUE_HASH_NEW);
					
				} else {
					queueFullName = CachePostfix.getKey(messageDm.getQueueName(), CachePostfix.KeyPostfix.QUEUE_PORTION);
					hashFullName = CachePostfix.getKey(messageDm.getQueueName(), CachePostfix.KeyPostfix.QUEUE_HASH_PORTION);
				}
				String activeFullName = CachePostfix.getKey(messageDm.getQueueName(), CachePostfix.KeyPostfix.QUEUE_ACTIVE);
				
				if (!jedis.exists(activeFullName)) {
					queueIsActiveMap.put(messageDm.getQueueName(), false);
					return false;
				}
				
				if (jedis.exists(messageDm.getUuid())) {
					return false;
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
					return false;
				}
				
				if (jedis.exists(messageDm.getUuid())) {
					jedis.hdel(hashFullName, messageDm.getUuid());
					return false;
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
				
				return true;
			}
		});
	}

	@Override
	public MessageDm pop(String queueName, boolean queueType) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<MessageDm>() {
			
			@Override
			public MessageDm doOperation(Jedis jedis) throws Exception {
				
				String queueFullName = null;
				String hashFullName = null;
				
				if (queueType) {				
					queueFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_NEW);
					hashFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_HASH_NEW);
				} else {
					queueFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_PORTION);
					hashFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_HASH_PORTION);
				}
				String activeFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE);
				
				if (!jedis.exists(activeFullName)) {
					queueIsActiveMap.put(queueName, false);
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
						
						return messageDm;
					}
				}
				
				return null;
			}
		});
	}

	@Override
	public boolean havePop(MessageDm messageDm) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				return jedis.exists(messageDm.getUuid());
			}
		});
	}
	
	@Override
	public void setPop(MessageDm messageDm) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				jedis.multi();
				Transaction transaction = new Transaction(jedis.getClient());
				transaction.set(messageDm.getUuid(), "");
				transaction.expire(messageDm.getUuid(), queueCacheConnectAgentConfParam.getExpire());
				transaction.exec();
				return null;
			}
		});
	}
	
	@Override
	public void removePop(MessageDm messageDm) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
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
				return null;
			}
		});
	}

	@Override
	public void setActive(String queueName, boolean bool) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				
				String queueNewFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_NEW);
				String hashNewFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_HASH_NEW);
				String queuePortionFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_PORTION);
				String hashPortionFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_HASH_PORTION);
				String activeFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE);
				
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
				
				return null;
			}
		});
	}

	@Override
	public boolean isActive(String queueName) throws Exception {
		
		Boolean isActive = queueIsActiveMap.get(queueName);
		if (isActive != null && isActive.equals(true)) {
			return true;
		}
		
		boolean bool =  JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				return jedis.exists(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE));
			}
		});
		
		queueIsActiveMap.put(queueName, bool);

		return bool;
	}
	
	@Override
	public long len(String queueName, boolean queueType) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Long>() {
			
			@Override
			public Long doOperation(Jedis jedis) throws Exception {
				
				String queueFullName = null;
				
				if (queueType) {				
					queueFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_NEW);
				} else {
					queueFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_PORTION);
				}
				String activeFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE);
				
				if (!jedis.exists(activeFullName)) {
					queueIsActiveMap.put(queueName, false);
					return 0L;
				}
				return jedis.llen(queueFullName);
			}
		});
	}

	@Override
	public long getDiffTime() throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Long>() {
			
			@Override
			public Long doOperation(Jedis jedis) throws Exception {
				jedis.set(CachePostfix.KeyPostfix.QUEUE_DIFFTIME.getCode(), "");
				jedis.expireAt(CachePostfix.KeyPostfix.QUEUE_DIFFTIME.getCode(), Integer.MAX_VALUE);
				return System.currentTimeMillis() - (Integer.MAX_VALUE - jedis.ttl(CachePostfix.KeyPostfix.QUEUE_DIFFTIME.getCode())) * 1000;
			}
		});
	}

	@Override
	public boolean getAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				
				String lockerFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_LOCKER);
				
				if(jedis.setnx(lockerFullName, String.valueOf(id)) != 0) {
					return jedis.expire(lockerFullName, (int)(intervalTime / 1000)) != 0;
				} else {
					if (jedis.ttl(lockerFullName) == -1) {
						jedis.expire(lockerFullName, (int)(intervalTime / 1000));
					}
				}
				
				return false;
			}
		});
	}

	@Override
	public boolean keepAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				
				String lockerFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_LOCKER);
				
				String strId = jedis.get(lockerFullName);
				if (strId != null) {
					if (Long.valueOf(strId) == id) {
						if (jedis.expire(lockerFullName, (int)(intervalTime / 1000)) != 0) {
							strId = jedis.get(lockerFullName);
							if (strId != null) {
								return Long.valueOf(strId) == id;
							}
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public void releaseAlive(String queueName, long id) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				String lockerFullName = CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_LOCKER);
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
				return null;
			}
		});
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				jedis.exists("CheckHealth");
				return null;
			}
		});
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

	@Override
	protected Class<?> getServiceInterfaces() {
		return QueueCacheConnectAgent.class;
	}
}
