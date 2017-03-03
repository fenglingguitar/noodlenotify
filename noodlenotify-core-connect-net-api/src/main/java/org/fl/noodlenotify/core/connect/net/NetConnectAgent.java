package org.fl.noodlenotify.core.connect.net;

import org.fl.noodlenotify.common.pojo.net.MessageRequest;

public interface NetConnectAgent {
	public String send(MessageRequest message) throws Exception;
	public String send(MessageRequest message, int readTimeout) throws Exception;
}
