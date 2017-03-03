package org.fl.noodlenotify.demo.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.core.cclient.ConsumerReceiver;

public class ConsumerReceiverDemo implements ConsumerReceiver {

	private final static Logger logger = LoggerFactory.getLogger(ConsumerReceiverDemo.class);
	
	@Override
	public boolean receive(MessageRequest message) {
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			logger.equals(e);
		}
		
		return true;
	}
}
