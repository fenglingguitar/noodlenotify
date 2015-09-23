package org.fl.noodlenotify.core.connect.net;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

public interface NetConnectReceiver {
	public void start() throws Exception;
	public void destroy() throws Exception;
	public void receive(Message message) throws Exception;
}
