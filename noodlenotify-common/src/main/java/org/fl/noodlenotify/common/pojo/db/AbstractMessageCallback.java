package org.fl.noodlenotify.common.pojo.db;

public abstract class AbstractMessageCallback implements MessageCallback {

	protected MessageDb messageDb;

	public MessageDb getMessageDm() {
		return messageDb;
	}

	public void setMessageDm(MessageDb messageDb) {
		this.messageDb = messageDb;
	}
}
