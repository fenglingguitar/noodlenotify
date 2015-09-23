package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface QueueService {
	public PageVo<QueueVo> queryQueuePage(QueueVo vo, int page, int rows) throws Exception;

	public List<QueueVo> queryQueueList(QueueVo vo) throws Exception;

	public void insertQueue(QueueVo vo) throws Exception;

	public void insertsQueue(QueueVo[] vos) throws Exception;

	public void updateQueue(QueueVo vo) throws Exception;

	public void updatesQueue(QueueVo[] vos) throws Exception;

	public void deleteQueue(QueueVo vo) throws Exception;

	public void deletesQueue(QueueVo[] vos) throws Exception;
	
	public PageVo<QueueVo> queryQueueMonitorPage(QueueVo vo, int page, int rows) throws Exception;
	
	public void updatesQueueStatus(List<QueueVo> voList) throws Exception;
}
