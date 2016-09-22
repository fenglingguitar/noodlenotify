package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;

public interface QueueMsgQueueCacheService {
	
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCachePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheIncludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheExcludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception;

	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception;
	
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception;
	
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheListTree(QueueMsgQueueCacheVo vo) throws Exception;

	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheByQueue(QueueMsgQueueCacheVo vo) throws Exception;

	public PageVo<QueueMsgQueueCacheVo> queryQueuePageByMsgQueueCache(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception;

	public void insertQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception;

	public void insertsQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception;

	public void updateQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception;

	public void updatesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception;

	public void updateQueueMsgQueueCacheSimple(QueueMsgQueueCacheVo vo) throws Exception;

	public void deleteQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception;

	public void deletesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception;	
}
