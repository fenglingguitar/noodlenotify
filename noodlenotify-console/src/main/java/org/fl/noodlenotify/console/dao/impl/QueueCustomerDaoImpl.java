package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.domain.QueueCustomerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueCustomerDao")
public class QueueCustomerDaoImpl implements QueueCustomerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerPage(QueueCustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customer_Id", vo.getCustomer_Id());
		return dynamicSqlTemplate.queryPage("queue-customer-query-list", paramsMap, page, rows, QueueCustomerVo.class);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerIncludePage(QueueCustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customer-query-includelist", paramsMap, page, rows, QueueCustomerVo.class);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueueCustomerExcludePage(QueueCustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customer-query-excludelist", paramsMap, page, rows, QueueCustomerVo.class);
	}

	@Override
	public List<QueueCustomerVo> queryQueueCustomerList(QueueCustomerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customer_Id", vo.getCustomer_Id());
		return dynamicSqlTemplate.queryList("queue-customer-query-list", paramsMap, QueueCustomerVo.class);
	}

	@Override
	public List<QueueCustomerVo> queryCustomersByQueue(QueueCustomerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-customer-query-customer", paramsMap, QueueCustomerVo.class);
	}

	@Override
	public List<QueueCustomerVo> queryQueuesByCustomer(QueueCustomerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customer_Id", vo.getCustomer_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-customer-query-queue", paramsMap, QueueCustomerVo.class);
	}

	@Override
	public PageVo<QueueCustomerVo> queryQueuePageByCustomer(QueueCustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customer_Id", vo.getCustomer_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customer-query-queue", paramsMap, page, rows, QueueCustomerVo.class);
	}

	@Override
	public void insertQueueCustomer(QueueCustomerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueCustomerMd.class);
	}

	@Override
	public void insertsQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		for (QueueCustomerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueCustomerMd.class);
		}
	}

	@Override
	public void updateQueueCustomer(QueueCustomerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueCustomerMd.class);

	}

	@Override
	public void updatesQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		for (QueueCustomerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueCustomerMd.class);
		}
	}

	@Override
	public void deleteQueueCustomer(QueueCustomerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueCustomerMd.class);
	}

	@Override
	public void deletesQueueCustomer(QueueCustomerVo[] vos) throws Exception {
		for (QueueCustomerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueCustomerMd.class);
		}
	}

	@Override
	public void deleteQueueCustomerByCustomerId(QueueCustomerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueCustomerMd.class, new String[] { "customer_Id" });
	}

	@Override
	public void deleteQueueCustomerByQueueNm(QueueCustomerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueCustomerMd.class, new String[] { "queue_Nm" });
	}
}
