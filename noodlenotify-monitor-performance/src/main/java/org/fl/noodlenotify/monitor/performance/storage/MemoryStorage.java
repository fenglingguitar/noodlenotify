package org.fl.noodlenotify.monitor.performance.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;

public class MemoryStorage {

	private static ConcurrentMap<String, ConcurrentMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>>> storageMap = 
								new ConcurrentHashMap<String, ConcurrentMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>>>();
	
	public static String moduleName = "";
	public static long moduleId = 0;
	
	public static <T> T get(String executerName, String monitorModuleName,
			long monitorModuleId, String queueName, String monitorName,
			Class<T> clazz) throws Exception {
		
		ConcurrentMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>> monitorModuleNameMap = storageMap.get(executerName);
		if (monitorModuleNameMap == null) {
			monitorModuleNameMap = new ConcurrentHashMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>>();
			ConcurrentMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>> monitorModuleNameMapBack = storageMap.putIfAbsent(executerName, monitorModuleNameMap);
			if (monitorModuleNameMapBack != null) {
				monitorModuleNameMap = monitorModuleNameMapBack;
			}
		}
		
		ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>> monitorModuleIdMap = monitorModuleNameMap.get(monitorModuleName);
		if (monitorModuleIdMap == null) {
			monitorModuleIdMap = new ConcurrentHashMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>();
			ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>> monitorModuleNameMapBack = monitorModuleNameMap.putIfAbsent(monitorModuleName, monitorModuleIdMap);
			if (monitorModuleNameMapBack != null) {
				monitorModuleIdMap = monitorModuleNameMapBack;
			}
		}
		
		ConcurrentMap<String, ConcurrentMap<String, Object>> queueNameMap = monitorModuleIdMap.get(monitorModuleId);
		if (queueNameMap == null) {
			queueNameMap = new ConcurrentHashMap<String, ConcurrentMap<String, Object>>();
			ConcurrentMap<String, ConcurrentMap<String, Object>> queueNameMapBack = monitorModuleIdMap.putIfAbsent(monitorModuleId, queueNameMap);
			if (queueNameMapBack != null) {
				queueNameMap = queueNameMapBack;
			}
		}
		
		ConcurrentMap<String, Object> monitorNameMap = queueNameMap.get(queueName);
		if (monitorNameMap == null) {
			monitorNameMap = new ConcurrentHashMap<String, Object>();
			ConcurrentMap<String, Object> monitorNameMapBack = queueNameMap.putIfAbsent(queueName, monitorNameMap);
			if (monitorNameMapBack != null) {
				monitorNameMap = monitorNameMapBack;
			}
		}
		
		@SuppressWarnings("unchecked")
		T object = (T) monitorNameMap.get(monitorName);
		if (object == null) {
			object = clazz.newInstance();
			@SuppressWarnings("unchecked")
			T objectBack = (T) monitorNameMap.putIfAbsent(monitorName, object);
			if (objectBack != null) {
				object = objectBack;
			}
		}
		
		return object;
	}
	
	public static List<KeyVo> getKeys(String executerName) {
		
		List<KeyVo> KeyVoList = new ArrayList<KeyVo>();
		
		ConcurrentMap<String, ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>>> monitorModuleNameMap = storageMap.get(executerName);
		if (monitorModuleNameMap != null) {
			for (String monitorModuleName : monitorModuleNameMap.keySet()) {
				ConcurrentMap<Long, ConcurrentMap<String, ConcurrentMap<String, Object>>> monitorModuleIdMap = monitorModuleNameMap.get(monitorModuleName);
				if (monitorModuleIdMap != null) {
					for (long monitorModuleId : monitorModuleIdMap.keySet()) {
						ConcurrentMap<String, ConcurrentMap<String, Object>> queueNameMap = monitorModuleIdMap.get(monitorModuleId);
						if (queueNameMap != null) {
							for (String queueName : queueNameMap.keySet()) {
								ConcurrentMap<String, Object> monitorNameMap = queueNameMap.get(queueName);
								if (monitorNameMap != null) {
									for (String monitorName : monitorNameMap.keySet()) {
										KeyVo keyVo = new KeyVo();
										keyVo.setExecuterName(executerName);
										keyVo.setModuleName(moduleName);
										keyVo.setModuleId(moduleId);
										keyVo.setMonitorModuleName(monitorModuleName);
										keyVo.setMonitorModuleId(monitorModuleId);
										keyVo.setQueueName(queueName);
										keyVo.setMonitorName(monitorName);
										KeyVoList.add(keyVo);
									}
								}
							}
						}
					}
				}
			}
		}
		
		return KeyVoList;
	}
}
