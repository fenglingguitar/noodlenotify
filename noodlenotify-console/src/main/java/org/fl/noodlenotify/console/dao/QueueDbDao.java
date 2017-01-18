package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueDbDao {
	public PageVo<QueueDbVo> queryQueueDbPage(QueueDbVo vo, int page, int rows) throws Exception;

	public List<QueueDbVo> queryQueueDbList(QueueDbVo vo) throws Exception;
	
	public PageVo<QueueDbVo> queryQueueDbIncludePage(QueueDbVo vo, int page, int rows) throws Exception;

	public PageVo<QueueDbVo> queryQueueDbExcludePage(QueueDbVo vo, int page, int rows) throws Exception;
	
	public List<QueueDbVo> queryQueueDbIncludeList(QueueDbVo vo) throws Exception;

	public List<QueueDbVo> queryQueueByDbListTree(QueueDbVo vo)	throws Exception;

	public List<QueueDbVo> queryDbByQueue(QueueDbVo vo) throws Exception;
	
	public List<QueueDbVo> queryDbByQueueExclude(QueueDbVo vo) throws Exception;

	public PageVo<QueueDbVo> queryQueueByDb(QueueDbVo vo, int page, int rows) throws Exception;

	public void insertQueueDb(QueueDbVo vo) throws Exception;

	public void insertsQueueDb(QueueDbVo[] vos) throws Exception;

	public void updateQueueDb(QueueDbVo vo) throws Exception;

	public void updatesQueueDb(QueueDbVo[] vos) throws Exception;

	public void deleteQueueDb(QueueDbVo vo) throws Exception;

	public void deletesQueueDb(QueueDbVo[] vos) throws Exception;

	public void deleteQueueDbByDbId(QueueDbVo vo) throws Exception;

	public void deleteQueueDbByQueueNm(QueueDbVo vo) throws Exception;

	

}
