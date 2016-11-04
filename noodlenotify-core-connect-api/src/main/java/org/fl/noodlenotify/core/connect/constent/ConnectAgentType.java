package org.fl.noodlenotify.core.connect.constent;

public enum ConnectAgentType {
	
	NET_HTTP("NET_HTTP"), 
	NET_NETTY("NET_NETTY"),
	DB("DB");
	
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
