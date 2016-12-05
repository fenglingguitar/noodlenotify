package org.fl.noodlenotify.core.connect.constent;

public enum ConnectAgentType {
	
	NET_HTTP("HTTP"), 
	NET_NETTY("NETTY"),
	DB("DB"),
	QUEUE_CACHE("QUEUE_CACHE"),
	BODY_CACHE("BODY_CACHE");
	
	private String code;

	private ConnectAgentType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}  
}
