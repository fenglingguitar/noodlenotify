package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.TraceStorageDao;
import org.fl.noodlenotify.console.domain.TraceStorageMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("traceStorageDao")
public class TraceStorageDaoImpl implements TraceStorageDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<TraceStorageVo> queryTraceStoragePage(TraceStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("traceStorage_Id", vo.getTraceStorage_Id() > 0 ? vo.getTraceStorage_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("trace-storage-query-list", paramsMap, page, rows, TraceStorageVo.class);
	}

	@Override
	public List<TraceStorageVo> queryTraceStorageList(TraceStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("traceStorage_Id", vo.getTraceStorage_Id() > 0 ? vo.getTraceStorage_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() > 0 ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() > 0 ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() > 0 ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("trace-storage-query-list", paramsMap, TraceStorageVo.class);
	}

	@Override
	public void insertTraceStorage(TraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, TraceStorageMd.class);
	}

	@Override
	public void insertsTraceStorage(TraceStorageVo[] vos) throws Exception {
		for (TraceStorageVo vo : vos) {
			dynamicSqlTemplate.insert(vo, TraceStorageMd.class);
		}
	}

	@Override
	public void updateTraceStorage(TraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, TraceStorageMd.class);
	}

	@Override
	public void updatesTraceStorage(TraceStorageVo[] vos) throws Exception {
		for (TraceStorageVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, TraceStorageMd.class);
		}
	}

	@Override
	public void updatesTraceStorageSystemStatus(TraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, TraceStorageMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deleteTraceStorage(TraceStorageVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, TraceStorageMd.class);
	}

	@Override
	public void deletesTraceStorage(TraceStorageVo[] vos) throws Exception {
		for (TraceStorageVo vo : vos) {
			dynamicSqlTemplate.delete(vo, TraceStorageMd.class);
		}
	}

}
