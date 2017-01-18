package org.fl.noodlenotify.console.vo;

public class QueueConsumerVo implements java.io.Serializable {

	private static final long serialVersionUID = -875644623622940627L;

	private String queue_Nm;
	private Long consumer_Id;
	private String name;
	private String ip;
	private Integer port;
	private String url;
	private String type;
	private Byte system_Status;
	private Byte manual_Status;
	private String consumerGroup_Nm;
	private Byte is_Repeat;
	private Long expire_Time;
	private Long dph_Timeout;

	public String getQueue_Nm() {
		return queue_Nm;
	}

	public void setQueue_Nm(String queue_Nm) {
		this.queue_Nm = queue_Nm;
	}

	public Long getConsumer_Id() {
		return consumer_Id;
	}

	public void setConsumer_Id(Long consumer_Id) {
		this.consumer_Id = consumer_Id;
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

	public Integer getPort() {
		return port;
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

	public Byte getIs_Repeat() {
		return is_Repeat;
	}

	public void setIs_Repeat(Byte is_Repeat) {
		this.is_Repeat = is_Repeat;
	}

	public Long getExpire_Time() {
		return expire_Time;
	}

	public void setExpire_Time(Long expire_Time) {
		this.expire_Time = expire_Time;
	}

	public Long getDph_Timeout() {
		return dph_Timeout;
	}

	public void setDph_Timeout(Long dph_Timeout) {
		this.dph_Timeout = dph_Timeout;
	}

}
