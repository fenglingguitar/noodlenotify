package org.fl.noodlenotify.monitor.performance.executer.service;

public interface PerformanceExecuterService {
	public void send();
	public long getInitialDelay() throws Exception;
	public long getDelay() throws Exception;
}
