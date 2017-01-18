package org.fl.noodlenotify.console.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_EXCHANGER")
public class ExchangerMd implements java.io.Serializable {

	private static final long serialVersionUID = -9133448923587856363L;

	private Long exchanger_Id;
	private String name;
	private String ip;
	private Integer port;
	private String url;
	private String type;
	private Byte system_Status;
	private Byte manual_Status;
	
	private Date beat_Time;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "EXCHANGER_ID", nullable = false, length = 16)
	public Long getExchanger_Id() {
		return this.exchanger_Id;
	}

	public void setExchanger_Id(Long exchanger_Id) {
		this.exchanger_Id = exchanger_Id;
	}

	@Column(name = "NAME", length = 32)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "IP", nullable = false, length = 64)
	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Column(name = "PORT", nullable = false, length = 8)
	public Integer getPort() {
		return this.port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	@Column(name = "URL", nullable = true, length = 512)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Column(name = "TYPE", nullable = false, length = 32)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Column(name = "SYSTEM_STATUS", nullable = false, length = 1)
	public Byte getSystem_Status() {
		return system_Status;
	}

	public void setSystem_Status(Byte system_Status) {
		this.system_Status = system_Status;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	@Column(name = "BEAT_TIME", nullable = true)
	public Date getBeat_Time() {
		return beat_Time;
	}

	public void setBeat_Time(Date beat_Time) {
		this.beat_Time = beat_Time;
	}
}
