package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.DistributerDao;
import org.fl.noodlenotify.console.dao.QueueDistributerDao;
import org.fl.noodlenotify.console.domain.DistributerMd;
import org.fl.noodlenotify.console.service.DistributerService;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("distributerService")
public class DistributerServiceImpl implements DistributerService {

	@Autowired
	private DistributerDao distributerDao;

	@Autowired
	private QueueDistributerDao queueDistributerDao;

	@Override
	public PageVo<DistributerVo> queryDistributerPage(DistributerVo vo, int page, int rows) throws Exception {
		return distributerDao.queryDistributerPage(vo, page, rows);
	}

	@Override
	public List<DistributerVo> queryDistributerList(DistributerVo vo) throws Exception {
		return distributerDao.queryDistributerList(vo);
	}
	
	@Override
	public List<DistributerVo> queryCheckDistributeListWithCache() throws Exception {
		List<DistributerVo> distributers = queryCheckDistributeList();
		return distributers;
	}

	@Override
	public List<DistributerVo> queryCheckDistributeList() throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return distributerDao.queryDistributerList(distributerVo);
	}

	@Override
	public void insertDistributer(DistributerVo vo) throws Exception {
		distributerDao.insertDistributer(vo);
	}

	@Override
	public void insertsDistributer(DistributerVo[] vos) throws Exception {
		distributerDao.insertsDistributer(vos);
	}

	@Override
	public void updateDistributer(DistributerVo vo) throws Exception {
		distributerDao.updateDistributer(vo);
	}

	@Override
	public void updatesDistributer(DistributerVo[] vos) throws Exception {
		distributerDao.updatesDistributer(vos);
	}

	@Override
	public void updateDistributerSystemStatus(DistributerVo vo) throws Exception {
		distributerDao.updateDistributerSystemStatus(vo);
	}

	@Override
	public void deleteDistributer(DistributerVo vo) throws Exception {
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(vo.getDistributer_Id());
		queueDistributerDao.deleteQueueDistributerByDistributerId(queueDistributerVo);
		distributerDao.deleteDistributer(vo);
	}

	@Override
	public void deletesDistributer(DistributerVo[] vos) throws Exception {
		for (DistributerVo vo : vos) {
			QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
			queueDistributerVo.setDistributer_Id(vo.getDistributer_Id());
			queueDistributerDao.deleteQueueDistributerByDistributerId(queueDistributerVo);
			distributerDao.deleteDistributer(vo);
		}
	}

	@Override
	public long saveRegister(String ip, int checkPort, String name) throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setIp(ip);
		distributerVo.setCheck_Port(checkPort);
		List<DistributerVo> distributerList = distributerDao.queryDistributerList(distributerVo);
		if (distributerList == null || distributerList.size() == 0) {
			distributerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			distributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			distributerVo = distributerList.get(0);
		}
		distributerVo.setName(name);
		DistributerMd distributerMd = distributerDao.insertOrUpdate(distributerVo);

		return distributerMd.getDistributer_Id();
	}

	@Override
	public void saveCancelRegister(long distributerId) throws Exception {
		DistributerVo distributerVo = new DistributerVo();
		distributerVo.setDistributer_Id(distributerId);
		distributerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		distributerDao.updateDistributerSystemStatus(distributerVo);
	}
}
