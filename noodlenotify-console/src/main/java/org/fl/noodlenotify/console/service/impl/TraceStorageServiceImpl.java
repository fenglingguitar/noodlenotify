package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.QueueTraceStorageDao;
import org.fl.noodlenotify.console.dao.TraceStorageDao;
import org.fl.noodlenotify.console.service.TraceStorageService;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("traceStorageService")
public class TraceStorageServiceImpl implements TraceStorageService {

	@Autowired
	private TraceStorageDao traceStorageDao;

	@Autowired
	private QueueTraceStorageDao queueTraceStorageDao;

	@Override
	public PageVo<TraceStorageVo> queryTraceStoragePage(TraceStorageVo vo, int page, int rows) throws Exception {
		return traceStorageDao.queryTraceStoragePage(vo, page, rows);
	}

	@Override
	public List<TraceStorageVo> queryTraceStorageList(TraceStorageVo vo) throws Exception {
		return traceStorageDao.queryTraceStorageList(vo);
	}

	@Override
	public List<TraceStorageVo> queryCheckTracestorageListWithCache() throws Exception {
		List<TraceStorageVo> traceStorages = queryCheckTracestorageList();
		return traceStorages;
	}

	@Override
	public List<TraceStorageVo> queryCheckTracestorageList() throws Exception {
		TraceStorageVo traceStorageVo = new TraceStorageVo();
		traceStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return traceStorageDao.queryTraceStorageList(traceStorageVo);
	}

	@Override
	public void insertTraceStorage(TraceStorageVo vo) throws Exception {
		traceStorageDao.insertTraceStorage(vo);
	}

	@Override
	public void insertsTraceStorage(TraceStorageVo[] vos) throws Exception {
		traceStorageDao.insertsTraceStorage(vos);
	}

	@Override
	public void updateTraceStorage(TraceStorageVo vo) throws Exception {
		traceStorageDao.updateTraceStorage(vo);
	}

	@Override
	public void updatesTraceStorage(TraceStorageVo[] vos) throws Exception {
		traceStorageDao.updatesTraceStorage(vos);
	}

	@Override
	public void updatesTraceStorageSystemStatus(TraceStorageVo vo) throws Exception {
		traceStorageDao.updatesTraceStorageSystemStatus(vo);
	}

	@Override
	public void deleteTraceStorage(TraceStorageVo vo) throws Exception {
		QueueTraceStorageVo queueTraceStorageVo = new QueueTraceStorageVo();
		queueTraceStorageVo.setTraceStorage_Id(vo.getTraceStorage_Id());
		queueTraceStorageDao.deleteQueueTraceStorageByTraceStorageId(queueTraceStorageVo);
		traceStorageDao.deleteTraceStorage(vo);
	}

	@Override
	public void deletesTraceStorage(TraceStorageVo[] vos) throws Exception {
		for (TraceStorageVo vo : vos) {
			QueueTraceStorageVo queueTraceStorageVo = new QueueTraceStorageVo();
			queueTraceStorageVo.setTraceStorage_Id(vo.getTraceStorage_Id());
			queueTraceStorageDao.deleteQueueTraceStorageByTraceStorageId(queueTraceStorageVo);
			traceStorageDao.deleteTraceStorage(vo);
		}
	}
}
