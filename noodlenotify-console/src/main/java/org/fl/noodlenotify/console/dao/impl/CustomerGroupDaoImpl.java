package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.fl.noodlenotify.console.dao.CustomerGroupDao;
import org.fl.noodlenotify.console.domain.CustomerGroupMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.CustomerGroupVo;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("customerGroupDao")
public class CustomerGroupDaoImpl implements CustomerGroupDao {
	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<CustomerGroupVo> queryCustomerGroupPage(CustomerGroupVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getCustomerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("customer-group-query-list", paramsMap, page, rows, CustomerGroupVo.class);
	}

	@Override
	public List<CustomerGroupVo> queryCustomerGroupList(CustomerGroupVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getCustomerGroup_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("customer-group-query-list", paramsMap, CustomerGroupVo.class);
	}
	
	@Override
	public PageVo<CustomerVo> queryCustomerIncludePage(CustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ?  vo.getCustomerGroup_Nm(): null);
		return dynamicSqlTemplate.queryPage("customergroup-query-includelist", paramsMap, page, rows, CustomerVo.class);
	}

	@Override
	public PageVo<CustomerVo> queryCustomerExcludePage(CustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() != null ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("customergroup-query-excludelist", paramsMap, page, rows, CustomerVo.class);
	}

	@Override
	public void insertCustomerGroup(CustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, CustomerGroupMd.class);
	}

	@Override
	public void insertsCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		for (CustomerGroupVo vo : vos) {
			dynamicSqlTemplate.insert(vo, CustomerGroupMd.class);
		}
	}

	@Override
	public void updateCustomerGroup(CustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, CustomerGroupMd.class);
	}

	@Override
	public void updatesCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		for (CustomerGroupVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, CustomerGroupMd.class);
		}
	}

	@Override
	public void deleteCustomerGroup(CustomerGroupVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, CustomerGroupMd.class);
	}

	@Override
	public void deletesCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		for (CustomerGroupVo vo : vos) {
			dynamicSqlTemplate.delete(vo, CustomerGroupMd.class);
		}
	}
}
