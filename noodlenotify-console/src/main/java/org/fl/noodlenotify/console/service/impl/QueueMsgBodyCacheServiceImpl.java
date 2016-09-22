package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.service.QueueMsgBodyCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueMsgBodyCacheService")
public class QueueMsgBodyCacheServiceImpl implements QueueMsgBodyCacheService {

	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueMsgBodyCacheDao queueMsgBodyCacheDao;

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCachePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		return queueMsgBodyCacheDao.queryQueueMsgBodyCachePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheIncludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		return queueMsgBodyCacheDao.queryQueueMsgBodyCacheIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheExcludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		return queueMsgBodyCacheDao.queryQueueMsgBodyCacheExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception {
		return queueMsgBodyCacheDao.queryQueueMsgBodyCacheList(vo);
	}
	
	@Override
	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception {
		return queueMsgBodyCacheDao.queryQueueMsgBodyCacheListTree(vo);
	}

	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception {
		return queueMsgBodyCacheDao.queryQueueByMsgBodyCacheList(vo);
	}
	
	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception {
		return queueMsgBodyCacheDao.queryQueueByMsgBodyCacheListTree(vo);
	}
	
	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueuePageByMsgBodyCache(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		return queueMsgBodyCacheDao.queryQueuePageByMsgBodyCache(vo, page, rows);
	}

	@Override
	public void insertQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheDao.insertQueueMsgBodyCache(vo);
	}

	@Override
	public void insertsQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheDao.insertsQueueMsgBodyCache(vos);
	}

	@Override
	public void updateQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheDao.updateQueueMsgBodyCache(vo);
	}

	@Override
	public void updatesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheDao.updatesQueueMsgBodyCache(vos);
	}

	@Override
	public void deleteQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheDao.deleteQueueMsgBodyCache(vo);
	}

	@Override
	public void deletesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheDao.deletesQueueMsgBodyCache(vos);
	}
}
