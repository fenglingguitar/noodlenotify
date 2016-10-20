package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;

public interface QueueConsumerGroupService {
	
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupPage(QueueConsumerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupIncludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupExcludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception;

	public List<QueueConsumerGroupVo> queryQueueConsumerGroupList(QueueConsumerGroupVo vo) throws Exception;
	
	public List<QueueConsumerGroupVo> queryQueueByConsumerGroupList(QueueConsumerGroupVo vo) throws Exception;
	
	public List<QueueConsumerGroupVo> queryConsumerGroupsByQueue(QueueConsumerGroupVo vo) throws Exception;

	public List<QueueConsumerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception;

	public PageVo<QueueConsumerGroupVo> queryQueuePageByConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueConsumerGroupVo> queryConsumerPageByQueueConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception;

	public void insertQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception;

	public void insertsQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception;

	public void updateQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception;

	public void updatesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception;

	public void deleteQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception;

	public void deletesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception;
}
