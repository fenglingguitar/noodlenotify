package org.fl.noodlenotify.core.exchange.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;

public class ExchangeConnectManager extends AbstractConnectManager {

	private ModuleRegister exchangeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, Long> queueConsumerGroupNumMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentMap<String, QueueExchangerVo> queueExchangerVoMap = new ConcurrentHashMap<String, QueueExchangerVo>();
	
	@Override
	protected void updateConnectAgent() {

		long moduleId = exchangeModuleRegister.getModuleId();
		
		Map<String, Long> consoleInfoMapGroupNum = null;
		
		try {
			consoleInfoMapGroupNum = consoleRemotingInvoke.exchangerGetQueueConsumerGroupNum(moduleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (consoleInfoMapGroupNum != null) {
			
			for (String queueName : consoleInfoMapGroupNum.keySet()) {
				if (!queueConsumerGroupNumMap.containsKey(queueName)) {
					queueConsumerGroupNumMap.putIfAbsent(queueName, consoleInfoMapGroupNum.get(queueName));
				} else {
					Long queueConsumerGroupNumOld = queueConsumerGroupNumMap.get(queueName);
					Long queueConsumerGroupNumNew = consoleInfoMapGroupNum.get(queueName);
					if (!queueConsumerGroupNumOld.equals(queueConsumerGroupNumNew)) {
						queueConsumerGroupNumMap.remove(queueName);
						queueConsumerGroupNumMap.put(queueName, queueConsumerGroupNumNew);
					}
				}
			}
			
			for (String queueName : queueConsumerGroupNumMap.keySet()) {
				if (!consoleInfoMapGroupNum.containsKey(queueName)) {
					queueConsumerGroupNumMap.remove(queueName);
				}
			}
		}
		
		List<QueueExchangerVo> consoleInfoMapQueues = null;
		
		try {
			consoleInfoMapQueues = consoleRemotingInvoke.exchangerGetQueues(moduleId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (consoleInfoMapQueues != null) {
			
			Set<String> queueNameSet = new HashSet<String>();
			
			for (QueueExchangerVo queueExchangerVo : consoleInfoMapQueues) {
				queueNameSet.add(queueExchangerVo.getQueue_Nm());
				if (!queueExchangerVoMap.containsKey(queueExchangerVo.getQueue_Nm())) {
					queueExchangerVoMap.put(queueExchangerVo.getQueue_Nm(), queueExchangerVo);
				}
			}
			
			for (String queueName : queueExchangerVoMap.keySet()) {
				if (!queueNameSet.contains(queueName)) {
					QueueExchangerVo queueExchangerVoOld = queueExchangerVoMap.get(queueName);
					queueExchangerVoMap.remove(queueExchangerVoOld.getQueue_Nm());
				} 
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {
		queueConsumerGroupNumMap.clear();
	}

	@Override
	public String getManagerName() {
		return ConnectManagerType.EXCHANGE.getCode();
	}

	public ConcurrentMap<String, Long> getQueueConsumerGroupNumMap() {
		return queueConsumerGroupNumMap;
	}
	
	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
}
