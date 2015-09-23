package org.fl.noodlenotify.console.vo;

public class MsgStorageVo implements java.io.Serializable {

	private static final long serialVersionUID = 446701832363322175L;

	private long msgStorage_Id;
	private String name;
	private String ip;
	private int port;
	private byte system_Status;
	private byte manual_Status;

	public long getMsgStorage_Id() {
		return this.msgStorage_Id;
	}

	public void setMsgStorage_Id(long msgStorage_Id) {
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

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte getSystem_Status() {
		return system_Status;
	}

	public void setSystem_Status(byte system_Status) {
		this.system_Status = system_Status;
	}

	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}

}
