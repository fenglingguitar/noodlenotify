package org.fl.noodlenotify.core.connect.net.jetty.server;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class JettyNetConnectServerReceiveHandler extends AbstractHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectServerReceiveHandler.class);
	
	private NetConnectReceiver netConnectReceiver;
	
	private String healthUrl = "/noodlenotify/check";
	private String url = "/noodlenotify";
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException {
		response.setContentType("application/x-www-form-urlencoded;charset=utf-8");  
        response.setStatus(HttpServletResponse.SC_OK);  
        baseRequest.setHandled(true);
        if (target.startsWith(healthUrl)) {
        	try {
    			response.getWriter().print(JsonTranslator.toString(new MessageResult(true)));
    		} catch (Exception exception) {
    			if (logger.isErrorEnabled()) {
    				logger.error("MessageReceived -> JsonTranslator toString -> ReceiveException -> " + exception);
    			}
    		}
        } else if (target.startsWith(url)) {
			doReceive(request, response);
		}
	}

	private void doReceive(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("utf-8");
		
		String uuid = null;
		
		String json = request.getParameter("input");
		
		if (json != null) {
			try {
				Message message = JsonTranslator.fromString(json, Message.class);
				if (message.getUuid() == null) {
					uuid = UUID.randomUUID().toString().replaceAll("-", ""); 
					message.setUuid(uuid);
				} else {
					uuid = message.getUuid();
				}
				netConnectReceiver.receive(message);
				response.getWriter().print(JsonTranslator.toString(new MessageResult(true, uuid)));
			} catch (Exception receiveException) {
				if (logger.isErrorEnabled()) {
					logger.error("DoReceive -> JsonTranslator FromString -> ReceiveException -> " + receiveException);
				}
				try {
					response.getWriter().print(
						JsonTranslator.toString(
								new MessageResult(
										false, uuid, JsonTranslator.toStringWithClassName(receiveException)
										)
								)
						);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("DoReceive -> JsonTranslator toString -> ReceiveException -> " + e);
					}
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error("DoReceive -> GetParameter Input -> Null");
			}
			try {
				response.getWriter().print(
						JsonTranslator.toString(
								new MessageResult(
										false, uuid, JsonTranslator.toStringWithClassName(new NullPointerException("input is null"))
										)
								)
						);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("DoReceive -> JsonTranslator toString -> Input NullPointerException -> " + e);
				}
			}
		}
	}

	public void setNetConnectReceiver(NetConnectReceiver netConnectReceiver) {
		this.netConnectReceiver = netConnectReceiver;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
