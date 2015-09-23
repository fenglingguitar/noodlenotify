package org.fl.noodlenotify.monitor.status.executer.service;

public interface ExecuterService {
	public void execute() throws Exception;
	public long getInitialDelay() throws Exception;
	public long getDelay() throws Exception;
}
