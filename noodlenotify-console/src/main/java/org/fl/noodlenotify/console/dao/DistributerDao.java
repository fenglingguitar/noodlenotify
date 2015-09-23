package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.domain.DistributerMd;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface DistributerDao {

	public PageVo<DistributerVo> queryDistributerPage(DistributerVo vo, int page, int rows) throws Exception;

	public List<DistributerVo> queryDistributerList(DistributerVo vo) throws Exception;

	public void insertDistributer(DistributerVo vo) throws Exception;

	public void insertsDistributer(DistributerVo[] vos) throws Exception;

	public DistributerMd insertOrUpdate(DistributerVo vo) throws Exception;

	public void updateDistributer(DistributerVo vo) throws Exception;

	public void updatesDistributer(DistributerVo[] vos) throws Exception;

	public void deleteDistributer(DistributerVo vo) throws Exception;

	public void deletesDistributer(DistributerVo[] vos) throws Exception;

	public void updateDistributerSystemStatus(DistributerVo vo) throws Exception;

	public boolean ifDistributerValid(long distributerId) throws Exception;
}
