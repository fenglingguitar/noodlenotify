package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.DbDao;
import org.fl.noodlenotify.console.dao.QueueDbDao;
import org.fl.noodlenotify.console.service.DbService;
import org.fl.noodlenotify.console.vo.DbVo;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dbService")
public class DbServiceImpl implements DbService {

	@Autowired
	private DbDao dbDao;

	@Autowired
	private QueueDbDao queueDbDao;

	@Override
	public PageVo<DbVo> queryDbPage(DbVo vo, int page, int rows) throws Exception {
		return dbDao.queryDbPage(vo, page, rows);
	}

	@Override
	public List<DbVo> queryDbList(DbVo vo) throws Exception {
		return dbDao.queryDbList(vo);
	}

	@Override
	public void insertDb(DbVo vo) throws Exception {
		dbDao.insertDb(vo);
	}

	@Override
	public void insertsDb(DbVo[] vos) throws Exception {
		dbDao.insertsDb(vos);
	}

	@Override
	public void updateDb(DbVo vo) throws Exception {
		dbDao.updateDb(vo);
	}

	@Override
	public void updatesDb(DbVo[] vos) throws Exception {
		dbDao.updatesDb(vos);
	}

	@Override
	public void updatesDbSystemStatus(DbVo vo) throws Exception {
		dbDao.updatesDbSystemStatus(vo);
	}

	@Override
	public void deleteDb(DbVo vo) throws Exception {
		QueueDbVo queueDbVo = new QueueDbVo();
		queueDbVo.setDb_Id(vo.getDb_Id());
		queueDbDao.deleteQueueDbByDbId(queueDbVo);
		dbDao.deleteDb(vo);
	}

	@Override
	public void deletesDb(DbVo[] vos) throws Exception {
		for (DbVo vo : vos) {
			QueueDbVo queueDbVo = new QueueDbVo();
			queueDbVo.setDb_Id(vo.getDb_Id());
			queueDbDao.deleteQueueDbByDbId(queueDbVo);
			dbDao.deleteDb(vo);
		}
	}
}
