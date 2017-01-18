package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueDbDao;
import org.fl.noodlenotify.console.domain.QueueDbMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueDbDao")
public class QueueDbDaoImpl implements QueueDbDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueDbVo> queryQueueDbPage(QueueDbVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("db_Id", vo.getDb_Id());
		return dynamicSqlTemplate.queryPage("queue-db-query-list", paramsMap, page, rows, QueueDbVo.class);
	}
	
	@Override
	public List<QueueDbVo> queryQueueDbList(QueueDbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("db_Id", vo.getDb_Id());
		return dynamicSqlTemplate.queryList("queue-db-query-list", paramsMap, QueueDbVo.class);
	}
	
	@Override
	public PageVo<QueueDbVo> queryQueueDbIncludePage(QueueDbVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-db-query-includepage", paramsMap, page, rows, QueueDbVo.class);
	}

	@Override
	public PageVo<QueueDbVo> queryQueueDbExcludePage(QueueDbVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-db-query-excludepage", paramsMap, page, rows, QueueDbVo.class);
	}
	
	@Override
	public List<QueueDbVo> queryQueueDbIncludeList(QueueDbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? vo.getName() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-db-query-includelist", paramsMap, QueueDbVo.class);
	}
	
	@Override
	public List<QueueDbVo> queryQueueByDbListTree(QueueDbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("db_Id", vo.getDb_Id());
		paramsMap.put("queue_Nm",  vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-db-query-queue-tree", paramsMap, QueueDbVo.class);
	}
	
	@Override
	public List<QueueDbVo> queryDbByQueue(QueueDbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-db-query-db", paramsMap, QueueDbVo.class);
	}
	
	@Override
	public List<QueueDbVo> queryDbByQueueExclude(QueueDbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-db-query-db-exclude", paramsMap, QueueDbVo.class);
	}

	@Override
	public PageVo<QueueDbVo> queryQueueByDb(QueueDbVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("db_Id", vo.getDb_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-db-query-db", paramsMap, page, rows, QueueDbVo.class);
	}

	@Override
	public void insertQueueDb(QueueDbVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueDbMd.class);
	}

	@Override
	public void insertsQueueDb(QueueDbVo[] vos) throws Exception {
		for (QueueDbVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueDbMd.class);
		}
	}

	@Override
	public void updateQueueDb(QueueDbVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueDbMd.class);
	}

	@Override
	public void updatesQueueDb(QueueDbVo[] vos) throws Exception {
		for (QueueDbVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueDbMd.class);
		}
	}

	@Override
	public void deleteQueueDb(QueueDbVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueDbMd.class);
	}

	@Override
	public void deletesQueueDb(QueueDbVo[] vos) throws Exception {
		for (QueueDbVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueDbMd.class);
		}
	}

	@Override
	public void deleteQueueDbByDbId(QueueDbVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueDbMd.class, new String[] { "db_Id" });
	}

	@Override
	public void deleteQueueDbByQueueNm(QueueDbVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueDbMd.class, new String[] { "queue_Nm" });
	}
}
