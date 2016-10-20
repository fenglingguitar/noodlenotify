package org.fl.noodlenotify.console.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_CONSUMER")
public class QueueConsumerMd implements java.io.Serializable {

	private static final long serialVersionUID = -8429228292899188978L;

	private QueueMd queueMd;
	private ConsumerMd consumerMd;

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
	@JoinColumn(name = "CONSUMER_ID", nullable = false)
	public ConsumerMd getConsumerMd() {
		return consumerMd;
	}

	public void setConsumerMd(ConsumerMd consumerMd) {
		this.consumerMd = consumerMd;
	}

}
