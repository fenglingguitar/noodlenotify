package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fl.noodlenotify.console.dao.ConsumerDao;
import org.fl.noodlenotify.console.dao.ConsumerGroupDao;
import org.fl.noodlenotify.console.dao.QueueConsumerGroupDao;
import org.fl.noodlenotify.console.service.ConsumerGroupService;
import org.fl.noodlenotify.console.vo.ConsumerGroupVo;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("consumerGroupService")
public class ConsumerGroupServiceImpl implements ConsumerGroupService {
	@Autowired
	private ConsumerDao consumerDao;

	@Autowired
	private ConsumerGroupDao consumerGroupDao;

	@Autowired
	private QueueConsumerGroupDao queueConsumerGroupDao;

	@Override
	public PageVo<ConsumerGroupVo> queryConsumerGroupPage(ConsumerGroupVo vo, int page, int rows) throws Exception {
		return consumerGroupDao.queryConsumerGroupPage(vo, page, rows);
	}

	@Override
	public List<ConsumerGroupVo> queryConsumerGroupList(ConsumerGroupVo vo) throws Exception {
		return consumerGroupDao.queryConsumerGroupList(vo);
	}

	@Override
	public PageVo<ConsumerVo> queryConsumerIncludePage(ConsumerVo vo, int page, int rows) throws Exception {
		return consumerGroupDao.queryConsumerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<ConsumerVo> queryConsumerExcludePage(ConsumerVo vo, int page, int rows) throws Exception {
		return consumerGroupDao.queryConsumerExcludePage(vo, page, rows);
	}

	@Override
	public void insertConsumerGroup(ConsumerGroupVo vo) throws Exception {
		consumerGroupDao.insertConsumerGroup(vo);
	}

	@Override
	public void insertsConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		consumerGroupDao.insertsConsumerGroup(vos);
	}

	@Override
	public void updateConsumerGroup(ConsumerGroupVo vo) throws Exception {
		consumerGroupDao.updateConsumerGroup(vo);
	}

	@Override
	public void updatesConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		consumerGroupDao.updatesConsumerGroup(vos);
	}

	@Override
	public void deleteConsumerGroup(ConsumerGroupVo vo) throws Exception {
		consumerDao.updateConsumerGroupNmToNull(vo.getConsumerGroup_Nm());
		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setConsumerGroup_Nm(vo.getConsumerGroup_Nm());
		queueConsumerGroupDao.deleteQueueConsumerGroupByConsumerGroupNm(queueConsumerGroupVo);
		consumerGroupDao.deleteConsumerGroup(vo);
	}

	@Override
	public void deletesConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		for (ConsumerGroupVo vo : vos) {
			consumerDao.updateConsumerGroupNmToNull(vo.getConsumerGroup_Nm());
			QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
			queueConsumerGroupVo.setConsumerGroup_Nm(vo.getConsumerGroup_Nm());
			queueConsumerGroupDao.deleteQueueConsumerGroupByConsumerGroupNm(queueConsumerGroupVo);
			consumerGroupDao.deleteConsumerGroup(vo);
		}
	}
}
