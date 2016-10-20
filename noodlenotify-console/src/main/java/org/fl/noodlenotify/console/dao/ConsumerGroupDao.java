package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.ConsumerGroupVo;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface ConsumerGroupDao {
	public PageVo<ConsumerGroupVo> queryConsumerGroupPage(ConsumerGroupVo vo, int page, int rows) throws Exception;

	public List<ConsumerGroupVo> queryConsumerGroupList(ConsumerGroupVo vo) throws Exception;
	
	public PageVo<ConsumerVo> queryConsumerIncludePage(ConsumerVo vo, int page, int rows) throws Exception;

	public PageVo<ConsumerVo> queryConsumerExcludePage(ConsumerVo vo, int page, int rows) throws Exception;

	public void insertConsumerGroup(ConsumerGroupVo vo) throws Exception;

	public void insertsConsumerGroup(ConsumerGroupVo[] vos) throws Exception;

	public void updateConsumerGroup(ConsumerGroupVo vo) throws Exception;

	public void updatesConsumerGroup(ConsumerGroupVo[] vos) throws Exception;

	public void deleteConsumerGroup(ConsumerGroupVo vo) throws Exception;

	public void deletesConsumerGroup(ConsumerGroupVo[] vos) throws Exception;
}
