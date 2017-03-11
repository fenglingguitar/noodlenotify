package org.fl.noodlenotify.common.pojo;

import org.fl.noodle.common.trace.operation.method.TraceParamToString;

public abstract class MessageBase implements TraceParamToString {
	
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
	
	@Override
	public String toString() {
		return new StringBuilder()
		.append(uuid)
		.append(",").append(queueName)
		.toString();
	}
}
