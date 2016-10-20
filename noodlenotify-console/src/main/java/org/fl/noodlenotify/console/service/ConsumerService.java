package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.ConsumerVo;

public interface ConsumerService {
	
	public PageVo<ConsumerVo> queryConsumerPage(ConsumerVo vo, int page, int rows) throws Exception;

	public PageVo<ConsumerVo> queryConsumerPageByEqual(ConsumerVo vo, int page, int rows) throws Exception;

	public List<ConsumerVo> queryConsumerList(ConsumerVo vo) throws Exception;

	public void insertConsumer(ConsumerVo vo) throws Exception;

	public void insertsConsumer(ConsumerVo[] vos) throws Exception;

	public void updateConsumer(ConsumerVo vo) throws Exception;

	public void updatesConsumer(ConsumerVo[] vos) throws Exception;

	public void updateConsumerSystemStatus(ConsumerVo vo) throws Exception;

	public void deleteConsumer(ConsumerVo vo) throws Exception;

	public void deletesConsumer(ConsumerVo[] vos) throws Exception;

	public void deletegroupConsumer(ConsumerVo vo) throws Exception;

	public void deletesgroupConsumer(ConsumerVo[] vos) throws Exception;

	public List<ConsumerVo> queryConsumerToOnlineList(ConsumerVo vo) throws Exception;
	public List<ConsumerVo> queryConsumerToOfflineList(ConsumerVo vo) throws Exception;
}
