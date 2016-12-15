package org.fl.noodlenotify.core.connect.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CachePostfix {
	
	private CachePostfix(){}

	public enum KeyPostfix {
		
		QUEUE_NEW("-Queue-New"), 
		QUEUE_PORTION("-Queue-Portion"),
		QUEUE_HASH_NEW("-Queue-Hash-New"),
		QUEUE_HASH_PORTION("-Queue-Hash-Portion"),
		QUEUE_ACTIVE("-Queue-IsActive"),
		QUEUE_DIFFTIME("Queue-DiffTime"),
		QUEUE_LOCKER("-Queue-Locker");
		
		private String code;

		private KeyPostfix(String code) {
			this.code = code;
		}
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}  
	}
	
	private static ConcurrentMap<String, ConcurrentMap<KeyPostfix, String>> postfixMap = new ConcurrentHashMap<String, ConcurrentMap<KeyPostfix, String>>();
	
	public static String getKey(String queueName, KeyPostfix keyPostfix) {
		ConcurrentMap<KeyPostfix, String> postfixTypeMap = postfixMap.get(queueName);
		if (postfixTypeMap == null) {
			postfixTypeMap = new ConcurrentHashMap<KeyPostfix, String>();
			ConcurrentMap<KeyPostfix, String> postfixTypeMapBack = postfixMap.putIfAbsent(queueName, postfixTypeMap);
			if (postfixTypeMapBack != null) {				
				postfixTypeMap = postfixTypeMapBack;
			}
		}
		String postfix = postfixTypeMap.get(keyPostfix);
		if (postfix == null) {
			postfix = queueName + keyPostfix.getCode();
			String postfixBack = postfixTypeMap.put(keyPostfix, postfix);
			if (postfixBack != null) {
				postfix = postfixBack;
			}
		}
		return postfix;
	}
}
