package org.fl.noodlenotify.common.pojo.cache;

import java.io.Serializable;

import org.fl.noodlenotify.common.pojo.MessageBase;

public class MessageCache extends MessageBase implements Serializable {
	
	private static final long serialVersionUID = -4505550212935402048L;
	
	private long contentId;
	private long db;
	private long id;
	private long executeQueue;
	private long resultQueue;
	private byte status;
	private long redisOne;
	private long redisTwo;
	private long beginTime;
	private long finishTime;
	private long cacheTimestamp;
	
	private String traceKey;

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}

	public long getDb() {
		return db;
	}

	public void setDb(long db) {
		this.db = db;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getExecuteQueue() {
		return executeQueue;
	}

	public void setExecuteQueue(long executeQueue) {
		this.executeQueue = executeQueue;
	}

	public long getResultQueue() {
		return resultQueue;
	}

	public void setResultQueue(long resultQueue) {
		this.resultQueue = resultQueue;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}
	
	public long getRedisOne() {
		return redisOne;
	}

	public void setRedisOne(long redisOne) {
		this.redisOne = redisOne;
	}

	public long getRedisTwo() {
		return redisTwo;
	}

	public void setRedisTwo(long redisTwo) {
		this.redisTwo = redisTwo;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public long getCacheTimestamp() {
		return cacheTimestamp;
	}

	public void setCacheTimestamp(long cacheTimestamp) {
		this.cacheTimestamp = cacheTimestamp;
	}

	public String getTraceKey() {
		return traceKey;
	}

	public void setTraceKey(String traceKey) {
		this.traceKey = traceKey;
	}
}
