package org.fl.noodlenotify.core.connect.cache.body.redis;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.cache.AbstractCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.redis.JedisTemplate;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class RedisBodyCacheConnectAgent extends AbstractCacheConnectAgent implements BodyCacheConnectAgent {

	//private final static Logger logger = LoggerFactory.getLogger(RedisBodyCacheConnectAgent.class);

	private JedisPool jedisPool;
	
	private BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam;
	
	public RedisBodyCacheConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList,
			CacheConnectAgentConfParam cacheConnectAgentConfParam,
			BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam) {
		super(
			connectId, ip, port, url, ConnectAgentType.BODY_CACHE.getCode(), 
			connectTimeout, readTimeout, encoding,
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList, cacheConnectAgentConfParam);
		this.bodyCacheConnectAgentConfParam = bodyCacheConnectAgentConfParam;
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
			throw new ConnectRefusedException("Connection refused for create body redis connect agent");
		} 
	}

	@Override
	protected void reconnectCacheActual() throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create body redis connect agent");
		}
	}

	@Override
	protected void closeCacheActual() {
		jedisPool.destroy();
	}
	
	@Override
	protected void setActual(final List<MessageDb> messageDbList) {
		try {
			JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
				
				@Override
				public Void doOperation(Jedis jedis) throws Exception {
					
					for (MessageDb messageDb : messageDbList) {
						try {
							if (messageDb.getContent() != null) {
								long dbSize = jedis.dbSize();
								if (dbSize < bodyCacheConnectAgentConfParam.getCapacity()) {
									jedis.multi();
									Transaction transaction = new Transaction(jedis.getClient());
									transaction.setnx(messageDb.getUuid(), new String(messageDb.getContent()));
									transaction.expire(messageDb.getUuid(), bodyCacheConnectAgentConfParam.getExpire());
									transaction.exec();
								}
							}
						} catch (JedisConnectionException e) {
							e.printStackTrace();
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void removeActual(final List<MessageDb> messageDbList) {
		try {
			JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<Void>() {
				
				@Override
				public Void doOperation(Jedis jedis) throws Exception {
					
					for (MessageDb messageDb : messageDbList) {
						try {
							jedis.del(messageDb.getUuid());
						} catch (JedisConnectionException e) {
							e.printStackTrace();
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void set(MessageDb messageDb) throws Exception {
		setBlockingQueue.offer(messageDb);
	}
	
	@Override
	public MessageDb get(final MessageDb messageDb) throws Exception {
		
		return JedisTemplate.execute(jedisPool, new JedisTemplate.JedisOperation<MessageDb>() {
			
			@Override
			public MessageDb doOperation(Jedis jedis) throws Exception {
				String messageDbContentStr = jedis.get(messageDb.getUuid());
				if (messageDbContentStr == null) {
					messageDb.setContent(null);
				} else {
					messageDb.setContent(messageDbContentStr.getBytes());
				}
				return messageDb;
			}
		});
	}

	@Override
	public void remove(MessageDb messageDb) throws Exception {
		removeBlockingQueue.offer(messageDb);
	}
	
	public void setBodyCacheConnectAgentConfParam(BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam) {
		this.bodyCacheConnectAgentConfParam = bodyCacheConnectAgentConfParam;
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return BodyCacheConnectAgent.class;
	}
}
