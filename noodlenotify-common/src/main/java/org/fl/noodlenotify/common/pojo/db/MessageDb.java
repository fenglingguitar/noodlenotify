package org.fl.noodlenotify.common.pojo.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.fl.noodlenotify.common.pojo.MessageBase;

public class MessageDb extends MessageBase implements Serializable {
	
	private static final long serialVersionUID = 643801698855169490L;
	
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
	private boolean bool;
	
	private String traceKey;
	
	private List<MessageCallback> messageCallbackList = new ArrayList<MessageCallback>();
	
	public MessageDb() {
	}
	
	public MessageDb(
			String queueName,
			String uuid,
			byte[] content,
			String traceKey
			) {
		super.queueName = queueName;
		super.uuid = uuid;
		this.content = content;
		this.traceKey = traceKey;
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

	public boolean getBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}
	
	public String getTraceKey() {
		return traceKey;
	}

	public void setTraceKey(String traceKey) {
		this.traceKey = traceKey;
	}

	public void addMessageCallback(MessageCallback messageCallback) {
		this.messageCallbackList.add(messageCallback);
	}
	
	public void executeMessageCallback() {
		for (MessageCallback messageCallback : this.messageCallbackList) {
			try {
				messageCallback.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.messageCallbackList.clear();
	}
}
