package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerGroupDao;
import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.service.QueueService;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("queueService")
public class QueueServiceImpl implements QueueService {

	@Autowired
	private QueueDao queueDao;

	@Autowired
	private QueueConsumerDao queueConsumerDao;

	@Autowired
	private QueueConsumerGroupDao queueConsumerGroupDao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Autowired
	private QueueMsgBodyCacheDao queueMsgBodyCacheDao;

	@Autowired
	private QueueMsgQueueCacheDao queueMsgQueueCacheDao;

	@Autowired
	private QueueMsgStorageDao queueMsgStorageDao;

	@Override
	public PageVo<QueueVo> queryQueuePage(QueueVo vo, int page, int rows) throws Exception {
		return queueDao.queryQueuePage(vo, page, rows);
	}

	@Override
	public List<QueueVo> queryQueueList(QueueVo vo) throws Exception {
		return queueDao.queryQueueList(vo);
	}

	@Override
	public void insertQueue(QueueVo vo) throws Exception {
		queueDao.insertQueue(vo);
	}

	@Override
	public void insertsQueue(QueueVo[] vos) throws Exception {
		queueDao.insertsQueue(vos);
	}

	@Override
	public void updateQueue(QueueVo vo) throws Exception {
		queueDao.updateQueue(vo);
	}

	@Override
	public void updatesQueue(QueueVo[] vos) throws Exception {
		queueDao.updatesQueue(vos);
	}

	@Override
	public void deleteQueue(QueueVo vo) throws Exception {
		QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
		queueConsumerVo.setQueue_Nm(vo.getQueue_Nm());
		queueConsumerDao.deleteQueueConsumerByQueueNm(queueConsumerVo);
		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setQueue_Nm(vo.getQueue_Nm());
		queueConsumerGroupDao.deleteQueueConsumerGroupByQueueNm(queueConsumerGroupVo);
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setQueue_Nm(vo.getQueue_Nm());
		queueExchangerDao.deleteQueueExchangerByQueueNm(queueExchangerVo);
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setQueue_Nm(vo.getQueue_Nm());
		queueDistributerDao.deleteQueueDistributerByQueueNm(queueDistributerVo);
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setQueue_Nm(vo.getQueue_Nm());
		queueMsgStorageDao.deleteQueueMsgStorageByQueueNm(queueMsgStorageVo);
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setQueue_Nm(vo.getQueue_Nm());
		queueMsgBodyCacheDao.deleteQueueMsgBodyCacheByQueueNm(queueMsgBodyCacheVo);
		QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVo.setQueue_Nm(vo.getQueue_Nm());
		queueMsgQueueCacheDao.deleteQueueMsgQueueCacheByQueueNm(queueMsgQueueCacheVo);
		queueDao.deleteQueue(vo);
	}

	@Override
	public void deletesQueue(QueueVo[] vos) throws Exception {
		for (QueueVo vo : vos) {
			QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
			queueConsumerVo.setQueue_Nm(vo.getQueue_Nm());
			queueConsumerDao.deleteQueueConsumerByQueueNm(queueConsumerVo);
			QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
			queueConsumerGroupVo.setQueue_Nm(vo.getQueue_Nm());
			queueConsumerGroupDao.deleteQueueConsumerGroupByQueueNm(queueConsumerGroupVo);
			QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
			queueExchangerVo.setQueue_Nm(vo.getQueue_Nm());
			queueExchangerDao.deleteQueueExchangerByQueueNm(queueExchangerVo);
			QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
			queueDistributerVo.setQueue_Nm(vo.getQueue_Nm());
			queueDistributerDao.deleteQueueDistributerByQueueNm(queueDistributerVo);
			QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
			queueMsgStorageVo.setQueue_Nm(vo.getQueue_Nm());
			queueMsgStorageDao.deleteQueueMsgStorageByQueueNm(queueMsgStorageVo);
			QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
			queueMsgBodyCacheVo.setQueue_Nm(vo.getQueue_Nm());
			queueMsgBodyCacheDao.deleteQueueMsgBodyCacheByQueueNm(queueMsgBodyCacheVo);
			QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
			queueMsgQueueCacheVo.setQueue_Nm(vo.getQueue_Nm());
			queueMsgQueueCacheDao.deleteQueueMsgQueueCacheByQueueNm(queueMsgQueueCacheVo);
			queueDao.deleteQueue(vo);
		}
	}
	
	@Override
	public PageVo<QueueVo> queryQueueMonitorPage(QueueVo vo, int page, int rows) throws Exception {
		return queueDao.queryQueueMonitorPage(vo, page, rows);
	}
	
	public void updatesQueueStatus(List<QueueVo> voList) throws Exception {
		queueDao.updatesQueueStatus(voList);
	}
}
