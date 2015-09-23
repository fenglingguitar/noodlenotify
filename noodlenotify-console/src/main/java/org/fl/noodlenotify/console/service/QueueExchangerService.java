package org.fl.noodlenotify.console.service;

import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueExchangerService {
	public PageVo<QueueExchangerVo> queryQueueExchangerPage(QueueExchangerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueExchangerVo> queryQueueExchangerIncludePage(QueueExchangerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueExchangerVo> queryQueueExchangerExcludePage(QueueExchangerVo vo, int page, int rows) throws Exception;

	public List<QueueExchangerVo> queryQueueExchangerList(QueueExchangerVo vo) throws Exception;
	
	public List<QueueExchangerVo> queryQueuesByExchanger(QueueExchangerVo vo) throws Exception;

	public PageVo<QueueExchangerVo> queryQueuePageByExchanger(QueueExchangerVo vo, int page, int rows) throws Exception;

	public void insertQueueExchanger(QueueExchangerVo vo) throws Exception;

	public void insertsQueueExchanger(QueueExchangerVo[] vos) throws Exception;

	public void updateQueueExchanger(QueueExchangerVo vo) throws Exception;

	public void updatesQueueExchanger(QueueExchangerVo[] vos) throws Exception;

	public void deleteQueueExchanger(QueueExchangerVo vo) throws Exception;

	public void deletesQueueExchanger(QueueExchangerVo[] vos) throws Exception;

	public Map<String, List<QueueExchangerVo>> getQueueExchangersForProducer(long producerId) throws Exception;

	public List<QueueExchangerVo> getQueueExchangersByExchangerId(long exchangerId) throws Exception;

	public List<QueueExchangerVo> queryQueuesByExchangerTree(QueueExchangerVo vo)throws Exception;
}
