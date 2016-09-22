package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;

public interface QueueMsgStorageService {
	
	public PageVo<QueueMsgStorageVo> queryQueueMsgStoragePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageIncludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueMsgStorageExcludePage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public List<QueueMsgStorageVo> queryQueueMsgStorageList(QueueMsgStorageVo vo) throws Exception;

	public List<QueueMsgStorageVo> queryMsgStoragesByQueueExclude(QueueMsgStorageVo vo) throws Exception;
	
	public List<QueueMsgStorageVo> queryQueueByMsgstorageList(QueueMsgStorageVo vo) throws Exception;
	
	public List<QueueMsgStorageVo> queryQueueByMsgstorageListTree(QueueMsgStorageVo queueMsgStorageVo) throws Exception;

	public PageVo<QueueMsgStorageVo> queryQueueByMsgStorage(QueueMsgStorageVo vo, int page, int rows) throws Exception;

	public void insertQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void insertsQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;

	public void updateQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void updatesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;

	public void updateQueueMsgStorageSimple(QueueMsgStorageVo vo) throws Exception;

	public void deleteQueueMsgStorage(QueueMsgStorageVo vo) throws Exception;

	public void deletesQueueMsgStorage(QueueMsgStorageVo[] vos) throws Exception;
}
