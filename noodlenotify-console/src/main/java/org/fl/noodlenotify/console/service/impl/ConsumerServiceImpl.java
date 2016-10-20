package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.ConsumerDao;
import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.service.ConsumerService;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("consumerService")
public class ConsumerServiceImpl implements ConsumerService {

	@Autowired
	private ConsumerDao consumerDao;

	@Autowired
	private QueueConsumerDao queueConsumerDao;

	@Override
	public PageVo<ConsumerVo> queryConsumerPage(ConsumerVo vo, int page, int rows) throws Exception {
		return consumerDao.queryConsumerPage(vo, page, rows);
	}

	@Override
	public PageVo<ConsumerVo> queryConsumerPageByEqual(ConsumerVo vo, int page, int rows) throws Exception {
		return consumerDao.queryConsumerPageByEqual(vo, page, rows);
	}

	@Override
	public List<ConsumerVo> queryConsumerList(ConsumerVo vo) throws Exception {
		return consumerDao.queryConsumerList(vo);
	}

	@Override
	public void insertConsumer(ConsumerVo vo) throws Exception {
		consumerDao.insertConsumer(vo);
	}

	@Override
	public void insertsConsumer(ConsumerVo[] vos) throws Exception {
		consumerDao.insertsConsumer(vos);
	}

	@Override
	public void updateConsumer(ConsumerVo vo) throws Exception {
		consumerDao.updateConsumer(vo);
	}

	@Override
	public void updatesConsumer(ConsumerVo[] vos) throws Exception {
		consumerDao.updatesConsumer(vos);
	}

	@Override
	public void updateConsumerSystemStatus(ConsumerVo vo) throws Exception {
		consumerDao.updateConsumerSystemStatus(vo);
	}

	@Override
	public void deleteConsumer(ConsumerVo vo) throws Exception {
		QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
		queueConsumerVo.setConsumer_Id(vo.getConsumer_Id());
		queueConsumerDao.deleteQueueConsumerByConsumerId(queueConsumerVo);
		consumerDao.deleteConsumer(vo);
	}

	@Override
	public void deletesConsumer(ConsumerVo[] vos) throws Exception {
		for (ConsumerVo vo : vos) {
			QueueConsumerVo queueConsumerVo = new QueueConsumerVo();
			queueConsumerVo.setConsumer_Id(vo.getConsumer_Id());
			queueConsumerDao.deleteQueueConsumerByConsumerId(queueConsumerVo);
			consumerDao.deleteConsumer(vo);
		}
	}

	@Override
	public void deletegroupConsumer(ConsumerVo vo) throws Exception {
		consumerDao.deletegroupConsumer(vo);
	}

	@Override
	public void deletesgroupConsumer(ConsumerVo[] vos) throws Exception {
		consumerDao.deletesgroupConsumer(vos);
	}

	@Override
	public List<ConsumerVo> queryConsumerToOnlineList(ConsumerVo vo) throws Exception {
		return consumerDao.queryConsumerToOnlineList(vo);
	}

	@Override
	public List<ConsumerVo> queryConsumerToOfflineList(ConsumerVo vo) throws Exception {
		return consumerDao.queryConsumerToOfflineList(vo);
	}
}
