package org.fl.noodlenotify.core.connect.cache.redis;

import org.fl.noodle.common.connect.exception.ConnectResetException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class JedisTemplate {
	
	public static <T> T execute(JedisPool jedisPool, JedisOperation<T> jedisOperation) throws Exception {
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			throw new ConnectResetException("Connection reset for queue redis connect agent");
		}
		
		boolean isBrokenResource = false;
		
		try {
			return jedisOperation.doOperation(jedis);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			isBrokenResource = true;
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			e.printStackTrace();
			jedisPool.returnBrokenResource(jedis);
			throw e;
		} finally {
			if (!isBrokenResource) {
				jedisPool.returnResource(jedis);
			}
		}
	}
	
	public static <T> T execute(Jedis jedis, JedisOperation<T> jedisOperation) throws Exception {
		try {
			return jedisOperation.doOperation(jedis);
		} catch (JedisConnectionException e) {
			e.printStackTrace();
			throw new ConnectResetException("Connection reset for queue redis connect agent");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static interface JedisOperation<T> {
		public T doOperation(Jedis jedis) throws Exception;
	}
}
