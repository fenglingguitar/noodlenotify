package org.fl.noodlenotify.console.vo;

public class MsgQueueCacheVo implements java.io.Serializable {

	private static final long serialVersionUID = -1686255202961047155L;

	private Long msgQueueCache_Id;
	private String name;
	private String ip;
	private Integer port;
	private Byte system_Status;
	private Byte manual_Status;

	public Long getMsgQueueCache_Id() {
		return this.msgQueueCache_Id;
	}

	public void setMsgQueueCache_Id(Long msgQueueCache_Id) {
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
