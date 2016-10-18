package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.domain.QueueDistributerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueDistributerDao")
public class QueueDistributerDaoImpl implements QueueDistributerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerPage(QueueDistributerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("distributer_Id", vo.getDistributer_Id() != null ? vo.getDistributer_Id() : null);
		return dynamicSqlTemplate.queryPage("queue-distributer-query-list", paramsMap, page, rows, QueueDistributerVo.class);
	}

	@Override
	public List<QueueDistributerVo> queryQueueDistributerList(QueueDistributerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("distributer_Id", vo.getDistributer_Id() != null ? vo.getDistributer_Id() : null);
		return dynamicSqlTemplate.queryList("queue-distributer-query-list", paramsMap, QueueDistributerVo.class);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerIncludePage(QueueDistributerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-distributer-query-includelist", paramsMap, page, rows, QueueDistributerVo.class);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueueDistributerExcludePage(QueueDistributerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-distributer-query-excludelist", paramsMap, page, rows, QueueDistributerVo.class);
	}

	@Override
	public List<QueueDistributerVo> queryQueuesByDistributer(QueueDistributerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("distributer_Id", vo.getDistributer_Id() != null ? vo.getDistributer_Id() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-distributer-query-queue", paramsMap, QueueDistributerVo.class);
	}
	
	@Override
	public List<QueueDistributerVo> queryQueuesByDistributerTree(QueueDistributerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("distributer_Id", vo.getDistributer_Id() != null ? vo.getDistributer_Id() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryList("queue-distributer-query-queue-tree", paramsMap, QueueDistributerVo.class);
	}

	@Override
	public PageVo<QueueDistributerVo> queryQueuePageByDistributer(QueueDistributerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("distributer_Id", vo.getDistributer_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-distributer-query-queue", paramsMap, page, rows, QueueDistributerVo.class);
	}
	
	@Override
	public void insertQueueDistributer(QueueDistributerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueDistributerMd.class);
	}

	@Override
	public void insertsQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		for (QueueDistributerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueDistributerMd.class);
		}
	}

	@Override
	public void updateQueueDistributer(QueueDistributerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueDistributerMd.class);
	}

	@Override
	public void updatesQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		for (QueueDistributerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueDistributerMd.class);
		}
	}

	@Override
	public void deleteQueueDistributer(QueueDistributerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueDistributerMd.class);
	}

	@Override
	public void deletesQueueDistributer(QueueDistributerVo[] vos) throws Exception {
		for (QueueDistributerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueDistributerMd.class);
		}
	}
	
	@Override
	public void deleteQueueDistributerByDistributerId(QueueDistributerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueDistributerMd.class, new String[]{"distributer_Id"});
	}
	
	@Override
	public void deleteQueueDistributerByQueueNm(QueueDistributerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueDistributerMd.class, new String[]{"queue_Nm"});
	}

}
