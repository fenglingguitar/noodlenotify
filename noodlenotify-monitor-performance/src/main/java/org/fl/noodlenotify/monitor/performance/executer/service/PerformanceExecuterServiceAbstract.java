package org.fl.noodlenotify.monitor.performance.executer.service;

import java.util.Map;

public abstract class PerformanceExecuterServiceAbstract implements
		PerformanceExecuterService {

	protected long initialDelay = 0;
	protected long delay = 0;
	
	protected Map<String, String> sendTypeMap;
	
	@Override
	public long getInitialDelay() throws Exception {
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay) {
		this.initialDelay = initialDelay;
	}

	@Override
	public long getDelay() throws Exception {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public Map<String, String> getSendTypeMap() {
		return sendTypeMap;
	}

	public void setSendTypeMap(Map<String, String> sendTypeMap) {
		this.sendTypeMap = sendTypeMap;
	}

}
