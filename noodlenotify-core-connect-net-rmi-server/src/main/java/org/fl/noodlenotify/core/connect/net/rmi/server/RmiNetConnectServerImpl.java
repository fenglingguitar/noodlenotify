package org.fl.noodlenotify.core.connect.net.rmi.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

public class RmiNetConnectServerImpl implements RmiNetConnectServer {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(RmiNetConnectServerImpl.class);
	
	private NetConnectReceiver netConnectReceiver;
	
	@Override
	public String send(Object object) throws Exception {
		Message message = (Message)object;
		if (message.getUuid() == null) {
			String uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
			message.setUuid(uuid);
		}
		netConnectReceiver.receive(message);
		return message.getUuid();
	}
	
	@Override
	public boolean checkHealth() throws Exception {
		return true;
	}
	
	public void setNetConnectReceiver(NetConnectReceiver netConnectReceiver) {
		this.netConnectReceiver = netConnectReceiver;
	}
}
