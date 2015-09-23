package org.fl.noodlenotify.monitor.performance.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.note.SuccessNote;
import org.fl.noodlenotify.monitor.performance.executer.service.PerformanceExecuterServiceAbstract;
import org.fl.noodlenotify.monitor.performance.net.UdpClient;
import org.fl.noodlenotify.monitor.performance.net.vo.NetVo;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;
import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;
import org.fl.noodlenotify.monitor.performance.vo.SuccessVo;

@Service("successPerformanceExecuterService")
public class SuccessPerformanceExecuterService extends PerformanceExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(SuccessPerformanceExecuterService.class);
	
	@Autowired
	UdpClient udpClient;
	
	private String executerName = MonitorPerformanceConstant.MONITOR_TYPE_SUCCESS;

	public void result(String monitorModuleName, long monitorModuleId, String queueName, String monitorName, boolean result) {
		
		SuccessNote successNote = null;
		
		try {
			successNote = MemoryStorage.get(executerName, monitorModuleName, monitorModuleId, queueName, monitorName, SuccessNote.class);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Before -> " 
								+ "ExecuterName: " + executerName
								+ "QueueName: " + queueName
								+ "MonitorName: " + monitorName
								+ ", MemoryStorage Get -> " + e);
			}
		}
		
		if (successNote != null) {
			successNote.totalCountAdd();
			if (result) {
				successNote.successCountAdd();
			}
		}
	}

	@Override
	public void send() {
		
		SuccessVo successVo = new SuccessVo();
		NetVo netVo = new NetVo();
		
		List<KeyVo> keyVoList = MemoryStorage.getKeys(executerName);
		for (KeyVo keyVo : keyVoList) {
			
			SuccessNote successNote = null;
			
			try {
				successNote = MemoryStorage.get(executerName, keyVo.getMonitorModuleName(), keyVo.getMonitorModuleId(), keyVo.getQueueName(), keyVo.getMonitorName(), SuccessNote.class);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Send -> " 
									+ "ExecuterName: " + executerName
									+ "QueueName: " + keyVo.getQueueName()
									+ "MonitorName: " + keyVo.getMonitorName()
									+ ", MemoryStorage Get -> " + e);
				}
			}
			
			if (successNote != null) {
				
				long totalCount = successNote.totalCountReset();
				long successCount = successNote.successCountReset();
				
				successVo.setTotalCount(totalCount);
				successVo.setSuccessCount(successCount);
				
				if (totalCount > 0 && successCount > 0) {
					successVo.setSuccessRate(1.0f * Math.rint(1.0f * successCount / totalCount * 10000) / 100);
				}
				
				successVo.setTimestamp(System.currentTimeMillis());
				keyVo.setTimestamp(successVo.getTimestamp());
				
				netVo.setKeyVo(keyVo);
				netVo.setBodyVo(successVo);
				
				try {
					if (sendTypeMap != null) {
						String sendType = sendTypeMap.get(keyVo.getMonitorName());
						if (sendType != null) {
							if (sendType.equals("Log")) {
								if (logger.isInfoEnabled()) {
									logger.info("Key: " + keyVo.toKeyString() + ", Body: " + JsonTranslator.toString(successVo));
								}
								continue;
							}
						}
					}
					udpClient.send(JsonTranslator.toString(netVo));
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("Send -> " 
										+ "ExecuterName: " + executerName
										+ "QueueName: " + keyVo.getQueueName()
										+ "MonitorName: " + keyVo.getMonitorName()
										+ ", JsonTranslator toString -> " + e);
					}
				}
			}
		}
	}
	
	public void setExecuterName(String executerName) {
		this.executerName = executerName;
	}
	
	public static class SuccessPerformanceVo {
		public long startTime;
	}
}
