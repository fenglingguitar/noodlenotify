package org.fl.noodlenotify.core.connect.constent;

public enum ConnectManagerType {
	
	NET("NET"),
	DB("DB");
	
	private String code;

	private ConnectManagerType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}  
}
