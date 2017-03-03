package org.fl.noodlenotify.core.connect.net;

import org.fl.noodlenotify.common.pojo.net.MessageRequest;

public interface NetConnectReceiver {
	public void receive(MessageRequest message) throws Exception;
}
