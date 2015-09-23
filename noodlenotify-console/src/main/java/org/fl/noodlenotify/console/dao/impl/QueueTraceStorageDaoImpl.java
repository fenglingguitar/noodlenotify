package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueTraceStorageDao;
import org.fl.noodlenotify.console.domain.QueueTraceStorageMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueTraceStorageDao")
public class QueueTraceStorageDaoImpl implements QueueTraceStorageDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStoragePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("traceStorage_Id", vo.getTraceStorage_Id());
		return dynamicSqlTemplate.queryPage("queue-tracestorage-query-list", paramsMap, page, rows, QueueTraceStorageVo.class);
	}

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageIncludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-tracestorage-query-includelist", paramsMap, page, rows, QueueTraceStorageVo.class);
	}

	@Override
	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageExcludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-tracestorage-query-excludelist", paramsMap, page, rows, QueueTraceStorageVo.class);
	}

	@Override
	public List<QueueTraceStorageVo> queryQueueTraceStorageList(QueueTraceStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgStorage_Id", vo.getTraceStorage_Id());
		return dynamicSqlTemplate.queryList("queue-tracestorage-query-list", paramsMap, QueueTraceStorageVo.class);
	}

	@Override
	public List<QueueTraceStorageVo> queryTraceStoragesByQueue(QueueTraceStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-tracestorage-query-tracestorage", paramsMap, QueueTraceStorageVo.class);
	}

	@Override 
	public PageVo<QueueTraceStorageVo> queryQueueByTraceStorage(QueueTraceStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("traceStorage_Id", vo.getTraceStorage_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-tracestorage-query-queue", paramsMap, page, rows, QueueTraceStorageVo.class);
	}

	@Override
	public void insertQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueTraceStorageMd.class);
	}

	@Override
	public void insertsQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		for (QueueTraceStorageVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueTraceStorageMd.class);
		}
	}

	@Override
	public void updateQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueTraceStorageMd.class);
	}

	@Override
	public void updatesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		for (QueueTraceStorageVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueTraceStorageMd.class);
		}
	}

	@Override
	public void deleteQueueTraceStorage(QueueTraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueTraceStorageMd.class);
	}

	@Override
	public void deletesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception {
		for (QueueTraceStorageVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueTraceStorageMd.class);
		}
	}

	@Override
	public void deleteQueueTraceStorageByTraceStorageId(QueueTraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueTraceStorageMd.class, new String[] { "traceStorage_Id" });
	}

	@Override
	public void deleteQueueTraceStorageByQueueNm(QueueTraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueTraceStorageMd.class, new String[] { "queue_Nm" });
	}
}
