package org.fl.noodlenotify.monitor.performance.vo;

import org.fl.noodlenotify.monitor.performance.persistence.vo.BaseVo;

public class SuccessVo extends BaseVo {
	
	private long totalCount;
	private long successCount;
	private double successRate;
	
	public long getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
	public long getSuccessCount() {
		return successCount;
	}
	
	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(double d) {
		this.successRate = d;
	}
}
