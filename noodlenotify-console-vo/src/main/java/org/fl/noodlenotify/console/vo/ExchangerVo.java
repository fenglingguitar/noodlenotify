package org.fl.noodlenotify.console.vo;

public class ExchangerVo implements java.io.Serializable {

	private static final long serialVersionUID = 4353744880800628439L;

	private long exchanger_Id;
	private String name;
	private String ip;
	private int port;
	private String url;
	private String type;
	private int check_Port;
	private byte system_Status;
	private byte manual_Status;

	public long getExchanger_Id() {
		return this.exchanger_Id;
	}

	public void setExchanger_Id(long exchanger_Id) {
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

	public int getPort() {
		return this.port;
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

}
