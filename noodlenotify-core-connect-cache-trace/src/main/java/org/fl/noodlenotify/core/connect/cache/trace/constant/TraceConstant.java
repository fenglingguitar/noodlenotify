package org.fl.noodlenotify.core.connect.cache.trace.constant;

public class TraceConstant {
	
	public final static int ACTION_TYPE_EXCHANGE_DB_INSERT = 1;
	public final static int ACTION_TYPE_EXCHANGE_RECEIVE = 2;
	public final static int ACTION_TYPE_CUSTOMER_SEND = 3;
	public final static int ACTION_TYPE_DISTRIBUTE_DISPATCH = 4;
	public final static int ACTION_TYPE_DISTRIBUTE_AGAIN_DISPATCH = 5;
	
	public final static byte RESULT_TYPE_SUCCESS = 1;
	public final static byte RESULT_TYPE_FAIL = 0;
	public final static byte RESULT_TYPE_PORTION_SUCCESS = 2;
	public final static byte RESULT_TYPE_EXPIRE = 3;
	public final static byte RESULT_TYPE_DB_UPDATE_FAIL = 4;
	
	public final static byte MODULE_TYPE_EXCHANGE = 1;
	public final static byte MODULE_TYPE_DISTRIBUTE = 2;
	public final static byte MODULE_TYPE_DB = 3;
	public final static byte MODULE_TYPE_CUSTOMER = 4;
	public final static byte MODULE_TYPE_CUSTOMER_GROUP = 5;
	
	public final static byte IS_TRACE_YES = 1;
	public final static byte IS_TRACE_NO = 0;
}
