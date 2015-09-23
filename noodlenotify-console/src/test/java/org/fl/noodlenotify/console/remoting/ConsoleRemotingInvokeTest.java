package org.fl.noodlenotify.console.remoting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;

@ContextConfiguration(locations = {
		"classpath:org/fl/noodlenotify/console/bean/noodlenotify-console-bean.xml" 
})
public class ConsoleRemotingInvokeTest extends AbstractJUnit4SpringContextTests {

	private final static Logger logger = LoggerFactory.getLogger(ConsoleRemotingInvokeTest.class);

	@Autowired
	private ConsoleRemotingInvoke remotingInvoke;

	@Test
	public void testProducerRegist() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "生产者-注册-test";
		remotingInvoke.producerRegister(ip, checkPort, checkUrl, checkType, name);
	}

	@Test
	public void testProducerCancel() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String checkUrl = "test/test";
		String checkType = "NETTY";
		String name = "生产者-注册-test";
		remotingInvoke.producerCancel(remotingInvoke.producerRegister(ip, checkPort, checkUrl, checkType, name));
	}

	@Test
	public void testExchangerRegist() throws Exception {
		String ip = "127.0.0.1";
		int port = 123453;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String name = "交换器-注册-test";
		remotingInvoke.exchangerRegister(ip, port, url, type, checkPort, name);
	}

	@Test
	public void testExchangerCancel() throws Exception {
		String ip = "127.0.0.1";
		int port = 123453;
		String url = "test/test";
		String type = "NETTY";
		int checkPort = 123;
		String name = "交换器-注册-test";
		remotingInvoke.exchangerCancel(remotingInvoke.exchangerRegister(ip, port, url, type, checkPort, name));
	}

	@Test
	public void testDistributerRegist() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String name = "分配器-注册-test";
		remotingInvoke.distributerRegister(ip, checkPort, name);
	}

	@Test
	public void testDistributerCancel() throws Exception {
		String ip = "127.0.0.1";
		int checkPort = 123;
		String name = "分配器-注册-test";
		remotingInvoke.distributerCancel(remotingInvoke.distributerRegister(ip, checkPort, name));
	}

	@Test
	public void testCustomerRegist() throws Exception {
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
		remotingInvoke.customerRegister(ip, port, url, type, checkPort, checkUrl, checkType, name, customerGroupName, queueNameList);
	}

	@Test
	public void testCustomerCancel() throws Exception {
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
		remotingInvoke.customerCancel(remotingInvoke.customerRegister(ip, port, url, type, checkPort, checkUrl, checkType, name, customerGroupName, queueNameList));
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
	public void testQueryCheckProducers() throws Exception {
		remotingInvoke.queryCheckProducers();
	}

	@Test
	public void testQueryCheckExchangers() throws Exception {
		remotingInvoke.queryCheckExchangers();
	}

	@Test
	public void testQueryCheckDistributers() throws Exception {
		remotingInvoke.queryCheckDistributers();
	}

	@Test
	public void testQueryCheckMsgBodyCaches() throws Exception {
		remotingInvoke.queryCheckMsgBodyCaches();
	}

	@Test
	public void testQueryCheckMsgQueueCaches() throws Exception {
		remotingInvoke.queryCheckMsgQueueCaches();
	}

	@Test
	public void testQueryCheckMsgStorages() throws Exception {
		remotingInvoke.queryCheckMsgStorages();
	}

	@Test
	public void testQueryCheckCustomers() throws Exception {
		remotingInvoke.queryCheckCustomers();
	}

	@Test
	public void testQueryCheckTracestorages() throws Exception {
		remotingInvoke.queryCheckTracestorages();
	}

	@Test
	public void testProducterGetTraceStorages() throws Exception {
		long producterId = 1;
		remotingInvoke.producterGetTraceStorages(producterId);
	}

	@Test
	public void testExchangerGetTraceStorages() throws Exception {
		long exchangerId = 4;
		Map<String, List<QueueTraceStorageVo>> map = remotingInvoke.exchangerGetTraceStorages(exchangerId);
		Set<String> set = map.keySet();
		for (String queueName : set) {
			List<QueueTraceStorageVo> queueTraceStorageVoList = map.get(queueName);
			for (QueueTraceStorageVo queueTraceStorageVo : queueTraceStorageVoList) {
				logger.info("QueueName: " + queueName + ", CustomerId: " + queueTraceStorageVo.getTraceStorage_Id());
			}
		}
	}

	@Test
	public void testDistributerGetTraceStorages() throws Exception {
		long distributerId = 1;
		remotingInvoke.distributerGetTraceStorages(distributerId);
	}

	@Test
	public void testCustomerGetTraceStorages() throws Exception {
		long customerId = 1;
		remotingInvoke.customerGetTraceStorages(customerId);
	}
}
