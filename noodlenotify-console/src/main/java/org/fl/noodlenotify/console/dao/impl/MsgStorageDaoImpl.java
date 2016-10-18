package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.MsgStorageDao;
import org.fl.noodlenotify.console.domain.MsgStorageMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("msgStorageDao")
public class MsgStorageDaoImpl implements MsgStorageDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<MsgStorageVo> queryMsgStoragePage(MsgStorageVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id() != null ? vo.getMsgStorage_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("msg-storage-query-list", paramsMap, page, rows, MsgStorageVo.class);
	}

	@Override
	public List<MsgStorageVo> queryMsgStorageList(MsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id() != null ? vo.getMsgStorage_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("msg-storage-query-list", paramsMap, MsgStorageVo.class);
	}
	
	@Override
	public List<MsgStorageVo> queryMsgStorageListExclude(MsgStorageVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("msgStorage_Id", vo.getMsgStorage_Id() != null ? vo.getMsgStorage_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("msg-storage-query-list-exclude", paramsMap, MsgStorageVo.class);
	}

	@Override
	public void insertMsgStorage(MsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, MsgStorageMd.class);
	}

	@Override
	public void insertsMsgStorage(MsgStorageVo[] vos) throws Exception {
		for (MsgStorageVo vo : vos) {
			dynamicSqlTemplate.insert(vo, MsgStorageMd.class);
		}
	}

	@Override
	public void updateMsgStorage(MsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, MsgStorageMd.class);
	}

	@Override
	public void updatesMsgStorage(MsgStorageVo[] vos) throws Exception {
		for (MsgStorageVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, MsgStorageMd.class);
		}
	}

	@Override
	public void updatesMsgStorageSystemStatus(MsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, MsgStorageMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deleteMsgStorage(MsgStorageVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, MsgStorageMd.class);
	}

	@Override
	public void deletesMsgStorage(MsgStorageVo[] vos) throws Exception {
		for (MsgStorageVo vo : vos) {
			dynamicSqlTemplate.delete(vo, MsgStorageMd.class);
		}
	}

}
