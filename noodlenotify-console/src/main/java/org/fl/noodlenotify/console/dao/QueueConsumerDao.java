package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueConsumerDao {
	public PageVo<QueueConsumerVo> queryQueueConsumerPage(QueueConsumerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueConsumerVo> queryQueueConsumerIncludePage(QueueConsumerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueConsumerVo> queryQueueConsumerExcludePage(QueueConsumerVo vo, int page, int rows) throws Exception;

	public List<QueueConsumerVo> queryQueueConsumerList(QueueConsumerVo vo) throws Exception;

	public List<QueueConsumerVo> queryConsumersByQueue(QueueConsumerVo vo) throws Exception;

	public List<QueueConsumerVo> queryQueuesByConsumer(QueueConsumerVo vo) throws Exception;

	public PageVo<QueueConsumerVo> queryQueuePageByConsumer(QueueConsumerVo vo, int page, int rows) throws Exception;

	public void insertQueueConsumer(QueueConsumerVo vo) throws Exception;

	public void insertsQueueConsumer(QueueConsumerVo[] vos) throws Exception;

	public void updateQueueConsumer(QueueConsumerVo vo) throws Exception;

	public void updatesQueueConsumer(QueueConsumerVo[] vos) throws Exception;

	public void deleteQueueConsumer(QueueConsumerVo vo) throws Exception;

	public void deletesQueueConsumer(QueueConsumerVo[] vos) throws Exception;

	public void deleteQueueConsumerByConsumerId(QueueConsumerVo vo) throws Exception;

	public void deleteQueueConsumerByQueueNm(QueueConsumerVo vo) throws Exception;
}
