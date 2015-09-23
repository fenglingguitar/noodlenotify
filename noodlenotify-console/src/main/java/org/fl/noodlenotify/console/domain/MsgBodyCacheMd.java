package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_MSG_BODY_CACHE")
public class MsgBodyCacheMd implements java.io.Serializable {

	private static final long serialVersionUID = -7280806215853965817L;

	private long msgBodyCache_Id;
	private String name;
	private String ip;
	private int port;
	private byte system_Status;
	private byte manual_Status;
	private long size;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "MSGBODYCACHE_ID", nullable = false)
	public long getMsgBodyCache_Id() {
		return this.msgBodyCache_Id;
	}

	public void setMsgBodyCache_Id(long msgBodyCache_Id) {
		this.msgBodyCache_Id = msgBodyCache_Id;
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
	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Column(name = "SYSTEM_STATUS", nullable = false, length = 1)
	public byte getSystem_Status() {
		return system_Status;
	}

	public void setSystem_Status(byte system_Status) {
		this.system_Status = system_Status;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(byte manual_Status) {
		this.manual_Status = manual_Status;
	}

	@Column(name = "SIZE")
	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
