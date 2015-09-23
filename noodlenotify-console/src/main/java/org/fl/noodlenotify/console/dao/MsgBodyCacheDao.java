package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface MsgBodyCacheDao {
	public PageVo<MsgBodyCacheVo> queryMsgBodyCachePage(MsgBodyCacheVo vo, int page, int rows) throws Exception;

	public List<MsgBodyCacheVo> queryMsgBodyCacheList(MsgBodyCacheVo vo) throws Exception;

	public void insertMsgBodyCache(MsgBodyCacheVo vo) throws Exception;

	public void insertsMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception;

	public void updateMsgBodyCache(MsgBodyCacheVo vo) throws Exception;

	public void updatesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception;

	public void updatesMsgBodyCacheSize(MsgBodyCacheVo vo) throws Exception;

	public void updatesMsgBodyCacheSystemStatus(MsgBodyCacheVo vo) throws Exception;

	public void deleteMsgBodyCache(MsgBodyCacheVo vo) throws Exception;

	public void deletesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception;
}
