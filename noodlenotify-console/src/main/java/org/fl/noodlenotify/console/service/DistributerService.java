package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface DistributerService {

	public PageVo<DistributerVo> queryDistributerPage(DistributerVo vo, int page, int rows) throws Exception;

	public List<DistributerVo> queryDistributerList(DistributerVo vo) throws Exception;

	public List<DistributerVo> queryCheckDistributeList() throws Exception;

	public List<DistributerVo> queryCheckDistributeListWithCache() throws Exception;

	public void insertDistributer(DistributerVo vo) throws Exception;

	public void insertsDistributer(DistributerVo[] vos) throws Exception;

	public void updateDistributer(DistributerVo vo) throws Exception;

	public void updatesDistributer(DistributerVo[] vos) throws Exception;

	public void updateDistributerSystemStatus(DistributerVo vo) throws Exception;

	public void deleteDistributer(DistributerVo vo) throws Exception;

	public void deletesDistributer(DistributerVo[] vos) throws Exception;

	public long saveRegister(String ip, int checkPort, String name) throws Exception;

	public void saveCancelRegister(long distributerId) throws Exception;

}
