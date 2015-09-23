package org.fl.noodlenotify.console.dao;

import java.util.List;

import org.fl.noodlenotify.console.domain.ProducerMd;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodle.common.mvc.vo.PageVo;

public interface ProducerDao {

	public PageVo<ProducerVo> queryProducerPage(ProducerVo vo, int page, int rows) throws Exception;

	public List<ProducerVo> queryProducerList(ProducerVo vo) throws Exception;

	public List<ProducerVo> queryCheckProducerList(ProducerVo vo) throws Exception;

	public void insertProducer(ProducerVo vo) throws Exception;

	public void insertsProducer(ProducerVo[] vos) throws Exception;

	public void updateProducer(ProducerVo vo) throws Exception;

	public void updatesProducer(ProducerVo[] vos) throws Exception;

	public void deleteProducer(ProducerVo vo) throws Exception;

	public void deletesProducer(ProducerVo[] vos) throws Exception;

	public ProducerMd insertOrUpdate(ProducerVo vo) throws Exception;

	public void updateProducerSystemStatus(ProducerVo vo) throws Exception;

	public boolean ifProducerValid(long producerId) throws Exception;
}
