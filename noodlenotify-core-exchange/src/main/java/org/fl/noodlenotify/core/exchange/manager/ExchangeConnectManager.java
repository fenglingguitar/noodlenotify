package org.fl.noodlenotify.core.exchange.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;

public class ExchangeConnectManager extends AbstractConnectManager {

	private ModuleRegister exchangeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, Long> queueConsumerGroupNumMap = new ConcurrentHashMap<String, Long>();
	
	private Map<String, Long> groupNumInfoMap = null;
	
	private Map<String, Long> addGroupNumInfoMap = null;
	private List<String> reduceGroupNumInfoList = null;

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

	@Override
	public void runUpdateAddComponent() {
		cleanComponent();
		queryInfo();
		addComponent();
	}

	@Override
	public void runUpdateReduceComponent() {
		cleanComponent();
		queryInfo();
		reduceComponent();
	}
	
	protected void cleanComponent() {
		groupNumInfoMap = null;
	}
	
	protected void addComponent() {
		if (groupNumInfoMap != null) {
			getAddGroupNum();
			addGroupNum();
		}
	}
	
	protected void reduceComponent() {
		if (groupNumInfoMap != null) {
			getReduceGroupNum();
			reduceGroupNum();
		}
	}
	
	protected void queryInfo() {
		try {
			groupNumInfoMap = consoleRemotingInvoke.exchangerGetQueueConsumerGroupNum(exchangeModuleRegister.getModuleId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAddGroupNum() {
		addGroupNumInfoMap = new HashMap<String, Long>();
		for (String name : groupNumInfoMap.keySet()) {
			if (!queueConsumerGroupNumMap.containsKey(name)
					|| !queueConsumerGroupNumMap.get(name).equals(groupNumInfoMap.get(name))) {
				addGroupNumInfoMap.put(name, groupNumInfoMap.get(name));
			}
		}
	}
	
	private void addGroupNum() {
		for (String name : addGroupNumInfoMap.keySet()) {
			queueConsumerGroupNumMap.put(name, addGroupNumInfoMap.get(name));
		}
	}
	
	private void getReduceGroupNum() {
		reduceGroupNumInfoList = new ArrayList<String>();
		for (String name : queueConsumerGroupNumMap.keySet()) {
			if (!groupNumInfoMap.containsKey(name)) {
				reduceGroupNumInfoList.add(name);
			}
		}
	}
	
	private void reduceGroupNum() {
		for (String name : reduceGroupNumInfoList) {
			queueConsumerGroupNumMap.remove(name);
		}
	}
}
