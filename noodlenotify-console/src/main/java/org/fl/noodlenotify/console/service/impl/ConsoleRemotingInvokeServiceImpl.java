package org.fl.noodlenotify.console.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ConsumerDao;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.MsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.MsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.MsgStorageDao;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.domain.ConsumerMd;
import org.fl.noodlenotify.console.domain.DistributerMd;
import org.fl.noodlenotify.console.domain.ExchangerMd;
import org.fl.noodlenotify.console.domain.ProducerMd;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
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
	private ConsumerDao consumerDao;
	
	@Autowired
	private QueueConsumerDao queueConsumerDao;
	
	@Autowired
	private QueueDao queueDao;
	
	@Autowired
	private QueueExchangerDao queueExchangerDao;
	
	@Autowired
	private QueueMsgStorageDao queueMsgStorageDao;
	
	@Autowired
	private QueueMsgBodyCacheDao queueMsgBodyCacheDao;
	
	@Autowired
	private QueueConsumerGroupDao queueConsumerGroupDao;
	
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
	public long saveConsumerRegister(String ip, int port, String url, String type, int checkPort, String checkUrl, String checkType, String name, String consumerGroupName, List<String> queueNameList) throws Exception {
		ConsumerVo consumerVo = new ConsumerVo();
		consumerVo.setIp(ip);
		consumerVo.setCheck_Port(checkPort);
		List<ConsumerVo> exchangerList = consumerDao.queryConsumerList(consumerVo);
		if (exchangerList == null || exchangerList.size() == 0) {
			consumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			consumerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			consumerVo = exchangerList.get(0);
		}
		consumerVo.setName(name);
		consumerVo.setPort(port);
		consumerVo.setUrl(url);
		consumerVo.setType(type);
		consumerVo.setCheck_Url(checkUrl);
		consumerVo.setCheck_Type(checkType);
		consumerVo.setConsumerGroup_Nm(consumerGroupName);
		ConsumerMd consumerMd = consumerDao.insertOrUpdate(consumerVo);
		QueueConsumerVo queueConsumerDeleteVo = new QueueConsumerVo();
		queueConsumerDeleteVo.setConsumer_Id(consumerMd.getConsumer_Id());
		queueConsumerDao.deleteQueueConsumerByConsumerId(queueConsumerDeleteVo);

		for (String queueName : queueNameList) {
			QueueConsumerVo queueConsumerInsertVo = new QueueConsumerVo();
			queueConsumerInsertVo.setQueue_Nm(queueName);
			queueConsumerInsertVo.setConsumer_Id(consumerMd.getConsumer_Id());
			queueConsumerDao.insertQueueConsumer(queueConsumerInsertVo);
		}
		return consumerMd.getConsumer_Id();
	}

	@Override
	public void saveConsumerCancel(long consumerId) throws Exception {
		ConsumerVo consumerVo = new ConsumerVo();
		consumerVo.setConsumer_Id(consumerId);
		consumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		consumerDao.updateConsumerSystemStatus(consumerVo);
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
	public Map<String, Long> exchangerGetQueueConsumerGroupNum(long exchangerId) throws Exception {
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
		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueExchangerVo qe : queues) {
			String queueNm = qe.getQueue_Nm();
			queueConsumerGroupVo.setQueue_Nm(queueNm);
			List<QueueConsumerGroupVo> consumerGroups = queueConsumerGroupDao.queryConsumerGroupList(queueConsumerGroupVo);
			long consumerNumResult = 0;
			for (QueueConsumerGroupVo queueConsumerGroup : consumerGroups) {
				consumerNumResult |= queueConsumerGroup.getConsumer_Num();
			}
			result.put(queueNm, consumerNumResult);
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
	public Map<String, List<QueueConsumerVo>> distributerGetQueueConsumers(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueConsumerVo>>();
		}

		Map<String, List<QueueConsumerVo>> result = new HashMap<String, List<QueueConsumerVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}
		QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
		queueConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueConsumerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueConsumerVo.setQueue_Nm(queueNm);
			List<QueueConsumerVo> consumers = queueConsumerDao.queryConsumersByQueue(queueConsumerVo);
			result.put(queueNm, consumers);
		}
		return result;
	}

	@Override
	public Map<String, Map<Long, List<QueueConsumerVo>>> distributerGetQueueConsumerGroups(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, Map<Long, List<QueueConsumerVo>>>();
		}

		Map<String, Map<Long, List<QueueConsumerVo>>> result = new HashMap<String, Map<Long, List<QueueConsumerVo>>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}

		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			Map<Long, List<QueueConsumerVo>> consumerMap = new HashMap<Long, List<QueueConsumerVo>>();
			String queueNm = qd.getQueue_Nm();
			queueConsumerGroupVo.setQueue_Nm(queueNm);
			List<QueueConsumerGroupVo> consumerGroups = queueConsumerGroupDao.queryConsumerGroupList(queueConsumerGroupVo);
			if (consumerGroups == null) {
				continue;
			}
			QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
			queueConsumerVo.setQueue_Nm(queueNm);
			queueConsumerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
			queueConsumerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
			for (QueueConsumerGroupVo vo : consumerGroups) {
				queueConsumerVo.setConsumerGroup_Nm(vo.getConsumerGroup_Nm());
				List<QueueConsumerVo> consumers = queueConsumerDao.queryConsumersByQueue(queueConsumerVo);
				if (consumers == null) {
					consumers = new ArrayList<QueueConsumerVo>();
				}
				for (QueueConsumerVo qcVo : consumers) {
					qcVo.setDph_Timeout(qd.getDph_Timeout());
				}
				consumerMap.put(vo.getConsumer_Num(), consumers);
			}
			result.put(queueNm, consumerMap);
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
	public void saveConsumerBeat(Long consumerId) throws Exception {
		ConsumerVo consumerVo = new ConsumerVo();
		consumerVo.setConsumer_Id(consumerId);
		consumerVo.setBeat_Time(new Date());
		consumerDao.updateConsumer(consumerVo);
	}
}
