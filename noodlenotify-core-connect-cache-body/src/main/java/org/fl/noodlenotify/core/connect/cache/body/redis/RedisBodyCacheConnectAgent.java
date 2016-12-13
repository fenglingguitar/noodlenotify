package org.fl.noodlenotify.core.connect.cache.body.redis;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodlenotify.core.connect.cache.AbstractCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheStatusChecker;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;


public class RedisBodyCacheConnectAgent extends AbstractCacheConnectAgent implements BodyCacheConnectAgent, BodyCacheStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(RedisBodyCacheConnectAgent.class);

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
			if (logger.isErrorEnabled()) {
				logger.error("ConnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
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
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectRefusedException("Connection refused for create body redis connect agent");
		}
	}

	@Override
	protected void closeCacheActual() {
		jedisPool.destroy();
	}
	
	@Override
	protected void setActual(List<MessageDm> messageDmList) {
		
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
			try {
				if (messageDm.getContent() != null) {	
					long dbSize = jedis.dbSize();
					if (dbSize < bodyCacheConnectAgentConfParam.getCapacity()) {
						jedis.multi();
						Transaction transaction = new Transaction(jedis.getClient());
						transaction.setnx(messageDm.getUuid(), new String(messageDm.getContent()));
						transaction.expire(messageDm.getUuid(), bodyCacheConnectAgentConfParam.getExpire());
						transaction.exec();
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("SetActual -> " 
									+ "ConnectId: " + connectId
									+ ", Ip: " + ip
									+ ", Port: " + port
									+ ", Capacity: " + bodyCacheConnectAgentConfParam.getCapacity()
									+ ", DbSize: " + dbSize
									+ ", Beyond Capacity");
						}
						continue;
					}
				}
			} catch (JedisConnectionException e) {
				if (logger.isErrorEnabled()) {
					logger.error("SetActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Set -> " + e);
				}
				jedisPool.returnBrokenResource(jedis);
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
				continue;
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
				jedis.del(messageDm.getUuid());
			} catch (JedisConnectionException e) {
				if (logger.isErrorEnabled()) {
					logger.error("SetActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", Set -> " + e);
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
	public void set(MessageDm messageDm) throws Exception {
		setBlockingQueue.offer(messageDm);
	}
	
	@Override
	public MessageDm get(MessageDm messageDm) throws Exception {
		
		Jedis jedis = getConnect();
		
		try {
			String messageDmContentStr = jedis.get(messageDm.getUuid());
			if (messageDmContentStr == null) {
				messageDm.setContent(null);
			} else {
				messageDm.setContent(messageDmContentStr.getBytes());
			}
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectResetException("Connection reset for body redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Get -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return messageDm;
	}

	@Override
	public void remove(MessageDm messageDm) throws Exception {
		removeBlockingQueue.offer(messageDm);
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		Jedis jedis = getConnect();
		
		try {
			jedis.exists("CheckHealth");
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectResetException("Connection reset for body redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "QueueCache: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get DiffTime -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
	}
	
	@Override
	public long checkSize() throws Exception {
		
		Jedis jedis = getConnect();
		
		long size = 0;
		
		try {
			size = jedis.dbSize();
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("SetActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Set -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectResetException("Connection reset for body redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "QueueCache: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get DiffTime -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return size;
	}
	
	private Jedis getConnect() throws Exception {
		
		Jedis jedis = null;
		
		try {
			jedis = jedisPool.getResource();
			return jedis;
		} catch (JedisConnectionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetConnect -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Connect -> " + e);
			}
			throw new ConnectResetException("Connection reset for body redis connect agent");
		}
	}
	
	public void setBodyCacheConnectAgentConfParam(
			BodyCacheConnectAgentConfParam bodyCacheConnectAgentConfParam) {
		this.bodyCacheConnectAgentConfParam = bodyCacheConnectAgentConfParam;
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return BodyCacheConnectAgent.class;
	}
}
