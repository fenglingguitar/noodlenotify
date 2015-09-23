package org.fl.noodlenotify.core.connect.cache.trace;

public class TraceCacheConnectAgentConfParam {
	
	private int newExpire = 86400;
	private int successExpire = 60;
	private long capacity = 3000000;
	
	public int getNewExpire() {
		return newExpire;
	}
	public void setNewExpire(int newExpire) {
		this.newExpire = newExpire;
	}
	
	public int getSuccessExpire() {
		return successExpire;
	}
	public void setSuccessExpire(int successExpire) {
		this.successExpire = successExpire;
	}
	
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
}
