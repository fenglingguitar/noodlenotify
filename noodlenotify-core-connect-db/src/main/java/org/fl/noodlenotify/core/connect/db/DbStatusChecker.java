package org.fl.noodlenotify.core.connect.db;

public interface DbStatusChecker {
	public void checkHealth() throws Exception;
	public long checkNewLen(String queueName) throws Exception;
	public long checkPortionLen(String queueName) throws Exception;
}