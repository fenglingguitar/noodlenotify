package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.service.QueueDistributerService;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.service.QueueService;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.persistence.RedisPersistenceTemplate;
import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;
import org.fl.noodlenotify.monitor.performance.vo.OvertimeVo;
import org.fl.noodlenotify.monitor.performance.vo.SuccessVo;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("queueStatusExecuterService")
public class QueueStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(QueueStatusExecuterServiceImpl.class);
	
	@Autowired
	private QueueService queueService;
	
	@Autowired
	private QueueExchangerService queueExchangerService;
	
	@Autowired
	private QueueDistributerService queueDistributerService;

	@Autowired
	private RedisPersistenceTemplate redisPersistenceTemplate;
	
	private long currentTimeMillis = 0;
	
	private long sampleSpaceMinute = 1;
	private long sampleSpaceHoure = 1;

	@Override
	public void execute() throws Exception {

		currentTimeMillis = System.currentTimeMillis();
		
		List<QueueVo> queueVoListResult = new ArrayList<QueueVo>();
		
		List<QueueVo> queueVoList = queueService.queryQueueList(new QueueVo());
		for (QueueVo queueVoIt : queueVoList) {
			
			QueueVo queueVo = new QueueVo();
			queueVo.setQueue_Nm(queueVoIt.getQueue_Nm());
			
			SumOverTimeVo sumRevOverTimeVoMinute = new SumOverTimeVo();
			SumOverTimeVo sumRevOverTimeVoHoure = new SumOverTimeVo();
			SumSuccessVo sumRevSuccessVoMinute = new SumSuccessVo();
			SumSuccessVo sumRevSuccessVoHoure = new SumSuccessVo();
			QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
			queueExchangerVo.setQueue_Nm(queueVoIt.getQueue_Nm());
			List<QueueExchangerVo> queueExchangerVoList = queueExchangerService.queryQueueExchangerList(queueExchangerVo);
			for (QueueExchangerVo queueExchangerVoIt : queueExchangerVoList) {
				sumOverTime(queueExchangerVoIt.getQueue_Nm(), queueExchangerVoIt.getExchanger_Id(), MonitorPerformanceConstant.MODULE_ID_EXCHANGE, MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE, sampleSpaceMinute, 60000, sumRevOverTimeVoMinute);
				sumOverTime(queueExchangerVoIt.getQueue_Nm(), queueExchangerVoIt.getExchanger_Id(), MonitorPerformanceConstant.MODULE_ID_EXCHANGE, MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE, sampleSpaceHoure, 3600000, sumRevOverTimeVoHoure);
				sumSuccess(queueExchangerVoIt.getQueue_Nm(), queueExchangerVoIt.getExchanger_Id(), MonitorPerformanceConstant.MODULE_ID_EXCHANGE, MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE, sampleSpaceMinute, 60000, sumRevSuccessVoMinute);
				sumSuccess(queueExchangerVoIt.getQueue_Nm(), queueExchangerVoIt.getExchanger_Id(), MonitorPerformanceConstant.MODULE_ID_EXCHANGE, MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE, sampleSpaceHoure, 3600000, sumRevSuccessVoHoure);
			}
			queueVo.setRev_T_Cnt_Mit(sumRevOverTimeVoMinute.totalCountSum / sampleSpaceMinute);
			queueVo.setRev_T_Cnt_Hor(sumRevOverTimeVoHoure.totalCountSum / sampleSpaceHoure);
			if (sumRevOverTimeVoMinute.totalCountSum > 0) {
				queueVo.setRev_O_Rate_Mit(1.0f * Math.rint(1.0f * sumRevOverTimeVoMinute.overtimeCountSum / sumRevOverTimeVoMinute.totalCountSum * 10000) / 100);				
			}
			if (sumRevOverTimeVoHoure.totalCountSum > 0) {
				queueVo.setRev_O_Rate_Hor(1.0f * Math.rint(1.0f * sumRevOverTimeVoHoure.overtimeCountSum / sumRevOverTimeVoHoure.totalCountSum * 10000) / 100);
			}
			if (sumRevSuccessVoMinute.totalCountSum > 0) {
				queueVo.setRev_E_Rate_Mit(1.0f * Math.rint(1.0f * (sumRevSuccessVoMinute.totalCountSum - sumRevSuccessVoMinute.successCount) / sumRevSuccessVoMinute.totalCountSum * 10000) / 100);				
			}
			if (sumRevSuccessVoHoure.totalCountSum > 0) {			
				queueVo.setRev_E_Rate_Hor(1.0f * Math.rint(1.0f * (sumRevSuccessVoHoure.totalCountSum - sumRevSuccessVoHoure.successCount) / sumRevSuccessVoHoure.totalCountSum * 10000) / 100);
			}
			
			SumOverTimeVo sumDphOverTimeVoMinute = new SumOverTimeVo();
			SumOverTimeVo sumDphOverTimeVoHoure = new SumOverTimeVo();
			SumSuccessVo sumDphSuccessVoMinute = new SumSuccessVo();
			SumSuccessVo sumDphSuccessVoHoure = new SumSuccessVo();
			SumExpireVo sumDphExpireVoMinute = new SumExpireVo();
			SumExpireVo sumDphExpireVoHoure = new SumExpireVo();
			QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
			queueDistributerVo.setQueue_Nm(queueVoIt.getQueue_Nm());
			List<QueueDistributerVo> queueDistributerVoList = queueDistributerService.queryQueueDistributerList(queueDistributerVo);
			for (QueueDistributerVo queueDistributerVoIt : queueDistributerVoList) {
				sumOverTime(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH, sampleSpaceMinute, 60000, sumDphOverTimeVoMinute);
				sumOverTime(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH, sampleSpaceHoure, 3600000, sumDphOverTimeVoHoure);				
				sumOverTime(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH, sampleSpaceMinute, 60000, sumDphOverTimeVoMinute);
				sumOverTime(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH, sampleSpaceHoure, 3600000, sumDphOverTimeVoHoure);				
				sumSuccess(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH, sampleSpaceMinute, 60000, sumDphSuccessVoMinute);
				sumSuccess(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH, sampleSpaceHoure, 3600000, sumDphSuccessVoHoure);
				sumSuccess(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH, sampleSpaceMinute, 60000, sumDphSuccessVoMinute);
				sumSuccess(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH, sampleSpaceHoure, 3600000, sumDphSuccessVoHoure);
				sumExpire(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_QUEUE_EXPIRE, sampleSpaceMinute, 60000, sumDphExpireVoMinute);
				sumExpire(queueDistributerVoIt.getQueue_Nm(), queueDistributerVoIt.getDistributer_Id(), MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE, MonitorPerformanceConstant.MONITOR_ID_QUEUE_EXPIRE, sampleSpaceHoure, 3600000, sumDphExpireVoHoure);
			}
			queueVo.setDph_T_Cnt_Mit(sumDphOverTimeVoMinute.totalCountSum / sampleSpaceMinute);
			queueVo.setDph_T_Cnt_Hor(sumDphOverTimeVoHoure.totalCountSum / sampleSpaceHoure);
			if (sumDphOverTimeVoMinute.totalCountSum > 0) {
				queueVo.setDph_O_Rate_Mit(1.0f * Math.rint(1.0f * sumDphOverTimeVoMinute.overtimeCountSum / sumDphOverTimeVoMinute.totalCountSum * 10000) / 100);
			}
			if (sumDphOverTimeVoHoure.totalCountSum > 0) {
				queueVo.setDph_O_Rate_Hor(1.0f * Math.rint(1.0f * sumDphOverTimeVoHoure.overtimeCountSum / sumDphOverTimeVoHoure.totalCountSum * 10000) / 100);
			}
			if (sumDphSuccessVoMinute.totalCountSum > 0) {	
				queueVo.setDph_E_Rate_Mit(1.0f * Math.rint(1.0f * (sumDphSuccessVoMinute.totalCountSum - sumDphSuccessVoMinute.successCount) / sumDphSuccessVoMinute.totalCountSum * 10000) / 100);
			}
			if (sumDphSuccessVoHoure.totalCountSum > 0) {			
				queueVo.setDph_E_Rate_Hor(1.0f * Math.rint(1.0f * (sumDphSuccessVoHoure.totalCountSum - sumDphSuccessVoHoure.successCount) / sumDphSuccessVoHoure.totalCountSum * 10000) / 100);
			}
			queueVo.setDph_OD_Cnt_Mit(sumDphExpireVoMinute.expireCount);
			queueVo.setDph_OD_Cnt_Hor(sumDphExpireVoHoure.expireCount);
			
			queueVoListResult.add(queueVo);
		}
		
		queueService.updatesQueueStatus(queueVoListResult);
	}
	
	private void sumOverTime(String queueName, long moduleId, String moduleName, String monitorName, long interval, long unit, SumOverTimeVo sumOverTimeVo) throws Exception {
		
		KeyVo keyVo = new KeyVo();
		keyVo.setExecuterName(MonitorPerformanceConstant.MONITOR_TYPE_OVERTIME);
		keyVo.setModuleId(moduleId);
		keyVo.setModuleName(moduleName);
		keyVo.setMonitorModuleId(moduleId);
		keyVo.setMonitorModuleName(moduleName);
		keyVo.setQueueName(queueName);
		keyVo.setMonitorName(monitorName);
		List<OvertimeVo> overtimeVoList = redisPersistenceTemplate.queryList(keyVo.toKeyString(), currentTimeMillis - interval * unit, currentTimeMillis, OvertimeVo.class);
		for (OvertimeVo overtimeVoIt : overtimeVoList) {
			sumOverTimeVo.totalCountSum += overtimeVoIt.getTotalCount();
			sumOverTimeVo.overtimeCountSum += overtimeVoIt.getOvertimeCount();
		}
	}
	
	private void sumSuccess(String queueName, long moduleId, String moduleName, String monitorName, long interval, long unit, SumSuccessVo sumSuccessVo) throws Exception {
		
		KeyVo keyVo = new KeyVo();
		keyVo.setExecuterName(MonitorPerformanceConstant.MONITOR_TYPE_SUCCESS);
		keyVo.setModuleId(moduleId);
		keyVo.setModuleName(moduleName);
		keyVo.setMonitorModuleId(moduleId);
		keyVo.setMonitorModuleName(moduleName);
		keyVo.setQueueName(queueName);
		keyVo.setMonitorName(monitorName);
		List<SuccessVo> successVoList = redisPersistenceTemplate.queryList(keyVo.toKeyString(), currentTimeMillis - interval * unit, currentTimeMillis, SuccessVo.class);
		for (SuccessVo successVoIt : successVoList) {
			sumSuccessVo.totalCountSum += successVoIt.getTotalCount();
			sumSuccessVo.successCount += successVoIt.getSuccessCount();
		}
	}

	private void sumExpire(String queueName, long moduleId, String moduleName, String monitorName, long interval, long unit, SumExpireVo sumExpireVo) throws Exception {
		KeyVo keyVo = new KeyVo();
		keyVo.setExecuterName(MonitorPerformanceConstant.MONITOR_TYPE_SUCCESS);
		keyVo.setModuleId(moduleId);
		keyVo.setModuleName(moduleName);
		keyVo.setMonitorModuleId(moduleId);
		keyVo.setMonitorModuleName(moduleName);
		keyVo.setQueueName(queueName);
		keyVo.setMonitorName(monitorName);
		List<SuccessVo> successVoList = redisPersistenceTemplate.queryList(keyVo.toKeyString(), currentTimeMillis - interval * unit, currentTimeMillis, SuccessVo.class);
		for (SuccessVo successVoIt : successVoList) {
			sumExpireVo.expireCount += (successVoIt.getTotalCount() - successVoIt.getSuccessCount());
		}
	}
	
	private class SumOverTimeVo {
		public long totalCountSum = 0;
		public long overtimeCountSum = 0;
	}
	
	private class SumSuccessVo {
		public long totalCountSum = 0;
		public long successCount = 0;
	}
	
	private class SumExpireVo {
		public long expireCount = 0;
	}

	public void setSampleSpaceMinute(long sampleSpaceMinute) {
		this.sampleSpaceMinute = sampleSpaceMinute;
	}
	
	public void setSampleSpaceHoure(long sampleSpaceHoure) {
		this.sampleSpaceHoure = sampleSpaceHoure;
	}
}
