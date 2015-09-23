package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueCustomerDao {
	public PageVo<QueueCustomerVo> queryQueueCustomerPage(QueueCustomerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueCustomerVo> queryQueueCustomerIncludePage(QueueCustomerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueCustomerVo> queryQueueCustomerExcludePage(QueueCustomerVo vo, int page, int rows) throws Exception;

	public List<QueueCustomerVo> queryQueueCustomerList(QueueCustomerVo vo) throws Exception;

	public List<QueueCustomerVo> queryCustomersByQueue(QueueCustomerVo vo) throws Exception;

	public List<QueueCustomerVo> queryQueuesByCustomer(QueueCustomerVo vo) throws Exception;

	public PageVo<QueueCustomerVo> queryQueuePageByCustomer(QueueCustomerVo vo, int page, int rows) throws Exception;

	public void insertQueueCustomer(QueueCustomerVo vo) throws Exception;

	public void insertsQueueCustomer(QueueCustomerVo[] vos) throws Exception;

	public void updateQueueCustomer(QueueCustomerVo vo) throws Exception;

	public void updatesQueueCustomer(QueueCustomerVo[] vos) throws Exception;

	public void deleteQueueCustomer(QueueCustomerVo vo) throws Exception;

	public void deletesQueueCustomer(QueueCustomerVo[] vos) throws Exception;

	public void deleteQueueCustomerByCustomerId(QueueCustomerVo vo) throws Exception;

	public void deleteQueueCustomerByQueueNm(QueueCustomerVo vo) throws Exception;
}
