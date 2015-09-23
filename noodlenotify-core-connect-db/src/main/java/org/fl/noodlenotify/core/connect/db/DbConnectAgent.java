package org.fl.noodlenotify.core.connect.db;

import java.util.List;

import org.fl.noodlenotify.core.domain.message.MessageDm;

public interface DbConnectAgent {
	public void createTable(String queueName) throws Exception;
	public void insert(MessageDm messageDm) throws Exception;
	public void update(MessageDm messageDm) throws Exception;
	public void delete(MessageDm messageDm) throws Exception;
	public List<MessageDm> select(String queueName, long start, long end, byte status) throws Exception;
	public long selectCount(String queueName, long start, long end, byte status) throws Exception;
	public List<MessageDm> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception;
	public MessageDm selectById(String queueName, long id) throws Exception;
	public long maxId(String queueName) throws Exception;
	public long maxIdDelay(String queueName, long delay) throws Exception;
	public long minId(String queueName) throws Exception;
	public long minUnFinishId(String queueName) throws Exception;
	public long minIdByStatus(String queueName, byte status) throws Exception;
	public long getDiffTime() throws Exception;
	public boolean getAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception;
	public boolean keepAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception;
	public void releaseAlive(String queueName, long id) throws Exception;
}
