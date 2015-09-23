package org.fl.noodlenotify.monitor.performance.storage;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalStorage {

	private static final ThreadLocal<Map<String, Map<String, Map<Long, Map<String, Map<String, Object>>>>>> executerMapThreadLocal = 
									new ThreadLocal<Map<String, Map<String, Map<Long, Map<String, Map<String, Object>>>>>>();
	
	public static <T> T get(String executerName, String monitorModuleName,
			long monitorModuleId, String queueName, String monitorName,
			Class<T> clazz) throws Exception {
		
		Map<String, Map<String, Map<Long, Map<String, Map<String, Object>>>>> executerNameMap = executerMapThreadLocal.get();
		if (executerNameMap == null) {
			executerNameMap = new HashMap<String, Map<String, Map<Long, Map<String, Map<String, Object>>>>>();
			executerMapThreadLocal.set(executerNameMap);
		}
		
		Map<String, Map<Long, Map<String, Map<String, Object>>>> monitorModuleNameMap = executerNameMap.get(executerName);
		if (monitorModuleNameMap == null) {
			monitorModuleNameMap = new HashMap<String, Map<Long, Map<String, Map<String, Object>>>>();
			executerNameMap.put(executerName, monitorModuleNameMap);
		}
		
		Map<Long, Map<String, Map<String, Object>>> monitorModuleIdMap = monitorModuleNameMap.get(monitorModuleName);
		if (monitorModuleIdMap == null) {
			monitorModuleIdMap = new HashMap<Long, Map<String, Map<String, Object>>>();
			monitorModuleNameMap.put(monitorModuleName, monitorModuleIdMap);
		}
		
		Map<String, Map<String, Object>> queueNameMap = monitorModuleIdMap.get(monitorModuleId);
		if (queueNameMap == null) {
			queueNameMap = new HashMap<String, Map<String, Object>>();
			monitorModuleIdMap.put(monitorModuleId, queueNameMap);
		}
		
		Map<String, Object> monitorNameMap = queueNameMap.get(queueName);
		if (monitorNameMap == null) {
			monitorNameMap = new HashMap<String, Object>();
			queueNameMap.put(queueName, monitorNameMap);
		}
		
		@SuppressWarnings("unchecked")
		T object = (T) monitorNameMap.get(monitorName);
		if (object == null) {
			object = clazz.newInstance();
			monitorNameMap.put(monitorName, object);
		}
		
		return object;
	}
}
