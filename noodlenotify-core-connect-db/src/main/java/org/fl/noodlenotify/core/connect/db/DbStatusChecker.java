package org.fl.noodlenotify.core.connect.db;

import java.util.List;

import org.fl.noodlenotify.core.domain.message.MessageVo;

public interface DbStatusChecker {
	public void checkHealth() throws Exception;
	public long checkNewLen(String queueName) throws Exception;
	public long checkPortionLen(String queueName) throws Exception;
	public List<MessageVo> queryPortionMessage(String queueName, String uuid, Long region, String content, Integer page, Integer rows) throws Exception;
	public void savePortionMessage(String queueName, Long contentId, String content) throws Exception;
	public void deletePortionMessage(String queueName, Long id) throws Exception;
}