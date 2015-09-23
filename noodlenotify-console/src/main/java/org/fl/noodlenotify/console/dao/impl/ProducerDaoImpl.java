package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.domain.ProducerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("producerDao")
public class ProducerDaoImpl implements ProducerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<ProducerVo> queryProducerPage(ProducerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("producer_Id", vo.getProducer_Id() > 0 ? vo.getProducer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("producer-query-list", paramsMap, page, rows, ProducerVo.class);
	}

	@Override
	public List<ProducerVo> queryProducerList(ProducerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("producer_Id", vo.getProducer_Id() > 0 ? vo.getProducer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("producer-query-list", paramsMap, ProducerVo.class);
	}

	@Override
	public List<ProducerVo> queryCheckProducerList(ProducerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("query-check-producer-list", paramsMap, ProducerVo.class);
	}

	@Override
	public void insertProducer(ProducerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, ProducerMd.class);
	}

	@Override
	public void insertsProducer(ProducerVo[] vos) throws Exception {
		for (ProducerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, ProducerMd.class);
		}
	}

	@Override
	public void updateProducer(ProducerVo vo) throws Exception {
		dynamicSqlTemplate.update(vo, ProducerMd.class);

	}

	@Override
	public void updatesProducer(ProducerVo[] vos) throws Exception {
		for (ProducerVo vo : vos) {
			dynamicSqlTemplate.update(vo, ProducerMd.class);
		}
	}

	@Override
	public void deleteProducer(ProducerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, ProducerMd.class);
	}

	@Override
	public void deletesProducer(ProducerVo[] vos) throws Exception {
		for (ProducerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, ProducerMd.class);
		}
	}

	@Override
	public ProducerMd insertOrUpdate(ProducerVo vo) throws Exception {
		return dynamicSqlTemplate.insertOrUpdate(vo, ProducerMd.class);
	}

	@Override
	public void updateProducerSystemStatus(ProducerVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, ProducerMd.class, new String[] { "system_Status" });
	}

	@Override
	public boolean ifProducerValid(long producerId) throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setProducer_Id(producerId);
		List<ProducerVo> queryProducers = queryProducerList(producerVo);
		if (queryProducers == null || queryProducers.size() == 0) {
			return false;
		}
		return queryProducers.get(0).getManual_Status() == ConsoleConstants.MANUAL_STATUS_VALID;
	}
}
