package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface MsgStorageService {
	public PageVo<MsgStorageVo> queryMsgStoragePage(MsgStorageVo vo, int page, int rows) throws Exception;

	public List<MsgStorageVo> queryMsgStorageList(MsgStorageVo vo) throws Exception;

	public void insertMsgStorage(MsgStorageVo vo) throws Exception;

	public void insertsMsgStorage(MsgStorageVo[] vos) throws Exception;

	public void updateMsgStorage(MsgStorageVo vo) throws Exception;

	public void updatesMsgStorage(MsgStorageVo[] vos) throws Exception;

	public void updatesMsgStorageSystemStatus(MsgStorageVo vo) throws Exception;

	public void deleteMsgStorage(MsgStorageVo vo) throws Exception;

	public void deletesMsgStorage(MsgStorageVo[] vos) throws Exception;
}
