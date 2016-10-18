package org.fl.noodlenotify.console.vo;

public class QueueMsgBodyCacheVo implements java.io.Serializable {

	private static final long serialVersionUID = 2777519492510006342L;

	private String queue_Nm;
	private Long msgBodyCache_Id;
	private String name;
	private String ip;
	private Integer port;
	private Byte system_Status;
	private Byte manual_Status;
	private Byte is_Repeat;
	private Long expire_Time;

	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public Long getMsgBodyCache_Id() {
		return msgBodyCache_Id;
	}

	public void setMsgBodyCache_Id(Long msgBodyCache_Id) {
		this.msgBodyCache_Id = msgBodyCache_Id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
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

	public Byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(Byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	public Long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(Long expire_Time) {
		this.expire_Time = expire_Time;
	}

}
