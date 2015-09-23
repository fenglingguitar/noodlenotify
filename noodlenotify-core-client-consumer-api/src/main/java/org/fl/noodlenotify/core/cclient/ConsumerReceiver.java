package org.fl.noodlenotify.core.cclient;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

public interface ConsumerReceiver {
	public boolean receive(Message message);
}
