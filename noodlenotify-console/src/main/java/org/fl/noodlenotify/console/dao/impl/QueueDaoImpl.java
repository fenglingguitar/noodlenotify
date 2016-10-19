package org.fl.noodlenotify.console.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.fl.noodlenotify.console.dao.QueueDao;
import org.fl.noodlenotify.console.domain.QueueMd;
import org.fl.noodle.common.dynamicsql.DynamicSqlTemplate;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodle.common.mvc.vo.PageVo;

@Repository("queueDao")
public class QueueDaoImpl implements QueueDao {

	@Autowired
	private DynamicSqlTemplate dynamicSqlTemplate;

	@Override
	public PageVo<QueueVo> queryQueuePage(QueueVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status",	vo.getManual_Status() != null ? vo.getManual_Status() : null);
		paramsMap.put("is_Repeat", vo.getIs_Repeat() != null ? vo.getIs_Repeat() : null);
		return dynamicSqlTemplate.queryPage("queue-query-list", paramsMap, page, rows, QueueVo.class);
	}

	@Override
	public List<QueueVo> queryQueueList(QueueVo vo) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("name", vo.getQueue_Nm() != null ? vo.getQueue_Nm() : null);
		paramsMap.put("manual_Status",	vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryList("queue-query-list", paramsMap, QueueVo.class);
	}

	@Override
	public void insertQueue(QueueVo vo) throws Exception {
		vo.setCreate_Tm(new Date());
		dynamicSqlTemplate.insert(vo, QueueMd.class);
	}

	@Override
	public void insertsQueue(QueueVo[] vos) throws Exception {
		for (QueueVo vo : vos) {
			vo.setCreate_Tm(new Date());
			dynamicSqlTemplate.insert(vo, QueueMd.class);
		}
	}

	@Override
	public void updateQueue(QueueVo vo) throws Exception {
		dynamicSqlTemplate.updateNonull(vo, QueueMd.class);
	}

	@Override
	public void updatesQueue(QueueVo[] vos) throws Exception {
		for (QueueVo vo : vos) {
			dynamicSqlTemplate.updateNonull(vo, QueueMd.class);
		}
	}

	@Override
	public void deleteQueue(QueueVo vo) throws Exception {
		dynamicSqlTemplate.delete(vo, QueueMd.class);
	}

	@Override
	public void deletesQueue(QueueVo[] vos) throws Exception {
		for (QueueVo vo : vos) {
			dynamicSqlTemplate.delete(vo, QueueMd.class);
		}
	}
	
	@Override
	public PageVo<QueueVo> queryQueueMonitorPage(QueueVo vo, int page, int rows) throws Exception {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("queue_Nm", vo.getQueue_Nm() != null ? (new StringBuilder().append("%").append(vo.getQueue_Nm()).append("%")).toString() : null);
		paramsMap.put("manual_Status",	vo.getManual_Status() != null ? vo.getManual_Status() : null);
		return dynamicSqlTemplate.queryPage("queue-query-monitor-list", paramsMap, page, rows, QueueVo.class);
	}

	public void updatesQueueStatus(List<QueueVo> voList) throws Exception {
		for (QueueVo vo : voList) {
			dynamicSqlTemplate.updateInclude(vo, QueueMd.class, new String[]{
																"rev_T_Cnt_Mit",
																"rev_T_Cnt_Hor",
																"rev_O_Rate_Mit",
																"rev_O_Rate_Hor",
																"rev_E_Rate_Mit",
																"rev_E_Rate_Hor",
																"dph_T_Cnt_Mit",
																"dph_T_Cnt_Hor",
																"dph_O_Rate_Mit",
																"dph_O_Rate_Hor",
																"dph_E_Rate_Mit",
																"dph_E_Rate_Hor",
																"dph_OD_Cnt_Mit",
																"dph_OD_Cnt_Hor"
																});
		}
	}
}
