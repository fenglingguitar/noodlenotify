package org.fl.noodlenotify.core.connect.constent;

public enum ConnectClusterExtendType {
	
	ID("ID"), 
	EITHER("EITHER"), 
	PARTALL("PARTALL"), 
	PART("PART"), 
	MASTER("MASTER"), 
	OTHER("OTHER"),
	LAYER("LAYER"),
	LAYERFAILOVER("LAYERFAILOVER");
	
	private String code;

	private ConnectClusterExtendType(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}  
}
