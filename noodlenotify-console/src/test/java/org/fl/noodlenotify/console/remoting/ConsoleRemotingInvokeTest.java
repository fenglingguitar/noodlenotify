package org.fl.noodlenotify.console.remoting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/common/noodlenotify-common.xml",
		"classpath:org/fl/noodlenotify/console/bean/noodlenotify-console-bean.xml" 
})
public class ConsoleRemotingInvokeTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(ConsoleRemotingInvokeTest.class);

	@Autowired
	private ConsoleRemotingInvoke remotingInvoke;

	@Test
	public void testSaveProducerRegist() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "生产者-注册-test";
		remotingInvoke.saveProducerRegister(ip, checkPort, checkUrl, checkType, name);
	}

	@Test
	public void testSaveProducerCancel() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "生产者-注册-test";
		remotingInvoke.saveProducerCancel(remotingInvoke.saveProducerRegister(ip, checkPort, checkUrl, checkType, name));
	}

	@Test
	public void testSaveExchangerRegist() throws Exception {
		String ip = "127.0.0.1";
		int port = 123453;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String name = "交换器-注册-test";
		remotingInvoke.saveExchangerRegister(ip, port, url, type, checkPort, name);
	}

	@Test
	public void testSaveExchangerCancel() throws Exception {
		String ip = "127.0.0.1";
		int port = 123453;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String name = "交换器-注册-test";
		remotingInvoke.saveExchangerCancel(remotingInvoke.saveExchangerRegister(ip, port, url, type, checkPort, name));
	}

	@Test
	public void testSaveDistributerRegist() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String name = "分配器-注册-test";
		remotingInvoke.saveDistributerRegister(ip, checkPort, name);
	}

	@Test
	public void testSaveDistributerCancel() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String name = "分配器-注册-test";
		remotingInvoke.saveDistributerCancel(remotingInvoke.saveDistributerRegister(ip, checkPort, name));
	}

	@Test
	public void testSaveCustomerRegist() throws Exception {
		String ip = "127.0.0.1";
		int port = 12345;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "消费者-注册-test";
		String customerGroupName = "TestCustomerGroup1";
		List<String> queueNameList = new ArrayList<String>();
		queueNameList.add("TestQueue1");
		remotingInvoke.saveCustomerRegister(ip, port, url, type, checkPort, checkUrl, checkType, name, customerGroupName, queueNameList);
	}

	@Test
	public void testSaveCustomerCancel() throws Exception {
		String ip = "127.0.0.1";
		int port = 12345;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "消费者-注册-test";
		String customerGroupName = "TestCustomerGroup1";
		List<String> queueNameList = new ArrayList<String>();
		queueNameList.add("TestQueue1");
		remotingInvoke.saveCustomerCancel(remotingInvoke.saveCustomerRegister(ip, port, url, type, checkPort, checkUrl, checkType, name, customerGroupName, queueNameList));
	}

	@Test
	public void testProducerGetExchangers() throws Exception {
		long producerId = 3;
		Map<String, List<QueueExchangerVo>> map = remotingInvoke.producerGetExchangers(producerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueExchangerVo> list = map.get(queueName);
			for (QueueExchangerVo queueExchangerVo : list) {
				logger.info("QueueName: " + queueName + ", ExchangerId: " + queueExchangerVo.getExchanger_Id());
			}
		}
	}

	@Test
	public void testExchangerGetQueues() throws Exception {
		long exchangerId = 3;
		List<QueueExchangerVo> list = remotingInvoke.exchangerGetQueues(exchangerId);
		for (QueueExchangerVo queueExchangerVo : list) {
			logger.info("QueueName: " + queueExchangerVo.getQueue_Nm() + ", ExchangerId: " + queueExchangerVo.getExchanger_Id());
		}
	}

	@Test
	public void testExchangerGetMsgBodyCaches() throws Exception {
		long exchangerId = 3;
		Map<String, List<QueueMsgBodyCacheVo>> map = remotingInvoke.exchangerGetMsgBodyCaches(exchangerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueMsgBodyCacheVo> list = map.get(queueName);
			for (QueueMsgBodyCacheVo queueMsgBodyCacheVo : list) {
				logger.info("QueueName: " + queueName + ", MsgBodyCacheId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id());
			}
		}
	}

	@Test
	public void testExchangerGetMsgStorages() throws Exception {
		long exchangerId = 3;
		Map<String, List<QueueMsgStorageVo>> map = remotingInvoke.exchangerGetMsgStorages(exchangerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueMsgStorageVo> list = map.get(queueName);
			for (QueueMsgStorageVo queueMsgStorageVo : list) {
				logger.info("QueueName: " + queueName + ", MsgStorageId: " + queueMsgStorageVo.getMsgStorage_Id());
			}
		}
	}

	@Test
	public void testExchangerGetQueueCustomerGroupNum() throws Exception {
		long exchangerId = 1;
		Map<String, Long> map = remotingInvoke.exchangerGetQueueCustomerGroupNum(exchangerId);
		for (Map.Entry<String, Long> queueCustomerGroupNum : map.entrySet()) {
			logger.info("QueueName: " + queueCustomerGroupNum.getKey() + ", QueueCustomerGroupNum: " + queueCustomerGroupNum.getValue());
		}
	}

	@Test
	public void testDistributerGetQueues() throws Exception {
		long distributerId = 4;
		List<QueueDistributerVo> list = remotingInvoke.distributerGetQueues(distributerId);
		for (QueueDistributerVo queueDistributerVo : list) {
			logger.info("QueueName: " + queueDistributerVo.getQueue_Nm() + ", ExchangerId: " + queueDistributerVo.getDistributer_Id());
		}
	}

	@Test
	public void testDistributerGetMsgStorages() throws Exception {
		long distributerId = 4;
		Map<String, List<QueueMsgStorageVo>> map = remotingInvoke.distributerGetMsgStorages(distributerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueMsgStorageVo> list = map.get(queueName);
			for (QueueMsgStorageVo queueMsgStorageVo : list) {
				logger.info("QueueName: " + queueName + ", MsgStorageId: " + queueMsgStorageVo.getMsgStorage_Id());
			}
		}
	}

	@Test
	public void testDistributerGetMsgBodyCaches() throws Exception {
		long distributerId = 4;
		Map<String, List<QueueMsgBodyCacheVo>> map = remotingInvoke.distributerGetMsgBodyCaches(distributerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueMsgBodyCacheVo> list = map.get(queueName);
			for (QueueMsgBodyCacheVo queueMsgBodyCacheVo : list) {
				logger.info("QueueName: " + queueName + ", MsgBodyCacheId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id());
			}
		}
	}

	@Test
	public void testDistributerGetMsgQueueCaches() throws Exception {
		long distributerId = 4;
		Map<String, List<QueueMsgQueueCacheVo>> map = remotingInvoke.distributerGetMsgQueueCaches(distributerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueMsgQueueCacheVo> list = map.get(queueName);
			for (QueueMsgQueueCacheVo queueMsgQueueCacheVo : list) {
				logger.info("QueueName: " + queueName + ", MsgQueueCacheId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id());
			}
		}
	}

	@Test
	public void testDistributerGetQueueCustomers() throws Exception {
		long distributerId = 4;
		Map<String, List<QueueCustomerVo>> map = remotingInvoke.distributerGetQueueCustomers(distributerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueCustomerVo> list = map.get(queueName);
			for (QueueCustomerVo queueCustomerVo : list) {
				logger.info("QueueName: " + queueName + ", CustomerId: " + queueCustomerVo.getCustomer_Id());
			}
		}
	}

	@Test
	public void testDistributerGetQueueCustomerGroups() throws Exception {
		long distributerId = 4;
		Map<String, Map<Long, List<QueueCustomerVo>>> map = remotingInvoke.distributerGetQueueCustomerGroups(distributerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			Map<Long, List<QueueCustomerVo>> map1 = map.get(queueName);
			Set<Long> set1 = map1.keySet();
			for (Long l : set1) {
				List<QueueCustomerVo> list = map1.get(l);
				for (QueueCustomerVo queueCustomerVo : list) {
					logger.info("QueueName: " + queueName + ", CustomerNum: " + l + ", CustomerId: " + queueCustomerVo.getCustomer_Id());
				}
			}
		}
	}
	
	@Test
	public void testSaveProducerBeat() throws Exception {
		remotingInvoke.saveProducerBeat(1L);
	}
	
	@Test
	public void testSaveCustomerBeat() throws Exception {
		remotingInvoke.saveCustomerBeat(1L);
	}
}
