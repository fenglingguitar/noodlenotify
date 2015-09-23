package org.fl.noodlenotify.core.connect.cache.body;

import org.fl.noodlenotify.core.domain.message.MessageDm;

public interface BodyCacheConnectAgent {
	public void set(MessageDm messageDm) throws Exception;
	public MessageDm get(MessageDm messageDm) throws Exception;
	public void remove(MessageDm messageDm) throws Exception;
}
