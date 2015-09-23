package org.fl.noodlenotify.console.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_EXCHANGER")
public class QueueExchangerMd implements java.io.Serializable {

	private static final long serialVersionUID = -2397329889283043896L;
	
	private QueueMd queueMd;
	private ExchangerMd exchangerMd;

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
	@JoinColumn(name = "EXCHANGER_ID", nullable = false)
	public ExchangerMd getExchangerMd() {
		return exchangerMd;
	}

	public void setExchangerMd(ExchangerMd exchangerMd) {
		this.exchangerMd = exchangerMd;
	}

}
