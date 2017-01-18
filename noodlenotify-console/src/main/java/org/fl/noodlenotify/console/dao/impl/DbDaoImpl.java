package org.fl.noodlenotify.console.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.DbDao;
import org.fl.noodlenotify.console.domain.DbMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.DbVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("dbDao")
public class DbDaoImpl implements DbDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<DbVo> queryDbPage(DbVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("db_Id", vo.getDb_Id() != null ? vo.getDb_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? (new StringBuilder().append("%").append(vo.getIp()).append("%")).toString() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("db-query-list", paramsMap, page, rows, DbVo.class);
	}

	@Override
	public List<DbVo> queryDbList(DbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("db_Id", vo.getDb_Id() != null ? vo.getDb_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("db-query-list", paramsMap, DbVo.class);
	}
	
	@Override
	public List<DbVo> queryDbListExclude(DbVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("db_Id", vo.getDb_Id() != null ? vo.getDb_Id() : null);
		paramsMap.put("name", vo.getName() != null ? (new StringBuilder().append("%").append(vo.getName()).append("%")).toString() : null);
		paramsMap.put("ip", vo.getIp() != null ? vo.getIp() : null);
		paramsMap.put("port", vo.getPort() != null ? vo.getPort() : null);
		paramsMap.put("system_Status", vo.getSystem_Status() != null ? vo.getSystem_Status() : null);
		paramsMap.put("manual_Status", vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("db-query-list-exclude", paramsMap, DbVo.class);
	}

	@Override
	public void insertDb(DbVo vo) throws Exception {
		dynamicSqlTemplate.insert(vo, DbMd.class);
	}

	@Override
	public void insertsDb(DbVo[] vos) throws Exception {
		for (DbVo vo : vos) {
			dynamicSqlTemplate.insert(vo, DbMd.class);
		}
	}

	@Override
	public void updateDb(DbVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, DbMd.class);
	}

	@Override
	public void updatesDb(DbVo[] vos) throws Exception {
		for (DbVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, DbMd.class);
		}
	}

	@Override
	public void updatesDbSystemStatus(DbVo vo) throws Exception {
		dynamicSqlTemplate.updateInclude(vo, DbMd.class, new String[] { "system_Status" });
	}

	@Override
	public void deleteDb(DbVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, DbMd.class);
	}

	@Override
	public void deletesDb(DbVo[] vos) throws Exception {
		for (DbVo vo : vos) {
			dynamicSqlTemplate.delete(vo, DbMd.class);
		}
	}

}
