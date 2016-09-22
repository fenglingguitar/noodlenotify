package org.fl.noodlenotify.console.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueExchangerService")
public class QueueExchangerServiceImpl implements QueueExchangerService {

	@Autowired
	private QueueDao queueDao;

	@Autowired
	private ProducerDao producerDao;

	@Autowired
	private ExchangerDao exchangerDao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerPage(QueueExchangerVo vo, int page, int rows) throws Exception {
		return queueExchangerDao.queryQueueExchangerPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerIncludePage(QueueExchangerVo vo, int page, int rows) throws Exception {
		return queueExchangerDao.queryQueueExchangerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerExcludePage(QueueExchangerVo vo, int page, int rows) throws Exception {
		return queueExchangerDao.queryQueueExchangerExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueExchangerVo> queryQueueExchangerList(QueueExchangerVo vo) throws Exception {
		return queueExchangerDao.queryQueueExchangerList(vo);
	}

	@Override
	public List<QueueExchangerVo> queryQueuesByExchanger(QueueExchangerVo vo) throws Exception {
		return queueExchangerDao.queryQueuesByExchanger(vo);
	}
	
	@Override
	public List<QueueExchangerVo> queryQueuesByExchangerTree(QueueExchangerVo vo) throws Exception {
		return queueExchangerDao.queryQueuesByExchangerTree(vo);
	}
	
	@Override
	public PageVo<QueueExchangerVo> queryQueuePageByExchanger(QueueExchangerVo vo, int page, int rows) throws Exception {
		return queueExchangerDao.queryQueuePageByExchanger(vo, page, rows);
	}

	@Override
	public void insertQueueExchanger(QueueExchangerVo vo) throws Exception {
		queueExchangerDao.insertQueueExchanger(vo);
	}

	@Override
	public void insertsQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		queueExchangerDao.insertsQueueExchanger(vos);
	}

	@Override
	public void updateQueueExchanger(QueueExchangerVo vo) throws Exception {
		queueExchangerDao.updateQueueExchanger(vo);
	}

	@Override
	public void updatesQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		queueExchangerDao.updatesQueueExchanger(vos);
	}

	@Override
	public void deleteQueueExchanger(QueueExchangerVo vo) throws Exception {
		queueExchangerDao.deleteQueueExchanger(vo);
	}

	@Override
	public void deletesQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		queueExchangerDao.deletesQueueExchanger(vos);
	}

	@Deprecated
	@Override
	public Map<String, List<QueueExchangerVo>> getQueueExchangersForProducer(long producerId) throws Exception {
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

	@Deprecated
	@Override
	public List<QueueExchangerVo> getQueueExchangersByExchangerId(long exchangerId) throws Exception {
		if (!exchangerDao.ifExchangerValid(exchangerId)) {
			return new ArrayList<QueueExchangerVo>();
		}
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(exchangerId);
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueExchangerVo> queues = queueExchangerDao.queryQueuesByExchanger(queueExchangerVo);
		return queues;
	}
}
