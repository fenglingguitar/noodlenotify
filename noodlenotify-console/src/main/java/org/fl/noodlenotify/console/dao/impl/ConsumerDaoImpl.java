package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ConsumerDao;
import org.fl.noodlenotify.console.domain.ConsumerMd;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("consumerDao")
public class ConsumerDaoImpl implements ConsumerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<ConsumerVo> queryConsumerPage(ConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumer_Id", vo.getConsumer_Id() != null ? vo.getConsumer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getConsumerGroup_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryPage("consumer-query-list", paramsMap, page, rows, ConsumerVo.class);
	}

	@Override
	public PageVo<ConsumerVo> queryConsumerPageByEqual(ConsumerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumer_Id", vo.getConsumer_Id() != null ? vo.getConsumer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? vo.getName() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm());
		return dynamicSqlTemplate.queryPage("consumer-query-list-equal", paramsMap, page, rows, ConsumerVo.class);
	}

	@Override
	public List<ConsumerVo> queryConsumerList(ConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumer_Id", vo.getConsumer_Id() != null ? vo.getConsumer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null? vo.getManual_Status() : null);
		paramsMap.put("consumerGroup_Nm", vo.getConsumerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getConsumerGroup_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryList("consumer-query-list", paramsMap, ConsumerVo.class);
	}

	@Override
	public void insertConsumer(ConsumerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, ConsumerMd.class);
	}

	@Override
	public void insertsConsumer(ConsumerVo[] vos) throws Exception {
		for (ConsumerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, ConsumerMd.class);
		}
	}

	@Override
	public ConsumerMd insertOrUpdate(ConsumerVo vo) throws Exception {
		return dynamicSqlTemplate.insertOrUpdate(vo, ConsumerMd.class);
	}

	@Override
	public void updateConsumer(ConsumerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, ConsumerMd.class);
	}

	@Override
	public void updatesConsumer(ConsumerVo[] vos) throws Exception {
		for (ConsumerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, ConsumerMd.class);
		}
	}

	@Override
	public void updateConsumerGroupNmToNull(String consumerGroupNm) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("consumerGroup_Nm", consumerGroupNm);
		dynamicSqlTemplate.updateSql("consumer-update-consumernm", paramsMap);
	}

	@Override
	public void deleteConsumer(ConsumerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, ConsumerMd.class);
	}

	@Override
	public void deletesConsumer(ConsumerVo[] vos) throws Exception {
		for (ConsumerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, ConsumerMd.class);
		}
	}

	@Override
	public void updateConsumerSystemStatus(ConsumerVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, ConsumerMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deletegroupConsumer(ConsumerVo vo) throws Exception {
		vo.setConsumerGroup_Nm(null);
		dynamicSqlTemplate.update(vo, ConsumerMd.class);
	}

	@Override
	public void deletesgroupConsumer(ConsumerVo[] vos) throws Exception {
		for (ConsumerVo vo : vos) {
			vo.setConsumerGroup_Nm(null);
			dynamicSqlTemplate.update(vo, ConsumerMd.class);
		}
	}

	@Override
	public List<ConsumerVo> queryConsumerToOnlineList(ConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("beat_Time", vo.getBeat_Time());
		paramsMap.put("system_Status", ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		return dynamicSqlTemplate.queryList("consumer-query-toonline-list", paramsMap, ConsumerVo.class);
	}

	@Override
	public List<ConsumerVo> queryConsumerToOfflineList(ConsumerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("beat_Time", vo.getBeat_Time());
		paramsMap.put("system_Status", ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		return dynamicSqlTemplate.queryList("consumer-query-tooffline-list", paramsMap, ConsumerVo.class);
	}
}
