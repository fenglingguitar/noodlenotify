package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.domain.ConsumerMd;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface ConsumerDao {

	public PageVo<ConsumerVo> queryConsumerPage(ConsumerVo vo, int page, int rows) throws Exception;

	public PageVo<ConsumerVo> queryConsumerPageByEqual(ConsumerVo vo, int page, int rows) throws Exception;

	public List<ConsumerVo> queryConsumerList(ConsumerVo vo) throws Exception;

	public void insertConsumer(ConsumerVo vo) throws Exception;

	public void insertsConsumer(ConsumerVo[] vos) throws Exception;

	public ConsumerMd insertOrUpdate(ConsumerVo vo) throws Exception;

	public void updateConsumer(ConsumerVo vo) throws Exception;

	public void updatesConsumer(ConsumerVo[] vos) throws Exception;

	public void updateConsumerGroupNmToNull(String consumerGroupNm) throws Exception;

	public void deleteConsumer(ConsumerVo vo) throws Exception;

	public void deletesConsumer(ConsumerVo[] vos) throws Exception;

	public void updateConsumerSystemStatus(ConsumerVo vo) throws Exception;

	public void deletegroupConsumer(ConsumerVo vo) throws Exception;

	public void deletesgroupConsumer(ConsumerVo[] vos) throws Exception;
	
	public List<ConsumerVo> queryConsumerToOnlineList(ConsumerVo vo) throws Exception;
	public List<ConsumerVo> queryConsumerToOfflineList(ConsumerVo vo) throws Exception;
}
