package org.fl.noodlenotify.core.distribute;

public class DistributeConfParam {
	
	private int setThreadPriorityFresh = 10;
	private int setThreadPriorityNew = 10;
	private int setThreadPriorityPortion = 10;
	private int setThreadPriorityDeleteTimeout = 10;
	
	private long selectByIdIntervalFresh = 10000;
	private long selectMinMaxTimeIntervalFresh = 1000;
	private long selectLenTimeIntervalFresh = 120000;
	private long selectTimeIntervalFresh = 3000;
	
	private long selectByIdIntervalNew = 10000;
	private long selectMinMaxTimeIntervalNew = 10000;
	private long selectEmptyTimeIntervalNew = 10000;
	private long selectLenTimeIntervalNew = 300000;
	private long selectLenTimeIntervalRatioNew = 50;
	private long selectCountDownTimeIntervalNew = 300000;
	private long selectCountDownTimeIntervalRatioNew = 50;
	private long selectCountDownTimeIntervalMinNew = 60000;

	private long selectByIdIntervalPortion = 30000;
	private long selectMinMaxTimeIntervalPortion = 20000;
	private long selectEmptyTimeIntervalPortion = 20000;
	private long selectLenTimeIntervalPortion = 20000;
	private long selectLenTimeIntervalRatioPortion = 50;
	private long selectCountDownTimeIntervalPortion = 30000;
	private long selectCountDownTimeIntervalRatioPortion = 50;
	private long selectCountDownTimeIntervalMinPortion = 60000;

	private long selectByIdIntervalDelete = 100000;
	private long selectMinMaxTimeIntervalDelete = 10000;
	private long selectEmptyTimeIntervalDelete = 10000;
	
	private long selectByIdIntervalDeleteTimeout = 100000;
	private long selectMinMaxTimeIntervalDeleteTimeout = 10000;
	private long selectEmptyTimeIntervalDeleteTimeout = 180000;
	private long selectDeleteTimeout = 300000;
	
	private int checkActiveTimeInterval = 3000;
	
	private long queueCacheCapacityNew = 100000;
	private long queueCacheCapacityPortion = 50000;
	
	private int executeCapacityNew = 2000;
	private int executeCapacityPortion = 1000;
	
	private double executeOfferTimeoutWait = 0.5;
	
	private int executeBatchNum = 10;
	private int executeCapacityBatch = 1000;
	private long executeBatchWaitTime = 1000;
	
