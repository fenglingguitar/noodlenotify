package org.fl.noodlenotify.console.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.service.QueueCustomerService;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueCustomerService")
public class QueueCustomerServiceImpl implements QueueCustomerService {

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueCustomerDao queueCustomerDao;

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerPage(QueueCustomerVo vo, int page, int rows) throws Exception {
		return queueCustomerDao.queryQueueCustomerPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerIncludePage(QueueCustomerVo vo, int page, int rows) throws Exception {
		return queueCustomerDao.queryQueueCustomerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerExcludePage(QueueCustomerVo vo, int page, int rows) throws Exception {
		return queueCustomerDao.queryQueueCustomerExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueCustomerVo> queryQueueCustomerList(QueueCustomerVo vo) throws Exception {
		return queueCustomerDao.queryQueueCustomerList(vo);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueuePageByCustomer(QueueCustomerVo vo, int page, int rows) throws Exception {
		return queueCustomerDao.queryQueuePageByCustomer(vo, page, rows);
	}

	@Override
	public void insertQueueCustomer(QueueCustomerVo vo) throws Exception {
		queueCustomerDao.insertQueueCustomer(vo);
	}

	@Override
	public void insertsQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		queueCustomerDao.insertsQueueCustomer(vos);
	}

	@Override
	public void updateQueueCustomer(QueueCustomerVo vo) throws Exception {
		queueCustomerDao.updateQueueCustomer(vo);
	}

	@Override
	public void updatesQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		queueCustomerDao.updatesQueueCustomer(vos);
	}

	@Override
	public void deleteQueueCustomer(QueueCustomerVo vo) throws Exception {
		queueCustomerDao.deleteQueueCustomer(vo);
	}

	@Override
	public void deletesQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		queueCustomerDao.deletesQueueCustomer(vos);
	}

	@Override
	public Map<String, List<QueueCustomerVo>> getQueueCustomerByDistributerId(long distributerId) throws Exception {
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
}
