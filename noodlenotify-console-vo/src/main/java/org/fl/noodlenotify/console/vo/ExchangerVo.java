package org.fl.noodlenotify.console.vo;

public class ExchangerVo implements java.io.Serializable {

	private static final long serialVersionUID = 4353744880800628439L;

	private Long exchanger_Id;
	private String name;
	private String ip;
	private Integer port;
	private String url;
	private String type;
	private Integer check_Port;
	private Byte system_Status;
	private Byte manual_Status;

	public Long getExchanger_Id() {
		return this.exchanger_Id;
	}

	public void setExchanger_Id(Long exchanger_Id) {
		this.exchanger_Id = exchanger_Id;
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
