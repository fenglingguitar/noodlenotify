package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueMsgStorageDao {
	public PageVo<QueueMsgStorageVo> queryQueueMsgStoragePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageIncludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageExcludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public List<QueueMsgStorageVo> queryQueueMsgStorageList(QueueMsgStorageVo vo) throws Exception;
	
	public List<QueueMsgStorageVo> queryQueueByMsgstorageList(QueueMsgStorageVo vo) throws Exception;
	
	public List<QueueMsgStorageVo> queryQueueByMsgstorageListTree(QueueMsgStorageVo vo)	throws Exception;

	public List<QueueMsgStorageVo> queryMsgStoragesByQueue(QueueMsgStorageVo vo) throws Exception;
	
	public List<QueueMsgStorageVo> queryMsgStoragesByQueueExclude(QueueMsgStorageVo vo) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueByMsgstorage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public void insertQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void insertsQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;

	public void updateQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void updatesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;

	public void deleteQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void deletesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;

	public void deleteQueueMsgStorageByMsgStorageId(QueueMsgStorageVo vo) throws Exception;

	public void deleteQueueMsgStorageByQueueNm(QueueMsgStorageVo vo) throws Exception;

	

}
