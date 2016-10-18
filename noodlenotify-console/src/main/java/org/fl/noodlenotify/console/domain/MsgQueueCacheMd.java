package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_MSG_QUEUE_CACHE")
public class MsgQueueCacheMd implements java.io.Serializable {

	private static final long serialVersionUID = -6828859696234155755L;

	private Long msgQueueCache_Id;
	private String name;
	private String ip;
	private Integer port;
	private Byte system_Status;
	private Byte manual_Status;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "MSGQUEUECACHE_ID", nullable = false, length = 16)
	public Long getMsgQueueCache_Id() {
		return this.msgQueueCache_Id;
	}

	public void setMsgQueueCache_Id(Long msgQueueCache_Id) {
		this.msgQueueCache_Id = msgQueueCache_Id;
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

}
