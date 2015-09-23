package org.fl.noodlenotify.core.connect.db;

public class DbConnectAgentConfParam {
	
	private int insertThreadCount = 10;
	private int insertBatchSize = 10000;
	private long insertTimeout = 300000;
	private long insertWaitTime = 10;
	private int insertCapacity = 1000000;
	private int insertThreadPriority = 5;

	private int updateThreadCount = 10;
	private int updateBatchSize = 10000;
	private long updateTimeout = 300000;
	private long updateWaitTime = 10;
	private int updateCapacity = 1000000;
	private int updateThreadPriority = 5;

	private int deleteThreadCount = 10;
	private int deleteBatchSize = 10000;
	private long deleteTimeout = 0;
	private long deleteWaitTime = 100;
	private int deleteCapacity = 100000;
	private int deleteThreadPriority = 5;
	
	public int getInsertThreadCount() {
		return insertThreadCount;
	}
	public void setInsertThreadCount(int insertThreadCount) {
		this.insertThreadCount = insertThreadCount;
	}
	public int getInsertBatchSize() {
		return insertBatchSize;
	}
	public void setInsertBatchSize(int insertBatchSize) {
		this.insertBatchSize = insertBatchSize;
	}
	public long getInsertTimeout() {
		return insertTimeout;
	}
	public void setInsertTimeout(long insertTimeout) {
		this.insertTimeout = insertTimeout;
	}
	public long getInsertWaitTime() {
		return insertWaitTime;
	}
	public void setInsertWaitTime(long insertWaitTime) {
		this.insertWaitTime = insertWaitTime;
	}
	public int getInsertCapacity() {
		return insertCapacity;
	}
	public void setInsertCapacity(int insertCapacity) {
		this.insertCapacity = insertCapacity;
	}
	public int getInsertThreadPriority() {
		return insertThreadPriority;
	}
	public void setInsertThreadPriority(int insertThreadPriority) {
		this.insertThreadPriority = insertThreadPriority;
	}
	public int getUpdateThreadCount() {
		return updateThreadCount;
	}
	public void setUpdateThreadCount(int updateThreadCount) {
		this.updateThreadCount = updateThreadCount;
	}
	public int getUpdateBatchSize() {
		return updateBatchSize;
	}
	public void setUpdateBatchSize(int updateBatchSize) {
		this.updateBatchSize = updateBatchSize;
	}
	public long getUpdateTimeout() {
		return updateTimeout;
	}
	public void setUpdateTimeout(long updateTimeout) {
		this.updateTimeout = updateTimeout;
	}
	public long getUpdateWaitTime() {
		return updateWaitTime;
	}
	public void setUpdateWaitTime(long updateWaitTime) {
		this.updateWaitTime = updateWaitTime;
	}
	public int getUpdateCapacity() {
		return updateCapacity;
	}
	public void setUpdateCapacity(int updateCapacity) {
		this.updateCapacity = updateCapacity;
	}	
	public int getUpdateThreadPriority() {
		return updateThreadPriority;
	}
	public void setUpdateThreadPriority(int updateThreadPriority) {
		this.updateThreadPriority = updateThreadPriority;
	}
	public int getDeleteThreadCount() {
		return deleteThreadCount;
	}
	public void setDeleteThreadCount(int deleteThreadCount) {
		this.deleteThreadCount = deleteThreadCount;
	}	
	public int getDeleteBatchSize() {
		return deleteBatchSize;
	}
	public void setDeleteBatchSize(int deleteBatchSize) {
		this.deleteBatchSize = deleteBatchSize;
	}	
	public long getDeleteTimeout() {
		return deleteTimeout;
	}
	public void setDeleteTimeout(long deleteTimeout) {
		this.deleteTimeout = deleteTimeout;
	}
	public long getDeleteWaitTime() {
		return deleteWaitTime;
	}
	public void setDeleteWaitTime(long deleteWaitTime) {
		this.deleteWaitTime = deleteWaitTime;
	}
	public int getDeleteCapacity() {
		return deleteCapacity;
	}
	public void setDeleteCapacity(int deleteCapacity) {
		this.deleteCapacity = deleteCapacity;
	}
	public int getDeleteThreadPriority() {
		return deleteThreadPriority;
	}
	public void setDeleteThreadPriority(int deleteThreadPriority) {
		this.deleteThreadPriority = deleteThreadPriority;
	}
}
