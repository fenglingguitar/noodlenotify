package org.fl.noodlenotify.console.service;

import java.util.List;

import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface ExchangerService {

	public PageVo<ExchangerVo> queryExchangerPage(ExchangerVo vo, int page, int rows) throws Exception;

	public List<ExchangerVo> queryExchangerList(ExchangerVo vo) throws Exception;

	public void insertExchanger(ExchangerVo vo) throws Exception;

	public void insertsExchanger(ExchangerVo[] vos) throws Exception;

	public void updateExchanger(ExchangerVo vo) throws Exception;

	public void updatesExchanger(ExchangerVo[] vos) throws Exception;

	public void updateExchangerSystemStatus(ExchangerVo vo) throws Exception;

	public void deleteExchanger(ExchangerVo vo) throws Exception;

	public void deletesExchanger(ExchangerVo[] vos) throws Exception;
}
