package org.fl.noodlenotify.core.connect.cache.body.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManagerAbstract;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheQueueAgent;

public class ExchangeBodyCacheConnectManager extends ConnectManagerAbstract {

	private final static Logger logger = LoggerFactory.getLogger(ExchangeBodyCacheConnectManager.class);
	
	public ExchangeBodyCacheConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		Map<String, List<QueueMsgBodyCacheVo>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.exchangerGetMsgBodyCaches(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Exchanger Get MsgBodyCaches -> " + e
							);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMap.keySet();
				for (String queueName : set) {
					List<QueueMsgBodyCacheVo> list = consoleInfoMap.get(queueName);
					for (QueueMsgBodyCacheVo queueMsgBodyCacheVo : list) {
						logger.debug("UpdateConnectAgent -> ExchangeGetMsgBodyCaches -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
									+ ", Name: " + queueMsgBodyCacheVo.getName()
									+ ", Ip: " + queueMsgBodyCacheVo.getIp()
									+ ", Port: " + queueMsgBodyCacheVo.getPort()
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
					queueAgent = new BodyCacheQueueAgent(queueName);
					queueAgentMap.put(queueName, queueAgent);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				}
				List<QueueMsgBodyCacheVo> queueMsgBodyCacheVoList = consoleInfoMap.get(queueName);
				for (QueueMsgBodyCacheVo queueMsgBodyCacheVo : queueMsgBodyCacheVoList) {
					connectIdSet.add(queueMsgBodyCacheVo.getMsgBodyCache_Id());
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgBodyCacheVo.getMsgBodyCache_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgBodyCacheVo.getIp(),
											queueMsgBodyCacheVo.getPort(), queueMsgBodyCacheVo.getMsgBodyCache_Id());
							try {
								connectAgent.connect();
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName 
											+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
											+ ", Name: " + queueMsgBodyCacheVo.getName()
											+ ", Ip: " + queueMsgBodyCacheVo.getIp()
											+ ", Port: " + queueMsgBodyCacheVo.getPort()
											);
								}
								connectAgentMap.put(queueMsgBodyCacheVo.getMsgBodyCache_Id(), connectAgent);
								connectAgentList.add(connectAgent);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
												+ ", Name: " + queueMsgBodyCacheVo.getName()
												+ ", Ip: " + queueMsgBodyCacheVo.getIp()
												+ ", Port: " + queueMsgBodyCacheVo.getPort()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(queueMsgBodyCacheVo.getIp()) 
									&& connectAgent.getPort() == queueMsgBodyCacheVo.getPort()) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
													+ ", Name: " + queueMsgBodyCacheVo.getName()
													+ ", Ip: " + queueMsgBodyCacheVo.getIp()
													+ ", Port: " + queueMsgBodyCacheVo.getPort()
													);
										}
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
														+ ", Name: " + queueMsgBodyCacheVo.getName()
														+ ", Ip: " + queueMsgBodyCacheVo.getIp()
														+ ", Port: " + queueMsgBodyCacheVo.getPort()
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
											+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgBodyCacheVo.getIp(),
												queueMsgBodyCacheVo.getPort(), queueMsgBodyCacheVo.getMsgBodyCache_Id());
								try {
									connectAgent.connect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Connect -> " 
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
												+ ", Name: " + queueMsgBodyCacheVo.getName()
												+ ", Ip: " + queueMsgBodyCacheVo.getIp()
												+ ", Port: " + queueMsgBodyCacheVo.getPort()
												+ ", Ip Or Port Change"
												);
									}
									connectAgentMap.put(queueMsgBodyCacheVo.getMsgBodyCache_Id(), connectAgent);
									connectAgentList.add(connectAgent);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName 
													+ ", ConnectId: " + queueMsgBodyCacheVo.getMsgBodyCache_Id()
													+ ", Name: " + queueMsgBodyCacheVo.getName()
													+ ", Ip: " + queueMsgBodyCacheVo.getIp()
													+ ", Port: " + queueMsgBodyCacheVo.getPort()
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
}
