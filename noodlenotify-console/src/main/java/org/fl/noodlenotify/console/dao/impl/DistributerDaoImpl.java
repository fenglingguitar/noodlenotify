package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.domain.DistributerMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("cistributerDao")
public class DistributerDaoImpl implements DistributerDao {
	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<DistributerVo> queryDistributerPage(DistributerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("distributer_Id", vo.getDistributer_Id() > 0 ? vo.getDistributer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("distributer-query-list", paramsMap, page, rows, DistributerVo.class);
	}

	@Override
	public List<DistributerVo> queryDistributerList(DistributerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("distributer_Id", vo.getDistributer_Id() > 0 ? vo.getDistributer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("distributer-query-list", paramsMap, DistributerVo.class);
	}

	@Override
	public void insertDistributer(DistributerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, DistributerMd.class);
	}

	@Override
	public void insertsDistributer(DistributerVo[] vos) throws Exception {
		for (DistributerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, DistributerMd.class);
		}
	}

	@Override
	public DistributerMd insertOrUpdate(DistributerVo vo) throws Exception {
		return dynamicSqlTemplate.insertOrUpdate(vo, DistributerMd.class);
	}

	@Override
	public void updateDistributer(DistributerVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, DistributerMd.class);

	}

	@Override
	public void updatesDistributer(DistributerVo[] vos) throws Exception {
		for (DistributerVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, DistributerMd.class);
		}
	}

	@Override
	public void deleteDistributer(DistributerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, DistributerMd.class);
	}

	@Override
	public void deletesDistributer(DistributerVo[] vos) throws Exception {
		for (DistributerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, DistributerMd.class);
		}
	}

	@Override
	public void updateDistributerSystemStatus(DistributerVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, DistributerMd.class, new String[] { "system_Status" });
	}

	@Override
	public boolean ifDistributerValid(long distributerId) throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setDistributer_Id(distributerId);
		List<DistributerVo> queryDistributers = queryDistributerList(distributerVo);
		if (queryDistributers == null || queryDistributers.size() == 0) {
			return false;
		}
		return queryDistributers.get(0).getManual_Status() == ConsoleConstants.MANUAL_STATUS_VALID;
	}
}
