package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.domain.CustomerMd;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface CustomerDao {

	public PageVo<CustomerVo> queryCustomerPage(CustomerVo vo, int page, int rows) throws Exception;

	public PageVo<CustomerVo> queryCustomerPageByEqual(CustomerVo vo, int page, int rows) throws Exception;

	public List<CustomerVo> queryCustomerList(CustomerVo vo) throws Exception;

	public void insertCustomer(CustomerVo vo) throws Exception;

	public void insertsCustomer(CustomerVo[] vos) throws Exception;

	public CustomerMd insertOrUpdate(CustomerVo vo) throws Exception;

	public void updateCustomer(CustomerVo vo) throws Exception;

	public void updatesCustomer(CustomerVo[] vos) throws Exception;

	public void updateCustomerGroupNmToNull(String customerGroupNm) throws Exception;

	public void deleteCustomer(CustomerVo vo) throws Exception;

	public void deletesCustomer(CustomerVo[] vos) throws Exception;

	public void updateCustomerSystemStatus(CustomerVo vo) throws Exception;

	public void deletegroupCustomer(CustomerVo vo) throws Exception;

	public void deletesgroupCustomer(CustomerVo[] vos) throws Exception;
	
	public List<CustomerVo> queryCustomerToOnlineList(CustomerVo vo) throws Exception;
	public List<CustomerVo> queryCustomerToOfflineList(CustomerVo vo) throws Exception;
}
