package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.QueueCustomerGroupService;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
