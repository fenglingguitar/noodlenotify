package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueDbDao;
import org.fl.noodlenotify.console.service.QueueDbService;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueDbService")
public class QueueDbServiceImpl implements QueueDbService {
	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueDbDao queueDbDao;

	@Override
	public PageVo<QueueDbVo> queryQueueDbPage(QueueDbVo vo, int page, int rows) throws Exception {
		return queueDbDao.queryQueueDbPage(vo, page, rows);
	}
	
	@Override
	public List<QueueDbVo> queryQueueDbList(QueueDbVo vo) throws Exception {
		return queueDbDao.queryQueueDbList(vo);
	}

	@Override
	public PageVo<QueueDbVo> queryQueueDbIncludePage(QueueDbVo vo, int page, int rows) throws Exception {
		return queueDbDao.queryQueueDbIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueDbVo> queryQueueDbExcludePage(QueueDbVo vo, int page, int rows) throws Exception {
		return queueDbDao.queryQueueDbExcludePage(vo, page, rows);
	}
	
	@Override
	public List<QueueDbVo> queryQueueDbIncludeList(QueueDbVo vo) throws Exception {
		return queueDbDao.queryQueueDbIncludeList(vo);
	}

	@Override
	public List<QueueDbVo> queryDbByQueueExclude(QueueDbVo vo) throws Exception {
		return queueDbDao.queryDbByQueueExclude(vo);
	}
	
	@Override
	public List<QueueDbVo> queryQueueByDbListTree(QueueDbVo vo) throws Exception {
		return queueDbDao.queryQueueByDbListTree(vo);
	}
	
	@Override
	public PageVo<QueueDbVo> queryQueueByDb(QueueDbVo vo, int page, int rows) throws Exception {
		return queueDbDao.queryQueueByDb(vo, page, rows);
	}

	@Override
	public void insertQueueDb(QueueDbVo vo) throws Exception {
		queueDbDao.insertQueueDb(vo);
	}

	@Override
	public void insertsQueueDb(QueueDbVo[] vos) throws Exception {
		queueDbDao.insertsQueueDb(vos);
	}

	@Override
	public void updateQueueDb(QueueDbVo vo) throws Exception {
		queueDbDao.updateQueueDb(vo);
	}
	
	@Override
	public void updateQueueDbSimple(QueueDbVo vo) throws Exception {
		queueDbDao.updateQueueDb(vo);
	}

	@Override
	public void updatesQueueDb(QueueDbVo[] vos) throws Exception {
		queueDbDao.updatesQueueDb(vos);
	}

	@Override
	public void deleteQueueDb(QueueDbVo vo) throws Exception {
		queueDbDao.deleteQueueDb(vo);
	}

	@Override
	public void deletesQueueDb(QueueDbVo[] vos) throws Exception {
		queueDbDao.deletesQueueDb(vos);
	}	
}
