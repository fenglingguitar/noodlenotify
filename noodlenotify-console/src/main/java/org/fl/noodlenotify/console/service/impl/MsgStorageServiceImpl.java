package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.MsgStorageDao;
import org.fl.noodlenotify.console.dao.QueueMsgStorageDao;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("msgStorageService")
public class MsgStorageServiceImpl implements MsgStorageService {

	@Autowired
	private MsgStorageDao msgStorageDao;

	@Autowired
	private QueueMsgStorageDao queueMsgStorageDao;

	@Override
	public PageVo<MsgStorageVo> queryMsgStoragePage(MsgStorageVo vo, int page, int rows) throws Exception {
		return msgStorageDao.queryMsgStoragePage(vo, page, rows);
	}

	@Override
	public List<MsgStorageVo> queryMsgStorageList(MsgStorageVo vo) throws Exception {
		return msgStorageDao.queryMsgStorageList(vo);
	}
	
	@Override
	public List<MsgStorageVo> queryCheckMsgStorageListWithCache() throws Exception {
		List<MsgStorageVo> msgStorages = queryCheckMsgStorageList();
		return msgStorages;
	}

	@Override
	public List<MsgStorageVo> queryCheckMsgStorageList() throws Exception {
		MsgStorageVo msgStorageVo = new MsgStorageVo();
		msgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
		return msgStorageDao.queryMsgStorageListExclude(msgStorageVo);
	}

	@Override
	public void insertMsgStorage(MsgStorageVo vo) throws Exception {
		msgStorageDao.insertMsgStorage(vo);
	}

	@Override
	public void insertsMsgStorage(MsgStorageVo[] vos) throws Exception {
		msgStorageDao.insertsMsgStorage(vos);
	}

	@Override
	public void updateMsgStorage(MsgStorageVo vo) throws Exception {
		msgStorageDao.updateMsgStorage(vo);
	}

	@Override
	public void updatesMsgStorage(MsgStorageVo[] vos) throws Exception {
		msgStorageDao.updatesMsgStorage(vos);
	}

	@Override
	public void updatesMsgStorageSystemStatus(MsgStorageVo vo) throws Exception {
		msgStorageDao.updatesMsgStorageSystemStatus(vo);
	}

	@Override
	public void deleteMsgStorage(MsgStorageVo vo) throws Exception {
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setMsgStorage_Id(vo.getMsgStorage_Id());
		queueMsgStorageDao.deleteQueueMsgStorageByMsgStorageId(queueMsgStorageVo);
		msgStorageDao.deleteMsgStorage(vo);
	}

	@Override
	public void deletesMsgStorage(MsgStorageVo[] vos) throws Exception {
		for (MsgStorageVo vo : vos) {
			QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
			queueMsgStorageVo.setMsgStorage_Id(vo.getMsgStorage_Id());
			queueMsgStorageDao.deleteQueueMsgStorageByMsgStorageId(queueMsgStorageVo);
			msgStorageDao.deleteMsgStorage(vo);
		}
	}
}
