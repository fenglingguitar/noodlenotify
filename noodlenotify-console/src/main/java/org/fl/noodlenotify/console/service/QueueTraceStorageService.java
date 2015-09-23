package org.fl.noodlenotify.console.service;

import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueTraceStorageService {
	public PageVo<QueueTraceStorageVo> queryQueueTraceStoragePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageIncludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueTraceStorageExcludePage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public List<QueueTraceStorageVo> queryQueueTraceStorageList(QueueTraceStorageVo vo) throws Exception;

	public PageVo<QueueTraceStorageVo> queryQueueByTraceStorage(QueueTraceStorageVo vo, int page, int rows) throws Exception;

	public void insertQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void insertsQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public void updateQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void updatesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public void deleteQueueTraceStorage(QueueTraceStorageVo vo) throws Exception;

	public void deletesQueueTraceStorage(QueueTraceStorageVo[] vos) throws Exception;

	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByProducerId(long producerId) throws Exception;

	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByExchangerId(long exchangerId) throws Exception;

	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByDistributerId(long distributerId) throws Exception;

	public Map<String, List<QueueTraceStorageVo>> getQueueTraceStorageByCustomerId(long customerId) throws Exception;
}
