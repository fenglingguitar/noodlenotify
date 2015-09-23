package org.fl.noodlenotify.core.connect;

public interface ConnectAgent {
	public void connect() throws Exception;
	public void reconnect() throws Exception;
	public void close();
	public long getConnectId();
	public boolean getConnectStatus();
	public void setConnectStatus(boolean status);
	public String getIp();
	public int getPort();
	public String getUrl();
	public String getType();
	public int getTimeout();
}
