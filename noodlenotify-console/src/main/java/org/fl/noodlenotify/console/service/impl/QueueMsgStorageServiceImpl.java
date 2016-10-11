package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public List<QueueMsgStorageVo> queryQueueMsgStorageList(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStorageList(vo);
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
	public List<QueueMsgStorageVo> queryQueueMsgStorageIncludeList(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryQueueMsgStorageIncludeList(vo);
	}

	@Override
	public List<QueueMsgStorageVo> queryMsgStoragesByQueueExclude(QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageDao.queryMsgStoragesByQueueExclude(vo);
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
}
