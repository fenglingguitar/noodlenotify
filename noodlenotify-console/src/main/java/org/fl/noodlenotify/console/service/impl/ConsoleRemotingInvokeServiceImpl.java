package org.fl.noodlenotify.console.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.MsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.MsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.MsgStorageDao;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.domain.CustomerMd;
import org.fl.noodlenotify.console.domain.DistributerMd;
import org.fl.noodlenotify.console.domain.ExchangerMd;
import org.fl.noodlenotify.console.domain.ProducerMd;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("consoleRemotingInvokeService")
public class ConsoleRemotingInvokeServiceImpl implements ConsoleRemotingInvoke {

	@Autowired
	private ProducerDao producerDao;
	
	@Autowired
	private ExchangerDao exchangerDao;

	@Autowired
	private DistributerDao distributerDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Autowired
	private QueueCustomerDao queueCustomerDao;
	
	@Autowired
	private QueueDao queueDao;
	
	@Autowired
	private QueueExchangerDao queueExchangerDao;
	
	@Autowired
	private QueueMsgStorageDao queueMsgStorageDao;
	
	@Autowired
	private QueueMsgBodyCacheDao queueMsgBodyCacheDao;
	
	@Autowired
	private QueueCustomerGroupDao queueCustomerGroupDao;
	
	@Autowired
	private QueueDistributerDao queueDistributerDao;
	
	@Autowired
	private QueueMsgQueueCacheDao queueMsgQueueCacheDao;
	
	@Autowired
	private MsgStorageDao msgStorageDao;
	
	@Autowired
	private MsgBodyCacheDao msgBodyCacheDao;
	
	@Autowired
	private MsgQueueCacheDao msgQueueCacheDao;
	
	@Override
	public long saveProducerRegister(String ip, int checkPort, String checkUrl, String checkType, String name) throws Exception {
		
		ProducerVo producerVo = new ProducerVo();
		producerVo.setIp(ip);
		producerVo.setCheck_Port(checkPort);
		List<ProducerVo> producerList = producerDao.queryProducerList(producerVo);
		if (producerList == null || producerList.size() == 0) {
			producerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			producerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			producerVo = producerList.get(0);
		}
		producerVo.setName(name);
		producerVo.setCheck_Url(checkUrl);
		producerVo.setCheck_Type(checkType);
		ProducerMd producerMd = producerDao.insertOrUpdate(producerVo);

		return producerMd.getProducer_Id();
	}

