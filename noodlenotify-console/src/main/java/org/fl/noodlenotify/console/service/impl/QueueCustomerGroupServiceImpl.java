package org.fl.noodlenotify.console.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.QueueCustomerGroupService;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueCustomerGroupService")
public class QueueCustomerGroupServiceImpl implements QueueCustomerGroupService {

	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueCustomerDao queueCustomerDao;

	@Autowired
	private QueueCustomerGroupDao queueCustomerGroupDao;

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupPage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		return queueCustomerGroupDao.queryQueueCustomerGroupPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupIncludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		return queueCustomerGroupDao.queryQueueCustomerGroupIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupExcludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		return queueCustomerGroupDao.queryQueueCustomerGroupExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueCustomerGroupVo> queryQueueCustomerGroupList(QueueCustomerGroupVo vo) throws Exception {
		return queueCustomerGroupDao.queryQueueCustomerGroupList(vo);
	}
	
	@Override
	public List<QueueCustomerGroupVo> queryQueueByCustomerGroupList(QueueCustomerGroupVo vo) throws Exception {
		return queueCustomerGroupDao.queryQueueByCustomerGroupList(vo);
	}

	@Override
	public List<QueueCustomerGroupVo> queryCustomerGroupsByQueue(QueueCustomerGroupVo vo) throws Exception {
		return queueCustomerGroupDao.queryCustomerGroupsByQueue(vo);
	}
	
	@Override
	public List<QueueCustomerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception {
		return queueCustomerGroupDao.queryUnuserGroupNumList(queueNm);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueuePageByCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		return queueCustomerGroupDao.queryQueuePageByCustomerGroup(vo, page, rows);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryCustomerPageByQueueCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		return queueCustomerGroupDao.queryCustomerPageByQueueCustomerGroup(vo, page, rows);
	}

	@Override
	public void insertQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupDao.insertQueueCustomerGroup(vo);
	}

	@Override
	public void insertsQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		queueCustomerGroupDao.insertsQueueCustomerGroup(vos);
	}

	@Override
	public void updateQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupDao.updateQueueCustomerGroup(vo);
	}

	@Override
	public void updatesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		queueCustomerGroupDao.updatesQueueCustomerGroup(vos);
	}

	@Override
	public void deleteQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupDao.deleteQueueCustomerGroup(vo);
	}

	@Override
	public void deletesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		queueCustomerGroupDao.deletesQueueCustomerGroup(vos);
	}

	@Override
	public Map<String, Long> getQueueCustomerGroupNumByExchangerId(long exchangerId) throws Exception {
		if (!exchangerdao.ifExchangerValid(exchangerId)) {
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
	public Map<String, Map<Long, List<QueueCustomerVo>>> getQueueCustomerGroupByDistributerId(long distributerId) throws Exception {
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
}
