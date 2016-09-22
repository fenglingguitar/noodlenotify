package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;

public interface QueueCustomerGroupService {
	
	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupPage(QueueCustomerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupIncludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueCustomerGroupVo> queryQueueCustomerGroupExcludePage(QueueCustomerGroupVo vo, int page, int rows) throws Exception;

	public List<QueueCustomerGroupVo> queryQueueCustomerGroupList(QueueCustomerGroupVo vo) throws Exception;
	
	public List<QueueCustomerGroupVo> queryQueueByCustomerGroupList(QueueCustomerGroupVo vo) throws Exception;
	
	public List<QueueCustomerGroupVo> queryCustomerGroupsByQueue(QueueCustomerGroupVo vo) throws Exception;

	public List<QueueCustomerGroupVo> queryUnuserGroupNumList(String queueNm) throws Exception;

	public PageVo<QueueCustomerGroupVo> queryQueuePageByCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception;

	public PageVo<QueueCustomerGroupVo> queryCustomerPageByQueueCustomerGroup(QueueCustomerGroupVo vo, int page, int rows) throws Exception;

	public void insertQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception;

	public void insertsQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception;

	public void updateQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception;

	public void updatesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception;

	public void deleteQueueCustomerGroup(QueueCustomerGroupVo vo) throws Exception;

	public void deletesQueueCustomerGroup(QueueCustomerGroupVo[] vos) throws Exception;
}
