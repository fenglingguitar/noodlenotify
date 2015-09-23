package org.fl.noodlenotify.console.vo;

public class QueueExchangerVo implements java.io.Serializable {

	private static final long serialVersionUID = -1203870670726424402L;

	private String queue_Nm;
	private long exchanger_Id;
	private String name;
	private String ip;
	private int port;
	private String url;
	private String type;
	private int check_Port;
	private byte system_Status;
	private byte manual_Status;
	private byte is_Repeat;
	private long expire_Time;
	private byte is_Trace;
	private int new_Pop_ThreadNum;
	private int new_Exe_ThreadNum;
	private int portion_Pop_ThreadNum;
	private int portion_Exe_ThreadNum;

	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public long getExchanger_Id() {
		return exchanger_Id;
	}

	public void setExchanger_Id(long exchanger_Id) {
		this.exchanger_Id = exchanger_Id;
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
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCheck_Port() {
		return check_Port;
	}

	public void setCheck_Port(int check_Port) {
		this.check_Port = check_Port;
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
	
	public byte getIs_Trace() {
		return is_Trace;
	}

	public void setIs_Trace(byte is_Trace) {
		this.is_Trace = is_Trace;
	}

	public int getNew_Pop_ThreadNum() {
		return new_Pop_ThreadNum;
	}

	public void setNew_Pop_ThreadNum(int new_Pop_ThreadNum) {
		this.new_Pop_ThreadNum = new_Pop_ThreadNum;
	}

	public int getNew_Exe_ThreadNum() {
		return new_Exe_ThreadNum;
	}

	public void setNew_Exe_ThreadNum(int new_Exe_ThreadNum) {
		this.new_Exe_ThreadNum = new_Exe_ThreadNum;
	}

	public int getPortion_Pop_ThreadNum() {
		return portion_Pop_ThreadNum;
	}

	public void setPortion_Pop_ThreadNum(int portion_Pop_ThreadNum) {
		this.portion_Pop_ThreadNum = portion_Pop_ThreadNum;
	}

	public int getPortion_Exe_ThreadNum() {
		return portion_Exe_ThreadNum;
	}

	public void setPortion_Exe_ThreadNum(int portion_Exe_ThreadNum) {
		this.portion_Exe_ThreadNum = portion_Exe_ThreadNum;
	}
}
