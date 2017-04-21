package org.fl.noodlenotify.core.connect.cache.queue.redis;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.common.pojo.cache.MessageCache;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.cache.AbstractCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.CachePostfix;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.redis.JedisTemplate;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisQueueCacheConnectAgent extends AbstractCacheConnectAgent implements QueueCacheConnectAgent {

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
	
	protected void setActual(List<MessageDb> messageDbList) {
	}
	
	@Override
	protected void removeActual(List<MessageDb> messageDbList) {
	}
	
	@Override
	public boolean push(final MessageDb messageDb) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				
				MessageCache messageCache = new MessageCache();

				String queueFullName = null;
				String hashFullName = null;
				
				if (messageDb.getBool()) {				
					queueFullName = CachePostfix.getKey(messageDb.getQueueName(), CachePostfix.KeyPostfix.QUEUE_NEW);
					hashFullName = CachePostfix.getKey(messageDb.getQueueName(), CachePostfix.KeyPostfix.QUEUE_HASH_NEW);
					
				} else {
					queueFullName = CachePostfix.getKey(messageDb.getQueueName(), CachePostfix.KeyPostfix.QUEUE_PORTION);
					hashFullName = CachePostfix.getKey(messageDb.getQueueName(), CachePostfix.KeyPostfix.QUEUE_HASH_PORTION);
				}
				String activeFullName = CachePostfix.getKey(messageDb.getQueueName(), CachePostfix.KeyPostfix.QUEUE_ACTIVE);
				
				if (!jedis.exists(activeFullName)) {
					queueIsActiveMap.put(messageDb.getQueueName(), false);
					return false;
				}
				
				if (jedis.exists(messageDb.getUuid())) {
					return false;
				}
				
				long nowTime = System.currentTimeMillis();
				messageCache.setCacheTimestamp(nowTime);
				if (jedis.hsetnx(hashFullName, messageDb.getUuid(), String.valueOf(nowTime)) == 0) {
					String value = jedis.hget(hashFullName, messageDb.getUuid());
					if (value != null) {
						if (System.currentTimeMillis() - Long.valueOf(value) >= queueCacheConnectAgentConfParam.getHashExpire()) {
							jedis.hdel(hashFullName, messageDb.getUuid());
						}
					}
					return false;
				}
				
				if (jedis.exists(messageDb.getUuid())) {
					jedis.hdel(hashFullName, messageDb.getUuid());
					return false;
				}
				
				messageCache.setQueueName(messageDb.getQueueName());
				messageCache.setUuid(messageDb.getUuid());
				messageCache.setContentId(messageDb.getContentId());
				messageCache.setDb(messageDb.getDb());
				messageCache.setId(messageDb.getId());
				messageCache.setExecuteQueue(messageDb.getExecuteQueue());
				messageCache.setResultQueue(messageDb.getResultQueue());
				messageCache.setStatus(messageDb.getStatus());
				messageCache.setRedisOne(messageDb.getRedisOne());
				messageCache.setRedisTwo(messageDb.getRedisTwo());
				messageCache.setBeginTime(messageDb.getBeginTime());
				messageCache.setFinishTime(messageDb.getFinishTime());
				messageCache.setTraceKey(messageDb.getTraceKey());
				messageCache.setParentKey(messageDb.getParentKey());
				
				jedis.rpush(queueFullName, JsonTranslator.toString(messageCache));
				
				return true;
			}
		});
	}

	@Override
	public MessageDb pop(final String queueName, final boolean queueType) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<MessageDb>() {
			
			@Override
			public MessageDb doOperation(Jedis jedis) throws Exception {
				
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
				
				List<String> messageDbStrList = jedis.blpop(queueCacheConnectAgentConfParam.getPopTimeout(), queueFullName);
				if (messageDbStrList != null && messageDbStrList.size() >= 2) {
					
					MessageDb messageDb = (MessageDb) JsonTranslator.fromString(messageDbStrList.get(1), MessageDb.class);
					
					String value = jedis.hget(hashFullName, messageDb.getUuid());
					if (value != null && Long.valueOf(value) == messageDb.getCacheTimestamp()) {
						
						jedis.multi();
						Transaction transaction = new Transaction(jedis.getClient());
						transaction.set(messageDb.getUuid(), "");
						transaction.expire(messageDb.getUuid(), queueCacheConnectAgentConfParam.getExpire());
						transaction.hdel(hashFullName, messageDb.getUuid());
						transaction.exec();
						
						return messageDb;
					}
				}
				
				return null;
			}
		});
	}

	@Override
	public boolean havePop(final MessageDb messageDb) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				return jedis.exists(messageDb.getUuid());
			}
		});
	}
	
	@Override
	public void setPop(final MessageDb messageDb) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				jedis.multi();
				Transaction transaction = new Transaction(jedis.getClient());
				transaction.set(messageDb.getUuid(), "");
				transaction.expire(messageDb.getUuid(), queueCacheConnectAgentConfParam.getExpire());
				transaction.exec();
				return null;
			}
		});
	}
	
	@Override
	public void removePop(final MessageDb messageDb) throws Exception {
		
		JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				if (messageDb.getDelayTime() > 0) {
					long extendTime = System.currentTimeMillis() - messageDb.getFinishTime();
					if (messageDb.getDelayTime() - extendTime > 1000) {
						jedis.expire(messageDb.getUuid(), (int)((messageDb.getDelayTime() - extendTime) / 1000));						
					} else {
						jedis.del(messageDb.getUuid());
					}
				} else {					
					jedis.del(messageDb.getUuid());
				}
				return null;
			}
		});
	}

	@Override
	public void setActive(final String queueName, final boolean bool) throws Exception {
		
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
	public boolean isActive(final String queueName) throws Exception {
		
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
	public long len(final String queueName, final boolean queueType) throws Exception {
		
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
	public boolean getAlive(final String queueName, final long id, final long diffTime, final long intervalTime) throws Exception {
		
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
	public boolean keepAlive(final String queueName, final long id, final long diffTime, final long intervalTime) throws Exception {
		
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
	public void releaseAlive(final String queueName, final long id) throws Exception {
		
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

	public void setQueueCacheConnectAgentConfParam(QueueCacheConnectAgentConfParam queueCacheConnectAgentConfParam) {
		this.queueCacheConnectAgentConfParam = queueCacheConnectAgentConfParam;
	}
	
	@Override
	protected Class<?> getServiceInterfaces() {
		return QueueCacheConnectAgent.class;
	}
}
