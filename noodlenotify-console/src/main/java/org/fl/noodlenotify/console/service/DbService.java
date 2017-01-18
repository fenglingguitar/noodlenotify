package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.DbVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface DbService {
	public PageVo<DbVo> queryDbPage(DbVo vo, int page, int rows) throws Exception;

	public List<DbVo> queryDbList(DbVo vo) throws Exception;

	public void insertDb(DbVo vo) throws Exception;

	public void insertsDb(DbVo[] vos) throws Exception;

	public void updateDb(DbVo vo) throws Exception;

	public void updatesDb(DbVo[] vos) throws Exception;

	public void updatesDbSystemStatus(DbVo vo) throws Exception;

	public void deleteDb(DbVo vo) throws Exception;

	public void deletesDb(DbVo[] vos) throws Exception;
}
