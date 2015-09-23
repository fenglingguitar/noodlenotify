package org.fl.noodlenotify.monitor.performance.vo;

import org.fl.noodlenotify.monitor.performance.persistence.vo.BaseVo;

public class OvertimeVo extends BaseVo {
	
	private long totalCount;
	private long overtimeCount;
	private long totalTime;
	private long overtimeTime;
	private long averageTotalTime;
	private long averageOvertimeTime;
	private long threshold;
	
	public long getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public long getOvertimeCount() {
		return overtimeCount;
	}

	public void setOvertimeCount(long overtimeCount) {
		this.overtimeCount = overtimeCount;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	public long getOvertimeTime() {
		return overtimeTime;
	}

	public void setOvertimeTime(long overtimeTime) {
		this.overtimeTime = overtimeTime;
	}

	public long getAverageTotalTime() {
		return averageTotalTime;
	}

	public void setAverageTotalTime(long averageTotalTime) {
		this.averageTotalTime = averageTotalTime;
	}

	public long getAverageOvertimeTime() {
		return averageOvertimeTime;
	}

	public void setAverageOvertimeTime(long averageOvertimeTime) {
		this.averageOvertimeTime = averageOvertimeTime;
	}

	public long getThreshold() {
		return threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}
}
