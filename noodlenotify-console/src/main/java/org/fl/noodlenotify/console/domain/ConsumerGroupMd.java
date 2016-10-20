package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_CONSUMER_GROUP")
public class ConsumerGroupMd implements java.io.Serializable {

	private static final long serialVersionUID = 5248390504913904876L;

	private String consumerGroup_Nm;
	private Byte manual_Status;

	@Id
	@Column(name = "CONSUMERGROUP_NM", nullable = false, length = 32)
	public String getConsumerGroup_Nm() {
		return consumerGroup_Nm;
	}

	public void setConsumerGroup_Nm(String consumerGroup_Nm) {
		this.consumerGroup_Nm = consumerGroup_Nm;
	}

	@Column(name = "MANUAL_STATUS", nullable = false, length = 1)
	public Byte getManual_Status() {
		return manual_Status;
	}

	public void setManual_Status(Byte manual_Status) {
		this.manual_Status = manual_Status;
	}

}
