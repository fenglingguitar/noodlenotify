package org.fl.noodlenotify.common.pojo.net;

import java.io.Serializable;

import org.fl.noodlenotify.common.pojo.MessageBase;

public class MessageRequest extends MessageBase implements Serializable {
	
	private static final long serialVersionUID = 643801698855169490L;
	
	private String content;
	private String traceKey;
	private String parentInvoke;
	private String parentStackKey;
	
	public MessageRequest() {
	}
	
	public MessageRequest(
			String queueName,
			String uuid,
			String content
			) {
		this.queueName = queueName;
		this.uuid = uuid;
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getTraceKey() {
		return traceKey;
	}

	public void setTraceKey(String traceKey) {
		this.traceKey = traceKey;
	}

	public String getParentInvoke() {
		return parentInvoke;
	}

	public void setParentInvoke(String parentInvoke) {
		this.parentInvoke = parentInvoke;
	}

	public String getParentStackKey() {
		return parentStackKey;
	}

	public void setParentStackKey(String parentStackKey) {
		this.parentStackKey = parentStackKey;
	}
}
