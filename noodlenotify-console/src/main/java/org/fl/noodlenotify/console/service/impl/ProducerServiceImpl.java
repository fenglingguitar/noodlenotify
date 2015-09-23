package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.domain.ProducerMd;
import org.fl.noodlenotify.console.service.ProducerService;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Service("producerService")
public class ProducerServiceImpl implements ProducerService {

	@Autowired
	private ProducerDao producerDao;

	@Override
	public PageVo<ProducerVo> queryProducerPage(ProducerVo vo, int page, int rows) throws Exception {
		return producerDao.queryProducerPage(vo, page, rows);
	}

	@Override
	public List<ProducerVo> queryProducerList(ProducerVo vo) throws Exception {
		return producerDao.queryProducerList(vo);
	}
	
	@Override
	public List<ProducerVo> queryCheckProducerListWithCache() throws Exception {
		List<ProducerVo> producers = queryCheckProducerList();
		return producers;
	}

	@Override
	public List<ProducerVo> queryCheckProducerList() throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return producerDao.queryProducerList(producerVo);
	}

	@Override
	public void insertProducer(ProducerVo vo) throws Exception {
		producerDao.insertProducer(vo);
	}

	@Override
	public void insertsProducer(ProducerVo[] vos) throws Exception {
		producerDao.insertsProducer(vos);
	}

	@Override
	public void updateProducer(ProducerVo vo) throws Exception {
		producerDao.updateProducer(vo);
	}

	@Override
	public void updatesProducer(ProducerVo[] vos) throws Exception {
		producerDao.updatesProducer(vos);
	}

	@Override
	public void updateProducerSystemStatus(ProducerVo vo) throws Exception {
		producerDao.updateProducerSystemStatus(vo);
	}

	@Override
	public void deleteProducer(ProducerVo vo) throws Exception {
		producerDao.deleteProducer(vo);
	}

	@Override
	public void deletesProducer(ProducerVo[] vos) throws Exception {
		producerDao.deletesProducer(vos);
	}

	@Override
	public long saveRegister(String ip, int checkPort, String checkUrl, String checkType, String name) throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setIp(ip);
		producerVo.setCheck_Port(checkPort);
		List<ProducerVo> producerList = producerDao.queryProducerList(producerVo);
		if (producerList == null || producerList.size() == 0) {
			producerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			producerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			producerVo = producerList.get(0);
		}
		producerVo.setName(name);
		producerVo.setCheck_Url(checkUrl);
		producerVo.setCheck_Type(checkType);
		ProducerMd producerMd = producerDao.insertOrUpdate(producerVo);

		return producerMd.getProducer_Id();
	}

	@Override
	public void saveCancelRegister(long producerId) throws Exception {
		ProducerVo producerVo = new ProducerVo();
		producerVo.setProducer_Id(producerId);
		producerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		producerDao.updateProducerSystemStatus(producerVo);
	}
}
