package org.fl.noodlenotify.core.connect.cache.trace.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.cache.CacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgentAbstract;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;
import org.fl.noodlenotify.core.connect.exception.ConnectionRefusedException;
import org.fl.noodlenotify.core.connect.exception.ConnectionResetException;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;

public class RedisTraceCacheConnectAgent extends TraceCacheConnectAgentAbstract implements TraceCacheConnectAgent, TraceCacheStatusChecker {

	private final static Logger logger = LoggerFactory.getLogger(RedisTraceCacheConnectAgent.class);
	
	private final static Logger loggerPersistence = LoggerFactory.getLogger("other.noodlenotify.trace.persistence");
	
	private JedisPool jedisPool;
	
	private TraceCacheConnectAgentConfParam traceCacheConnectAgentConfParam;
	
	public RedisTraceCacheConnectAgent(String ip, int port, long connectId) {
		super(ip, port, connectId);
		this.traceCacheConnectAgentConfParam = new TraceCacheConnectAgentConfParam();
	}

	public RedisTraceCacheConnectAgent(String ip, int port, long connectId,
			CacheConnectAgentConfParam redisConnectAgentConfParam,
			TraceCacheConnectAgentConfParam traceCacheConnectAgentConfParam) {
		super(ip, port, connectId, redisConnectAgentConfParam);
		this.traceCacheConnectAgentConfParam = traceCacheConnectAgentConfParam;
	}

	@Override
	protected void connectCacheActual() throws Exception {
		
		jedisPool = new JedisPool(cacheConnectAgentConfParam, ip, port, cacheConnectAgentConfParam.getTimeout());
		
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			jedisPool.destroy();
			throw new ConnectionRefusedException("Connection refused for create trace redis connect agent");
		}
	}

	@Override
	protected void reconnectCacheActual() throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectCacheActual -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Resource -> " + e);
			}
			throw new ConnectionRefusedException("Connection refused for create trace redis connect agent");
		}
	}

	@Override
	protected void closeCacheActual() {
		jedisPool.destroy();
	}
	
	@Override
	protected void setActual(List<TraceVo> traceVoList) {
		
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
		
		for (TraceVo traceVo : traceVoList) {
			try {
				long dbSize = jedis.dbSize();
				if (dbSize < traceCacheConnectAgentConfParam.getCapacity()) {
					jedis.multi();
					Transaction transaction = new Transaction(jedis.getClient());
					transaction.sadd(traceVo.getUuid(), JsonTranslator.toString(traceVo));
					transaction.expire(traceVo.getUuid(), traceCacheConnectAgentConfParam.getNewExpire());
					transaction.exec();
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("SetActual -> " 
								+ "ConnectId: " + connectId
								+ ", Ip: " + ip
								+ ", Port: " + port
								+ ", Capacity: " + traceCacheConnectAgentConfParam.getCapacity()
								+ ", DbSize: " + dbSize
								+ ", Beyond Capacity");
					}
					continue;
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
				return;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("SetActual -> " 
							+ "ConnectId: " + connectId
							+ ", Ip: " + ip
							+ ", Port: " + port
							+ ", TraceVo: " + traceVo.getUuid()
							+ ", Set -> " + e);
				}
				continue;
			}
		}
		
		jedisPool.returnResource(jedis);
	}

	@Override
	protected void removeActual(List<String> uuidList) {
		
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
		
		for (String uuid : uuidList) {
			try {
				if (loggerPersistence != null) {
					if (loggerPersistence.isDebugEnabled()) {
						Set<String> traceVoStrSet = jedis.smembers(uuid);
						if (traceVoStrSet != null) {
							for (String traceVoStr : traceVoStrSet) {
								loggerPersistence.debug(traceVoStr);
							}
						}
					}
				}
				if (traceCacheConnectAgentConfParam.getSuccessExpire() > 0) {
					jedis.expire(uuid, traceCacheConnectAgentConfParam.getSuccessExpire());
				} else {					
					jedis.del(uuid);
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
							+ ", TraceVo: " + uuid
							+ ", Remove -> " + e);
				}
				continue;
			}
		}
		
		jedisPool.returnResource(jedis);
	}
	
	@Override
	public void set(TraceVo traceVo) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the trace redis connect agent");
		}
		
		setBlockingQueue.offer(traceVo);
	}
	
	@Override
	public List<TraceVo> gets(String uuid) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the trace redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		List<TraceVo> traceVoList = new ArrayList<TraceVo>();
		Set<String> traceVoStrSet = null;
		
		try {
			traceVoStrSet = jedis.smembers(uuid);
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("Gets -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Smembers -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for trace redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Gets -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Smembers -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		if (traceVoStrSet != null) {
			for (String traceVoStr : traceVoStrSet) {
				try {
					traceVoList.add(JsonTranslator.fromString(traceVoStr, TraceVo.class));
				} catch (JedisConnectionException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Get -> " 
								+ "ConnectId: " + connectId
								+ ", Ip: " + ip
								+ ", Port: " + port
								+ ", JsonTranslator FromString -> " + e);
					}
				}
			}
		}
		
		jedisPool.returnResource(jedis);
		
		return traceVoList;
	}

	@Override
	public void remove(String uuid) throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the trace redis connect agent");
		}
		
		removeBlockingQueue.offer(uuid);
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the trace redis connect agent");
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
			throw new ConnectionResetException("Connection reset for trace redis connect agent");
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
	
	@Override
	public long checkSize() throws Exception {
		
		if (connectStatus.get() == false) {
			throw new ConnectionUnableException("Connection disable for the trace redis connect agent");
		}
		
		Jedis jedis = getConnect();
		
		long size = 0;
		
		try {
			size = jedis.dbSize();
		} catch (JedisConnectionException e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("CheckSize -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Check Size -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw new ConnectionResetException("Connection reset for trace redis connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CheckSize -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Check Size -> " + e);
			}
			jedisPool.returnBrokenResource(jedis);
			throw e;
		}
		
		jedisPool.returnResource(jedis);
		
		return size;
	}
	
	private Jedis getConnect() throws Exception {
		try {
			Jedis jedis = jedisPool.getResource();
			return jedis;
		} catch (Exception e) {
			connectStatus.set(false);
			if (logger.isErrorEnabled()) {
				logger.error("GetConnect -> " 
						+ "ConnectId: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", GetConnect -> " + e);
			}
			throw new ConnectionResetException("Connection reset for create queue redis connect agent");
		}
	}
	
	public void setTraceCacheConnectAgentConfParam(
			TraceCacheConnectAgentConfParam traceCacheConnectAgentConfParam) {
		this.traceCacheConnectAgentConfParam = traceCacheConnectAgentConfParam;
	}
}
