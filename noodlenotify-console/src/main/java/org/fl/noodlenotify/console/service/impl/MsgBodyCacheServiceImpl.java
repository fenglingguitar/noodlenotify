package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.MsgBodyCacheDao;
import org.fl.noodlenotify.console.dao.QueueMsgBodyCacheDao;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("msgBodyCacheService")
public class MsgBodyCacheServiceImpl implements MsgBodyCacheService {

	@Autowired
	private MsgBodyCacheDao msgBodyCacheDao;

	@Autowired
	private QueueMsgBodyCacheDao queueMsgBodyCacheDao;

	@Override
	public PageVo<MsgBodyCacheVo> queryMsgBodyCachePage(MsgBodyCacheVo vo, int page, int rows) throws Exception {
		return msgBodyCacheDao.queryMsgBodyCachePage(vo, page, rows);
	}

	@Override
	public List<MsgBodyCacheVo> queryMsgBodyCacheList(MsgBodyCacheVo vo) throws Exception {
		return msgBodyCacheDao.queryMsgBodyCacheList(vo);
	}

	@Override
	public void insertMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheDao.insertMsgBodyCache(vo);
	}

	@Override
	public void insertsMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		msgBodyCacheDao.insertsMsgBodyCache(vos);
	}

	@Override
	public void updateMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheDao.updateMsgBodyCache(vo);
	}

	@Override
	public void updatesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		msgBodyCacheDao.updatesMsgBodyCache(vos);
	}

	@Override
	public void updatesMsgBodyCacheSystemStatus(MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheDao.updatesMsgBodyCacheSystemStatus(vo);
	}

	@Override
	public void updatesMsgBodyCacheSize(MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheDao.updatesMsgBodyCacheSize(vo);
	}

	@Override
	public void deleteMsgBodyCache(MsgBodyCacheVo vo) throws Exception {
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setMsgBodyCache_Id(vo.getMsgBodyCache_Id());
		queueMsgBodyCacheDao.deleteQueueMsgBodyCacheByMsgBodyCacheId(queueMsgBodyCacheVo);
		msgBodyCacheDao.deleteMsgBodyCache(vo);
	}

	@Override
	public void deletesMsgBodyCache(MsgBodyCacheVo[] vos) throws Exception {
		for (MsgBodyCacheVo vo : vos) {
			QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
			queueMsgBodyCacheVo.setMsgBodyCache_Id(vo.getMsgBodyCache_Id());
			queueMsgBodyCacheDao.deleteQueueMsgBodyCacheByMsgBodyCacheId(queueMsgBodyCacheVo);
			msgBodyCacheDao.deleteMsgBodyCache(vo);
		}
	}
}
