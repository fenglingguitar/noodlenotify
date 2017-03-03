package org.fl.noodlenotify.core.connect.net.netty.server;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.common.pojo.net.MessageRequest;
import org.fl.noodlenotify.common.pojo.net.MessageResult;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;

public class NettyNetConnectServerReceiveHandler extends SimpleChannelHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectServerReceiveHandler.class);
	
	private NetConnectReceiver netConnectReceiver;
	
	private String healthName = "/check";
	
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
			throws Exception {
		
		NettyNetConnectServerModel nettyNetConnectServerModel = (NettyNetConnectServerModel) e.getMessage();
		
		if (nettyNetConnectServerModel.getName().startsWith(healthName)) {
			try {
				ctx.getChannel().write(JsonTranslator.toString(new MessageResult(true)));
			} catch (Exception exception) {
				if (logger.isErrorEnabled()) {
					logger.error("MessageReceived -> JsonTranslator toString -> ReceiveException -> " + exception);
				}
			}
			return;
		}
		
		String uuid = null;
		
		try {
			MessageRequest messageRequest = JsonTranslator.fromString(nettyNetConnectServerModel.getData(), MessageRequest.class);
			if (messageRequest.getUuid() == null) {
				uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
				messageRequest.setUuid(uuid);
			} else {
				uuid = messageRequest.getUuid();
			}
			netConnectReceiver.receive(messageRequest);
			ctx.getChannel().write(JsonTranslator.toString(new MessageResult(true, uuid)));
		} catch (Exception receiveException) {
			if (logger.isErrorEnabled()) {
				logger.error("MessageReceived -> JsonTranslator FromString -> ReceiveException -> " + receiveException);
			}
			try {
				ctx.getChannel().write(
						JsonTranslator.toString(
								new MessageResult(
										false, uuid, JsonTranslator.toStringWithClassName(receiveException)
										)
								)
						);
			} catch (Exception exception) {
				if (logger.isErrorEnabled()) {
					logger.error("MessageReceived -> JsonTranslator toStringWithClassName -> ReceiveException -> " + exception);
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		
		e.getChannel().close();
	}

	public void setNetConnectReceiver(NetConnectReceiver netConnectReceiver) {
		this.netConnectReceiver = netConnectReceiver;
	}
}
