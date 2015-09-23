package org.fl.noodlenotify.core.connect;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ConnectAgentAbstract implements ConnectAgent {
	
	protected AtomicBoolean connectStatus = new AtomicBoolean(false);
	
	protected String ip;
	protected int port;
	protected long connectId;
	protected String url;
	protected String type;
	
	public ConnectAgentAbstract(String ip, int port, long connectId) {
		this.ip = ip;
		this.port = port;
		this.connectId = connectId;
	}
	
	public ConnectAgentAbstract(String ip, int port, long connectId, String type) {
		this.ip = ip;
		this.port = port;
		this.connectId = connectId;
		this.type = type;
	}
	
	public ConnectAgentAbstract(String ip, int port, long connectId, String url, String type) {
		this.ip = ip;
		this.port = port;
		this.connectId = connectId;
		this.url = url;
		this.type = type;
	}
	
	@Override
	public void connect() throws Exception {
		
		try {
			connectActual();
		} catch (Exception e) {
			connectStatus.set(false);
			throw e;
		}
		connectStatus.set(true);
	}
	
	protected abstract void connectActual() throws Exception;
	
	@Override
	public void reconnect() throws Exception {
		try {
			reconnectActual();
		} catch (Exception e) {
			connectStatus.set(false);
			throw e;
		}
		connectStatus.set(true);
	}
	
	protected abstract void reconnectActual() throws Exception;

	@Override
	public void close() {
		connectStatus.set(false);
		closeActual();
	}
	
	protected abstract void closeActual();

	@Override
	public boolean getConnectStatus() {
		return connectStatus.get();
	}
	
	@Override
	public void setConnectStatus(boolean status) {
		connectStatus.set(status);
	}
	
	@Override
	public long getConnectId() {
		return connectId;
	}

	@Override
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getTimeout() {
		return 0;
	}
}
