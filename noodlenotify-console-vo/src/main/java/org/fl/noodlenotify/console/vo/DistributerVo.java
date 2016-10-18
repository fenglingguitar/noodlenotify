package org.fl.noodlenotify.console.vo;

public class DistributerVo implements java.io.Serializable {

	private static final long serialVersionUID = 650614461776727073L;

	private Long distributer_Id;
	private String name;
	private String ip;
	private Integer port;
	private Integer check_Port;
	private Byte system_Status;
	private Byte manual_Status;

	public Long getDistributer_Id() {
		return this.distributer_Id;
	}

	public void setDistributer_Id(Long distributer_Id) {
		this.distributer_Id = distributer_Id;
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

}
