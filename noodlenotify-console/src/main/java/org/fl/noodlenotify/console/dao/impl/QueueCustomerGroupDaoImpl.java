package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueCustomerGroupDao;
import org.fl.noodlenotify.console.domain.QueueCustomerGroupMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueCustomerGroupDao")
public class QueueCustomerGroupDaoImpl implements QueueCustomerGroupDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupPage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customer_Num", vo.getCustomer_Num());
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		return dynamicSqlTemplate.queryPage("queue-customergroup-query-list", paramsMap, page, rows, QueueCustomerGroupVo.class);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupIncludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("customer_Num", vo.getCustomer_Num() != null ? vo.getCustomer_Num() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customergroup-query-includelist", paramsMap, page, rows, QueueCustomerGroupVo.class);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupExcludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getCustomerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customergroup-query-excludelist", paramsMap, page, rows, QueueCustomerGroupVo.class);
	}

	@Override
	public List<QueueCustomerGroupVo> queryQueueCustomerGroupList(QueueCustomerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("customer_Num", vo.getCustomer_Num() != null ? vo.getCustomer_Num() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? vo.getCustomerGroup_Nm() : null);
		return dynamicSqlTemplate.queryList("queue-customergroup-query-list", paramsMap, QueueCustomerGroupVo.class);
	}
	
	@Override
	public List<QueueCustomerGroupVo> queryQueueByCustomerGroupList(QueueCustomerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-customergroup-query-byqueue", paramsMap, QueueCustomerGroupVo.class);
	}

	@Override
	public List<QueueCustomerGroupVo> queryCustomerGroupsByQueue(QueueCustomerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-customergroup-query-customer", paramsMap, QueueCustomerGroupVo.class);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryQueuePageByCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customergroup-query-queue", paramsMap, page, rows, QueueCustomerGroupVo.class);
	}

	@Override
	public PageVo<QueueCustomerGroupVo> queryCustomerPageByQueueCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-customergroup-query-customer", paramsMap, page, rows, QueueCustomerGroupVo.class);
	}

	@Override
	public List<QueueCustomerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", queueNm);
		return dynamicSqlTemplate.queryList("queue-customergroupnum-query-unuselist", paramsMap, QueueCustomerGroupVo.class);
	}

	@Override
	public List<QueueCustomerGroupVo> queryCustomerGroupList(QueueCustomerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-customergroup-query-customergroup", paramsMap, QueueCustomerGroupVo.class);
	}

	@Override
	public void insertQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueCustomerGroupMd.class);
	}

	@Override
	public void insertsQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		for (QueueCustomerGroupVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueCustomerGroupMd.class);
		}
	}

	@Override
	public void updateQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueCustomerGroupMd.class);

	}

	@Override
	public void updatesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		for (QueueCustomerGroupVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueCustomerGroupMd.class);
		}
	}

	@Override
	public void deleteQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueCustomerGroupMd.class);
	}

	@Override
	public void deletesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception {
		for (QueueCustomerGroupVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueCustomerGroupMd.class);
		}
	}

	@Override
	public void deleteQueueCustomerGroupByCustomerGroupNm(QueueCustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueCustomerGroupMd.class, new String[] { "customerGroup_Nm" });
	}

	@Override
	public void deleteQueueCustomerGroupByQueueNm(QueueCustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueCustomerGroupMd.class, new String[] { "queue_Nm" });
	}

}
