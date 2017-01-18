package org.fl.noodlenotify.console.remoting;

import java.util.List;
import java.util.Map;

import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;

public interface ConsoleRemotingInvoke {

	public long saveProducerRegister(String ip, String name) throws Exception;

	public void saveProducerCancel(long producerId) throws Exception;

	public long saveExchangerRegister(String ip, int port, String url, String type, String name) throws Exception;

	public void saveExchangerCancel(long exchangerId) throws Exception;

	public long saveDistributerRegister(String ip, String name) throws Exception;

	public void saveDistributerCancel(long distributerId) throws Exception;

	public long saveConsumerRegister(String ip, int port, String url, String type, String name, String consumerGroupName, List<String> queueNameList) throws Exception;

	public void saveConsumerCancel(long consumerId) throws Exception;

	public Map<String, List<QueueExchangerVo>> producerGetExchangers(long producerId) throws Exception;

	public List<QueueExchangerVo> exchangerGetQueues(long exchangerId) throws Exception;

	public Map<String, List<QueueMsgStorageVo>> exchangerGetMsgStorages(long exchangerId) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> exchangerGetMsgBodyCaches(long exchangerId) throws Exception;

	public Map<String, Long> exchangerGetQueueConsumerGroupNum(long exchangerId) throws Exception;

	public Map<QueueDistributerVo, List<QueueMsgStorageVo>> distributerGetQueues(long distributerId) throws Exception;

	public Map<String, List<QueueMsgStorageVo>> distributerGetMsgStorages(long distributerId) throws Exception;

	public Map<String, List<QueueMsgBodyCacheVo>> distributerGetMsgBodyCaches(long distributerId) throws Exception;

	public Map<String, List<QueueMsgQueueCacheVo>> distributerGetMsgQueueCaches(long distributerId) throws Exception;

	public Map<String, List<QueueConsumerVo>> distributerGetQueueConsumers(long distributerId) throws Exception;

	public Map<String, Map<Long, List<QueueConsumerVo>>> distributerGetQueueConsumerGroups(long distributerId) throws Exception;
	
	public void saveProducerBeat(Long producerId) throws Exception;
	public void saveConsumerBeat(Long consumerId) throws Exception;
	public void saveExchangeBeat(Long exchangeId) throws Exception;
}
