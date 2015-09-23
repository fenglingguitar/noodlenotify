package org.fl.noodlenotify.core.connect.net.netty.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.core.connect.net.pojo.MessageResult;

public class NettyNetConnect {

	private final static Logger logger = LoggerFactory.getLogger(NettyNetConnect.class);

	private Socket socket;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	
	private String ip;
	private int port;
	private int timeout;
	
	public NettyNetConnect(String ip, int port, int timeout) {
		this.ip = ip;
		this.port = port;
		this.timeout = timeout;
	}

	public void connect() {
		if (!isConnected()) {
			try {
				socket = new Socket();
	            socket.setReuseAddress(true);
	            socket.setKeepAlive(true);
	            socket.setTcpNoDelay(true);
	            socket.setSoLinger(true, 0);
	            socket.connect(new InetSocketAddress(ip, port), timeout);
	            socket.setSoTimeout(timeout);
	            outputStream = socket.getOutputStream(); 
				inputStream = socket.getInputStream(); 
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Connect -> "
								+ "Ip: " + ip
								+ ", Port: " + port
								+ ", New Socket -> " + e
								);
				}
			} 
		}
	}

	public void close() {
		if (isConnected()) {
			try {
				outputStream.close();
				inputStream.close();
				if (!socket.isClosed()) {
                    socket.close();
                }
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Close -> "
								+ "Ip: " + ip
								+ ", Port: " + port
								+ ", Close Socket -> " + e
								);
				}
			}
		}
	}
	
	public boolean isConnected() {
        return socket != null && socket.isBound() && !socket.isClosed()
                && socket.isConnected() && !socket.isInputShutdown()
                && !socket.isOutputShutdown();
    }
	
	public String send(Object object) throws Exception {
		
		connect();
		
		String jMessage = JsonTranslator.toString(object);
		
		byte[] bMessage = jMessage.getBytes("UTF-8");
		
		int bufSendSize = bMessage.length;
		int bufSendSizeApply = ((bMessage.length + 4) / 64 + 1) * 64;
		
		byte[] bufSend = new byte[bufSendSizeApply];
		
		bufSend[0] = (byte) ((bufSendSize >>> 24) & 0xFF);
		bufSend[1] = (byte) ((bufSendSize >>> 16) & 0xFF);
		bufSend[2] = (byte) ((bufSendSize >>>  8) & 0xFF);
		bufSend[3] = (byte) ((bufSendSize >>>  0) & 0xFF);
		
		System.arraycopy(bMessage, 0, bufSend, 4, bufSendSize);
		
		outputStream.write(bufSend, 0, bufSendSize + 4);
		
		byte[] bufRecvSizeBuf = new byte[4];

		int recvSizeBufSize = 0;
		int recvSizeBufNextSize = 0;
		do {
			recvSizeBufNextSize = 4 - recvSizeBufSize;
			int size = inputStream.read(bufRecvSizeBuf, recvSizeBufSize, recvSizeBufNextSize);
			if (size == -1) {
				throw new java.net.SocketException();
			}
			recvSizeBufSize += size;
		} while (recvSizeBufSize < 4);
		
		int bufRecvSize = 
				((bufRecvSizeBuf[0] & 255) << 24) +
                ((bufRecvSizeBuf[1] & 255) << 16) +
                ((bufRecvSizeBuf[2] & 255) <<  8) +
                ((bufRecvSizeBuf[3] & 255) <<  0);
		int bufRecvSizeApply = (bufRecvSize / 64 + 1 ) * 64;
		
		byte[] bufRecv = new byte[bufRecvSizeApply];
		
		int recvSize = 0;
		int recvNextSize = 0;
		do {
			recvNextSize = bufRecvSize - recvSize;
			int size = inputStream.read(bufRecv, recvSize, recvNextSize);
			if (size == -1) {
				throw new java.net.SocketException();
			}
			recvSize += size;
		} while (recvSize < bufRecvSize);
		
		String jResult = new String(bufRecv, 0, bufRecvSize, "UTF-8");

		MessageResult messageResult = JsonTranslator.fromString(jResult, MessageResult.class);
		
		String uuid = null;
		
		if (messageResult.getResult()) {
			uuid = messageResult.getUuid();
		} else {
			throw (Exception) JsonTranslator.fromStringWithClassName(messageResult.getError());
		}
		
		return uuid;
	}
}
