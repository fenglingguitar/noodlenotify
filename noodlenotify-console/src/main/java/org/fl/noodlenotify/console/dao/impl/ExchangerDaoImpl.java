package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.domain.ExchangerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("exchangerDao")
public class ExchangerDaoImpl implements ExchangerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<ExchangerVo> queryExchangerPage(ExchangerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("exchanger-query-list", paramsMap, page, rows, ExchangerVo.class);
	}

	@Override
	public List<ExchangerVo> queryExchangerList(ExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("exchanger_Id", vo.getExchanger_Id() > 0 ? vo.getExchanger_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("exchanger-query-list", paramsMap, ExchangerVo.class);
	}

	@Override
	public List<ExchangerVo> queryCheckExchangerList(ExchangerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("query-check-exchanger-list", paramsMap, ExchangerVo.class);
	}

	@Override
	public void insertExchanger(ExchangerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, ExchangerMd.class);
	}

	@Override
	public void insertsExchanger(ExchangerVo[] vos) throws Exception {
		for (ExchangerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, ExchangerMd.class);
		}
	}

	@Override
	public ExchangerMd insertOrUpdate(ExchangerVo vo) throws Exception {
		return dynamicSqlTemplate.insertOrUpdate(vo, ExchangerMd.class);
	}

	@Override
	public void updateExchanger(ExchangerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, ExchangerMd.class);

	}

	@Override
	public void updatesExchanger(ExchangerVo[] vos) throws Exception {
		for (ExchangerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, ExchangerMd.class);
		}
	}

	@Override
	public void deleteExchanger(ExchangerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, ExchangerMd.class);
	}

	@Override
	public void deletesExchanger(ExchangerVo[] vos) throws Exception {
		for (ExchangerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, ExchangerMd.class);
		}
	}

	@Override
	public void updateExchangerSystemStatus(ExchangerVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, ExchangerMd.class, new String[] { "system_Status" });
	}

	@Override
	public boolean ifExchangerValid(long exchangerId) throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setExchanger_Id(exchangerId);
		List<ExchangerVo> queryExchangers = queryExchangerList(exchangerVo);
		if (queryExchangers == null || queryExchangers.size() == 0) {
			return false;
		}
		return queryExchangers.get(0).getManual_Status() == ConsoleConstants.MANUAL_STATUS_VALID;
	}
}
