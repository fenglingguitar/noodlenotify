package org.fl.noodlenotify.core.connect.aop;

public enum LocalStorageType {
	
	LAYER_CONNECT_CLUSTER("LocalStorageType.LayerConnectCluster"),
	MESSAGE_DM("LocalStorageType.messageDb"),
	QUEUE_DISTRIBUTER_VO("LocalStorageType.queueDistributerVo"),
	CONNECT_ID("LocalStorageType.connectId");
	
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
