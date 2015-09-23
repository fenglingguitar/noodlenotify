package org.fl.noodlenotify.monitor.performance.executer.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.monitor.performance.constant.MonitorPerformanceConstant;
import org.fl.noodlenotify.monitor.performance.executer.note.OvertimeNote;
import org.fl.noodlenotify.monitor.performance.executer.service.PerformanceExecuterServiceAbstract;
import org.fl.noodlenotify.monitor.performance.net.UdpClient;
import org.fl.noodlenotify.monitor.performance.net.vo.NetVo;
import org.fl.noodlenotify.monitor.performance.storage.MemoryStorage;
import org.fl.noodlenotify.monitor.performance.storage.ThreadLocalStorage;
import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;
import org.fl.noodlenotify.monitor.performance.vo.OvertimeVo;

@Service("overtimePerformanceExecuterService")
public class OvertimePerformanceExecuterService extends PerformanceExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(OvertimePerformanceExecuterService.class);
	
	@Autowired
	UdpClient udpClient;
	
	private String executerName = MonitorPerformanceConstant.MONITOR_TYPE_OVERTIME;
	
	private long threshold = 200;
	private Map<String, Long> thresholdMap;
	
	public void before(String monitorModuleName, long monitorModuleId, String queueName, String monitorName) {
		
		OvertimePerformanceVo overtimePerformanceVo = null;
		
		try {
			overtimePerformanceVo = ThreadLocalStorage.get(executerName, monitorModuleName, monitorModuleId, queueName, monitorName, OvertimePerformanceVo.class);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Before -> " 
								+ "ExecuterName: " + executerName
								+ "QueueName: " + queueName
								+ "MonitorName: " + monitorName
								+ ", ThreadLocalStorage Get -> " + e);
			}
		}
		
		if (overtimePerformanceVo != null) {
			overtimePerformanceVo.startTime = System.currentTimeMillis();
		}
	}

	public void after(String monitorModuleName, long monitorModuleId, String queueName, String monitorName) {
		
		long elapseTime = 0;
		
		OvertimePerformanceVo overtimePerformanceVo = null;
		
		try {
			overtimePerformanceVo = ThreadLocalStorage.get(executerName, monitorModuleName, monitorModuleId, queueName, monitorName, OvertimePerformanceVo.class);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Before -> " 
								+ "ExecuterName: " + executerName
								+ "QueueName: " + queueName
								+ "MonitorName: " + monitorName
								+ ", ThreadLocalStorage Get -> " + e);
			}
		}
		
		if (overtimePerformanceVo != null) {
			long startTime = overtimePerformanceVo.startTime;
			long endTime = System.currentTimeMillis();
			elapseTime = endTime - startTime;
		}
		
		OvertimeNote overtimeNote = null;
		
		try {
			overtimeNote = MemoryStorage.get(executerName, monitorModuleName, monitorModuleId, queueName, monitorName, OvertimeNote.class);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("After -> " 
								+ "ExecuterName: " + executerName
								+ "QueueName: " + queueName
								+ "MonitorName: " + monitorName
								+ ", MemoryStorage Get -> " + e);
			}
		}
		
		if (overtimeNote != null) {
			overtimeNote.totalCountAdd();
			overtimeNote.totalTimeAdd(elapseTime);
			long thresholdTemp = threshold;
			if (thresholdMap != null) {
				Long thresholdSpecial = thresholdMap.get(monitorName);
				if (thresholdSpecial != null) {
					thresholdTemp = thresholdSpecial;
				}
			}
			if (elapseTime > thresholdTemp) {
				overtimeNote.overtimeCountAdd();
				overtimeNote.overtimeTimeAdd(elapseTime);
	        }
		}
	}

	@Override
	public void send() {
		
		OvertimeVo overtimeVo = new OvertimeVo();
		NetVo netVo = new NetVo();
		
		List<KeyVo> keyVoList = MemoryStorage.getKeys(executerName);
		for (KeyVo keyVo : keyVoList) {
			
			OvertimeNote overtimeNote = null;
			
			try {
				overtimeNote = MemoryStorage.get(executerName, keyVo.getMonitorModuleName(), keyVo.getMonitorModuleId(), keyVo.getQueueName(), keyVo.getMonitorName(), OvertimeNote.class);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Send -> " 
									+ "ExecuterName: " + executerName
									+ "QueueName: " + keyVo.getQueueName()
									+ "MonitorName: " + keyVo.getMonitorName()
									+ ", MemoryStorage Get -> " + e);
				}
			}
			
			if (overtimeNote != null) {
				
				long totalCount = overtimeNote.totalCountReset();
				long overtimeCount = overtimeNote.overtimeCountReset();
				long totalTime = overtimeNote.totalTimeReset();
				long overtimeTime = overtimeNote.overtimeTimeReset();
				
				overtimeVo.setTotalCount(totalCount);
				overtimeVo.setOvertimeCount(overtimeCount);
				overtimeVo.setTotalTime(totalTime);
				overtimeVo.setOvertimeTime(overtimeTime);
				
				if (totalCount > 0 && totalTime > 0) {
					overtimeVo.setAverageTotalTime(totalTime / totalCount);
				} else {
					overtimeVo.setAverageTotalTime(0);
				}
				
				if (overtimeCount > 0 && overtimeTime > 0) {
					overtimeVo.setAverageOvertimeTime(overtimeTime / overtimeCount);
				} else {
					overtimeVo.setAverageOvertimeTime(0);
				}
				
				long thresholdTemp = threshold;
				if (thresholdMap != null) {
					Long thresholdSpecial = thresholdMap.get(keyVo.getMonitorName());
					if (thresholdSpecial != null) {
						thresholdTemp = thresholdSpecial;
					}
				}
				overtimeVo.setThreshold(thresholdTemp);
				
				overtimeVo.setTimestamp(System.currentTimeMillis());
				keyVo.setTimestamp(overtimeVo.getTimestamp());
				
				netVo.setKeyVo(keyVo);
				netVo.setBodyVo(overtimeVo);
				
				try {
					if (sendTypeMap != null) {
						String sendType = sendTypeMap.get(keyVo.getMonitorName());
						if (sendType != null) {
							if (sendType.equals("Log")) {
								if (logger.isInfoEnabled()) {
									logger.info("Key: " + keyVo.toKeyString() + ", Body: " + JsonTranslator.toString(overtimeVo));
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
	
	public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

	public void setThresholdMap(Map<String, Long> thresholdMap) {
		this.thresholdMap = thresholdMap;
	}

	public static class OvertimePerformanceVo {
		public long startTime;
	}
}
