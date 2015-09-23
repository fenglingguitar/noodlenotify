package org.fl.noodlenotify.core.distribute.locker;

public interface DistributeSetLocker {
	public void start();
	public void destroy();
	public boolean getStatus();
	public boolean waitLocker();
	public void releaseLocker();
}
