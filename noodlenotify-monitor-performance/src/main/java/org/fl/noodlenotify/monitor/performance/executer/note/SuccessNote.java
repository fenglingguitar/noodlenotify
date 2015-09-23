package org.fl.noodlenotify.monitor.performance.executer.note;

import java.util.concurrent.atomic.AtomicLong;

public class SuccessNote { 

	private AtomicLong totalCount = new AtomicLong(0);
	private AtomicLong successCount = new AtomicLong(0);
	
	public void totalCountAdd() {
		totalCount.incrementAndGet();
	}
	
	public long totalCountReset() {
		return totalCount.getAndSet(0);
	}
	
	public void successCountAdd() {
		successCount.incrementAndGet();
	}
	
	public long successCountReset() {
		return successCount.getAndSet(0);
	}
} 
