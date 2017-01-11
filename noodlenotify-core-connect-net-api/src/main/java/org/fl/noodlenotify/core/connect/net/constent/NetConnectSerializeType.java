package org.fl.noodlenotify.core.connect.net.constent;

public enum NetConnectSerializeType {
	
	JSON("JSON");
	
	private String code;

	private NetConnectSerializeType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}  
}
