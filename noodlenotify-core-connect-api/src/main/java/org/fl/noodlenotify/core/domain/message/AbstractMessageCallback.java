package org.fl.noodlenotify.core.domain.message;

public abstract class AbstractMessageCallback implements MessageCallback {

	protected MessageDm messageDm;

	public MessageDm getMessageDm() {
		return messageDm;
	}

	public void setMessageDm(MessageDm messageDm) {
		this.messageDm = messageDm;
	}
}
