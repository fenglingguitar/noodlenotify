package org.fl.noodlenotify.console.vo;

public class MsgQueueCacheVo implements java.io.Serializable {

	private static final long serialVersionUID = -1686255202961047155L;

	private long msgQueueCache_Id;
	private String name;
	private String ip;
	private int port;
	private byte system_Status;
	private byte manual_Status;

	public long getMsgQueueCache_Id() {
		return this.msgQueueCache_Id;
	}

	public void setMsgQueueCache_Id(long msgQueueCache_Id) {
		this.msgQueueCache_Id = msgQueueCache_Id;
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
