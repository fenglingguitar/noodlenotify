package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.MsgBodyCacheDao;
import org.fl.noodlenotify.console.domain.MsgBodyCacheMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("msgBodyCacheDao")
public class MsgBodyCacheDaoImpl implements MsgBodyCacheDao {
	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<MsgBodyCacheVo> queryMsgBodyCachePage(MsgBodyCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id() > 0 ? vo.getMsgBodyCache_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("msg-body-cache-query-list", paramsMap, page, rows, MsgBodyCacheVo.class);
	}

	@Override
	public List<MsgBodyCacheVo> queryMsgBodyCacheList(MsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id() > 0 ? vo.getMsgBodyCache_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("msg-body-cache-query-list", paramsMap, MsgBodyCacheVo.class);
	}
	
	@Override
	public void insertMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, MsgBodyCacheMd.class);
	}

	@Override
	public void insertsMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		for (MsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.insert(vo, MsgBodyCacheMd.class);
		}
	}

	@Override
	public void updateMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, MsgBodyCacheMd.class);
	}

	@Override
	public void updatesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		for (MsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, MsgBodyCacheMd.class);
		}
	}

	@Override
	public void updatesMsgBodyCacheSystemStatus(MsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, MsgBodyCacheMd.class, new String[] { "system_Status" });
	}

	@Override
	public void updatesMsgBodyCacheSize(MsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, MsgBodyCacheMd.class, new String[] { "size" });
	}

	@Override
	public void deleteMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, MsgBodyCacheMd.class);
	}

	@Override
	public void deletesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		for (MsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.delete(vo, MsgBodyCacheMd.class);
		}
	}

}
