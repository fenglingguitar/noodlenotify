package org.fl.noodlenotify.console.vo;

public class QueueTraceStorageVo implements java.io.Serializable {

	private static final long serialVersionUID = 1998911281890422323L;
	
	private String queue_Nm;
	private long traceStorage_Id;
	private String name;
	private String ip;
	private int port;
	private byte system_Status;
	private byte manual_Status;
	private byte is_Repeat;
	private long expire_Time;

	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public long getTraceStorage_Id() {
		return traceStorage_Id;
	}

	public void setTraceStorage_Id(long traceStorage_Id) {
		this.traceStorage_Id = traceStorage_Id;
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

	public int getPort() {
		return port;
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

	public byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	public long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(long expire_Time) {
		this.expire_Time = expire_Time;
	}

}
