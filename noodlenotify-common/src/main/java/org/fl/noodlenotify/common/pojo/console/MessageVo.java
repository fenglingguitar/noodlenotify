package org.fl.noodlenotify.common.pojo.console;

import java.io.Serializable;
import java.util.Date;

import org.fl.noodlenotify.common.pojo.MessageBase;

public class MessageVo extends MessageBase implements Serializable {
	
	private static final long serialVersionUID = 643801698855169490L;
	
	private String content;
	private Long contentId;
	private Long db;
	private Long id;
	private Long executeQueue;
	private Long resultQueue;
	private Byte status;
	private Date beginTime;
	private Date finishTime;
	
	private Long region;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public Long getDb() {
		return db;
	}

	public void setDb(Long db) {
		this.db = db;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExecuteQueue() {
		return executeQueue;
	}

	public void setExecuteQueue(Long executeQueue) {
		this.executeQueue = executeQueue;
	}

	public Long getResultQueue() {
		return resultQueue;
	}

	public void setResultQueue(Long resultQueue) {
		this.resultQueue = resultQueue;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public Long getRegion() {
		return region;
	}

	public void setRegion(Long region) {
		this.region = region;
	}
}
