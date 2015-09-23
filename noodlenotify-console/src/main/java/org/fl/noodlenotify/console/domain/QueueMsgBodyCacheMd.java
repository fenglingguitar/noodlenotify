package org.fl.noodlenotify.console.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_MSG_BODY_CACHE")
public class QueueMsgBodyCacheMd implements java.io.Serializable {
	
	private static final long serialVersionUID = 4843763978327772750L;
	
	private QueueMd queueMd;
	private MsgBodyCacheMd msgBodyCacheMd;

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
	@JoinColumn(name = "MSGBODYCACHE_ID", nullable = false)
	public MsgBodyCacheMd getMsgBodyCacheMd() {
		return msgBodyCacheMd;
	}

	public void setMsgBodyCacheMd(MsgBodyCacheMd msgBodyCacheMd) {
		this.msgBodyCacheMd = msgBodyCacheMd;
	}
}
