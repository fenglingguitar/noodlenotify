package org.fl.noodlenotify.console.vo;

public class MsgStorageVo implements java.io.Serializable {

	private static final long serialVersionUID = 446701832363322175L;

	private Long msgStorage_Id;
	private String name;
	private String ip;
	private Integer port;
	private Byte system_Status;
	private Byte manual_Status;

	public Long getMsgStorage_Id() {
		return this.msgStorage_Id;
	}

	public void setMsgStorage_Id(Long msgStorage_Id) {
		this.msgStorage_Id = msgStorage_Id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Byte getSystem_Status() {
		return system_Status;
	}

	public void setSystem_Status(Byte system_Status) {
		this.system_Status = system_Status;
	}

	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}

}
