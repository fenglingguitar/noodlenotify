package org.fl.noodlenotify.core.connect.cache.queue.redis;

import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodlenotify.core.connect.cache.CachePostfix;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.redis.JedisTemplate;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.status.AbstractStatusChecker;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class RedisQueueCacheStatusChecker extends AbstractStatusChecker implements QueueCacheStatusChecker {

	//private final static Logger logger = LoggerFactory.getLogger(RedisBodyCacheConnectAgent.class);

	private Jedis jedis;
	
	public RedisQueueCacheStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding) {
		super(
			connectId, ip, port, url, ConnectAgentType.QUEUE_CACHE.getCode(), 
			connectTimeout, readTimeout, encoding);
	}

	@Override
	protected void connect() throws Exception {
		jedis = new Jedis(ip, port, readTimeout);
		try {
			jedis.connect();
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create body redis connect agent");
		} 
	}

	@Override
	protected void close() {
		jedis.disconnect();
	}
	
	@Override
	protected Class<?> getServiceInterfaces() {
		return QueueCacheStatusChecker.class;
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		JedisTemplate.execute(jedis, new JedisTemplate.JedisOperation<Void>() {
			
			@Override
			public Void doOperation(Jedis jedis) throws Exception {
				jedis.exists("CheckHealth");
				return null;
			}
		});
		
	}
	
	@Override
	public boolean checkIsActive(final String queueName) throws Exception {
		
		return JedisTemplate.execute(jedis, new JedisTemplate.JedisOperation<Boolean>() {
			
			@Override
			public Boolean doOperation(Jedis jedis) throws Exception {
				return jedis.exists(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE));
			}
		});
	}

	@Override
	public long checkNewLen(final String queueName) throws Exception {
		
		return JedisTemplate.execute(jedis, new JedisTemplate.JedisOperation<Long>() {
			
			@Override
			public Long doOperation(Jedis jedis) throws Exception {
				if (jedis.exists(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE))) {
					return jedis.llen(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_NEW));
				}
				return 0L;
			}
		});
	}

	@Override
	public long checkPortionLen(final String queueName) throws Exception {
		
		return JedisTemplate.execute(jedis, new JedisTemplate.JedisOperation<Long>() {
			
			@Override
			public Long doOperation(Jedis jedis) throws Exception {
				if (jedis.exists(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_ACTIVE))) {
					return jedis.llen(CachePostfix.getKey(queueName, CachePostfix.KeyPostfix.QUEUE_PORTION));
				}
				return 0L;
			}
		});
	}
}
