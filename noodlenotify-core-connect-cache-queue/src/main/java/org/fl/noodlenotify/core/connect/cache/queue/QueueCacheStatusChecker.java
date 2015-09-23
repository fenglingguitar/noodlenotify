package org.fl.noodlenotify.core.connect.cache.queue;

public interface QueueCacheStatusChecker {
	public void checkHealth() throws Exception;
	public boolean checkIsActive(String queueName) throws Exception;
	public long checkNewLen(String queueName) throws Exception;
	public long checkPortionLen(String queueName) throws Exception;
}