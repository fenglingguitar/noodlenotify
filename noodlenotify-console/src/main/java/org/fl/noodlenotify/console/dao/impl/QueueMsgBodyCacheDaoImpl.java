package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.domain.QueueMsgBodyCacheMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueMsgBodyCacheDao")
public class QueueMsgBodyCacheDaoImpl implements QueueMsgBodyCacheDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCachePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		return dynamicSqlTemplate.queryPage("queue-msgbodycache-query-list", paramsMap, page, rows, QueueMsgBodyCacheVo.class);
	}

	@Override
	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		return dynamicSqlTemplate.queryList("queue-msgbodycache-query-list", paramsMap, QueueMsgBodyCacheVo.class);
	}
	
	@Override
	public List<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		return dynamicSqlTemplate.queryList("queue-msgbodycache-query-list-tree", paramsMap, QueueMsgBodyCacheVo.class);
	}

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheIncludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgbodycache-query-includelist", paramsMap, page, rows, QueueMsgBodyCacheVo.class);
	}

	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueueMsgBodyCacheExcludePage(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgbodycache-query-excludelist", paramsMap, page, rows, QueueMsgBodyCacheVo.class);
	}

	@Override
	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheList(QueueMsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgbodycache-query-queue", paramsMap, QueueMsgBodyCacheVo.class);
	}
	
	@Override
	public List<QueueMsgBodyCacheVo> queryQueueByMsgBodyCacheListTree(QueueMsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgbodycache-query-queue-tree", paramsMap, QueueMsgBodyCacheVo.class);
	}
	
	@Override
	public List<QueueMsgBodyCacheVo> queryMsgBodyCacheByQueue(QueueMsgBodyCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgbodycache-query-msgbodycache", paramsMap, QueueMsgBodyCacheVo.class);
	}
	
	@Override
	public PageVo<QueueMsgBodyCacheVo> queryQueuePageByMsgBodyCache(QueueMsgBodyCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgBodyCache_Id", vo.getMsgBodyCache_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgbodycache-query-queue", paramsMap, page, rows, QueueMsgBodyCacheVo.class);
	}

	@Override
	public void insertQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueMsgBodyCacheMd.class);
	}

	@Override
	public void insertsQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		for (QueueMsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueMsgBodyCacheMd.class);
		}
	}

	@Override
	public void updateQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueMsgBodyCacheMd.class);
	}

	@Override
	public void updatesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		for (QueueMsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueMsgBodyCacheMd.class);
		}
	}

	@Override
	public void deleteQueueMsgBodyCache(QueueMsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueMsgBodyCacheMd.class);
	}

	@Override
	public void deletesQueueMsgBodyCache(QueueMsgBodyCacheVo[] vos) throws Exception {
		for (QueueMsgBodyCacheVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueMsgBodyCacheMd.class);
		}
	}

	@Override
	public void deleteQueueMsgBodyCacheByMsgBodyCacheId(QueueMsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgBodyCacheMd.class, new String[] { "msgBodyCache_Id" });
	}
	
	@Override
	public void deleteQueueMsgBodyCacheByQueueNm(QueueMsgBodyCacheVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgBodyCacheMd.class, new String[] { "queue_Nm" });
	}

}
