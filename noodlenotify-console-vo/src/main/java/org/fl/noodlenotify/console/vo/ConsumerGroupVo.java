package org.fl.noodlenotify.console.vo;

public class ConsumerGroupVo implements java.io.Serializable {
	
	private static final long serialVersionUID = -2030752486865252965L;
	
	private String consumerGroup_Nm;
	private Byte manual_Status;

	public String getConsumerGroup_Nm() {
		return consumerGroup_Nm;
	}

	public void setConsumerGroup_Nm(String consumerGroup_Nm) {
		this.consumerGroup_Nm = consumerGroup_Nm;
	}

	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}
}
