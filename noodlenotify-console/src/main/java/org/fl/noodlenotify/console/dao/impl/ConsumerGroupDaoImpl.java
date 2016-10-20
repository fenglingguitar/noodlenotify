package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.fl.noodlenotify.console.dao.ConsumerGroupDao;
import org.fl.noodlenotify.console.domain.ConsumerGroupMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.ConsumerGroupVo;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("consumerGroupDao")
public class ConsumerGroupDaoImpl implements ConsumerGroupDao {
	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<ConsumerGroupVo> queryConsumerGroupPage(ConsumerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getConsumerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("consumer-group-query-list", paramsMap, page, rows, ConsumerGroupVo.class);
	}

	@Override
	public List<ConsumerGroupVo> queryConsumerGroupList(ConsumerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getConsumerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("consumer-group-query-list", paramsMap, ConsumerGroupVo.class);
	}
	
	@Override
	public PageVo<ConsumerVo> queryConsumerIncludePage(ConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ?  vo.getConsumerGroup_Nm(): null);
		return dynamicSqlTemplate.queryPage("consumergroup-query-includelist", paramsMap, page, rows, ConsumerVo.class);
	}

	@Override
	public PageVo<ConsumerVo> queryConsumerExcludePage(ConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("consumergroup-query-excludelist", paramsMap, page, rows, ConsumerVo.class);
	}

	@Override
	public void insertConsumerGroup(ConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, ConsumerGroupMd.class);
	}

	@Override
	public void insertsConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		for (ConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.insert(vo, ConsumerGroupMd.class);
		}
	}

	@Override
	public void updateConsumerGroup(ConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, ConsumerGroupMd.class);
	}

	@Override
	public void updatesConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		for (ConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, ConsumerGroupMd.class);
		}
	}

	@Override
	public void deleteConsumerGroup(ConsumerGroupVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, ConsumerGroupMd.class);
	}

	@Override
	public void deletesConsumerGroup(ConsumerGroupVo[] vos) throws Exception {
		for (ConsumerGroupVo vo : vos) {
			dynamicSqlTemplate.delete(vo, ConsumerGroupMd.class);
		}
	}
}
