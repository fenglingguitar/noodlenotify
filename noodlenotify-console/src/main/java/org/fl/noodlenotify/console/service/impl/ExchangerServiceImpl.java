package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("exchangerService")
public class ExchangerServiceImpl implements ExchangerService {

	@Autowired
	private ExchangerDao exchangerdao;

	@Autowired
	private QueueExchangerDao queueExchangerDao;

	@Override
	public PageVo<ExchangerVo> queryExchangerPage(ExchangerVo vo, int page, int rows) throws Exception {
		return exchangerdao.queryExchangerPage(vo, page, rows);
	}

	@Override
	public List<ExchangerVo> queryExchangerList(ExchangerVo vo) throws Exception {
		return exchangerdao.queryExchangerList(vo);
	}

	@Override
	public void insertExchanger(ExchangerVo vo) throws Exception {
		exchangerdao.insertExchanger(vo);
	}

	@Override
	public void insertsExchanger(ExchangerVo[] vos) throws Exception {
		exchangerdao.insertsExchanger(vos);
	}

	@Override
	public void updateExchanger(ExchangerVo vo) throws Exception {
		exchangerdao.updateExchanger(vo);
	}

	@Override
	public void updatesExchanger(ExchangerVo[] vos) throws Exception {
		exchangerdao.updatesExchanger(vos);
	}

	@Override
	public void updateExchangerSystemStatus(ExchangerVo vo) throws Exception {
		exchangerdao.updateExchangerSystemStatus(vo);
	}

	@Override
	public void deleteExchanger(ExchangerVo vo) throws Exception {
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(vo.getExchanger_Id());
		queueExchangerDao.deleteQueueExchangerByExchangerId(queueExchangerVo);
		exchangerdao.deleteExchanger(vo);
	}

	@Override
	public void deletesExchanger(ExchangerVo[] vos) throws Exception {
		for (ExchangerVo vo : vos) {
			QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
			queueExchangerVo.setExchanger_Id(vo.getExchanger_Id());
			queueExchangerDao.deleteQueueExchangerByExchangerId(queueExchangerVo);
			exchangerdao.deleteExchanger(vo);
		}
	}

	@Override
	public List<ExchangerVo> queryExchangerToOnlineList(ExchangerVo vo) throws Exception {
		return exchangerdao.queryExchangerToOnlineList(vo);
	}

	@Override
	public List<ExchangerVo> queryExchangerToOfflineList(ExchangerVo vo) throws Exception {
		return exchangerdao.queryExchangerToOfflineList(vo);
	}
}
