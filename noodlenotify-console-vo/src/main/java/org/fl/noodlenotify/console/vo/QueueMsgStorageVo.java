package org.fl.noodlenotify.console.vo;

public class QueueMsgStorageVo implements java.io.Serializable {

	private static final long serialVersionUID = -1863080062770997616L;

	private String queue_Nm;
	private Long msgStorage_Id;
	private Long new_Len;
	private Long portion_Len;

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

	public Long getMsgStorage_Id() {
		return msgStorage_Id;
	}

	public void setMsgStorage_Id(Long msgStorage_Id) {
		this.msgStorage_Id = msgStorage_Id;
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

	public Long getNew_Len() {
		return new_Len;
	}

	public void setNew_Len(Long new_Len) {
		this.new_Len = new_Len;
	}

	public Long getPortion_Len() {
		return portion_Len;
	}

	public void setPortion_Len(Long portion_Len) {
		this.portion_Len = portion_Len;
	}

}
