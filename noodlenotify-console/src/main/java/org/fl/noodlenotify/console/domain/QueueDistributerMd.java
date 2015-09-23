package org.fl.noodlenotify.console.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_DISTRIBUTER")
public class QueueDistributerMd implements java.io.Serializable {

	private static final long serialVersionUID = -7730600186646763870L;
	
	private QueueMd queueMd;
	private DistributerMd distributerMd;

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
	@JoinColumn(name = "DISTRIBUTER_ID", nullable = false)
	public DistributerMd getDistributerMd() {
		return distributerMd;
	}

	public void setDistributerMd(DistributerMd distributerMd) {
		this.distributerMd = distributerMd;
	}

}
