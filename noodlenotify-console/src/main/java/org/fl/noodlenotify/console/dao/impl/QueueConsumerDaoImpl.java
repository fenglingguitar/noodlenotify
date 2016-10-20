package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueConsumerDao;
import org.fl.noodlenotify.console.domain.QueueConsumerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueConsumerDao")
public class QueueConsumerDaoImpl implements QueueConsumerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerPage(QueueConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumer_Id", vo.getConsumer_Id());
		return dynamicSqlTemplate.queryPage("queue-consumer-query-list", paramsMap, page, rows, QueueConsumerVo.class);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerIncludePage(QueueConsumerVo vo, int page, int rows) throws Exception {
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
		return dynamicSqlTemplate.queryPage("queue-consumer-query-includelist", paramsMap, page, rows, QueueConsumerVo.class);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueueConsumerExcludePage(QueueConsumerVo vo, int page, int rows) throws Exception {
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
		return dynamicSqlTemplate.queryPage("queue-consumer-query-excludelist", paramsMap, page, rows, QueueConsumerVo.class);
	}

	@Override
	public List<QueueConsumerVo> queryQueueConsumerList(QueueConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumer_Id", vo.getConsumer_Id());
		return dynamicSqlTemplate.queryList("queue-consumer-query-list", paramsMap, QueueConsumerVo.class);
	}

	@Override
	public List<QueueConsumerVo> queryConsumersByQueue(QueueConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-consumer-query-consumer", paramsMap, QueueConsumerVo.class);
	}

	@Override
	public List<QueueConsumerVo> queryQueuesByConsumer(QueueConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumer_Id", vo.getConsumer_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-consumer-query-queue", paramsMap, QueueConsumerVo.class);
	}

	@Override
	public PageVo<QueueConsumerVo> queryQueuePageByConsumer(QueueConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumer_Id", vo.getConsumer_Id());
		paramsMap.put("queue_Nm", vo.getQueue_Nm());
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-consumer-query-queue", paramsMap, page, rows, QueueConsumerVo.class);
	}

	@Override
	public void insertQueueConsumer(QueueConsumerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, QueueConsumerMd.class);
	}

	@Override
	public void insertsQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		for (QueueConsumerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, QueueConsumerMd.class);
		}
	}

	@Override
	public void updateQueueConsumer(QueueConsumerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueConsumerMd.class);

	}

	@Override
	public void updatesQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		for (QueueConsumerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueConsumerMd.class);
		}
	}

	@Override
	public void deleteQueueConsumer(QueueConsumerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueConsumerMd.class);
	}

	@Override
	public void deletesQueueConsumer(QueueConsumerVo[] vos) throws Exception {
		for (QueueConsumerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueConsumerMd.class);
		}
	}

	@Override
	public void deleteQueueConsumerByConsumerId(QueueConsumerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueConsumerMd.class, new String[] { "consumer_Id" });
	}

	@Override
	public void deleteQueueConsumerByQueueNm(QueueConsumerVo vo) throws Exception {
		dynamicSqlTemplate.deleteNoById(vo, QueueConsumerMd.class, new String[] { "queue_Nm" });
	}
}
