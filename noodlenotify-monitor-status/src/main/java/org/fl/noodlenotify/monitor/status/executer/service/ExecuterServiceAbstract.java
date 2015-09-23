package org.fl.noodlenotify.monitor.status.executer.service;

public abstract class ExecuterServiceAbstract implements
		ExecuterService {

	protected long initialDelay = 0;
	protected long delay = 0;
	
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

}
