package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.domain.QueueExchangerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueExchangerDao")
public class QueueExchangerDaoImpl implements QueueExchangerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerPage(QueueExchangerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		return dynamicSqlTemplate.queryPage("queue-exchanger-query-list", paramsMap, page, rows, QueueExchangerVo.class);
	}

	@Override
	public List<QueueExchangerVo> queryQueueExchangerList(QueueExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		return dynamicSqlTemplate.queryList("queue-exchanger-query-list", paramsMap, QueueExchangerVo.class);
	}

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerIncludePage(QueueExchangerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-exchanger-query-includelist", paramsMap, page, rows, QueueExchangerVo.class);
	}

	@Override
	public PageVo<QueueExchangerVo> queryQueueExchangerExcludePage(QueueExchangerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-exchanger-query-excludelist", paramsMap, page, rows, QueueExchangerVo.class);
	}

	@Override
	public List<QueueExchangerVo> queryQueuesByExchanger(QueueExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-exchanger-query-queue", paramsMap, QueueExchangerVo.class);
	}
	
	@Override
	public List<QueueExchangerVo> queryQueuesByExchangerTree(QueueExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryList("queue-exchanger-query-queue-tree", paramsMap, QueueExchangerVo.class);
	}

	@Override
	public List<QueueExchangerVo> queryExchangersByQueue(QueueExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-exchanger-query-exchanger", paramsMap, QueueExchangerVo.class);
	}

	@Override
	public PageVo<QueueExchangerVo> queryQueuePageByExchanger(QueueExchangerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("exchanger_Id", vo.getExchanger_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-exchanger-query-queue", paramsMap, page, rows, QueueExchangerVo.class);
	}

	@Override
	public void insertQueueExchanger(QueueExchangerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueExchangerMd.class);
	}

	@Override
	public void insertsQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		for (QueueExchangerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueExchangerMd.class);
		}
	}

	@Override
	public void updateQueueExchanger(QueueExchangerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueExchangerMd.class);
	}

	@Override
	public void updatesQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		for (QueueExchangerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueExchangerMd.class);
		}
	}

	@Override
	public void deleteQueueExchanger(QueueExchangerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueExchangerMd.class);
	}

	@Override
	public void deletesQueueExchanger(QueueExchangerVo[] vos) throws Exception {
		for (QueueExchangerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueExchangerMd.class);
		}
	}

	@Override
	public void deleteQueueExchangerByExchangerId(QueueExchangerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueExchangerMd.class, new String[] { "exchanger_Id" });
	}

	@Override
	public void deleteQueueExchangerByQueueNm(QueueExchangerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueExchangerMd.class, new String[] { "queue_Nm" });
	}
}
