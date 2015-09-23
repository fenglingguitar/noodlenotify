package org.fl.noodlenotify.core.connect.cache.body;

public class BodyCacheConnectAgentConfParam {
	
	private int expire = 10800;
	private long capacity = 100000;
	
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	
	public long getCapacity() {
		return capacity;
	}
	public void setCapacity(long capacity) {
		this.capacity = capacity;
	}
}
