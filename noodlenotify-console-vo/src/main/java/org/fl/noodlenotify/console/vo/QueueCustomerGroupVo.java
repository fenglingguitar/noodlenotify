package org.fl.noodlenotify.console.vo;

public class QueueCustomerGroupVo implements java.io.Serializable {

	private static final long serialVersionUID = 2882134207890658338L;

	private String queue_Nm;
	private String customerGroup_Nm;
	private long customer_Num;

	private long customer_Id;
	private String name;
	private String ip;
	private int port;
	private String url;
	private String type;
	private int check_Port;
	private String check_Url;
	private String check_Type;
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

	public String getCustomerGroup_Nm() {
		return customerGroup_Nm;
	}

	public void setCustomerGroup_Nm(String customerGroup_Nm) {
		this.customerGroup_Nm = customerGroup_Nm;
	}

	public long getCustomer_Num() {
		return customer_Num;
	}

	public void setCustomer_Num(long customer_Num) {
		this.customer_Num = customer_Num;
	}

	public long getCustomer_Id() {
		return customer_Id;
	}

	public void setCustomer_Id(long customer_Id) {
		this.customer_Id = customer_Id;
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
	
	public String getCheck_Url() {
		return check_Url;
	}

	public void setCheck_Url(String check_Url) {
		this.check_Url = check_Url;
	}

	public String getCheck_Type() {
		return check_Type;
	}

	public void setCheck_Type(String check_Type) {
		this.check_Type = check_Type;
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
