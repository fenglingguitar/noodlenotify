package org.fl.noodlenotify.monitor.performance.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodle.common.mvc.vo.PageVo;

public class RedisPersistenceTemplateImpl extends JedisPoolConfig implements RedisPersistenceTemplate {
	
	private final static Logger logger = LoggerFactory.getLogger(RedisPersistenceTemplateImpl.class);
	
	private JedisPool jedisPool;
	
	private String ip;
	private int port;
	private int timeout;
	
	public void start() throws Exception {
		jedisPool = new JedisPool(this, ip, port, timeout);
	}
	
	public void destroy() throws Exception {
		jedisPool.destroy();
	}

	public <T> PageVo<T> queryPage(String keyName, double min, double max, int pageIndex, int pageSize, Class<T> clazz) throws Exception {
				
		long total = 0;
		
		int start = pageIndex > 0 && pageSize > 0 ? (pageIndex - 1) * pageSize : 0;

		Set<String> values = null;
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryPage -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		try {
			values = jedis.zrangeByScore(keyName, min, max, start, pageSize);
			total = jedis.zcount(keyName, min, max);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryPage -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", ZrangeByScore And Zcount -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
		
		List<T> list = new ArrayList<T>();

		try {
			if (values != null) {				
				for (String value : values) {
					T t = JsonTranslator.fromString(value, clazz);
					list.add(t);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryPage -> "
							+ "Ip: " + ip
							+ ", Port: " + port
							+ ", KeyName: " + keyName
							+ ", JsonTranslator -> " 
							+ e);
			}
		}
		
		return new PageVo<T>(start, total, pageSize, list);
	}

	public <T> List<T> queryList(String keyName, double min, double max, Class<T> clazz) throws Exception {
		
		Set<String> values = null;
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryList -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		try {
			values = jedis.zrangeByScore(keyName, min, max);		
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryList -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", ZrangeByScore -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
		
		List<T> result = new ArrayList<T>();

		try {
			if (values != null) {				
				for (String value : values) {
					T t = JsonTranslator.fromString(value, clazz);
					result.add(t);
				}	
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("QueryList -> "
							+ "Ip: " + ip
							+ ", Port: " + port
							+ ", KeyName: " + keyName
							+ ", JsonTranslator -> " 
							+ e);
			}
		}
		
		return result;
	}

	public <T> void insert(String keyName, double score, Object vo) throws Exception {
		
		String member = null;
		
		try {
			member = JsonTranslator.toString(vo);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Insert -> "
							+ "Ip: " + ip
							+ ", Port: " + port
							+ ", KeyName: " + keyName
							+ ", JsonTranslator -> " 
							+ e);
			}
			return;
		}
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Insert -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		try {		
			jedis.zadd(keyName, score, member);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Insert -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", Zadd -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
	}

	public <T> void delete(String keyName, Object vo) throws Exception {
		
		String member = null;
		
		try {
			member = JsonTranslator.toString(vo);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Delete -> "
							+ "Ip: " + ip
							+ ", Port: " + port
							+ ", KeyName: " + keyName
							+ ", JsonTranslator -> " 
							+ e);
			}
			return;
		}
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Delete -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		try {
			jedis.zrem(keyName, member);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Delete -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", Zrem -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
	}

	public <T> void deletes(String keyName, double min, double max) throws Exception {
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("Deletes -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		try {
			jedis.zremrangeByScore(keyName, min, max);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Deletes -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", KeyName: " + keyName
						+ ", ZremrangeByScore -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
	}


	@Override
	public Set<String> getKeys() throws Exception {
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetKeys -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", GetResource -> " 
						+ e);
			}
			throw e;
		}
		
		Set<String> keysSet = null;
		
		try {
			keysSet = jedis.keys("*");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetKeys -> " 
						+ "Ip: " + ip
						+ ", Port: " + port
						+ ", Keys -> " 
						+ e);
			}
			jedisPool.returnBrokenResource(jedis);
		} 
		jedisPool.returnResource(jedis);
		
		return keysSet;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
