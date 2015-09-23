package org.fl.noodlenotify.core.connect.cache.trace;

public interface TraceCacheStatusChecker {
	public void checkHealth() throws Exception;
	public long checkSize() throws Exception;
}