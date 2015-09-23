package org.fl.noodlenotify.console.remoting.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.service.DistributerService;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.service.ProducerService;
import org.fl.noodlenotify.console.service.QueueCustomerGroupService;
import org.fl.noodlenotify.console.service.QueueCustomerService;
import org.fl.noodlenotify.console.service.QueueDistributerService;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.service.QueueMsgBodyCacheService;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.service.QueueTraceStorageService;
import org.fl.noodlenotify.console.service.TraceStorageService;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodlenotify.console.vo.TraceStorageVo;

@Component
public class ConsoleRemotingInvokeImpl implements ConsoleRemotingInvoke {

	@Autowired
	private ProducerService producerService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ExchangerService exchangerService;

	@Autowired
	private DistributerService distributerService;

	@Autowired
	private MsgStorageService msgStorageService;

	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	private MsgQueueCacheService msgQueueCacheService;

	@Autowired
	private QueueCustomerService queueCustomerService;

	@Autowired
	private QueueExchangerService queueExchangerService;

	@Autowired
	private QueueDistributerService queueDistributerService;

	@Autowired
	private QueueMsgStorageService queueMsgStorageService;

	@Autowired
	private QueueMsgBodyCacheService queueMsgBodyCacheService;

	@Autowired
	private QueueMsgQueueCacheService queueMsgQueueCacheService;

	@Autowired
	private QueueCustomerGroupService queueCustomerGroupService;

	@Autowired
	private TraceStorageService traceStorageService;

	@Autowired
	private QueueTraceStorageService queueTraceStorageService;

	@Override
	public long producerRegister(String ip, int checkPort, String checkUrl, String checkType, String name) throws Exception {
		return producerService.saveRegister(ip, checkPort, checkUrl, checkType, name);
	}

	@Override
	public void producerCancel(long producerId) throws Exception {
		producerService.saveCancelRegister(producerId);
	}

	@Override
	public long exchangerRegister(String ip, int port, String url, String type, int checkPort, String name) throws Exception {
		return exchangerService.saveRegister(ip, port, url, type, checkPort, name);
	}

	@Override
	public void exchangerCancel(long exchangerId) throws Exception {
		exchangerService.saveCancelRegister(exchangerId);
	}

	@Override
	public long distributerRegister(String ip, int checkPort, String name) throws Exception {
		return distributerService.saveRegister(ip, checkPort, name);
	}
	
	@Override
	public void distributerCancel(long distributerId) throws Exception {
		distributerService.saveCancelRegister(distributerId);
	}

	@Override
	public long customerRegister(String ip, int port, String url, String type, int checkPort, String checkUrl, String checkType, String name, String customerGroupName, List<String> queueNameList) throws Exception {
		return customerService.saveRegister(ip, port, url, type, checkPort, checkUrl, checkType, name, customerGroupName, queueNameList);
	}

	@Override
	public void customerCancel(long customerId) throws Exception {
		customerService.saveCancelRegister(customerId);
	}

	@Override
	public Map<String, List<QueueExchangerVo>> producerGetExchangers(long producerId) throws Exception {
		return queueExchangerService.getQueueExchangersForProducer(producerId);
	}

	@Override
	public List<QueueExchangerVo> exchangerGetQueues(long exchangerId) throws Exception {
		return queueExchangerService.getQueueExchangersByExchangerId(exchangerId);
	}

	@Override
	public Map<String, List<QueueMsgStorageVo>> exchangerGetMsgStorages(long exchangerId) throws Exception {
		return queueMsgStorageService.getQueueMsgStorageByExchangerId(exchangerId);
	}

	@Override
	public Map<String, List<QueueMsgBodyCacheVo>> exchangerGetMsgBodyCaches(long exchangerId) throws Exception {
		return queueMsgBodyCacheService.getQueueMsgBodyCacheByExchangerId(exchangerId);
	}

	@Override
	public Map<String, Long> exchangerGetQueueCustomerGroupNum(long exchangerId) throws Exception {
		return queueCustomerGroupService.getQueueCustomerGroupNumByExchangerId(exchangerId);
	}

	@Override
	public List<QueueDistributerVo> distributerGetQueues(long distributerId) throws Exception {
		return queueDistributerService.getQueueDistributers(distributerId);
	}

	@Override
	public Map<String, List<QueueMsgStorageVo>> distributerGetMsgStorages(long distributerId) throws Exception {
		return queueMsgStorageService.getQueueMsgStorageByDistributerId(distributerId);
	}

	@Override
	public Map<String, List<QueueMsgBodyCacheVo>> distributerGetMsgBodyCaches(long distributerId) throws Exception {
		return queueMsgBodyCacheService.getQueueMsgBodyCacheByDistributerId(distributerId);
	}

	@Override
	public Map<String, List<QueueMsgQueueCacheVo>> distributerGetMsgQueueCaches(long distributerId) throws Exception {
		return queueMsgQueueCacheService.getQueueMsgQueueCacheByDistributerId(distributerId);
	}

	@Override
	public Map<String, List<QueueCustomerVo>> distributerGetQueueCustomers(long distributerId) throws Exception {
		return queueCustomerService.getQueueCustomerByDistributerId(distributerId);
	}

	@Override
	public Map<String, Map<Long, List<QueueCustomerVo>>> distributerGetQueueCustomerGroups(long distributerId) throws Exception {
		return queueCustomerGroupService.getQueueCustomerGroupByDistributerId(distributerId);
	}

	@Override
	public List<ProducerVo> queryCheckProducers() throws Exception {
		return producerService.queryCheckProducerListWithCache();
	}

	@Override
	public List<CustomerVo> queryCheckCustomers() throws Exception {
		return customerService.queryCheckCustomerListWithCache();
	}

	@Override
	public List<ExchangerVo> queryCheckExchangers() throws Exception {
		return exchangerService.queryCheckExchangerListWithCache();
	}

	@Override
	public List<DistributerVo> queryCheckDistributers() throws Exception {
		return distributerService.queryCheckDistributeListWithCache();
	}

	@Override
	public List<MsgStorageVo> queryCheckMsgStorages() throws Exception {
		return msgStorageService.queryCheckMsgStorageListWithCache();
	}

	@Override
	public List<MsgBodyCacheVo> queryCheckMsgBodyCaches() throws Exception {
		return msgBodyCacheService.queryCheckMsgBodyCacheListWithCache();
	}

	@Override
	public List<MsgQueueCacheVo> queryCheckMsgQueueCaches() throws Exception {
		return msgQueueCacheService.queryCheckMsgQueueCacheListWithCache();
	}

	@Override
	public List<TraceStorageVo> queryCheckTracestorages() throws Exception {
		return traceStorageService.queryCheckTracestorageListWithCache();
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> producterGetTraceStorages(long producerId) throws Exception {
		return queueTraceStorageService.getQueueTraceStorageByProducerId(producerId);
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> exchangerGetTraceStorages(long exchangerId) throws Exception {
		return queueTraceStorageService.getQueueTraceStorageByExchangerId(exchangerId);
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> distributerGetTraceStorages(long distributerId) throws Exception {
		return queueTraceStorageService.getQueueTraceStorageByDistributerId(distributerId);
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> customerGetTraceStorages(long customerId) throws Exception {
		return queueTraceStorageService.getQueueTraceStorageByCustomerId(customerId);
	}

}
