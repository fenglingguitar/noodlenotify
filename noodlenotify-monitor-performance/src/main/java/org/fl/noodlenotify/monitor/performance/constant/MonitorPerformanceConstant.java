package org.fl.noodlenotify.monitor.performance.constant;

public class MonitorPerformanceConstant {
	
	public final static String MONITOR_TYPE_OVERTIME = "OVERTIME";
	public final static String MONITOR_TYPE_SUCCESS = "SUCCESS";
	
	public final static String MODULE_ID_EXCHANGE = "EXCHANGE";
	public final static String MODULE_NAME_EXCHANGE = "交换中心";
	
	public final static String MODULE_ID_DISTRIBUTE = "DISTRIBUTE";
	public final static String MODULE_NAME_DISTRIBUTE = "分配中心";
	
	public final static String MODULE_ID_DB = "DB";
	public final static String MODULE_NAME_DB = "数据库";
	
	public final static String MODULE_ID_BODYCACHE = "BODYCACHE";
	public final static String MODULE_NAME_BODYCACHE = "消息体缓存";
	
	public final static String MODULE_ID_QUEUECACHE = "QUEUECACHE";
	public final static String MODULE_NAME_QUEUECACHE = "队列缓存";
	
	public final static String MODULE_ID_CUSTOMER = "CUSTOMER";
	public final static String MODULE_NAME_CUSTOMER = "消费者";
	
	
	public final static String MONITOR_ID_EXCHANGE_RECEIVE = "EXCHANGE_RECEIVE";
	public final static String MONITOR_NAME_EXCHANGE_RECEIVE = "接收";
	
	public final static String MONITOR_ID_DISTRIBUTE_NEW_DISPATCH = "DISTRIBUTE_NEW_DISPATCH";
	public final static String MONITOR_NAME_DISTRIBUTE_NEW_DISPATCH = "新分派";
	
	public final static String MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH = "DISTRIBUTE_PORTION_DISPATCH";
	public final static String MONITOR_NAME_DISTRIBUTE_PORTION_DISPATCH = "未完成分派";
	
	
	public final static String MONITOR_ID_DB_INSERT = "DB_INSERT";
	public final static String MONITOR_NAME_DB_INSERT = "插入";
	
	public final static String MONITOR_ID_DB_UPDATE = "DB_UPDATE";
	public final static String MONITOR_NAME_DB_UPDATE = "更新";
	
	public final static String MONITOR_ID_DB_DELETE = "DB_DELETE";
	public final static String MONITOR_NAME_DB_DELETE = "删除";
	
	
	public final static String MONITOR_ID_BODYCACHE_SET = "BODYCACHE_SET";
	public final static String MONITOR_NAME_BODYCACHE_SET = "插入";
	
	public final static String MONITOR_ID_BODYCACHE_GET = "BODYCACHE_GET";
	public final static String MONITOR_NAME_BODYCACHE_GET = "获取";
	
	public final static String MONITOR_ID_BODYCACHE_REMOVE = "BODYCACHE_REMOVE";
	public final static String MONITOR_NAME_BODYCACHE_REMOVE = "删除";
	
	
	public final static String MONITOR_ID_QUEUECACHE_NEW_SET = "QUEUECACHE_NEW_SET";
	public final static String MONITOR_NAME_QUEUECACHE_NEW_SET = "新插入";
	
	public final static String MONITOR_ID_QUEUECACHE_PORTION_SET = "QUEUECACHE_PORTION_SET";
	public final static String MONITOR_NAME_QUEUECACHE_PORTION_SET = "未完成插入";
	
	public final static String MONITOR_ID_QUEUECACHE_NEW_POP = "QUEUECACHE_NEW_POP";
	public final static String MONITOR_NAME_QUEUECACHE_NEW_POP = "新获取";
	
	public final static String MONITOR_ID_QUEUECACHE_PORTION_POP = "QUEUECACHE_PORTION_POP";
	public final static String MONITOR_NAME_QUEUECACHE_PORTION_POP = "未完成获取";
	
	public final static String MONITOR_ID_CUSTOMER_SEND = "CUSTOMER_SEND";
	public final static String MONITOR_NAME_CUSTOMER_SEND = "发送";
	
	public final static String MONITOR_ID_QUEUE_EXPIRE = "QUEUE_EXPIRE";
}
