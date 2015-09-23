package org.fl.noodlenotify.core.connect.cache.queue.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManagerAbstract;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheQueueAgent;

public class DistributeQueueCacheConnectManager extends ConnectManagerAbstract {

	private final static Logger logger = LoggerFactory.getLogger(DistributeQueueCacheConnectManager.class);
	
	public DistributeQueueCacheConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}
	
	@Override
	protected void updateConnectAgent() {
		
		Map<String, List<QueueMsgQueueCacheVo>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.distributerGetMsgQueueCaches(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Distribute Get MsgQueueCaches -> " + e);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMap.keySet();
				for (String queueName : set) {
					List<QueueMsgQueueCacheVo> list = consoleInfoMap.get(queueName);
					for (QueueMsgQueueCacheVo queueMsgQueueCacheVo : list) {
						logger.debug("UpdateConnectAgent -> DistributeGetMsgQueueCaches -> List -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
									+ ", Name: " + queueMsgQueueCacheVo.getName()
									+ ", Ip: " + queueMsgQueueCacheVo.getIp()
									+ ", Port: " + queueMsgQueueCacheVo.getPort()
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
					queueAgent = new QueueCacheQueueAgent(queueName);
					queueAgentMap.put(queueName, queueAgent);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				}
				List<QueueMsgQueueCacheVo> queueMsgQueueCacheVoList = consoleInfoMap.get(queueName);
				for (QueueMsgQueueCacheVo queueMsgQueueCacheVo : queueMsgQueueCacheVoList) {
					connectIdSet.add(queueMsgQueueCacheVo.getMsgQueueCache_Id());
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgQueueCacheVo.getMsgQueueCache_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgQueueCacheVo.getIp(),
											queueMsgQueueCacheVo.getPort(), queueMsgQueueCacheVo.getMsgQueueCache_Id());
							try {
								connectAgent.connect();
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName 
											+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
											+ ", Name: " + queueMsgQueueCacheVo.getName()
											+ ", Ip: " + queueMsgQueueCacheVo.getIp()
											+ ", Port: " + queueMsgQueueCacheVo.getPort()
											);
								}
								connectAgentMap.put(queueMsgQueueCacheVo.getMsgQueueCache_Id(), connectAgent);
								connectAgentList.add(connectAgent);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
												+ ", Name: " + queueMsgQueueCacheVo.getName()
												+ ", Ip: " + queueMsgQueueCacheVo.getIp()
												+ ", Port: " + queueMsgQueueCacheVo.getPort()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(queueMsgQueueCacheVo.getIp()) 
									&& connectAgent.getPort() == queueMsgQueueCacheVo.getPort()) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
													+ ", Name: " + queueMsgQueueCacheVo.getName()
													+ ", Ip: " + queueMsgQueueCacheVo.getIp()
													+ ", Port: " + queueMsgQueueCacheVo.getPort()
													);
										}
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
														+ ", Name: " + queueMsgQueueCacheVo.getName()
														+ ", Ip: " + queueMsgQueueCacheVo.getIp()
														+ ", Port: " + queueMsgQueueCacheVo.getPort()
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
											+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgQueueCacheVo.getIp(),
												queueMsgQueueCacheVo.getPort(), queueMsgQueueCacheVo.getMsgQueueCache_Id());
								try {
									connectAgent.connect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Connect -> " 
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
												+ ", Name: " + queueMsgQueueCacheVo.getName()
												+ ", Ip: " + queueMsgQueueCacheVo.getIp()
												+ ", Port: " + queueMsgQueueCacheVo.getPort()
												+ ", Ip Or Port Change"
												);
									}
									connectAgentMap.put(queueMsgQueueCacheVo.getMsgQueueCache_Id(), connectAgent);
									connectAgentList.add(connectAgent);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName 
													+ ", ConnectId: " + queueMsgQueueCacheVo.getMsgQueueCache_Id()
													+ ", Name: " + queueMsgQueueCacheVo.getName()
													+ ", Ip: " + queueMsgQueueCacheVo.getIp()
													+ ", Port: " + queueMsgQueueCacheVo.getPort()
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