	@Override
	public void saveProducerCancel(long producerId) throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setProducer_Id(producerId);
		producerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		producerDao.updateProducerSystemStatus(producerVo);
	}

	@Override
	public long saveExchangerRegister(String ip, int port, String url, String type, int checkPort, String name) throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setIp(ip);
		exchangerVo.setCheck_Port(checkPort);
		List<ExchangerVo> exchangerList = exchangerDao.queryExchangerList(exchangerVo);
		if (exchangerList == null || exchangerList.size() == 0) {
			exchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			exchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			exchangerVo = exchangerList.get(0);
		}
		exchangerVo.setPort(port);
		exchangerVo.setUrl(url);
		exchangerVo.setType(type);
		exchangerVo.setName(name);
		ExchangerMd exchangerMd = exchangerDao.insertOrUpdate(exchangerVo);
		
		return exchangerMd.getExchanger_Id();
	}

	@Override
	public void saveExchangerCancel(long exchangerId) throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setExchanger_Id(exchangerId);
		exchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		exchangerDao.updateExchangerSystemStatus(exchangerVo);
	}

	@Override
	public long saveDistributerRegister(String ip, int checkPort, String name) throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setIp(ip);
		distributerVo.setCheck_Port(checkPort);
		List<DistributerVo> distributerList = distributerDao.queryDistributerList(distributerVo);
		if (distributerList == null || distributerList.size() == 0) {
			distributerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			distributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			distributerVo = distributerList.get(0);
		}
		distributerVo.setName(name);
		DistributerMd distributerMd = distributerDao.insertOrUpdate(distributerVo);

		return distributerMd.getDistributer_Id();
	}
	
	@Override
	public void saveDistributerCancel(long distributerId) throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setDistributer_Id(distributerId);
		distributerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		distributerDao.updateDistributerSystemStatus(distributerVo);
	}

	@Override
	public long saveCustomerRegister(String ip, int port, String url, String type, int checkPort, String checkUrl, String checkType, String name, String customerGroupName, List<String> queueNameList) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setIp(ip);
		customerVo.setCheck_Port(checkPort);
		List<CustomerVo> exchangerList = customerDao.queryCustomerList(customerVo);
		if (exchangerList == null || exchangerList.size() == 0) {
			customerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			customerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			customerVo = exchangerList.get(0);
		}
		customerVo.setName(name);
		customerVo.setPort(port);
		customerVo.setUrl(url);
		customerVo.setType(type);
		customerVo.setCheck_Url(checkUrl);
		customerVo.setCheck_Type(checkType);
		customerVo.setCustomerGroup_Nm(customerGroupName);
		CustomerMd customerMd = customerDao.insertOrUpdate(customerVo);
		QueueCustomerVo queueCustomerDeleteVo = new QueueCustomerVo();
		queueCustomerDeleteVo.setCustomer_Id(customerMd.getCustomer_Id());
		queueCustomerDao.deleteQueueCustomerByCustomerId(queueCustomerDeleteVo);

		for (String queueName : queueNameList) {
			QueueCustomerVo queueCustomerInsertVo = new QueueCustomerVo();
			queueCustomerInsertVo.setQueue_Nm(queueName);
			queueCustomerInsertVo.setCustomer_Id(customerMd.getCustomer_Id());
			queueCustomerDao.insertQueueCustomer(queueCustomerInsertVo);
		}
		return customerMd.getCustomer_Id();
	}

	@Override
	public void saveCustomerCancel(long customerId) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setCustomer_Id(customerId);
		customerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		customerDao.updateCustomerSystemStatus(customerVo);
	}

	@Override
	public Map<String, List<QueueExchangerVo>> producerGetExchangers(long producerId) throws Exception {
		if (producerId > 0 && !producerDao.ifProducerValid(producerId)) {
			return new HashMap<String, List<QueueExchangerVo>>();
		}
		Map<String, List<QueueExchangerVo>> result = new HashMap<String, List<QueueExchangerVo>>();
		QueueVo queueVo = new QueueVo();
		queueVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueVo> queues = queueDao.queryQueueList(queueVo);
		if (queues == null) {
			return result;
		}
		QueueExchangerVo vo = new QueueExchangerVo();
		vo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		vo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		for (QueueVo qv : queues) {
			String queueNm = qv.getQueue_Nm();
			vo.setQueue_Nm(queueNm);
			List<QueueExchangerVo> exchangers = queueExchangerDao.queryExchangersByQueue(vo);
			result.put(queueNm, exchangers);
		}
		return result;
	}

	@Override
	public List<QueueExchangerVo> exchangerGetQueues(long exchangerId) throws Exception {
		if (!exchangerDao.ifExchangerValid(exchangerId)) {
			return new ArrayList<QueueExchangerVo>();
		}
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queues = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		return queues;
	}

	@Override
	public Map<String, List<QueueMsgStorageVo>> exchangerGetMsgStorages(long exchangerId) throws Exception {
		if (!exchangerDao.ifExchangerValid(exchangerId)) {
			return new HashMap<String, List<QueueMsgStorageVo>>();
		}

		Map<String, List<QueueMsgStorageVo>> result = new HashMap<String, List<QueueMsgStorageVo>>();
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queueExchangerList = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		if (queueExchangerList == null) {
			return result;
		}
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueExchangerVo qe : queueExchangerList) {
			String queueNm = qe.getQueue_Nm();
			queueMsgStorageVo.setQueue_Nm(queueNm);
			List<QueueMsgStorageVo> msgStorages = queueMsgStorageDao.queryMsgStoragesByQueue(queueMsgStorageVo);
			result.put(queueNm, msgStorages);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueMsgBodyCacheVo>> exchangerGetMsgBodyCaches(long exchangerId) throws Exception {
		if (!exchangerDao.ifExchangerValid(exchangerId)) {
			return new HashMap<String, List<QueueMsgBodyCacheVo>>();
		}
		Map<String, List<QueueMsgBodyCacheVo>> result = new HashMap<String, List<QueueMsgBodyCacheVo>>();
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queues = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		if (queues == null) {
			return result;
		}
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgBodyCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueExchangerVo qe : queues) {
			String queueNm = qe.getQueue_Nm();
			queueMsgBodyCacheVo.setQueue_Nm(queueNm);
			List<QueueMsgBodyCacheVo> queueMsgBodyCacheList = queueMsgBodyCacheDao.queryMsgBodyCacheByQueue(queueMsgBodyCacheVo);
			result.put(queueNm, queueMsgBodyCacheList);
		}
		return result;
	}

	@Override
	public Map<String, Long> exchangerGetQueueCustomerGroupNum(long exchangerId) throws Exception {
		if (!exchangerDao.ifExchangerValid(exchangerId)) {
			return new HashMap<String, Long>();
		}

		Map<String, Long> result = new HashMap<String, Long>();
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queues = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		if (queues == null) {
			return result;
		}
		QueueCustomerGroupVo queueCustomerGroupVo = new QueueCustomerGroupVo();
		queueCustomerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueExchangerVo qe : queues) {
			String queueNm = qe.getQueue_Nm();
			queueCustomerGroupVo.setQueue_Nm(queueNm);
			List<QueueCustomerGroupVo> customerGroups = queueCustomerGroupDao.queryCustomerGroupList(queueCustomerGroupVo);
			long customerNumResult = 0;
			for (QueueCustomerGroupVo queueCustomerGroup : customerGroups) {
				customerNumResult |= queueCustomerGroup.getCustomer_Num();
			}
			result.put(queueNm, customerNumResult);
		}
		return result;
	}

	@Override
	public List<QueueDistributerVo> distributerGetQueues(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new ArrayList<QueueDistributerVo>();
		}
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queueDistributers = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		return queueDistributers;
	}

	@Override
	public Map<String, List<QueueMsgStorageVo>> distributerGetMsgStorages(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueMsgStorageVo>>();
		}
		Map<String, List<QueueMsgStorageVo>> result = new HashMap<String, List<QueueMsgStorageVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}

		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueMsgStorageVo.setQueue_Nm(queueNm);
			List<QueueMsgStorageVo> msgStorages = queueMsgStorageDao.queryMsgStoragesByQueueExclude(queueMsgStorageVo);
			result.put(queueNm, msgStorages);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueMsgBodyCacheVo>> distributerGetMsgBodyCaches(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueMsgBodyCacheVo>>();
		}
		Map<String, List<QueueMsgBodyCacheVo>> result = new HashMap<String, List<QueueMsgBodyCacheVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgBodyCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueMsgBodyCacheVo.setQueue_Nm(queueNm);
			List<QueueMsgBodyCacheVo> msgBodyCaches = queueMsgBodyCacheDao.queryMsgBodyCacheByQueue(queueMsgBodyCacheVo);
			result.put(queueNm, msgBodyCaches);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueMsgQueueCacheVo>> distributerGetMsgQueueCaches(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueMsgQueueCacheVo>>();
		}

		Map<String, List<QueueMsgQueueCacheVo>> result = new HashMap<String, List<QueueMsgQueueCacheVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}
		QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueMsgQueueCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueMsgQueueCacheVo.setQueue_Nm(queueNm);
			List<QueueMsgQueueCacheVo> msgQueueCaches = queueMsgQueueCacheDao.queryQueueMsgQueueCacheByQueue(queueMsgQueueCacheVo);
			result.put(queueNm, msgQueueCaches);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueCustomerVo>> distributerGetQueueCustomers(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueCustomerVo>>();
		}

		Map<String, List<QueueCustomerVo>> result = new HashMap<String, List<QueueCustomerVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}
		QueueCustomerVo queueCustomerVo = new QueueCustomerVo();
		queueCustomerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueCustomerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueCustomerVo.setQueue_Nm(queueNm);
			List<QueueCustomerVo> customers = queueCustomerDao.queryCustomersByQueue(queueCustomerVo);
			result.put(queueNm, customers);
		}
		return result;
	}

	@Override
	public Map<String, Map<Long, List<QueueCustomerVo>>> distributerGetQueueCustomerGroups(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, Map<Long, List<QueueCustomerVo>>>();
		}

		Map<String, Map<Long, List<QueueCustomerVo>>> result = new HashMap<String, Map<Long, List<QueueCustomerVo>>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}

		QueueCustomerGroupVo queueCustomerGroupVo = new QueueCustomerGroupVo();
		queueCustomerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			Map<Long, List<QueueCustomerVo>> customerMap = new HashMap<Long, List<QueueCustomerVo>>();
			String queueNm = qd.getQueue_Nm();
			queueCustomerGroupVo.setQueue_Nm(queueNm);
			List<QueueCustomerGroupVo> customerGroups = queueCustomerGroupDao.queryCustomerGroupList(queueCustomerGroupVo);
			if (customerGroups == null) {
				continue;
			}
			QueueCustomerVo queueCustomerVo = new QueueCustomerVo();
			queueCustomerVo.setQueue_Nm(queueNm);
			queueCustomerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
			queueCustomerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
			for (QueueCustomerGroupVo vo : customerGroups) {
				queueCustomerVo.setCustomerGroup_Nm(vo.getCustomerGroup_Nm());
				List<QueueCustomerVo> customers = queueCustomerDao.queryCustomersByQueue(queueCustomerVo);
				if (customers == null) {
					customers = new ArrayList<QueueCustomerVo>();
				}
				for (QueueCustomerVo qcVo : customers) {
					qcVo.setDph_Timeout(qd.getDph_Timeout());
				}
				customerMap.put(vo.getCustomer_Num(), customers);
			}
			result.put(queueNm, customerMap);
		}
		return result;
	}

	@Override
	public void saveProducerBeat(Long producerId) throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setProducer_Id(producerId);
		producerVo.setBeat_Time(new Date());
		producerDao.updateProducer(producerVo);
	}

	@Override
	public void saveCustomerBeat(Long customerId) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setCustomer_Id(customerId);
		customerVo.setBeat_Time(new Date());
		customerDao.updateCustomer(customerVo);
	}
}
