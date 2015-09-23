package org.fl.noodlenotify.console.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.dao.ExchangerDao;
import org.fl.noodlenotify.console.dao.QueueExchangerDao;
import org.fl.noodlenotify.console.domain.ExchangerMd;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

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
	public List<ExchangerVo> queryCheckExchangerListWithCache() throws Exception {
		List<ExchangerVo> exchangers = queryCheckExchangerList();
		return exchangers;
	}

	@Override
	public List<ExchangerVo> queryCheckExchangerList() throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		return exchangerdao.queryExchangerList(exchangerVo);
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
	public long saveRegister(String ip, int port, String url, String type, int checkPort, String name) throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setIp(ip);
		exchangerVo.setCheck_Port(checkPort);
		List<ExchangerVo> exchangerList = exchangerdao.queryExchangerList(exchangerVo);
		if (exchangerList == null || exchangerList.size() == 0) {
			exchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
			exchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		} else {
			exchangerVo = exchangerList.get(0);
		}
		exchangerVo.setPort(port);
		exchangerVo.setUrl(url);
		exchangerVo.setType(type);
		exchangerVo.setName(name);
		ExchangerMd exchangerMd = exchangerdao.insertOrUpdate(exchangerVo);
		
		return exchangerMd.getExchanger_Id();
	}

	@Override
	public void saveCancelRegister(long exchangerId) throws Exception {
		ExchangerVo exchangerVo = new ExchangerVo();
		exchangerVo.setExchanger_Id(exchangerId);
		exchangerVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_OFF_LINE);
		exchangerdao.updateExchangerSystemStatus(exchangerVo);
	}
}
