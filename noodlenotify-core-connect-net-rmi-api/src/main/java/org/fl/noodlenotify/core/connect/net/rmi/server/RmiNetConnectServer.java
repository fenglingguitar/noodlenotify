package org.fl.noodlenotify.core.connect.net.rmi.server;

public interface RmiNetConnectServer {
	public String send(Object object) throws Exception;
	public boolean checkHealth() throws Exception;
}
