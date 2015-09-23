package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.MsgQueueCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgQueueCacheDao;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("msgQueueCacheServiceImpl")
public class MsgQueueCacheServiceImpl implements MsgQueueCacheService {

	@Autowired
	private MsgQueueCacheDao msgQueueCacheDao;

	@Autowired
	private QueueMsgQueueCacheDao queueMsgQueueCacheDao;

	@Override
	public PageVo<MsgQueueCacheVo> queryMsgQueueCachePage(MsgQueueCacheVo vo, int page, int rows) throws Exception {
		return msgQueueCacheDao.queryMsgQueueCachePage(vo, page, rows);
	}

	@Override
	public List<MsgQueueCacheVo> queryMsgQueueCacheList(MsgQueueCacheVo vo) throws Exception {
		return msgQueueCacheDao.queryMsgQueueCacheList(vo);
	}
	
	@Override
	public List<MsgQueueCacheVo> queryCheckMsgQueueCacheListWithCache() throws Exception {
		List<MsgQueueCacheVo> msgQueueCaches = queryCheckMsgQueueCacheList();
		return msgQueueCaches;
	}

	@Override
	public List<MsgQueueCacheVo> queryCheckMsgQueueCacheList() throws Exception {
		MsgQueueCacheVo msgQueueCacheVo = new MsgQueueCacheVo();
		msgQueueCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return msgQueueCacheDao.queryMsgQueueCacheList(msgQueueCacheVo);
	}

	@Override
	public void insertMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheDao.insertMsgQueueCache(vo);
	}

	@Override
	public void insertsMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		msgQueueCacheDao.insertsMsgQueueCache(vos);
	}

	@Override
	public void updateMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheDao.updateMsgQueueCache(vo);
	}

	@Override
	public void updatesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		msgQueueCacheDao.updatesMsgQueueCache(vos);
	}

	@Override
	public void updatesMsgQueueCacheSystemStatus(MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheDao.updatesMsgQueueCacheSystemStatus(vo);
	}

	@Override
	public void deleteMsgQueueCache(MsgQueueCacheVo vo) throws Exception {
		QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVo.setMsgQueueCache_Id(vo.getMsgQueueCache_Id());
		queueMsgQueueCacheDao.deleteQueueMsgQueueCacheByMsgQueueCacheId(queueMsgQueueCacheVo);
		msgQueueCacheDao.deleteMsgQueueCache(vo);
	}

	@Override
	public void deletesMsgQueueCache(MsgQueueCacheVo[] vos) throws Exception {
		for (MsgQueueCacheVo vo : vos) {
			QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
			queueMsgQueueCacheVo.setMsgQueueCache_Id(vo.getMsgQueueCache_Id());
			queueMsgQueueCacheDao.deleteQueueMsgQueueCacheByMsgQueueCacheId(queueMsgQueueCacheVo);
			msgQueueCacheDao.deleteMsgQueueCache(vo);
		}
	}
}
