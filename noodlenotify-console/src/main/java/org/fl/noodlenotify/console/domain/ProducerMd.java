package org.fl.noodlenotify.console.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_PRODUCER")
public class ProducerMd implements java.io.Serializable {

	private static final long serialVersionUID = 2447552144609677362L;

	private Long producer_Id;
	private String name;
	private String ip;
	private Byte system_Status;
	private Byte manual_Status;
	
	private Date beat_Time;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "PRODUCER_ID", nullable = false)
	public Long getProducer_Id() {
		return this.producer_Id;
	}

	public void setProducer_Id(Long producer_Id) {
		this.producer_Id = producer_Id;
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
