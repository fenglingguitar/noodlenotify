package org.fl.noodlenotify.core.connect.cache;

import redis.clients.jedis.JedisPoolConfig;

public class CacheConnectAgentConfParam extends JedisPoolConfig {
	
	private int timeout = 10000;
	
	private int setThreadCount = 10;
	private int setBatchSize = 1000;
	private long setTimeout = 3000;
	private long setWaitTime = 10;
	private int setCapacity = 100000;
	private int setThreadPriority = 5;
	
	private int removeThreadCount = 10;
	private int removeBatchSize = 1000;
	private long removeTimeout = 0;
	private long removeWaitTime = 10;
	private int removeCapacity = 100000;
	private int removeThreadPriority = 5;
	private int removeDelay = 3000;
	
	public CacheConnectAgentConfParam() {
		maxActive = 3000;
 		minIdle = 8;
		maxIdle = 40;
		maxWait = 3000;
		minEvictableIdleTimeMillis = 600000;
		timeBetweenEvictionRunsMillis = 60000;
	}
	
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public int getSetThreadCount() {
		return setThreadCount;
	}
	public void setSetThreadCount(int setThreadCount) {
		this.setThreadCount = setThreadCount;
	}
	public int getSetBatchSize() {
		return setBatchSize;
	}
	public void setSetBatchSize(int setBatchSize) {
		this.setBatchSize = setBatchSize;
	}
	public long getSetTimeout() {
		return setTimeout;
	}
	public void setSetTimeout(long setTimeout) {
		this.setTimeout = setTimeout;
	}
	public long getSetWaitTime() {
		return setWaitTime;
	}
	public void setSetWaitTime(long setWaitTime) {
		this.setWaitTime = setWaitTime;
	}
	public int getSetCapacity() {
		return setCapacity;
	}
	public void setSetCapacity(int setCapacity) {
		this.setCapacity = setCapacity;
	}
	public int getSetThreadPriority() {
		return setThreadPriority;
	}
	public void setSetThreadPriority(int setThreadPriority) {
		this.setThreadPriority = setThreadPriority;
	}
	public int getRemoveThreadCount() {
		return removeThreadCount;
	}
	public void setRemoveThreadCount(int removeThreadCount) {
		this.removeThreadCount = removeThreadCount;
	}
	public int getRemoveBatchSize() {
		return removeBatchSize;
	}
	public void setRemoveBatchSize(int removeBatchSize) {
		this.removeBatchSize = removeBatchSize;
	}
	public long getRemoveTimeout() {
		return removeTimeout;
	}
	public void setRemoveTimeout(long removeTimeout) {
		this.removeTimeout = removeTimeout;
	}
	public long getRemoveWaitTime() {
		return removeWaitTime;
	}
	public void setRemoveWaitTime(long removeWaitTime) {
		this.removeWaitTime = removeWaitTime;
	}
	public int getRemoveCapacity() {
		return removeCapacity;
	}
	public void setRemoveCapacity(int removeCapacity) {
		this.removeCapacity = removeCapacity;
	}
	public int getRemoveThreadPriority() {
		return removeThreadPriority;
	}
	public void setRemoveThreadPriority(int removeThreadPriority) {
		this.removeThreadPriority = removeThreadPriority;
	}
	public int getRemoveDelay() {
		return removeDelay;
	}
	public void setRemoveDelay(int removeDelay) {
		this.removeDelay = removeDelay;
	}
}
