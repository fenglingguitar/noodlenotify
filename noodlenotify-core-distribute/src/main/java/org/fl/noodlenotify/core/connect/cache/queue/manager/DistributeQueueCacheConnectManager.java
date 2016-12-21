package org.fl.noodlenotify.core.connect.cache.queue.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributeQueueCacheConnectManager extends AbstractConnectManager {

	private final static Logger logger = LoggerFactory.getLogger(DistributeQueueCacheConnectManager.class);
	
	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	protected void updateConnectAgent() {
		
		long moduleId = distributeModuleRegister.getModuleId();
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("DEFALT", connectClusterFactoryMap.get("MASTER").createConnectCluster(QueueCacheConnectAgent.class));
			connectClusterMap.put("OTHER", connectClusterFactoryMap.get("OTHER").createConnectCluster(QueueCacheConnectAgent.class));
			connectClusterMap.put("ALL", connectClusterFactoryMap.get("ALL").createConnectCluster(QueueCacheConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("DEFALT", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
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
				ConnectNode connectNode = connectNodeMap.get(queueName);
				if (connectNode == null) {
					connectNode = new ConnectNodeImpl(queueName);
					connectNodeMap.put(queueName, connectNode);
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
					ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get("CACHE_QUEUE");
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgQueueCacheVo.getMsgQueueCache_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgQueueCacheVo.getMsgQueueCache_Id(), queueMsgQueueCacheVo.getIp(),
											queueMsgQueueCacheVo.getPort(), null);
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
							if (connectAgent.isSameConnect(queueMsgQueueCacheVo.getIp(), queueMsgQueueCacheVo.getPort(), null, ConnectAgentType.QUEUE_CACHE.getCode())) {
								if (!connectAgent.isHealthyConnect()) {
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
											//+ ", Ip: " + connectAgent.getIp()
											//+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgQueueCacheVo.getMsgQueueCache_Id(), queueMsgQueueCacheVo.getIp(),
												queueMsgQueueCacheVo.getPort(), null);
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
				connectNode.updateConnectAgentList(connectAgentList);
				connectAgentList.clear();
			}
			
			Set<String> queueAgentMapSet = connectNodeMap.keySet();
			for (String queueName : queueAgentMapSet) {
				if (!queueNameSet.contains(queueName)) {
					connectNodeMap.remove(queueName);
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
								//+ ", Ip: " + connectAgent.getIp()
								//+ ", Port: " + connectAgent.getPort()
								);
					}
				}
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {
		
		connectNodeMap.clear();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
			if (logger.isDebugEnabled()) {
				logger.debug("DestroyConnectAgent -> Close -> " 
						//+ "ModuleId: " + moduleId
						+ ", ConnectId: " + connectAgent.getConnectId()
						//+ ", Ip: " + connectAgent.getIp()
						//+ ", Port: " + connectAgent.getPort()
						);
			}
		}
		connectAgentMap.clear();
	}
	
	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	@Override
	public String getManagerName() {
		return ConnectManagerType.QUEUE_CACHE.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
