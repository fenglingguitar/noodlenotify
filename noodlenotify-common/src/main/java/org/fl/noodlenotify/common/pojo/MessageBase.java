package org.fl.noodlenotify.common.pojo;

import java.io.Serializable;

public class MessageBase implements Serializable {
	
	private static final long serialVersionUID = 5553747257631976006L;
	
	protected String uuid;
	protected String queueName;
	
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
}
