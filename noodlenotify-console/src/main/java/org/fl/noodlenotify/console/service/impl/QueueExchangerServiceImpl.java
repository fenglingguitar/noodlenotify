package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
