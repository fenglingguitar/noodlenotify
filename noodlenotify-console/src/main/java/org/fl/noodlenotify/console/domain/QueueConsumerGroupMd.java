package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_CONSUMER_GROUP")
public class QueueConsumerGroupMd implements java.io.Serializable {

	private static final long serialVersionUID = 6328110704380175731L;
	
	private QueueMd queueMd;
	private ConsumerGroupMd consumerGroupMd;
	private Long consumer_Num;

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "QUEUE_NM", nullable = false)
	public QueueMd getQueueMd() {
		return queueMd;
	}

	public void setQueueMd(QueueMd queueMd) {
		this.queueMd = queueMd;
	}

	@Id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONSUMERGROUP_NM", nullable = false)
	public ConsumerGroupMd getConsumerGroupMd() {
		return consumerGroupMd;
	}

	public void setConsumerGroupMd(ConsumerGroupMd consumerGroupMd) {
		this.consumerGroupMd = consumerGroupMd;
	}

	@Column(name = "CONSUMER_NUM", nullable = false, length = 16)
	public Long getConsumer_Num() {
		return consumer_Num;
	}

	public void setConsumer_Num(Long consumer_Num) {
		this.consumer_Num = consumer_Num;
	}

}
