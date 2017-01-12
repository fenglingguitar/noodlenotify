package org.fl.noodlenotify.core.connect.cache.body.redis;

import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.redis.JedisTemplate;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.status.AbstractStatusChecker;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class RedisBodyCacheStatusChecker extends AbstractStatusChecker implements BodyCacheStatusChecker {

	//private final static Logger logger = LoggerFactory.getLogger(RedisBodyCacheConnectAgent.class);

	private Jedis jedis;
	
	public RedisBodyCacheStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding) {
		super(
			connectId, ip, port, url, ConnectAgentType.BODY_CACHE.getCode(), 
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
	public long checkSize() throws Exception {
		
		return JedisTemplate.execute(jedis, new JedisTemplate.JedisOperation<Long>() {
			
			@Override
			public Long doOperation(Jedis jedis) throws Exception {
				return jedis.dbSize();
			}
		});
		
	}
	
	@Override
	protected Class<?> getServiceInterfaces() {
		return BodyCacheStatusChecker.class;
	}
}
