package org.fl.noodlenotify.core.connect.net;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

public interface NetConnectReceiver {
	public void receive(Message message) throws Exception;
}
