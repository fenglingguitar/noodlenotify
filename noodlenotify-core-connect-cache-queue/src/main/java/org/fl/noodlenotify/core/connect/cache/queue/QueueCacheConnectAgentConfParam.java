package org.fl.noodlenotify.core.connect.cache.queue;

public class QueueCacheConnectAgentConfParam {
	
	private int popTimeout = 5;
	private long hashExpire = 1200000;
	private int expire = 600;
	
	public int getPopTimeout() {
		return popTimeout;
	}
	public void setPopTimeout(int popTimeout) {
		this.popTimeout = popTimeout;
	}
	public int getExpire() {
		return expire;
	}
	public void setExpire(int expire) {
		this.expire = expire;
	}
	public long getHashExpire() {
		return hashExpire;
	}
	public void setHashExpire(long hashExpire) {
		this.hashExpire = hashExpire;
	}
}
