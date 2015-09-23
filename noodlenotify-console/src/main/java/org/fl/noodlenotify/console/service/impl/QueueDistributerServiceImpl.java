package org.fl.noodlenotify.console.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.service.QueueDistributerService;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("queueDistributerService")
public class QueueDistributerServiceImpl implements QueueDistributerService {

	@Autowired
	private DistributerDao distributerDao;
	
	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerPage(QueueDistributerVo vo, int page, int rows) throws Exception {
		return queueDistributerDao.queryQueueDistributerPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerIncludePage(QueueDistributerVo vo, int page, int rows) throws Exception {
		return queueDistributerDao.queryQueueDistributerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerExcludePage(QueueDistributerVo vo, int page, int rows) throws Exception {
		return queueDistributerDao.queryQueueDistributerExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueDistributerVo> queryQueueDistributerList(QueueDistributerVo vo) throws Exception {
		return queueDistributerDao.queryQueueDistributerList(vo);
	}
	
	@Override
	public List<QueueDistributerVo> queryQueuesByDistributer(QueueDistributerVo vo) throws Exception {
		return queueDistributerDao.queryQueuesByDistributer(vo);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueuePageByDistributer(QueueDistributerVo vo, int page, int rows) throws Exception {
		return queueDistributerDao.queryQueuePageByDistributer(vo, page, rows);
	}

	@Override
	public void insertQueueDistributer(QueueDistributerVo vo) throws Exception {
		queueDistributerDao.insertQueueDistributer(vo);
	}

	@Override
	public void insertsQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		queueDistributerDao.insertsQueueDistributer(vos);
	}

	@Override
	public void updateQueueDistributer(QueueDistributerVo vo) throws Exception {
		queueDistributerDao.updateQueueDistributer(vo);
	}

	@Override
	public void updatesQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		queueDistributerDao.updatesQueueDistributer(vos);
	}

	@Override
	public void deleteQueueDistributer(QueueDistributerVo vo) throws Exception {
		queueDistributerDao.deleteQueueDistributer(vo);
	}

	@Override
	public void deletesQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		queueDistributerDao.deletesQueueDistributer(vos);
	}

	@Override
	public List<QueueDistributerVo> getQueueDistributers(long distributerId) throws Exception {
		if (!distributerDao.ifDistributerValid(distributerId)) {
			return new ArrayList<QueueDistributerVo>();
		}
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(distributerId);
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueDistributerVo> queueDistributers = queueDistributerDao.queryQueuesByDistributer(queueDistributerVo);
		return queueDistributers;
	}

	@Override
	public List<QueueDistributerVo> queryQueuesByDistributerTree(
			QueueDistributerVo vo) throws Exception {
		return queueDistributerDao.queryQueuesByDistributerTree(vo);
	}
}
