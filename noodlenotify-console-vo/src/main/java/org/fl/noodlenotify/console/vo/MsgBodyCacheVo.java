package org.fl.noodlenotify.console.vo;

public class MsgBodyCacheVo implements java.io.Serializable {

	private static final long serialVersionUID = -176104255014117075L;

	private long msgBodyCache_Id;
	private String name;
	private String ip;
	private int port;
	private byte system_Status;
	private byte manual_Status;
	private long size;

	public long getMsgBodyCache_Id() {
		return this.msgBodyCache_Id;
	}

	public void setMsgBodyCache_Id(long msgBodyCache_Id) {
		this.msgBodyCache_Id = msgBodyCache_Id;
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
