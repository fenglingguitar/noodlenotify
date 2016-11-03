package org.fl.noodlenotify.core.connect.net.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.cluster.LayerConnectCluster;
import org.fl.noodlenotify.core.connect.net.constent.NetConnectManagerType;

public class DistributeNetConnectManager extends AbstractConnectManager {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeNetConnectManager.class);
	
	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	@Override
	protected void updateConnectAgent() {
		
		long moduleId = distributeModuleRegister.getModuleId();
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("DEFALT", connectClusterFactoryMap.get("LAYER").createConnectCluster(NetConnectAgent.class));
			((LayerConnectCluster)connectClusterMap.get("DEFALT")).setConnectCluster(connectClusterFactoryMap.get("LAYERFAILOVER").createConnectCluster(NetConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("DEFALT", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
		Map<String, Map<Long, List<QueueConsumerVo>>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.distributerGetQueueConsumerGroups(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Distributer Get QueueConsumerGroups -> " + e);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				for (String queueName : consoleInfoMap.keySet()) {
					Map<Long, List<QueueConsumerVo>> consumerGroupNmMap = consoleInfoMap.get(queueName);
					for (long consumerGroupNm : consumerGroupNmMap.keySet()) {
						List<QueueConsumerVo> list = consumerGroupNmMap.get(consumerGroupNm);
						for (QueueConsumerVo queueConsumerVo : list) {
							logger.debug("UpdateConnectAgent -> DistributerGetQueueConsumerGroups -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", ConsumerNum: " + consumerGroupNm
									+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
									+ ", Name: " + queueConsumerVo.getName()
									+ ", Ip: " + queueConsumerVo.getIp()
									+ ", Port: " + queueConsumerVo.getPort()
									+ ", Url: " + queueConsumerVo.getUrl()
									+ ", Type: " + queueConsumerVo.getType()
									+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
									);
						}
					}
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();
			Set<Long> consumerGroupNmSet = new HashSet<Long>();
			
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
				Map<Long, List<QueueConsumerVo>> consumerGroupNmMap = consoleInfoMap.get(queueName);
				for (long consumerGroupNm : consumerGroupNmMap.keySet()) {
					consumerGroupNmSet.add(consumerGroupNm);
					ConnectNode consumerGroupNmConnectNode = connectNode.getChildConnectNode(consumerGroupNm);
					if (consumerGroupNmConnectNode == null) {
						consumerGroupNmConnectNode = new ConnectNodeImpl(queueName);
						connectNode.addChildConnectNode(consumerGroupNm, consumerGroupNmConnectNode);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Add Group -> " 
									+ "ModuleId: " + moduleId
									+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
									);
						}
					}
					List<QueueConsumerVo> queueConsumerVoList = consumerGroupNmMap.get(consumerGroupNm);
					for (QueueConsumerVo queueConsumerVo : queueConsumerVoList) {
						connectIdSet.add(queueConsumerVo.getConsumer_Id());						
						if (connectAgentFactoryMap != null) {
							ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(queueConsumerVo.getType());
							if (connectAgentFactory != null) {
								ConnectAgent connectAgent = connectAgentMap.get(queueConsumerVo.getConsumer_Id());
								if (connectAgent == null) {
									connectAgent = connectAgentFactory
											.createConnectAgent(queueConsumerVo.getConsumer_Id(), queueConsumerVo.getIp(),
													queueConsumerVo.getPort(), queueConsumerVo.getUrl(), (int)queueConsumerVo.getDph_Timeout().intValue(), (int)queueConsumerVo.getDph_Timeout().intValue());
									try {
										connectAgent.connect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Connect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
													+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
													+ ", Name: " + queueConsumerVo.getName()
													+ ", Ip: " + queueConsumerVo.getIp()
													+ ", Port: " + queueConsumerVo.getPort()
													+ ", Url: " + queueConsumerVo.getUrl()
													+ ", Type: " + queueConsumerVo.getType()
													+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
													);
										}
										connectAgentMap.put(queueConsumerVo.getConsumer_Id(), connectAgent);
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> Connect -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
														+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
														+ ", Name: " + queueConsumerVo.getName()
														+ ", Ip: " + queueConsumerVo.getIp()
														+ ", Port: " + queueConsumerVo.getPort()
														+ ", Url: " + queueConsumerVo.getUrl()
														+ ", Type: " + queueConsumerVo.getType()
														+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
														+ ", ConnectAgent Connect -> " + e
														);
										}
									}
								} else {
									if (connectAgent.isSameConnect(queueConsumerVo.getIp(), queueConsumerVo.getPort(), queueConsumerVo.getUrl(), queueConsumerVo.getType())) {
										if (!connectAgent.isHealthyConnect()) {
											try {
												connectAgent.reconnect();
												if (logger.isDebugEnabled()) {
													logger.debug("UpdateConnectAgent -> Reconnect -> " 
															+ "ModuleId: " + moduleId
															+ ", QueueName: " + queueName
															+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
															+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
															+ ", Name: " + queueConsumerVo.getName()
															+ ", Ip: " + queueConsumerVo.getIp()
															+ ", Port: " + queueConsumerVo.getPort()
															+ ", Url: " + queueConsumerVo.getUrl()
															+ ", Type: " + queueConsumerVo.getType()
															+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
															);
												}
												connectAgentList.add(connectAgent);
											} catch (Exception e) {
												if (logger.isErrorEnabled()) {
													logger.error("UpdateConnectAgent -> "
																+ "ModuleId: " + moduleId
																+ ", QueueName: " + queueName
																+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
																+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
																+ ", Name: " + queueConsumerVo.getName()
																+ ", Ip: " + queueConsumerVo.getIp()
																+ ", Port: " + queueConsumerVo.getPort()
																+ ", Url: " + queueConsumerVo.getUrl()
																+ ", Type: " + queueConsumerVo.getType()
																+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
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
													+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
													+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
													//+ ", Ip: " + connectAgent.getIp()
													//+ ", Port: " + connectAgent.getPort()
													+ ", Url: " + queueConsumerVo.getUrl()
													+ ", Type: " + queueConsumerVo.getType()
													+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
													+ ", Ip Or Port Or URL Or Type Change"
													);
										}
										
										connectAgent = connectAgentFactory
												.createConnectAgent(queueConsumerVo.getConsumer_Id(), queueConsumerVo.getIp(),
														queueConsumerVo.getPort(), queueConsumerVo.getUrl(), (int)queueConsumerVo.getDph_Timeout().intValue(), (int)queueConsumerVo.getDph_Timeout().intValue());
										try {
											connectAgent.connect();
											if (logger.isDebugEnabled()) {
												logger.debug("UpdateConnectAgent -> Connect -> " 
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
														+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
														+ ", Name: " + queueConsumerVo.getName()
														+ ", Ip: " + queueConsumerVo.getIp()
														+ ", Port: " + queueConsumerVo.getPort()
														+ ", Url: " + queueConsumerVo.getUrl()
														+ ", Type: " + queueConsumerVo.getType()
														+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
														+ ", Ip Or Port Or URL Or Type Change"
														);
											}
											connectAgentMap.put(queueConsumerVo.getConsumer_Id(), connectAgent);
											connectAgentList.add(connectAgent);
										} catch (Exception e) {
											if (logger.isErrorEnabled()) {
												logger.error("UpdateConnectAgent -> Connect -> "
															+ "ModuleId: " + moduleId
															+ ", QueueName: " + queueName
															+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
															+ ", ConnectId: " + queueConsumerVo.getConsumer_Id()
															+ ", Name: " + queueConsumerVo.getName()
															+ ", Ip: " + queueConsumerVo.getIp()
															+ ", Port: " + queueConsumerVo.getPort()
															+ ", Url: " + queueConsumerVo.getUrl()
															+ ", Type: " + queueConsumerVo.getType()
															+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
															+ ", Ip Or Port Or URL Or Type Change, ConnectAgent Connect -> " + e
															);
											}
										}
									}
								}
							}
						}
					}
					consumerGroupNmConnectNode.updateConnectAgentList(connectAgentList);
					connectAgentList.clear();
				}
			}
			
			Set<String> connectNodeMapSet = connectNodeMap.keySet();
			for (String queueName : connectNodeMapSet) {
				if (!queueNameSet.contains(queueName)) {
					ConnectNode consumerGroupNmConnectNode = connectNodeMap.get(queueName);
					ConcurrentMap<Long, ConnectNode> consumerGroupNmMap = consumerGroupNmConnectNode.getChildConnectNodeMap();
					consumerGroupNmMap.clear();
					consumerGroupNmMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				} else {
					ConnectNode consumerGroupNmConnectNode = connectNodeMap.get(queueName);
					ConcurrentMap<Long, ConnectNode> consumerGroupNmMap = consumerGroupNmConnectNode.getChildConnectNodeMap();
					for (long consumerGroupNm : consumerGroupNmMap.keySet()) {
						if (!consumerGroupNmSet.contains(consumerGroupNm)) {
							consumerGroupNmMap.remove(consumerGroupNm);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove Group -> "
										+ "ModuleId: " + moduleId
										+ ", GroupNm: " + Long.toBinaryString(consumerGroupNm)
										);
							}
						}
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
								//+ ", Url: " + connectAgent.getUrl()
								//+ ", Type: " + connectAgent.getType()
								//+ ", Timeout: " + connectAgent.getTimeout()
								);
					}
				}
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {
		
		Set<String> connectNodeMapSet = connectNodeMap.keySet();
		for (String queueName : connectNodeMapSet) {
			ConnectNode consumerGroupNmConnectNode = connectNodeMap.get(queueName);
			ConcurrentMap<Long, ConnectNode> consumerGroupNmMap = consumerGroupNmConnectNode.getChildConnectNodeMap();
			consumerGroupNmMap.clear();
		}
		
		connectNodeMap.clear();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
			if (logger.isDebugEnabled()) {
				logger.debug("DestroyConnectAgent -> Close -> " 
						+ "ModuleId: " + distributeModuleRegister.getModuleId()
						+ ", ConnectId: " + connectAgent.getConnectId()
						//+ ", Ip: " + connectAgent.getIp()
						//+ ", Port: " + connectAgent.getPort()
						//+ ", Url: " + connectAgent.getUrl()
						//+ ", Type: " + connectAgent.getType()
						);
			}
		}
		connectAgentMap.clear();
	}
	
	public void setConnectAgentFactoryMap(
			Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	@Override
	protected String getManagerName() {
		return NetConnectManagerType.NET.getCode();
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
