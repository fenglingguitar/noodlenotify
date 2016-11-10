package org.fl.noodlenotify.core.connect.cache.body.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeBodyCacheConnectManager extends AbstractConnectManager {

	private final static Logger logger = LoggerFactory.getLogger(ExchangeBodyCacheConnectManager.class);

	private ModuleRegister exchangeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	protected void updateConnectAgent() {
		
		long moduleId = exchangeModuleRegister.getModuleId();
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("DEFALT", connectClusterFactoryMap.get("PART").createConnectCluster(BodyCacheConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("DEFALT", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
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
				List<QueueMsgBodyCacheVo> queueMsgBodyCacheVoList = consoleInfoMap.get(queueName);
				for (QueueMsgBodyCacheVo queueMsgBodyCacheVo : queueMsgBodyCacheVoList) {
					connectIdSet.add(queueMsgBodyCacheVo.getMsgBodyCache_Id());
					ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get("CACHE_BODY");
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgBodyCacheVo.getMsgBodyCache_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgBodyCacheVo.getMsgBodyCache_Id(), queueMsgBodyCacheVo.getIp(),
											queueMsgBodyCacheVo.getPort(), null);
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
							if (connectAgent.isSameConnect(queueMsgBodyCacheVo.getIp(), queueMsgBodyCacheVo.getPort(), null, ConnectAgentType.BODY_CACHE.getCode())) {
								if (!connectAgent.isHealthyConnect()) {
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
											//+ ", Ip: " + connectAgent.getIp()
											//+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgBodyCacheVo.getMsgBodyCache_Id(), queueMsgBodyCacheVo.getIp(),
												queueMsgBodyCacheVo.getPort(), null);
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

	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	@Override
	protected String getManagerName() {
		return ConnectManagerType.BODY_CACHE.getCode();
	}
	
	@Override
	public ConnectCluster getConnectCluster(String clusterName) {
		return connectClusterMap.get("DEFALT");
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
