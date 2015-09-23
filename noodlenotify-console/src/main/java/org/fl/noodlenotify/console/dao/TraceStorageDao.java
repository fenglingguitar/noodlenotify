package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface TraceStorageDao {
	public PageVo<TraceStorageVo> queryTraceStoragePage(TraceStorageVo vo, int page, int rows) throws Exception;

	public List<TraceStorageVo> queryTraceStorageList(TraceStorageVo vo) throws Exception;

	public void insertTraceStorage(TraceStorageVo vo) throws Exception;

	public void insertsTraceStorage(TraceStorageVo[] vos) throws Exception;

	public void updateTraceStorage(TraceStorageVo vo) throws Exception;

	public void updatesTraceStorage(TraceStorageVo[] vos) throws Exception;

	public void updatesTraceStorageSystemStatus(TraceStorageVo vo) throws Exception;

	public void deleteTraceStorage(TraceStorageVo vo) throws Exception;

	public void deletesTraceStorage(TraceStorageVo[] vos) throws Exception;
}
