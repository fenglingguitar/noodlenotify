package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.MsgQueueCacheDao;
import org.fl.noodlenotify.console.domain.MsgQueueCacheMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("msgQueueCacheDao")
public class MsgQueueCacheDaoImpl implements MsgQueueCacheDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<MsgQueueCacheVo> queryMsgQueueCachePage(MsgQueueCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id() > 0 ? vo.getMsgQueueCache_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("msg-queue-cache-query-list", paramsMap, page, rows, MsgQueueCacheVo.class);
	}

	@Override
	public List<MsgQueueCacheVo> queryMsgQueueCacheList(MsgQueueCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id() > 0 ? vo.getMsgQueueCache_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("msg-queue-cache-query-list", paramsMap, MsgQueueCacheVo.class);
	}

	@Override
	public void insertMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, MsgQueueCacheMd.class);
	}

	@Override
	public void insertsMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		for (MsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.insert(vo, MsgQueueCacheMd.class);
		}
	}

	@Override
	public void updateMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, MsgQueueCacheMd.class);

	}

	@Override
	public void updatesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		for (MsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, MsgQueueCacheMd.class);
		}
	}

	@Override
	public void updatesMsgQueueCacheSystemStatus(MsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, MsgQueueCacheMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deleteMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, MsgQueueCacheMd.class);
	}

	@Override
	public void deletesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		for (MsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.delete(vo, MsgQueueCacheMd.class);
		}
	}

}
