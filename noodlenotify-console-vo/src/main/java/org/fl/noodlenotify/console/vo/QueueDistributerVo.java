package org.fl.noodlenotify.console.vo;

public class QueueDistributerVo implements java.io.Serializable {

	private static final long serialVersionUID = -3867552721595453876L;

	private String queue_Nm;
	private long distributer_Id;
	private String name;
	private String ip;
	private int port;
	private int check_Port;
	private byte system_Status;
	private byte manual_Status;
	private byte is_Repeat;
	private long expire_Time;
	private long interval_Time;
	private long dph_Delay_Time;
	private long dph_Timeout;
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

	public long getDistributer_Id() {
		return distributer_Id;
	}

	public void setDistributer_Id(long distributer_Id) {
		this.distributer_Id = distributer_Id;
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

	public long getInterval_Time() {
		return interval_Time;
	}

	public void setInterval_Time(long interval_Time) {
		this.interval_Time = interval_Time;
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

	public long getDph_Delay_Time() {
		return dph_Delay_Time;
	}

	public void setDph_Delay_Time(long dph_Delay_Time) {
		this.dph_Delay_Time = dph_Delay_Time;
	}

	public long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}
	
}
