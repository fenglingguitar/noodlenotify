package org.fl.noodlenotify.core.pclient;

public interface ProducerClient {
	public String send(String queueName, String content) throws Exception;
}
