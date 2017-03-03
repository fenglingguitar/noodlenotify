package org.fl.noodlenotify.core.connect.cache.body;

import org.fl.noodlenotify.common.pojo.db.MessageDb;

public interface BodyCacheConnectAgent {
	public void set(MessageDb messageDb) throws Exception;
	public MessageDb get(MessageDb messageDb) throws Exception;
	public void remove(MessageDb messageDb) throws Exception;
}
