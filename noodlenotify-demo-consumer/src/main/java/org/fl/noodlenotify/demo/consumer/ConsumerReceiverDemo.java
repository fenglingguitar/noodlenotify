package org.fl.noodlenotify.demo.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.core.cclient.ConsumerReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class ConsumerReceiverDemo implements ConsumerReceiver {

	private final static Logger logger = LoggerFactory.getLogger(ConsumerReceiverDemo.class);
	
	@Override
	public boolean receive(Message message) {
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			logger.equals(e);
		}
		
		return true;
	}
}
