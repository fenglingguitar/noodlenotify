package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueTraceStorageDao {
	public PageVo<QueueTraceStorageVo> queryQueueTraceStoragePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageIncludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageExcludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public List<QueueTraceStorageVo> queryQueueTraceStorageList(QueueTraceStorageVo vo) throws Exception;

	public List<QueueTraceStorageVo> queryTraceStoragesByQueue(QueueTraceStorageVo vo) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueByTraceStorage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public void insertQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void insertsQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public void updateQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void updatesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public void deleteQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void deletesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public void deleteQueueTraceStorageByTraceStorageId(QueueTraceStorageVo vo) throws Exception;

	public void deleteQueueTraceStorageByQueueNm(QueueTraceStorageVo vo) throws Exception;

}
