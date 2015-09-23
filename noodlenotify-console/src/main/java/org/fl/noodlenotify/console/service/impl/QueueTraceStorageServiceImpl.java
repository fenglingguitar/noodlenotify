package org.fl.noodlenotify.console.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueTraceStorageDao;
import org.fl.noodlenotify.console.service.QueueTraceStorageService;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueTraceStorageService")
public class QueueTraceStorageServiceImpl implements QueueTraceStorageService {

	@Autowired
	private QueueDao queueDao;

	@Autowired
	private ProducerDao producerDao;

	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueTraceStorageDao queueTraceStorageDao;

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private QueueCustomerDao queueCustomerDao;

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStoragePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		return queueTraceStorageDao.queryQueueTraceStoragePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageIncludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		return queueTraceStorageDao.queryQueueTraceStorageIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageExcludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		return queueTraceStorageDao.queryQueueTraceStorageExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueTraceStorageVo> queryQueueTraceStorageList(QueueTraceStorageVo vo) throws Exception {
		return queueTraceStorageDao.queryQueueTraceStorageList(vo);
	}

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueByTraceStorage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		return queueTraceStorageDao.queryQueueByTraceStorage(vo, page, rows);
	}

	@Override
	public void insertQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageDao.insertQueueTraceStorage(vo);
	}

	@Override
	public void insertsQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageDao.insertsQueueTraceStorage(vos);
	}

	@Override
	public void updateQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageDao.updateQueueTraceStorage(vo);
	}

	@Override
	public void updatesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageDao.updatesQueueTraceStorage(vos);
	}

	@Override
	public void deleteQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageDao.deleteQueueTraceStorage(vo);
	}

	@Override
	public void deletesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageDao.deletesQueueTraceStorage(vos);
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByProducerId(long producerId) throws Exception {
		if (!producerDao.ifProducerValid(producerId)) {
			return new HashMap<String, List<QueueTraceStorageVo>>();
		}
		Map<String, List<QueueTraceStorageVo>> result = new HashMap<String, List<QueueTraceStorageVo>>();

		QueueVo queueVo = new QueueVo();
		queueVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueVo> queues = queueDao.queryQueueList(queueVo);
		if (queues == null) {
			return result;
		}
		QueueTraceStorageVo vo = new QueueTraceStorageVo();
		vo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		vo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		for (QueueVo qv : queues) {
			String queueNm = qv.getQueue_Nm();
			vo.setQueue_Nm(queueNm);
			List<QueueTraceStorageVo> traceStorages = queueTraceStorageDao.queryTraceStoragesByQueue(vo);
			result.put(queueNm, traceStorages);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByExchangerId(long exchangerId) throws Exception {
		if (!exchangerdao.ifExchangerValid(exchangerId)) {
			return new HashMap<String, List<QueueTraceStorageVo>>();
		}

		Map<String, List<QueueTraceStorageVo>> result = new HashMap<String, List<QueueTraceStorageVo>>();
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queueExchangerList = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		if (queueExchangerList == null) {
			return result;
		}
		QueueTraceStorageVo queueTraceStorageVo = new QueueTraceStorageVo();
		queueTraceStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueTraceStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueExchangerVo qe : queueExchangerList) {
			String queueNm = qe.getQueue_Nm();
			queueTraceStorageVo.setQueue_Nm(queueNm);
			List<QueueTraceStorageVo> msgStorages = queueTraceStorageDao.queryTraceStoragesByQueue(queueTraceStorageVo);
			result.put(queueNm, msgStorages);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByDistributerId(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new HashMap<String, List<QueueTraceStorageVo>>();
		}
		Map<String, List<QueueTraceStorageVo>> result = new HashMap<String, List<QueueTraceStorageVo>>();
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queues = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		if (queues == null) {
			return result;
		}

		QueueTraceStorageVo queueTraceStorageVo = new QueueTraceStorageVo();
		queueTraceStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueTraceStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueDistributerVo qd : queues) {
			String queueNm = qd.getQueue_Nm();
			queueTraceStorageVo.setQueue_Nm(queueNm);
			List<QueueTraceStorageVo> msgStorages = queueTraceStorageDao.queryTraceStoragesByQueue(queueTraceStorageVo);
			result.put(queueNm, msgStorages);
		}
		return result;
	}

	@Override
	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByCustomerId(long customerId) throws Exception {
		if (!customerDao.ifCustomerValid(customerId)) {
			return new HashMap<String, List<QueueTraceStorageVo>>();
		}
		Map<String, List<QueueTraceStorageVo>> result = new HashMap<String, List<QueueTraceStorageVo>>();

		QueueCustomerVo queueCustomerVo = new QueueCustomerVo();
		queueCustomerVo.setCustomer_Id(customerId);
		queueCustomerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueCustomerVo> queues = queueCustomerDao.queryQueuesByCustomer(queueCustomerVo);
		if (queues == null) {
			return result;
		}

		QueueTraceStorageVo queueTraceStorageVo = new QueueTraceStorageVo();
		queueTraceStorageVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueTraceStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		for (QueueCustomerVo qc : queues) {
			String queueNm = qc.getQueue_Nm();
			queueTraceStorageVo.setQueue_Nm(queueNm);
			List<QueueTraceStorageVo> msgStorages = queueTraceStorageDao.queryTraceStoragesByQueue(queueTraceStorageVo);
			result.put(queueNm, msgStorages);
		}
		return result;
	}
}
