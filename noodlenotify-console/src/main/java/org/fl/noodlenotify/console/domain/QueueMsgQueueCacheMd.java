package org.fl.noodlenotify.console.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "CSL_QUEUE_MSG_QUEUE_CACHE")
public class QueueMsgQueueCacheMd implements java.io.Serializable {

	private static final long serialVersionUID = 3902930193972774762L;

	private QueueMd queueMd;
	private MsgQueueCacheMd MsgCacheMd;
	private Byte is_Active;
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
	@JoinColumn(name = "MSGQUEUECACHE_ID", nullable = false)
	public MsgQueueCacheMd getMsgCacheMd() {
		return MsgCacheMd;
	}

	public void setMsgCacheMd(MsgQueueCacheMd msgCacheMd) {
		MsgCacheMd = msgCacheMd;
	}

	@Column(name = "IS_ACTIVE")
	public Byte getIs_Active() {
		return is_Active;
	}

	public void setIs_Active(Byte is_Active) {
		this.is_Active = is_Active;
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
