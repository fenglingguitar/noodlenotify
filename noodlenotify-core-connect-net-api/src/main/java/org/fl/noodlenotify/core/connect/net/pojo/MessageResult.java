package org.fl.noodlenotify.core.connect.net.pojo;

import java.io.Serializable;

public class MessageResult implements Serializable {

	private static final long serialVersionUID = 4208063383099910296L;
	
	private boolean result;
	private String uuid;
	private String error;
	
	public MessageResult() {
	}
	
	public MessageResult(
			boolean result
			) {
		this.result = result;
	}
	
	public MessageResult(
			boolean result,
			String uuid
			) {
		this.result = result;
		this.uuid = uuid;
	}
	
	public MessageResult(
			boolean result,
			String uuid,
			String error
			) {
		this.result = result;
		this.uuid = uuid;
		this.error = error;
	}
	
	public boolean getResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
