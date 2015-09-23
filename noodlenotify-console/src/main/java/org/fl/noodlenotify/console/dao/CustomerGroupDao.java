package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.CustomerGroupVo;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface CustomerGroupDao {
	public PageVo<CustomerGroupVo> queryCustomerGroupPage(CustomerGroupVo vo, int page, int rows) throws Exception;

	public List<CustomerGroupVo> queryCustomerGroupList(CustomerGroupVo vo) throws Exception;
	
	public PageVo<CustomerVo> queryCustomerIncludePage(CustomerVo vo, int page, int rows) throws Exception;

	public PageVo<CustomerVo> queryCustomerExcludePage(CustomerVo vo, int page, int rows) throws Exception;

	public void insertCustomerGroup(CustomerGroupVo vo) throws Exception;

	public void insertsCustomerGroup(CustomerGroupVo[] vos) throws Exception;

	public void updateCustomerGroup(CustomerGroupVo vo) throws Exception;

	public void updatesCustomerGroup(CustomerGroupVo[] vos) throws Exception;

	public void deleteCustomerGroup(CustomerGroupVo vo) throws Exception;

	public void deletesCustomerGroup(CustomerGroupVo[] vos) throws Exception;
}
