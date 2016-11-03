package org.fl.noodlenotify.core.connect.net.aop;

public enum LocalStorageType {
	
	LAYER_CONNECT_CLUSTER("LocalStorageType.LayerConnectCluster"),
	MESSAGEDM("LocalStorageType.messageDm");
	
	private String code;

	private LocalStorageType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}  
}
