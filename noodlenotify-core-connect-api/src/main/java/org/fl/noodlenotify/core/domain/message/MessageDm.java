package org.fl.noodlenotify.core.domain.message;

import java.io.Serializable;

public class MessageDm implements Serializable {
	
	private static final long serialVersionUID = 643801698855169490L;
	
	private String uuid;
	private String queueName;
	private byte[] content;
	private long contentId;
	private long db;
	private long id;
	private long executeQueue;
	private long resultQueue;
	private byte status;
	private long redisOne;
	private long redisTwo;
	private boolean result;
	private Exception exception;
	private long beginTime;
	private long finishTime;
	private long cacheTimestamp;
	private long delayTime;
	private Object objectOne;
	private Object objectTwo;
	private Object objectThree;
	private Object objectFour;
	private Object objectFive;
	private boolean bool;
	
	public MessageDm() {
	}
	
	public MessageDm(
			String queueName,
			String uuid,
			byte[] content
			) {
		this.queueName = queueName;
		this.uuid = uuid;
		this.content = content;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}

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

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
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
	
	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public Object getObjectOne() {
		return objectOne;
	}

	public void setObjectOne(Object objectOne) {
		this.objectOne = objectOne;
	}

	public Object getObjectTwo() {
		return objectTwo;
	}

	public void setObjectTwo(Object objectTwo) {
		this.objectTwo = objectTwo;
	}

	public Object getObjectThree() {
		return objectThree;
	}

	public void setObjectThree(Object objectThree) {
		this.objectThree = objectThree;
	}

	public Object getObjectFour() {
		return objectFour;
	}

	public void setObjectFour(Object objectFour) {
		this.objectFour = objectFour;
	}

	public Object getObjectFive() {
		return objectFive;
	}

	public void setObjectFive(Object objectFive) {
		this.objectFive = objectFive;
	}

	public boolean getBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}
}
