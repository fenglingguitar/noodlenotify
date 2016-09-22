package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueMsgQueueCacheService")
public class QueueMsgQueueCacheServiceImpl implements QueueMsgQueueCacheService {

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueMsgQueueCacheDao queueMsgQueueCacheDao;

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCachePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		return queueMsgQueueCacheDao.queryQueueMsgQueueCachePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheIncludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		return queueMsgQueueCacheDao.queryQueueMsgQueueCacheIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheExcludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		return queueMsgQueueCacheDao.queryQueueMsgQueueCacheExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception {
		return queueMsgQueueCacheDao.queryQueueMsgQueueCacheList(vo);
	}

	@Override
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception {
		return queueMsgQueueCacheDao.queryQueueByMsgQueueCacheList(vo);
	}
	
	@Override
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheListTree(QueueMsgQueueCacheVo vo) throws Exception {
		return queueMsgQueueCacheDao.queryQueueByMsgQueueCacheListTree(vo);
	}
	
	@Override
	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheByQueue(QueueMsgQueueCacheVo vo) throws Exception {
		return queueMsgQueueCacheDao.queryQueueMsgQueueCacheByQueue(vo);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueuePageByMsgQueueCache(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		return queueMsgQueueCacheDao.queryQueuePageByMsgQueueCache(vo, page, rows);
	}

	@Override
	public void insertQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheDao.insertQueueMsgQueueCache(vo);
	}

	@Override
	public void insertsQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheDao.insertsQueueMsgQueueCache(vos);
	}

	@Override
	public void updateQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheDao.updateQueueMsgQueueCache(vo);
	}

	@Override
	public void updateQueueMsgQueueCacheSimple(QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheDao.updateQueueMsgQueueCache(vo);
	}

	@Override
	public void updatesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheDao.updatesQueueMsgQueueCache(vos);
	}

	@Override
	public void deleteQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheDao.deleteQueueMsgQueueCache(vo);
	}

	@Override
	public void deletesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheDao.deletesQueueMsgQueueCache(vos);
	}
}
