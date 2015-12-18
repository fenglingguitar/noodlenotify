package org.fl.noodlenotify.core.connect.net.netty.client;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.core.connect.net.netty.client.NettyNetConnect;
import org.fl.noodlenotify.core.connect.net.pojo.Message;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/core/connect/net/netty/server/noodlenotify-core-connect-net-server-netty-test.xml"
})
public class NettyNetConnectTest extends AbstractJUnit4SpringContextTests {

	private static NettyNetConnect nettyNetConnect;

	@Test
	public void testDoConnect() {
		nettyNetConnect = new NettyNetConnect("127.0.0.1", 20001, 3000);
		nettyNetConnect.connect();
	}

	@Test
	public void testSend() throws Exception {
		testDoConnect();
		nettyNetConnect.send(new Message(), 3000);
		testClose();
	}

	@Test
	public void testClose() {
		testDoConnect();
		nettyNetConnect.close();
	}
}
