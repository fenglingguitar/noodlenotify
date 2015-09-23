package org.fl.noodlenotify.core.connect.net.pojo;

import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = 643801698855169490L;
	
	private String uuid;
	private String queueName;
	private String content;
	
	public Message() {
	}
	
	public Message(
			String queueName,
			String uuid,
			String content
			) {
		this.queueName = queueName;
		this.uuid = uuid;
		this.content = content;
	}
	
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

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
