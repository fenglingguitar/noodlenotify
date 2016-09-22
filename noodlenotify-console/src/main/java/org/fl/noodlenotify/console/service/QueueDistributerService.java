package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;

public interface QueueDistributerService {
	
	public PageVo<QueueDistributerVo> queryQueueDistributerPage(QueueDistributerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueDistributerVo> queryQueueDistributerIncludePage(QueueDistributerVo vo, int page, int rows) throws Exception;

	public PageVo<QueueDistributerVo> queryQueueDistributerExcludePage(QueueDistributerVo vo, int page, int rows) throws Exception;

	public List<QueueDistributerVo> queryQueueDistributerList(QueueDistributerVo vo) throws Exception;
	
	public List<QueueDistributerVo> queryQueuesByDistributer(QueueDistributerVo vo) throws Exception;
	
	public List<QueueDistributerVo> queryQueuesByDistributerTree(QueueDistributerVo vo) throws Exception;

	public PageVo<QueueDistributerVo> queryQueuePageByDistributer(QueueDistributerVo vo, int page, int rows) throws Exception;

	public void insertQueueDistributer(QueueDistributerVo vo) throws Exception;

	public void insertsQueueDistributer(QueueDistributerVo[] vos) throws Exception;

	public void updateQueueDistributer(QueueDistributerVo vo) throws Exception;

	public void updatesQueueDistributer(QueueDistributerVo[] vos) throws Exception;

	public void deleteQueueDistributer(QueueDistributerVo vo) throws Exception;

	public void deletesQueueDistributer(QueueDistributerVo[] vos) throws Exception;
}
