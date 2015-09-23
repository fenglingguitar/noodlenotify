package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.CustomerVo;

public interface CustomerService {
	public PageVo<CustomerVo> queryCustomerPage(CustomerVo vo, int page, int rows) throws Exception;

	public PageVo<CustomerVo> queryCustomerPageByEqual(CustomerVo vo, int page, int rows) throws Exception;

	public List<CustomerVo> queryCustomerList(CustomerVo vo) throws Exception;

	public List<CustomerVo> queryCheckCustomerList() throws Exception;

	public List<CustomerVo> queryCheckCustomerListWithCache() throws Exception;

	public void insertCustomer(CustomerVo vo) throws Exception;

	public void insertsCustomer(CustomerVo[] vos) throws Exception;

	public void updateCustomer(CustomerVo vo) throws Exception;

	public void updatesCustomer(CustomerVo[] vos) throws Exception;

	public void updateCustomerSystemStatus(CustomerVo vo) throws Exception;

	public void deleteCustomer(CustomerVo vo) throws Exception;

	public void deletesCustomer(CustomerVo[] vos) throws Exception;

	public long saveRegister(String ip, int port, String url, String type, int checkPort, String check_Url, String check_Type, String name, String customerGroupName, List<String> queueNameList) throws Exception;

	public void saveCancelRegister(long customerId) throws Exception;

	public void deletegroupCustomer(CustomerVo vo) throws Exception;

	public void deletesgroupCustomer(CustomerVo[] vos) throws Exception;

}
