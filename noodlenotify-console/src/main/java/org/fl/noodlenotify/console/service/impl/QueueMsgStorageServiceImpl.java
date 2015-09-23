package org.fl.noodlenotify.console.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueMsgStorageService")
public class QueueMsgStorageServiceImpl implements QueueMsgStorageService {
	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueMsgStorageDao queueMsgStorageDao;

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStoragePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStoragePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageIncludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStorageIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageExcludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStorageExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueMsgStorageVo> queryQueueMsgStorageList(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStorageList(vo);
	}

	@Override
	public List<QueueMsgStorageVo> queryMsgStoragesByQueueExclude(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryMsgStoragesByQueueExclude(vo);
	}

	@Override
	public List<QueueMsgStorageVo> queryQueueByMsgstorageList(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryQueueByMsgstorageList(vo);
	}
	
	@Override
	public List<QueueMsgStorageVo> queryQueueByMsgstorageListTree(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryQueueByMsgstorageListTree(vo);
	}
	
	@Override
	public PageVo<QueueMsgStorageVo> queryQueueByMsgStorage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		return queueMsgStorageDao.queryQueueByMsgstorage(vo, page, rows);
	}

	@Override
	public void insertQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageDao.insertQueueMsgStorage(vo);
	}

	@Override
	public void insertsQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageDao.insertsQueueMsgStorage(vos);
	}

	@Override
	public void updateQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageDao.updateQueueMsgStorage(vo);
	}
	
	@Override
	public void updateQueueMsgStorageSimple(QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageDao.updateQueueMsgStorage(vo);
	}

	@Override
	public void updatesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageDao.updatesQueueMsgStorage(vos);
	}

	@Override
	public void deleteQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageDao.deleteQueueMsgStorage(vo);
	}

	@Override
	public void deletesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageDao.deletesQueueMsgStorage(vos);
	}

	@Override
	public Map<String, List<QueueMsgStorageVo>> getQueueMsgStorageByExchangerId(long exchangerId) throws Exception {
		if (!exchangerdao.ifExchangerValid(exchangerId)) {
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
	public Map<String, List<QueueMsgStorageVo>> getQueueMsgStorageByDistributerId(long distributerId) throws Exception {
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
}