	public int getSetThreadPriorityFresh() {
		return setThreadPriorityFresh;
	}
	public void setSetThreadPriorityFresh(int setThreadPriorityFresh) {
		this.setThreadPriorityFresh = setThreadPriorityFresh;
	}
	public int getSetThreadPriorityNew() {
		return setThreadPriorityNew;
	}
	public void setSetThreadPriorityNew(int setThreadPriorityNew) {
		this.setThreadPriorityNew = setThreadPriorityNew;
	}
	public int getSetThreadPriorityPortion() {
		return setThreadPriorityPortion;
	}
	public void setSetThreadPriorityPortion(int setThreadPriorityPortion) {
		this.setThreadPriorityPortion = setThreadPriorityPortion;
	}
	public int getSetThreadPriorityDeleteTimeout() {
		return setThreadPriorityDeleteTimeout;
	}
	public void setSetThreadPriorityDeleteTimeout(
			int setThreadPriorityDeleteTimeout) {
		this.setThreadPriorityDeleteTimeout = setThreadPriorityDeleteTimeout;
	}
	public long getSelectByIdIntervalFresh() {
		return selectByIdIntervalFresh;
	}
	public void setSelectByIdIntervalFresh(long selectByIdIntervalFresh) {
		this.selectByIdIntervalFresh = selectByIdIntervalFresh;
	}
	public long getSelectMinMaxTimeIntervalFresh() {
		return selectMinMaxTimeIntervalFresh;
	}
	public void setSelectMinMaxTimeIntervalFresh(long selectMinMaxTimeIntervalFresh) {
		this.selectMinMaxTimeIntervalFresh = selectMinMaxTimeIntervalFresh;
	}
	public long getSelectLenTimeIntervalFresh() {
		return selectLenTimeIntervalFresh;
	}
	public void setSelectLenTimeIntervalFresh(long selectLenTimeIntervalFresh) {
		this.selectLenTimeIntervalFresh = selectLenTimeIntervalFresh;
	}
	public long getSelectTimeIntervalFresh() {
		return selectTimeIntervalFresh;
	}
	public void setSelectTimeIntervalFresh(long selectTimeIntervalFresh) {
		this.selectTimeIntervalFresh = selectTimeIntervalFresh;
	}
	public long getSelectByIdIntervalNew() {
		return selectByIdIntervalNew;
	}
	public void setSelectByIdIntervalNew(long selectByIdIntervalNew) {
		this.selectByIdIntervalNew = selectByIdIntervalNew;
	}
	public long getSelectMinMaxTimeIntervalNew() {
		return selectMinMaxTimeIntervalNew;
	}
	public void setSelectMinMaxTimeIntervalNew(long selectMinMaxTimeIntervalNew) {
		this.selectMinMaxTimeIntervalNew = selectMinMaxTimeIntervalNew;
	}
	public long getSelectEmptyTimeIntervalNew() {
		return selectEmptyTimeIntervalNew;
	}
	public void setSelectEmptyTimeIntervalNew(long selectEmptyTimeIntervalNew) {
		this.selectEmptyTimeIntervalNew = selectEmptyTimeIntervalNew;
	}
	public long getSelectLenTimeIntervalNew() {
		return selectLenTimeIntervalNew;
	}
	public void setSelectLenTimeIntervalNew(long selectLenTimeIntervalNew) {
		this.selectLenTimeIntervalNew = selectLenTimeIntervalNew;
	}
	public long getSelectLenTimeIntervalRatioNew() {
		return selectLenTimeIntervalRatioNew;
	}
	public void setSelectLenTimeIntervalRatioNew(
			long selectLenTimeIntervalRatioNew) {
		this.selectLenTimeIntervalRatioNew = selectLenTimeIntervalRatioNew;
	}
	public long getSelectCountDownTimeIntervalNew() {
		return selectCountDownTimeIntervalNew;
	}
	public void setSelectCountDownTimeIntervalNew(
			long selectCountDownTimeIntervalNew) {
		this.selectCountDownTimeIntervalNew = selectCountDownTimeIntervalNew;
	}
	public long getSelectCountDownTimeIntervalRatioNew() {
		return selectCountDownTimeIntervalRatioNew;
	}
	public void setSelectCountDownTimeIntervalRatioNew(
			long selectCountDownTimeIntervalRatioNew) {
		this.selectCountDownTimeIntervalRatioNew = selectCountDownTimeIntervalRatioNew;
	}
	public long getSelectCountDownTimeIntervalMinNew() {
		return selectCountDownTimeIntervalMinNew;
	}
	public void setSelectCountDownTimeIntervalMinNew(
			long selectCountDownTimeIntervalMinNew) {
		this.selectCountDownTimeIntervalMinNew = selectCountDownTimeIntervalMinNew;
	}
	public long getSelectByIdIntervalPortion() {
		return selectByIdIntervalPortion;
	}
	public void setSelectByIdIntervalPortion(long selectByIdIntervalPortion) {
		this.selectByIdIntervalPortion = selectByIdIntervalPortion;
	}
	public long getSelectMinMaxTimeIntervalPortion() {
		return selectMinMaxTimeIntervalPortion;
	}
	public void setSelectMinMaxTimeIntervalPortion(long selectMinMaxTimeIntervalPortion) {
		this.selectMinMaxTimeIntervalPortion = selectMinMaxTimeIntervalPortion;
	}
	public long getSelectEmptyTimeIntervalPortion() {
		return selectEmptyTimeIntervalPortion;
	}
	public void setSelectEmptyTimeIntervalPortion(
			long selectEmptyTimeIntervalPortion) {
		this.selectEmptyTimeIntervalPortion = selectEmptyTimeIntervalPortion;
	}
	public long getSelectLenTimeIntervalPortion() {
		return selectLenTimeIntervalPortion;
	}
	public void setSelectLenTimeIntervalPortion(long selectLenTimeIntervalPortion) {
		this.selectLenTimeIntervalPortion = selectLenTimeIntervalPortion;
	}
	public long getSelectLenTimeIntervalRatioPortion() {
		return selectLenTimeIntervalRatioPortion;
	}
	public void setSelectLenTimeIntervalRatioPortion(
			long selectLenTimeIntervalRatioPortion) {
		this.selectLenTimeIntervalRatioPortion = selectLenTimeIntervalRatioPortion;
	}
	public long getSelectCountDownTimeIntervalPortion() {
		return selectCountDownTimeIntervalPortion;
	}
	public void setSelectCountDownTimeIntervalPortion(
			long selectCountDownTimeIntervalPortion) {
		this.selectCountDownTimeIntervalPortion = selectCountDownTimeIntervalPortion;
	}
	public long getSelectCountDownTimeIntervalRatioPortion() {
		return selectCountDownTimeIntervalRatioPortion;
	}
	public void setSelectCountDownTimeIntervalRatioPortion(
			long selectCountDownTimeIntervalRatioPortion) {
		this.selectCountDownTimeIntervalRatioPortion = selectCountDownTimeIntervalRatioPortion;
	}
	public long getSelectCountDownTimeIntervalMinPortion() {
		return selectCountDownTimeIntervalMinPortion;
	}
	public void setSelectCountDownTimeIntervalMinPortion(
			long selectCountDownTimeIntervalMinPortion) {
		this.selectCountDownTimeIntervalMinPortion = selectCountDownTimeIntervalMinPortion;
	}
	public long getSelectByIdIntervalDelete() {
		return selectByIdIntervalDelete;
	}
	public void setSelectByIdIntervalDelete(long selectByIdIntervalDelete) {
		this.selectByIdIntervalDelete = selectByIdIntervalDelete;
	}
	public long getSelectMinMaxTimeIntervalDelete() {
		return selectMinMaxTimeIntervalDelete;
	}
	public void setSelectMinMaxTimeIntervalDelete(long selectMinMaxTimeIntervalDelete) {
		this.selectMinMaxTimeIntervalDelete = selectMinMaxTimeIntervalDelete;
	}
	public long getSelectEmptyTimeIntervalDelete() {
		return selectEmptyTimeIntervalDelete;
	}
	public void setSelectEmptyTimeIntervalDelete(
			long selectEmptyTimeIntervalDelete) {
		this.selectEmptyTimeIntervalDelete = selectEmptyTimeIntervalDelete;
	}
	public long getSelectByIdIntervalDeleteTimeout() {
		return selectByIdIntervalDeleteTimeout;
	}
	public void setSelectByIdIntervalDeleteTimeout(
			long selectByIdIntervalDeleteTimeout) {
		this.selectByIdIntervalDeleteTimeout = selectByIdIntervalDeleteTimeout;
	}
	public long getSelectMinMaxTimeIntervalDeleteTimeout() {
		return selectMinMaxTimeIntervalDeleteTimeout;
	}
	public void setSelectMinMaxTimeIntervalDeleteTimeout(
			long selectMinMaxTimeIntervalDeleteTimeout) {
		this.selectMinMaxTimeIntervalDeleteTimeout = selectMinMaxTimeIntervalDeleteTimeout;
	}
	public long getSelectEmptyTimeIntervalDeleteTimeout() {
		return selectEmptyTimeIntervalDeleteTimeout;
	}
	public void setSelectEmptyTimeIntervalDeleteTimeout(
			long selectEmptyTimeIntervalDeleteTimeout) {
		this.selectEmptyTimeIntervalDeleteTimeout = selectEmptyTimeIntervalDeleteTimeout;
	}
	public long getSelectDeleteTimeout() {
		return selectDeleteTimeout;
	}
	public void setSelectDeleteTimeout(long selectDeleteTimeout) {
		this.selectDeleteTimeout = selectDeleteTimeout;
	}
	public int getCheckActiveTimeInterval() {
		return checkActiveTimeInterval;
	}
	public void setCheckActiveTimeInterval(int checkActiveTimeInterval) {
		this.checkActiveTimeInterval = checkActiveTimeInterval;
	}
	public long getQueueCacheCapacityNew() {
		return queueCacheCapacityNew;
	}
	public void setQueueCacheCapacityNew(long queueCacheCapacityNew) {
		this.queueCacheCapacityNew = queueCacheCapacityNew;
	}
	public long getQueueCacheCapacityPortion() {
		return queueCacheCapacityPortion;
	}
	public void setQueueCacheCapacityPortion(long queueCacheCapacityPortion) {
		this.queueCacheCapacityPortion = queueCacheCapacityPortion;
	}
	public int getExecuteCapacityNew() {
		return executeCapacityNew;
	}
	public void setExecuteCapacityNew(int executeCapacityNew) {
		this.executeCapacityNew = executeCapacityNew;
	}
	public int getExecuteCapacityPortion() {
		return executeCapacityPortion;
	}
	public void setExecuteCapacityPortion(int executeCapacityPortion) {
		this.executeCapacityPortion = executeCapacityPortion;
	}
	public double getExecuteOfferTimeoutWait() {
		return executeOfferTimeoutWait;
	}
	public void setExecuteOfferTimeoutWait(double executeOfferTimeoutWait) {
		this.executeOfferTimeoutWait = executeOfferTimeoutWait;
	}
	public int getExecuteBatchNum() {
		return executeBatchNum;
	}
	public void setExecuteBatchNum(int executeBatchNum) {
		this.executeBatchNum = executeBatchNum;
	}
	public int getExecuteCapacityBatch() {
		return executeCapacityBatch;
	}
	public void setExecuteCapacityBatch(int executeCapacityBatch) {
		this.executeCapacityBatch = executeCapacityBatch;
	}
	public long getExecuteBatchWaitTime() {
		return executeBatchWaitTime;
	}
	public void setExecuteBatchWaitTime(long executeBatchWaitTime) {
		this.executeBatchWaitTime = executeBatchWaitTime;
	}
}
