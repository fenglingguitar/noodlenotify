package org.fl.noodlenotify.core.connect.net;

import org.fl.noodlenotify.core.connect.net.pojo.Message;

public interface NetConnectAgent {
	public String send(Message message) throws Exception;
	public String send(Message message, int readTimeout) throws Exception;
}
