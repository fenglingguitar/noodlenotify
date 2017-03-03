package org.fl.noodlenotify.core.cclient;

import org.fl.noodlenotify.common.pojo.net.MessageRequest;

public interface ConsumerReceiver {
	public boolean receive(MessageRequest message);
}
