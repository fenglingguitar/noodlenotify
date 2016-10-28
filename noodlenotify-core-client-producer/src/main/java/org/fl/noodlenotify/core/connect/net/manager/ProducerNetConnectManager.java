package org.fl.noodlenotify.core.connect.net.manager;

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
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.constent.NetConnectManagerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerNetConnectManager extends AbstractConnectManager {
	
	private final static Logger logger = LoggerFactory.getLogger(ProducerNetConnectManager.class);
	
	private ModuleRegister producerModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	@Override
	protected void updateConnectAgent() {
		
		long moduleId = producerModuleRegister.getModuleId();
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("Defalt", connectClusterFactoryMap.get("FAILOVER").createConnectCluster(NetConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("Defalt", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
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
		
		if (consoleInfoMap == null) { return; }
		
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
		for (String queueName : consoleInfoMap.keySet()) {
			
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
			
			for (QueueExchangerVo queueExchangerVo : consoleInfoMap.get(queueName)) {
				
				connectIdSet.add(queueExchangerVo.getExchanger_Id());
				
				if (connectAgentFactoryMap == null) {
					if (logger.isErrorEnabled()) {
						logger.error("UpdateConnectAgent -> "
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName
									+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
									+ ", Name: " + queueExchangerVo.getName()
									+ ", Ip: " + queueExchangerVo.getIp()
									+ ", Port: " + queueExchangerVo.getPort()
									+ ", ConnectAgent Reconnect -> connectAgentFactoryMap is null "
									);
					}
					continue; 
				}
				
				ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(queueExchangerVo.getType());
				
				if (connectAgentFactory == null) {
					if (logger.isErrorEnabled()) {
						logger.error("UpdateConnectAgent -> "
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName
									+ ", ConnectId: " + queueExchangerVo.getExchanger_Id()
									+ ", Name: " + queueExchangerVo.getName()
									+ ", Ip: " + queueExchangerVo.getIp()
									+ ", Port: " + queueExchangerVo.getPort()
									+ ", Type: " + queueExchangerVo.getType()
									+ ", ConnectAgent Reconnect -> connectAgentFactory is null "
									);
					}
					continue; 
				}
				
				ConnectAgent connectAgent = connectAgentMap.get(queueExchangerVo.getExchanger_Id());
				if (connectAgent == null) {
					connectAgent = connectAgentFactory
							.createConnectAgent(queueExchangerVo.getExchanger_Id(), queueExchangerVo.getIp(),
									queueExchangerVo.getPort(), queueExchangerVo.getUrl());
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
					if (connectAgent.isSameConnect(queueExchangerVo.getIp(), queueExchangerVo.getPort(), queueExchangerVo.getUrl(), queueExchangerVo.getType())) {
						if (!connectAgent.isHealthyConnect()) {
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
									//+ ", Ip: " + connectAgent.getIp()
									//+ ", Port: " + connectAgent.getPort()
									+ ", Ip Or Port Change"
									);
						}
						
						connectAgent = connectAgentFactory
								.createConnectAgent(queueExchangerVo.getExchanger_Id(), queueExchangerVo.getIp(),
										queueExchangerVo.getPort(), queueExchangerVo.getUrl());
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
			connectNode.updateConnectAgentList(connectAgentList);
			connectAgentList.clear();
		}
		
		for (String queueName : connectNodeMap.keySet()) {
			if (!consoleInfoMap.containsKey(queueName)) {
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

	@Override
	protected void destroyConnectAgent() {

		connectNodeMap.clear();
		
		long moduleId = producerModuleRegister.getModuleId();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
			if (logger.isDebugEnabled()) {
				logger.debug("DestroyConnectAgent -> Close -> " 
						+ "ModuleId: " + moduleId
						+ ", ConnectId: " + connectAgent.getConnectId()
						//+ ", Ip: " + connectAgent.getIp()
						//+ ", Port: " + connectAgent.getPort()
						);
			}
		}
		connectAgentMap.clear();
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setProducerModuleRegister(ModuleRegister producerModuleRegister) {
		this.producerModuleRegister = producerModuleRegister;
	}

	@Override
	protected String getManagerName() {
		return NetConnectManagerType.NET.getCode();
	}
	
	@Override
	public ConnectCluster getConnectCluster(String clusterName) {
		return connectClusterMap.get("Defalt");
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("RANDOM");
	}
}
