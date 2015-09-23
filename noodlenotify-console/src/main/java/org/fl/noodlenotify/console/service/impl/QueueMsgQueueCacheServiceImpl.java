package org.fl.noodlenotify.console.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

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

	@Override
	public Map<String, List<QueueMsgQueueCacheVo>> getQueueMsgQueueCacheByDistributerId(long distributerId) throws Exception {
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
}
