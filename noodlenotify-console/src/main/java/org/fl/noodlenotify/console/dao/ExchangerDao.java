package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.domain.ExchangerMd;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface ExchangerDao {
	public PageVo<ExchangerVo> queryExchangerPage(ExchangerVo vo, int page, int rows) throws Exception;

	public List<ExchangerVo> queryExchangerList(ExchangerVo vo) throws Exception;

	public void insertExchanger(ExchangerVo vo) throws Exception;

	public void insertsExchanger(ExchangerVo[] vos) throws Exception;

	public ExchangerMd insertOrUpdate(ExchangerVo vo) throws Exception;

	public void updateExchanger(ExchangerVo vo) throws Exception;

	public void updatesExchanger(ExchangerVo[] vos) throws Exception;

	public void deleteExchanger(ExchangerVo vo) throws Exception;

	public void deletesExchanger(ExchangerVo[] vos) throws Exception;

	public void updateExchangerSystemStatus(ExchangerVo vo) throws Exception;

	public boolean ifExchangerValid(long exchangerId) throws Exception;
	
	public List<ExchangerVo> queryExchangerToOnlineList(ExchangerVo vo) throws Exception;
	public List<ExchangerVo> queryExchangerToOfflineList(ExchangerVo vo) throws Exception;
}
