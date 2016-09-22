package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.dao.QueueCustomerDao;
import org.fl.noodlenotify.console.service.CustomerService;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public void deletegroupCustomer(CustomerVo vo) throws Exception {
		customerDao.deletegroupCustomer(vo);
	}

	@Override
	public void deletesgroupCustomer(CustomerVo[] vos) throws Exception {
		customerDao.deletesgroupCustomer(vos);
	}
}
