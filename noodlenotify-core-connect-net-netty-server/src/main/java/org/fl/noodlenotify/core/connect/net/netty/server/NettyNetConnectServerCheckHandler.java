package org.fl.noodlenotify.core.connect.net.netty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class NettyNetConnectServerCheckHandler extends SimpleChannelHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(NettyNetConnectServerCheckHandler.class);
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		
		try {
			ctx.getChannel().write(JsonTranslator.toString(new MessageResult(true)));
		} catch (Exception exception) {
			if (logger.isErrorEnabled()) {
				logger.error("MessageReceived -> JsonTranslator toString -> ReceiveException -> " + exception);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		
		e.getChannel().close();
	}
}
