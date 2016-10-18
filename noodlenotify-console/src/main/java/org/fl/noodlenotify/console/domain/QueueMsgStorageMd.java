package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_MSG_STORAGE")
public class QueueMsgStorageMd implements java.io.Serializable {

	private static final long serialVersionUID = 7086025680232873235L;

	private QueueMd queueMd;
	private MsgStorageMd msgStorageMd;
	private Long new_Len;
	private Long portion_Len;

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
	@JoinColumn(name = "MSGSTORAGE_ID", nullable = false)
	public MsgStorageMd getMsgStorageMd() {
		return msgStorageMd;
	}

	public void setMsgStorageMd(MsgStorageMd msgStorageMd) {
		this.msgStorageMd = msgStorageMd;
	}

	@Column(name = "NEW_LEN")
	public Long getNew_Len() {
		return new_Len;
	}

	public void setNew_Len(Long new_Len) {
		this.new_Len = new_Len;
	}

	@Column(name = "PORTION_LEN")
	public Long getPortion_Len() {
		return portion_Len;
	}

	public void setPortion_Len(Long portion_Len) {
		this.portion_Len = portion_Len;
	}

}
