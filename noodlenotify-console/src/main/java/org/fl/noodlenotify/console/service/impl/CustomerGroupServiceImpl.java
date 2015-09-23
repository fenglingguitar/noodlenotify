package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.fl.noodlenotify.console.dao.CustomerDao;
import org.fl.noodlenotify.console.dao.CustomerGroupDao;
import org.fl.noodlenotify.console.dao.QueueCustomerGroupDao;
import org.fl.noodlenotify.console.service.CustomerGroupService;
import org.fl.noodlenotify.console.vo.CustomerGroupVo;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("customerGroupService")
public class CustomerGroupServiceImpl implements CustomerGroupService {
	@Autowired
	private CustomerDao customerDao;

	@Autowired
	private CustomerGroupDao customerGroupDao;

	@Autowired
	private QueueCustomerGroupDao queueCustomerGroupDao;

	@Override
	public PageVo<CustomerGroupVo> queryCustomerGroupPage(CustomerGroupVo vo, int page, int rows) throws Exception {
		return customerGroupDao.queryCustomerGroupPage(vo, page, rows);
	}

	@Override
	public List<CustomerGroupVo> queryCustomerGroupList(CustomerGroupVo vo) throws Exception {
		return customerGroupDao.queryCustomerGroupList(vo);
	}

	@Override
	public PageVo<CustomerVo> queryCustomerIncludePage(CustomerVo vo, int page, int rows) throws Exception {
		return customerGroupDao.queryCustomerIncludePage(vo, page, rows);
	}

	@Override
	public PageVo<CustomerVo> queryCustomerExcludePage(CustomerVo vo, int page, int rows) throws Exception {
		return customerGroupDao.queryCustomerExcludePage(vo, page, rows);
	}

	@Override
	public void insertCustomerGroup(CustomerGroupVo vo) throws Exception {
		customerGroupDao.insertCustomerGroup(vo);
	}

	@Override
	public void insertsCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		customerGroupDao.insertsCustomerGroup(vos);
	}

	@Override
	public void updateCustomerGroup(CustomerGroupVo vo) throws Exception {
		customerGroupDao.updateCustomerGroup(vo);
	}

	@Override
	public void updatesCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		customerGroupDao.updatesCustomerGroup(vos);
	}

	@Override
	public void deleteCustomerGroup(CustomerGroupVo vo) throws Exception {
		customerDao.updateCustomerGroupNmToNull(vo.getCustomerGroup_Nm());
		QueueCustomerGroupVo queueCustomerGroupVo = new QueueCustomerGroupVo();
		queueCustomerGroupVo.setCustomerGroup_Nm(vo.getCustomerGroup_Nm());
		queueCustomerGroupDao.deleteQueueCustomerGroupByCustomerGroupNm(queueCustomerGroupVo);
		customerGroupDao.deleteCustomerGroup(vo);
	}

	@Override
	public void deletesCustomerGroup(CustomerGroupVo[] vos) throws Exception {
		for (CustomerGroupVo vo : vos) {
			customerDao.updateCustomerGroupNmToNull(vo.getCustomerGroup_Nm());
			QueueCustomerGroupVo queueCustomerGroupVo = new QueueCustomerGroupVo();
			queueCustomerGroupVo.setCustomerGroup_Nm(vo.getCustomerGroup_Nm());
			queueCustomerGroupDao.deleteQueueCustomerGroupByCustomerGroupNm(queueCustomerGroupVo);
			customerGroupDao.deleteCustomerGroup(vo);
		}
	}
}
