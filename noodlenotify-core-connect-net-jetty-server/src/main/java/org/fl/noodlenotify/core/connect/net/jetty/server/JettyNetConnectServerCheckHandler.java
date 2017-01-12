package org.fl.noodlenotify.core.connect.net.jetty.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class JettyNetConnectServerCheckHandler extends AbstractHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectServerCheckHandler.class);
	
	private String url = "/noodlenotify";
	
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
	        throws IOException, ServletException {
		response.setContentType("application/x-www-form-urlencoded;charset=utf-8");  
        response.setStatus(HttpServletResponse.SC_OK);  
        baseRequest.setHandled(true);
		if (target.equals(url)) {
			doReceive(request, response);
		}
	}

	private void doReceive(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			response.getWriter().print(JsonTranslator.toString(new MessageResult(true)));
		} catch (Exception exception) {
			if (logger.isErrorEnabled()) {
				logger.error("MessageReceived -> JsonTranslator toString -> ReceiveException -> " + exception);
			}
		}
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
