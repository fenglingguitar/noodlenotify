package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.domain.QueueMsgStorageMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueMsgStorageDao")
public class QueueMsgStorageDaoImpl implements QueueMsgStorageDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStoragePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id());
		return dynamicSqlTemplate.queryPage("queue-msgstorage-query-list", paramsMap, page, rows, QueueMsgStorageVo.class);
	}

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageIncludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgstorage-query-includelist", paramsMap, page, rows, QueueMsgStorageVo.class);
	}

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageExcludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgstorage-query-excludelist", paramsMap, page, rows, QueueMsgStorageVo.class);
	}

	@Override
	public List<QueueMsgStorageVo> queryQueueMsgStorageList(QueueMsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id());
		return dynamicSqlTemplate.queryList("queue-msgstorage-query-list", paramsMap, QueueMsgStorageVo.class);
	}

	@Override
	public List<QueueMsgStorageVo> queryQueueByMsgstorageList(QueueMsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgstorage-query-queue", paramsMap, QueueMsgStorageVo.class);
	}
	
	@Override
	public List<QueueMsgStorageVo> queryQueueByMsgstorageListTree(QueueMsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id());
		paramsMap.put("queue_Nm",  vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgstorage-query-queue-tree", paramsMap, QueueMsgStorageVo.class);
	}
	
	@Override
	public List<QueueMsgStorageVo> queryMsgStoragesByQueue(QueueMsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgstorage-query-msgstorage", paramsMap, QueueMsgStorageVo.class);
	}
	
	@Override
	public List<QueueMsgStorageVo> queryMsgStoragesByQueueExclude(QueueMsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-msgstorage-query-msgstorage-exclude", paramsMap, QueueMsgStorageVo.class);
	}

	@Override
	public PageVo<QueueMsgStorageVo> queryQueueByMsgstorage(QueueMsgStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-msgstorage-query-queue", paramsMap, page, rows, QueueMsgStorageVo.class);
	}

	@Override
	public void insertQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueMsgStorageMd.class);
	}

	@Override
	public void insertsQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		for (QueueMsgStorageVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueMsgStorageMd.class);
		}
	}

	@Override
	public void updateQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueMsgStorageMd.class);
	}

	@Override
	public void updatesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		for (QueueMsgStorageVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueMsgStorageMd.class);
		}
	}

	@Override
	public void deleteQueueMsgStorage(QueueMsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueMsgStorageMd.class);
	}

	@Override
	public void deletesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception {
		for (QueueMsgStorageVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueMsgStorageMd.class);
		}
	}

	@Override
	public void deleteQueueMsgStorageByMsgStorageId(QueueMsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgStorageMd.class, new String[] { "msgStorage_Id" });
	}

	@Override
	public void deleteQueueMsgStorageByQueueNm(QueueMsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueMsgStorageMd.class, new String[] { "queue_Nm" });
	}
}
