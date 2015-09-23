package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.domain.QueueMsgQueueCacheMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueMsgQueueCacheDao")
public class QueueMsgQueueCacheDaoImpl implements QueueMsgQueueCacheDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCachePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id());
		return dynamicSqlTemplate.queryPage("queue-msgqueuecache-query-list", paramsMap, page, rows, QueueMsgQueueCacheVo.class);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheIncludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgqueuecache-query-includelist", paramsMap, page, rows, QueueMsgQueueCacheVo.class);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheExcludePage(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgqueuecache-query-excludelist", paramsMap, page, rows, QueueMsgQueueCacheVo.class);
	}

	@Override
	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id());
		return dynamicSqlTemplate.queryList("queue-msgqueuecache-query-list", paramsMap, QueueMsgQueueCacheVo.class);
	}

	@Override
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheList(QueueMsgQueueCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgqueuecache-query-queue", paramsMap, QueueMsgQueueCacheVo.class);
	}
	

	@Override
	public List<QueueMsgQueueCacheVo> queryQueueByMsgQueueCacheListTree(
			QueueMsgQueueCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id());
		paramsMap.put("queue_Nm",  vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgqueuecache-query-queue-tree", paramsMap, QueueMsgQueueCacheVo.class);
	}
	
	@Override
	public List<QueueMsgQueueCacheVo> queryQueueMsgQueueCacheByQueue(QueueMsgQueueCacheVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgqueuecache-query-msgqueuecache", paramsMap, QueueMsgQueueCacheVo.class);
	}

	@Override
	public PageVo<QueueMsgQueueCacheVo> queryQueuePageByMsgQueueCache(QueueMsgQueueCacheVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgQueueCache_Id", vo.getMsgQueueCache_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgqueuecache-query-queue", paramsMap, page, rows, QueueMsgQueueCacheVo.class);
	}

	@Override
	public void insertQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueMsgQueueCacheMd.class);
	}

	@Override
	public void insertsQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		for (QueueMsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueMsgQueueCacheMd.class);
		}
	}

	@Override
	public void updateQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueMsgQueueCacheMd.class);
	}

	@Override
	public void updatesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		for (QueueMsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueMsgQueueCacheMd.class);
		}
	}

	@Override
	public void deleteQueueMsgQueueCache(QueueMsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueMsgQueueCacheMd.class);
	}

	@Override
	public void deletesQueueMsgQueueCache(QueueMsgQueueCacheVo[] vos) throws Exception {
		for (QueueMsgQueueCacheVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueMsgQueueCacheMd.class);
		}
	}

	@Override
	public void deleteQueueMsgQueueCacheByMsgQueueCacheId(QueueMsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgQueueCacheMd.class, new String[] { "msgQueueCache_Id" });
	}

	@Override
	public void deleteQueueMsgQueueCacheByQueueNm(QueueMsgQueueCacheVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgQueueCacheMd.class, new String[] { "queue_Nm" });
	}

}
