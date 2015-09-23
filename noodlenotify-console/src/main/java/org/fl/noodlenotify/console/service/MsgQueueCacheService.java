package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface MsgQueueCacheService {
	public PageVo<MsgQueueCacheVo> queryMsgQueueCachePage(MsgQueueCacheVo vo, int page, int rows) throws Exception;

	public List<MsgQueueCacheVo> queryMsgQueueCacheList(MsgQueueCacheVo vo) throws Exception;

	public List<MsgQueueCacheVo> queryCheckMsgQueueCacheList() throws Exception;

	public List<MsgQueueCacheVo> queryCheckMsgQueueCacheListWithCache() throws Exception;

	public void insertMsgQueueCache(MsgQueueCacheVo vo) throws Exception;

	public void insertsMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception;

	public void updateMsgQueueCache(MsgQueueCacheVo vo) throws Exception;

	public void updatesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception;

	public void updatesMsgQueueCacheSystemStatus(MsgQueueCacheVo vo) throws Exception;

	public void deleteMsgQueueCache(MsgQueueCacheVo vo) throws Exception;

	public void deletesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception;
}
