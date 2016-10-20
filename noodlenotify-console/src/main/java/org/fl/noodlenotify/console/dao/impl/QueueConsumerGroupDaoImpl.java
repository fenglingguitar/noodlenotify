package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueConsumerGroupDao;
import org.fl.noodlenotify.console.domain.QueueConsumerGroupMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueConsumerGroupDao")
public class QueueConsumerGroupDaoImpl implements QueueConsumerGroupDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupPage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumer_Num", vo.getConsumer_Num());
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		return dynamicSqlTemplate.queryPage("queue-consumergroup-query-list", paramsMap, page, rows, QueueConsumerGroupVo.class);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupIncludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("consumer_Num", vo.getConsumer_Num() != null ? vo.getConsumer_Num() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-consumergroup-query-includelist", paramsMap, page, rows, QueueConsumerGroupVo.class);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueueConsumerGroupExcludePage(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getConsumerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-consumergroup-query-excludelist", paramsMap, page, rows, QueueConsumerGroupVo.class);
	}

	@Override
	public List<QueueConsumerGroupVo> queryQueueConsumerGroupList(QueueConsumerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("consumer_Num", vo.getConsumer_Num() != null ? vo.getConsumer_Num() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? vo.getConsumerGroup_Nm() : null);
		return dynamicSqlTemplate.queryList("queue-consumergroup-query-list", paramsMap, QueueConsumerGroupVo.class);
	}
	
	@Override
	public List<QueueConsumerGroupVo> queryQueueByConsumerGroupList(QueueConsumerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-consumergroup-query-byqueue", paramsMap, QueueConsumerGroupVo.class);
	}

	@Override
	public List<QueueConsumerGroupVo> queryConsumerGroupsByQueue(QueueConsumerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-consumergroup-query-consumer", paramsMap, QueueConsumerGroupVo.class);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryQueuePageByConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-consumergroup-query-queue", paramsMap, page, rows, QueueConsumerGroupVo.class);
	}

	@Override
	public PageVo<QueueConsumerGroupVo> queryConsumerPageByQueueConsumerGroup(QueueConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-consumergroup-query-consumer", paramsMap, page, rows, QueueConsumerGroupVo.class);
	}

	@Override
	public List<QueueConsumerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", queueNm);
		return dynamicSqlTemplate.queryList("queue-consumergroupnum-query-unuselist", paramsMap, QueueConsumerGroupVo.class);
	}

	@Override
	public List<QueueConsumerGroupVo> queryConsumerGroupList(QueueConsumerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-consumergroup-query-consumergroup", paramsMap, QueueConsumerGroupVo.class);
	}

	@Override
	public void insertQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueConsumerGroupMd.class);
	}

	@Override
	public void insertsQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		for (QueueConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueConsumerGroupMd.class);
		}
	}

	@Override
	public void updateQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueConsumerGroupMd.class);

	}

	@Override
	public void updatesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		for (QueueConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueConsumerGroupMd.class);
		}
	}

	@Override
	public void deleteQueueConsumerGroup(QueueConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueConsumerGroupMd.class);
	}

	@Override
	public void deletesQueueConsumerGroup(QueueConsumerGroupVo[] vos) throws Exception {
		for (QueueConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueConsumerGroupMd.class);
		}
	}

	@Override
	public void deleteQueueConsumerGroupByConsumerGroupNm(QueueConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueConsumerGroupMd.class, new String[] { "consumerGroup_Nm" });
	}

	@Override
	public void deleteQueueConsumerGroupByQueueNm(QueueConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueConsumerGroupMd.class, new String[] { "queue_Nm" });
	}

}
