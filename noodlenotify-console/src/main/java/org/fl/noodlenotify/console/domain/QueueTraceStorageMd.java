package org.fl.noodlenotify.console.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_TRACE_STORAGE")
public class QueueTraceStorageMd implements java.io.Serializable {

	private static final long serialVersionUID = 7086025680232873235L;

	private QueueMd queueMd;
	private TraceStorageMd traceStorageMd;

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
	@JoinColumn(name = "TRACESTORAGE_ID", nullable = false)
	public TraceStorageMd getTraceStorageMd() {
		return traceStorageMd;
	}

	public void setTraceStorageMd(TraceStorageMd traceStorageMd) {
		this.traceStorageMd = traceStorageMd;
	}

}
