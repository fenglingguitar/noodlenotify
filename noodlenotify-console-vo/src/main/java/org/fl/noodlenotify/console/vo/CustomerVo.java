package org.fl.noodlenotify.console.vo;

import java.util.Date;

public class CustomerVo implements java.io.Serializable {

	private static final long serialVersionUID = -7856386332878028902L;

	private Long customer_Id;
	private String name;
	private String ip;
	private Integer port;
	private String url;
	private String type;
	private Integer check_Port;
	private String check_Url;
	private String check_Type;
	private Byte system_Status;
	private Byte manual_Status;
	private String customerGroup_Nm;
	
	private Date beat_Time;

	public Long getCustomer_Id() {
		return this.customer_Id;
	}

	public void setCustomer_Id(Long customer_Id) {
		this.customer_Id = customer_Id;
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

	public Integer getCheck_Port() {
		return check_Port;
	}

	public void setCheck_Port(Integer check_Port) {
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

	public String getCustomerGroup_Nm() {
		return customerGroup_Nm;
	}

	public void setCustomerGroup_Nm(String customerGroup_Nm) {
		this.customerGroup_Nm = customerGroup_Nm;
	}

	public Date getBeat_Time() {
		return beat_Time;
	}

	public void setBeat_Time(Date beat_Time) {
		this.beat_Time = beat_Time;
	}
}
