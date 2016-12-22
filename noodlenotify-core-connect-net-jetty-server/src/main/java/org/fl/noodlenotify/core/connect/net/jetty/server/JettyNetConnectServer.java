package org.fl.noodlenotify.core.connect.net.jetty.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.fl.noodle.common.connect.server.ConnectServer;

public class JettyNetConnectServer implements ConnectServer {
	
	private final static Logger logger = LoggerFactory.getLogger(JettyNetConnectServer.class);
	
	private Server server;
	
	private int port;
	
	private Handler servletHandler;

	@Override
	public void start() throws Exception {
		
		server = new Server();
		SelectChannelConnector conn = new SelectChannelConnector();
		conn.setPort(port);
		server.setConnectors(new Connector[]{conn});
		server.setHandler(servletHandler);
		server.start();
		if (logger.isDebugEnabled()) {
			logger.debug("Start a JettyNetConnectServer -> Port: " + port);
		}
	}

	@Override
	public void destroy() throws Exception {
		
		server.stop();
		//server.join();
		if (logger.isDebugEnabled()) {
			logger.debug("Close a JettyNetConnectServer -> Port: " + port);
		}
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setServletHandler(Handler servletHandler) {
		this.servletHandler = servletHandler;
	}
	
}
