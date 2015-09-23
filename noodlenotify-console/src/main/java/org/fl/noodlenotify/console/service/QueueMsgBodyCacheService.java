package org.fl.noodlenotify.console.service;

import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueMsgBodyCacheService {
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCachePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheIncludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheExcludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception;

	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception;
	
	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception;

	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception;
	
	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception;
	
	public PageVo<QueueMsgBodyCacheVo> queryQueuePageByMsgBodyCache(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception;

	public void insertQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception;

	public void insertsQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception;

	public void updateQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception;

	public void updatesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception;

	public void deleteQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception;

	public void deletesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> getQueueMsgBodyCacheByExchangerId(long exchangerId) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> getQueueMsgBodyCacheByDistributerId(long distributerId) throws Exception;

	

}
