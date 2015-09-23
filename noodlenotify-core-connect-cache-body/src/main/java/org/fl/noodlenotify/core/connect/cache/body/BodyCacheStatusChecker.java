package org.fl.noodlenotify.core.connect.cache.body;

public interface BodyCacheStatusChecker {
	public void checkHealth() throws Exception;
	public long checkSize() throws Exception;
}