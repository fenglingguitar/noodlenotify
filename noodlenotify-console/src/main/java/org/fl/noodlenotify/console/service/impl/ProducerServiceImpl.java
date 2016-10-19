package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.ProducerDao;
import org.fl.noodlenotify.console.service.ProducerService;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public void updateClientOnline(ProducerVo vo) throws Exception {
		producerDao.updateClientOnline(vo);
	}

	@Override
	public void updateClientOffline(ProducerVo vo) throws Exception {
		producerDao.updateClientOffline(vo);
	}
}
