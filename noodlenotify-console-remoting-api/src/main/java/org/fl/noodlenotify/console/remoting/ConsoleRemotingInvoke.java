package org.fl.noodlenotify.console.remoting;

import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;

public interface ConsoleRemotingInvoke {

	public long saveProducerRegister(String ip, int checkPort, String checkUrl, String checkType, String name) throws Exception;

	public void saveProducerCancel(long producerId) throws Exception;

	public long saveExchangerRegister(String ip, int port, String url, String type, int checkPort, String name) throws Exception;

	public void saveExchangerCancel(long exchangerId) throws Exception;

	public long saveDistributerRegister(String ip, int checkPort, String name) throws Exception;

	public void saveDistributerCancel(long distributerId) throws Exception;

	public long saveCustomerRegister(String ip, int port, String url, String type, int checkPort, String check_Url, String check_Type, String name, String customerGroupName, List<String> queueNameList) throws Exception;

	public void saveCustomerCancel(long customerId) throws Exception;

	public Map<String, List<QueueExchangerVo>> producerGetExchangers(long producerId) throws Exception;

	public List<QueueExchangerVo> exchangerGetQueues(long exchangerId) throws Exception;

	public Map<String, List<QueueMsgStorageVo>> exchangerGetMsgStorages(long exchangerId) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> exchangerGetMsgBodyCaches(long exchangerId) throws Exception;

	public Map<String, Long> exchangerGetQueueCustomerGroupNum(long exchangerId) throws Exception;

	public List<QueueDistributerVo> distributerGetQueues(long distributerId) throws Exception;

	public Map<String, List<QueueMsgStorageVo>> distributerGetMsgStorages(long distributerId) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> distributerGetMsgBodyCaches(long distributerId) throws Exception;

	public Map<String, List<QueueMsgQueueCacheVo>> distributerGetMsgQueueCaches(long distributerId) throws Exception;

	public Map<String, List<QueueCustomerVo>> distributerGetQueueCustomers(long distributerId) throws Exception;

	public Map<String, Map<Long, List<QueueCustomerVo>>> distributerGetQueueCustomerGroups(long distributerId) throws Exception;
	
	public void saveProducerBeat(Long producerId) throws Exception;
	public void saveCustomerBeat(Long customerId) throws Exception;
}
