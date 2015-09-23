package org.fl.noodlenotify.monitor.performance.executer.note;

import java.util.concurrent.atomic.AtomicLong;

public class OvertimeNote { 

	private AtomicLong totalCount = new AtomicLong(0);
	private AtomicLong overtimeCount = new AtomicLong(0);
	private AtomicLong totalTime = new AtomicLong(0);
	private AtomicLong overtimeTime = new AtomicLong(0);
	
	public void totalCountAdd() {
		totalCount.incrementAndGet();
	}
	
	public long totalCountReset() {
		return totalCount.getAndSet(0);
	}
	
	public void overtimeCountAdd() {
		overtimeCount.incrementAndGet();
	}
	
	public long overtimeCountReset() {
		return overtimeCount.getAndSet(0);
	}
	
	public void totalTimeAdd(long time) {
		totalTime.addAndGet(time);
	}
	
	public long totalTimeReset() {
		return totalTime.getAndSet(0);
	}
	
	public void overtimeTimeAdd(long time) {
		overtimeTime.addAndGet(time);
	}
	
	public long overtimeTimeReset() {
		return overtimeTime.getAndSet(0);
	}
} 
