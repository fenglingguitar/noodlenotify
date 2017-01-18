package org.fl.noodlenotify.console.vo;

import java.util.Date;

public class ConsumerVo implements java.io.Serializable {

	private static final long serialVersionUID = -7856386332878028902L;

	private Long consumer_Id;
	private String name;
	private String ip;
	private Integer port;
	private String url;
	private String type;
	private Byte system_Status;
	private Byte manual_Status;
	private String consumerGroup_Nm;
	
	private Date beat_Time;

	public Long getConsumer_Id() {
		return this.consumer_Id;
	}

	public void setConsumer_Id(Long consumer_Id) {
		this.consumer_Id = consumer_Id;
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

	public String getConsumerGroup_Nm() {
		return consumerGroup_Nm;
	}

	public void setConsumerGroup_Nm(String consumerGroup_Nm) {
		this.consumerGroup_Nm = consumerGroup_Nm;
	}

	public Date getBeat_Time() {
		return beat_Time;
	}

	public void setBeat_Time(Date beat_Time) {
		this.beat_Time = beat_Time;
	}
}
