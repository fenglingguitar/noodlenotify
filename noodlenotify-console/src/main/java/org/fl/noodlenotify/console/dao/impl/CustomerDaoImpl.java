package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.domain.CustomerMd;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("customerDao")
public class CustomerDaoImpl implements CustomerDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<CustomerVo> queryCustomerPage(CustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customer_Id", vo.getCustomer_Id() > 0 ? vo.getCustomer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getCustomerGroup_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryPage("customer-query-list", paramsMap, page, rows, CustomerVo.class);
	}

	@Override
	public PageVo<CustomerVo> queryCustomerPageByEqual(CustomerVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customer_Id", vo.getCustomer_Id() > 0 ? vo.getCustomer_Id() : null);
		paramsMap.put("name", vo.getName());
		paramsMap.put("ip", vo.getIp());
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm());
		return dynamicSqlTemplate.queryPage("customer-query-list-equal", paramsMap, page, rows, CustomerVo.class);
	}

	@Override
	public List<CustomerVo> queryCustomerList(CustomerVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customer_Id", vo.getCustomer_Id() > 0 ? vo.getCustomer_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("url", vo.getUrl() != null ? vo.getUrl() : null);
		paramsMap.put("type", vo.getType() != null ? vo.getType() : null);
		paramsMap.put("check_Port", vo.getCheck_Port() > 0 ? vo.getCheck_Port() : null);
		paramsMap.put("check_Type", vo.getCheck_Type() != null ? vo.getCheck_Type() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		paramsMap.put("customerGroup_Nm", vo.getCustomerGroup_Nm() != null ? (new StringBuilder().append("%").append(vo.getCustomerGroup_Nm()).append("%")).toString() : null);
		return dynamicSqlTemplate.queryList("customer-query-list", paramsMap, CustomerVo.class);
	}

	@Override
	public void insertCustomer(CustomerVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, CustomerMd.class);
	}

	@Override
	public void insertsCustomer(CustomerVo[] vos) throws Exception {
		for (CustomerVo vo : vos) {
			dynamicSqlTemplate.insert(vo, CustomerMd.class);
		}
	}

	@Override
	public CustomerMd insertOrUpdate(CustomerVo vo) throws Exception {
		return dynamicSqlTemplate.insertOrUpdate(vo, CustomerMd.class);
	}

	@Override
	public void updateCustomer(CustomerVo vo) throws Exception {
		dynamicSqlTemplate.update(vo, CustomerMd.class);
	}

	@Override
	public void updatesCustomer(CustomerVo[] vos) throws Exception {
		for (CustomerVo vo : vos) {
			dynamicSqlTemplate.update(vo, CustomerMd.class);
		}
	}

	@Override
	public void updateCustomerGroupNmToNull(String customerGroupNm) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("customerGroup_Nm", customerGroupNm);
		dynamicSqlTemplate.updateSql("customer-update-customernm", paramsMap);
	}

	@Override
	public void deleteCustomer(CustomerVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, CustomerMd.class);
	}

	@Override
	public void deletesCustomer(CustomerVo[] vos) throws Exception {
		for (CustomerVo vo : vos) {
			dynamicSqlTemplate.delete(vo, CustomerMd.class);
		}
	}

	@Override
	public void updateCustomerSystemStatus(CustomerVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, CustomerMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deletegroupCustomer(CustomerVo vo) throws Exception {
		vo.setCustomerGroup_Nm(null);
		dynamicSqlTemplate.update(vo, CustomerMd.class);
	}

	@Override
	public void deletesgroupCustomer(CustomerVo[] vos) throws Exception {
		for (CustomerVo vo : vos) {
			vo.setCustomerGroup_Nm(null);
			dynamicSqlTemplate.update(vo, CustomerMd.class);
		}
	}
	
	@Override
	public boolean ifCustomerValid(long customerId) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setCustomer_Id(customerId);
		List<CustomerVo> customers = queryCustomerList(customerVo);
		if (customers == null || customers.size() == 0) {
			return false;
		}
		return customers.get(0).getManual_Status() == ConsoleConstants.MANUAL_STATUS_VALID;
	}
}
