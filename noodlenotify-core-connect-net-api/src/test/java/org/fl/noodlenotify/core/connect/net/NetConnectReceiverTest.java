package org.fl.noodlenotify.core.connect.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class NetConnectReceiverTest implements NetConnectReceiver {

	private final static Logger logger = LoggerFactory.getLogger(NetConnectReceiverTest.class);
	
	@Override
	public void receive(Message message) throws Exception {
		logger.info("Receive: " + JsonTranslator.toString(message));
	}
}
