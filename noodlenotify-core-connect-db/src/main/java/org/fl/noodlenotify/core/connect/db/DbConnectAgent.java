package org.fl.noodlenotify.core.connect.db;

import java.util.List;

import org.fl.noodlenotify.common.pojo.db.MessageDb;

public interface DbConnectAgent {
	public void createTable(String queueName) throws Exception;
	public void insert(MessageDb messageDb) throws Exception;
	public void update(MessageDb messageDb) throws Exception;
	public void delete(MessageDb messageDb) throws Exception;
	public List<MessageDb> select(String queueName, long start, long end, byte status) throws Exception;
	public long selectCount(String queueName, long start, long end, byte status) throws Exception;
	public List<MessageDb> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception;
	public MessageDb selectById(String queueName, long id) throws Exception;
	public long maxId(String queueName) throws Exception;
	public long maxIdDelay(String queueName, long delay) throws Exception;
	public long minId(String queueName) throws Exception;
	public long minUnFinishId(String queueName) throws Exception;
	public long minIdByStatus(String queueName, byte status) throws Exception;
}
