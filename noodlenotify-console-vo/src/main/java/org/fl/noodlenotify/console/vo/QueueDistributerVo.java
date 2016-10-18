package org.fl.noodlenotify.console.vo;

public class QueueDistributerVo implements java.io.Serializable {

	private static final long serialVersionUID = -3867552721595453876L;

	private String queue_Nm;
	private Long distributer_Id;
	private String name;
	private String ip;
	private Integer port;
	private Integer check_Port;
	private Byte system_Status;
	private Byte manual_Status;
	private Byte is_Repeat;
	private Long expire_Time;
	private Long interval_Time;
	private Long dph_Delay_Time;
	private Long dph_Timeout;
	private Integer new_Pop_ThreadNum;
	private Integer new_Exe_ThreadNum;
	private Integer portion_Pop_ThreadNum;
	private Integer portion_Exe_ThreadNum;

	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public Long getDistributer_Id() {
		return distributer_Id;
	}

	public void setDistributer_Id(Long distributer_Id) {
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

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getCheck_Port() {
		return check_Port;
	}

	public void setCheck_Port(Integer check_Port) {
		this.check_Port = check_Port;
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

	public Long getInterval_Time() {
		return interval_Time;
	}

	public void setInterval_Time(Long interval_Time) {
		this.interval_Time = interval_Time;
	}
	
	public Integer getNew_Pop_ThreadNum() {
		return new_Pop_ThreadNum;
	}

	public void setNew_Pop_ThreadNum(Integer new_Pop_ThreadNum) {
		this.new_Pop_ThreadNum = new_Pop_ThreadNum;
	}

	public Integer getNew_Exe_ThreadNum() {
		return new_Exe_ThreadNum;
	}

	public void setNew_Exe_ThreadNum(Integer new_Exe_ThreadNum) {
		this.new_Exe_ThreadNum = new_Exe_ThreadNum;
	}

	public Integer getPortion_Pop_ThreadNum() {
		return portion_Pop_ThreadNum;
	}

	public void setPortion_Pop_ThreadNum(Integer portion_Pop_ThreadNum) {
		this.portion_Pop_ThreadNum = portion_Pop_ThreadNum;
	}

	public Integer getPortion_Exe_ThreadNum() {
		return portion_Exe_ThreadNum;
	}

	public void setPortion_Exe_ThreadNum(Integer portion_Exe_ThreadNum) {
		this.portion_Exe_ThreadNum = portion_Exe_ThreadNum;
	}

	public Long getDph_Delay_Time() {
		return dph_Delay_Time;
	}

	public void setDph_Delay_Time(Long dph_Delay_Time) {
		this.dph_Delay_Time = dph_Delay_Time;
	}

	public Long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(Long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}
	
}
