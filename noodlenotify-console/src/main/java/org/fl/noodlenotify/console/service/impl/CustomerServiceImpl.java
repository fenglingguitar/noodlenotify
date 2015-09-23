package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.domain.CustomerMd;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("customerService")
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private QueueCustomerDao queueCustomerDao;

	@Override
	public PageVo<CustomerVo> queryCustomerPage(CustomerVo vo, int page, int rows) throws Exception {
		return customerDao.queryCustomerPage(vo, page, rows);
	}

	@Override
	public PageVo<CustomerVo> queryCustomerPageByEqual(CustomerVo vo, int page, int rows) throws Exception {
		return customerDao.queryCustomerPageByEqual(vo, page, rows);
	}

	@Override
	public List<CustomerVo> queryCustomerList(CustomerVo vo) throws Exception {
		return customerDao.queryCustomerList(vo);
	}
	
	@Override
	public List<CustomerVo> queryCheckCustomerListWithCache() throws Exception {
		List<CustomerVo> customers = queryCheckCustomerList();
		return customers;
	}

	@Override
	public List<CustomerVo> queryCheckCustomerList() throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return customerDao.queryCustomerList(customerVo);
	}

	@Override
	public void insertCustomer(CustomerVo vo) throws Exception {
		customerDao.insertCustomer(vo);
	}

	@Override
	public void insertsCustomer(CustomerVo[] vos) throws Exception {
		customerDao.insertsCustomer(vos);
	}

	@Override
	public void updateCustomer(CustomerVo vo) throws Exception {
		customerDao.updateCustomer(vo);
	}

	@Override
	public void updatesCustomer(CustomerVo[] vos) throws Exception {
		customerDao.updatesCustomer(vos);
	}

	@Override
	public void updateCustomerSystemStatus(CustomerVo vo) throws Exception {
		customerDao.updateCustomerSystemStatus(vo);
	}

	@Override
	public void deleteCustomer(CustomerVo vo) throws Exception {
		QueueCustomerVo queueCustomerVo = new QueueCustomerVo();
		queueCustomerVo.setCustomer_Id(vo.getCustomer_Id());
		queueCustomerDao.deleteQueueCustomerByCustomerId(queueCustomerVo);
		customerDao.deleteCustomer(vo);
	}

	@Override
	public void deletesCustomer(CustomerVo[] vos) throws Exception {
		for (CustomerVo vo : vos) {
			QueueCustomerVo queueCustomerVo = new QueueCustomerVo();
			queueCustomerVo.setCustomer_Id(vo.getCustomer_Id());
			queueCustomerDao.deleteQueueCustomerByCustomerId(queueCustomerVo);
			customerDao.deleteCustomer(vo);
		}
	}

	@Override
	public long saveRegister(String ip, int port, String url, String type, int checkPort, String checkUrl, String checkType, String name, String customerGroupName, List<String> queueNameList) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setIp(ip);
		customerVo.setCheck_Port(checkPort);
		List<CustomerVo> exchangerList = customerDao.queryCustomerList(customerVo);
		if (exchangerList == null || exchangerList.size() == 0) {
			customerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			customerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			customerVo = exchangerList.get(0);
		}
		customerVo.setName(name);
		customerVo.setPort(port);
		customerVo.setUrl(url);
		customerVo.setType(type);
		customerVo.setCheck_Url(checkUrl);
		customerVo.setCheck_Type(checkType);
		customerVo.setCustomerGroup_Nm(customerGroupName);
		CustomerMd customerMd = customerDao.insertOrUpdate(customerVo);
		QueueCustomerVo queueCustomerDeleteVo = new QueueCustomerVo();
		queueCustomerDeleteVo.setCustomer_Id(customerMd.getCustomer_Id());
		queueCustomerDao.deleteQueueCustomerByCustomerId(queueCustomerDeleteVo);

		for (String queueName : queueNameList) {
			QueueCustomerVo queueCustomerInsertVo = new QueueCustomerVo();
			queueCustomerInsertVo.setQueue_Nm(queueName);
			queueCustomerInsertVo.setCustomer_Id(customerMd.getCustomer_Id());
			queueCustomerDao.insertQueueCustomer(queueCustomerInsertVo);
		}
		return customerMd.getCustomer_Id();
	}

	@Override
	public void saveCancelRegister(long customerId) throws Exception {
		CustomerVo customerVo = new CustomerVo();
		customerVo.setCustomer_Id(customerId);
		customerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		customerDao.updateCustomerSystemStatus(customerVo);
	}

	@Override
	public void deletegroupCustomer(CustomerVo vo) throws Exception {
		customerDao.deletegroupCustomer(vo);
	}

	@Override
	public void deletesgroupCustomer(CustomerVo[] vos) throws Exception {
		customerDao.deletesgroupCustomer(vos);
	}
}
