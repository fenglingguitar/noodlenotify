package org.fl.noodlenotify.core.connect.net.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManagerAbstract;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.net.NetQueueAgent;

public class ProducerNetConnectManager extends ConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ProducerNetConnectManager.class);
	
	Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	public ProducerNetConnectManager() {
	}
	
	public ProducerNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		Map<String, List<QueueExchangerVo>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.producerGetExchangers(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Producer Get Exchangers -> " + e
							);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMap.keySet();
				for (String queueName : set) {
					List<QueueExchangerVo> list = consoleInfoMap.get(queueName);
					for (QueueExchangerVo queueExchangerVo : list) {
						logger.debug("UpdateConnectAgent -> ProducerGetExchangers -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
									+ ", Name: " + queueExchangerVo.getName()
									+ ", Ip: " + queueExchangerVo.getIp()
									+ ", Port: " + queueExchangerVo.getPort()
									);
					}
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			List<ConnectAgent> connectAgentList = new ArrayList<ConnectAgent>();
			Set<String> queueNameSet = consoleInfoMap.keySet();
			for (String queueName : queueNameSet) {
				QueueAgent queueAgent = queueAgentMap.get(queueName);
				if (queueAgent == null) {
					queueAgent = new NetQueueAgent(queueName);
					queueAgentMap.put(queueName, queueAgent);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				}
				List<QueueExchangerVo> queueExchangerVoList = consoleInfoMap.get(queueName);
				for (QueueExchangerVo queueExchangerVo : queueExchangerVoList) {
					connectIdSet.add(queueExchangerVo.getExchanger_Id());
					if (connectAgentFactoryMap != null) {
						ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(queueExchangerVo.getType());
						ConnectAgent connectAgent = connectAgentMap.get(queueExchangerVo.getExchanger_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueExchangerVo.getIp(),
											queueExchangerVo.getPort(), queueExchangerVo.getUrl(), queueExchangerVo.getExchanger_Id());
							try {
								connectAgent.connect();
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName
											+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
											+ ", Name: " + queueExchangerVo.getName()
											+ ", Ip: " + queueExchangerVo.getIp()
											+ ", Port: " + queueExchangerVo.getPort()
											);
								}
								connectAgentMap.put(queueExchangerVo.getExchanger_Id(), connectAgent);
								connectAgentList.add(connectAgent);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName
												+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
												+ ", Name: " + queueExchangerVo.getName()
												+ ", Ip: " + queueExchangerVo.getIp()
												+ ", Port: " + queueExchangerVo.getPort()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(queueExchangerVo.getIp()) 
									&& connectAgent.getPort() == queueExchangerVo.getPort()
										&& (connectAgent.getUrl() != null && queueExchangerVo.getUrl() != null && connectAgent.getUrl().equals(queueExchangerVo.getUrl()) || (connectAgent.getUrl() == null && queueExchangerVo.getUrl() == null))
											&& connectAgent.getType().equals(queueExchangerVo.getType())) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
													+ ", Name: " + queueExchangerVo.getName()
													+ ", Ip: " + queueExchangerVo.getIp()
													+ ", Port: " + queueExchangerVo.getPort()
													);
										}
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
														+ ", Name: " + queueExchangerVo.getName()
														+ ", Ip: " + queueExchangerVo.getIp()
														+ ", Port: " + queueExchangerVo.getPort()
														+ ", ConnectAgent Reconnect -> " + e
														);
										}
									}
								} else {
									connectAgentList.add(connectAgent);
								}
							} else {
								connectAgent.close();
								connectAgentMap.remove(connectAgent.getConnectId());
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Remove Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName
											+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueExchangerVo.getIp(),
												queueExchangerVo.getPort(), queueExchangerVo.getUrl(), queueExchangerVo.getExchanger_Id());
								try {
									connectAgent.connect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Change -> " 
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName
												+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
												+ ", Name: " + queueExchangerVo.getName()
												+ ", Ip: " + queueExchangerVo.getIp()
												+ ", Port: " + queueExchangerVo.getPort()
												+ ", Ip Or Port Change"
												);
									}
									connectAgentMap.put(queueExchangerVo.getExchanger_Id(), connectAgent);
									connectAgentList.add(connectAgent);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
													+ ", Name: " + queueExchangerVo.getName()
													+ ", Ip: " + queueExchangerVo.getIp()
													+ ", Port: " + queueExchangerVo.getPort()
													+ ", Ip Or Port Change, ConnectAgent Connect -> " + e
													);
									}
								}
							}
						}
					}
				}
				queueAgent.updateConnectAgents(connectAgentList);
				connectAgentList.clear();
			}
			
			Set<String> queueAgentMapSet = queueAgentMap.keySet();
			for (String queueName : queueAgentMapSet) {
				if (!queueNameSet.contains(queueName)) {
					queueAgentMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				}
			}
			
			Set<Long> connectAgentMapSet = connectAgentMap.keySet();
			for (long connectId : connectAgentMapSet) {
				if (!connectIdSet.contains(connectId)) {
					ConnectAgent connectAgent = connectAgentMap.get(connectId);
					connectAgent.close();
					connectAgentMap.remove(connectId);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Connect -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + connectAgent.getConnectId()
								+ ", Ip: " + connectAgent.getIp()
								+ ", Port: " + connectAgent.getPort()
								);
					}
				}
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {

		queueAgentMap.clear();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
			if (logger.isDebugEnabled()) {
				logger.debug("DestroyConnectAgent -> Close -> " 
						+ "ModuleId: " + moduleId
						+ ", ConnectId: " + connectAgent.getConnectId()
						+ ", Ip: " + connectAgent.getIp()
						+ ", Port: " + connectAgent.getPort()
						);
			}
		}
		connectAgentMap.clear();
	}
	
	public void setConnectAgentFactoryMap(
			Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}
}
