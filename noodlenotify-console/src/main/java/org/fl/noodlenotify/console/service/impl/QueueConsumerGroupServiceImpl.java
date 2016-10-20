package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.QueueConsumerGroupService;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueConsumerGroupService")
public class QueueConsumerGroupServiceImpl implements QueueConsumerGroupService {

	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueConsumerDao queueConsumerDao;

	@Autowired
	private QueueConsumerGroupDao queueConsumerGroupDao;

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupPage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		return queueConsumerGroupDao.queryQueueConsumerGroupPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupIncludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		return queueConsumerGroupDao.queryQueueConsumerGroupIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupExcludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		return queueConsumerGroupDao.queryQueueConsumerGroupExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueConsumerGroupVo> queryQueueConsumerGroupList(QueueConsumerGroupVo vo) throws Exception {
		return queueConsumerGroupDao.queryQueueConsumerGroupList(vo);
	}
	
	@Override
	public List<QueueConsumerGroupVo> queryQueueByConsumerGroupList(QueueConsumerGroupVo vo) throws Exception {
		return queueConsumerGroupDao.queryQueueByConsumerGroupList(vo);
	}

	@Override
	public List<QueueConsumerGroupVo> queryConsumerGroupsByQueue(QueueConsumerGroupVo vo) throws Exception {
		return queueConsumerGroupDao.queryConsumerGroupsByQueue(vo);
	}
	
	@Override
	public List<QueueConsumerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception {
		return queueConsumerGroupDao.queryUnuserGroupNumList(queueNm);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueuePageByConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		return queueConsumerGroupDao.queryQueuePageByConsumerGroup(vo, page, rows);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryConsumerPageByQueueConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		return queueConsumerGroupDao.queryConsumerPageByQueueConsumerGroup(vo, page, rows);
	}

	@Override
	public void insertQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupDao.insertQueueConsumerGroup(vo);
	}

	@Override
	public void insertsQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		queueConsumerGroupDao.insertsQueueConsumerGroup(vos);
	}

	@Override
	public void updateQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupDao.updateQueueConsumerGroup(vo);
	}

	@Override
	public void updatesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		queueConsumerGroupDao.updatesQueueConsumerGroup(vos);
	}

	@Override
	public void deleteQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupDao.deleteQueueConsumerGroup(vo);
	}

	@Override
	public void deletesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		queueConsumerGroupDao.deletesQueueConsumerGroup(vos);
	}
}
