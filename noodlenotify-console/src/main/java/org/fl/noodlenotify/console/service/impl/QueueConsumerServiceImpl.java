package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.service.QueueConsumerService;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueConsumerService")
public class QueueConsumerServiceImpl implements QueueConsumerService {

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueConsumerDao queueConsumerDao;

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerPage(QueueConsumerVo vo, int page, int rows) throws Exception {
		return queueConsumerDao.queryQueueConsumerPage(vo, page, rows);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerIncludePage(QueueConsumerVo vo, int page, int rows) throws Exception {
		return queueConsumerDao.queryQueueConsumerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerExcludePage(QueueConsumerVo vo, int page, int rows) throws Exception {
		return queueConsumerDao.queryQueueConsumerExcludePage(vo, page, rows);
	}

	@Override
	public List<QueueConsumerVo> queryQueueConsumerList(QueueConsumerVo vo) throws Exception {
		return queueConsumerDao.queryQueueConsumerList(vo);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueuePageByConsumer(QueueConsumerVo vo, int page, int rows) throws Exception {
		return queueConsumerDao.queryQueuePageByConsumer(vo, page, rows);
	}

	@Override
	public void insertQueueConsumer(QueueConsumerVo vo) throws Exception {
		queueConsumerDao.insertQueueConsumer(vo);
	}

	@Override
	public void insertsQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		queueConsumerDao.insertsQueueConsumer(vos);
	}

	@Override
	public void updateQueueConsumer(QueueConsumerVo vo) throws Exception {
		queueConsumerDao.updateQueueConsumer(vo);
	}

	@Override
	public void updatesQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		queueConsumerDao.updatesQueueConsumer(vos);
	}

	@Override
	public void deleteQueueConsumer(QueueConsumerVo vo) throws Exception {
		queueConsumerDao.deleteQueueConsumer(vo);
	}

	@Override
	public void deletesQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		queueConsumerDao.deletesQueueConsumer(vos);
	}
}
