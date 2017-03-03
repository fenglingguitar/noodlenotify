package org.fl.noodlenotify.core.connect.cache.queue;

import org.fl.noodlenotify.common.pojo.db.MessageDb;

public interface QueueCacheConnectAgent {
	public boolean push(MessageDb messageDb) throws Exception;
	public MessageDb pop(String queueName, boolean queueType) throws Exception;
	public boolean havePop(MessageDb messageDb) throws Exception;
	public void setPop(MessageDb messageDb) throws Exception;
	public void removePop(MessageDb messageDb) throws Exception;
	public void setActive(String queueName, boolean bool) throws Exception;
	public boolean isActive(String queueName) throws Exception;
	public long len(String queueName, boolean queueType) throws Exception;
	public long getDiffTime() throws Exception;
	public boolean getAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception;
	public boolean keepAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception;
	public void releaseAlive(String queueName, long id) throws Exception;
}
