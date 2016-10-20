package org.fl.noodlenotify.core.connect.net.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManagerAbstract;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.net.NetQueueAgent;

public class DistributeNetConnectManager extends ConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeNetConnectManager.class);
	
	Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	public DistributeNetConnectManager() {
	}
	
	public DistributeNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
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
				Map<Long, List<QueueConsumerVo>> consumerGroupNmMap = consoleInfoMap.get(queueName);
				for (long consumerGroupNm : consumerGroupNmMap.keySet()) {
					consumerGroupNmSet.add(consumerGroupNm);
					QueueAgent consumerGroupNmQueueAgent = queueAgent.getChildQueueAgent(consumerGroupNm);
					if (consumerGroupNmQueueAgent == null) {
						consumerGroupNmQueueAgent = new NetQueueAgent(queueName);
						queueAgent.addChildQueueAgent(consumerGroupNm, consumerGroupNmQueueAgent);
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
											.createConnectAgent(queueConsumerVo.getIp(),
													queueConsumerVo.getPort(), queueConsumerVo.getUrl(), queueConsumerVo.getConsumer_Id(), (int)queueConsumerVo.getDph_Timeout().intValue());
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
									if (connectAgent.getIp().equals(queueConsumerVo.getIp()) 
											&& connectAgent.getPort() == queueConsumerVo.getPort()
												&& (connectAgent.getUrl() != null && queueConsumerVo.getUrl() != null && connectAgent.getUrl().equals(queueConsumerVo.getUrl()) || (connectAgent.getUrl() == null && queueConsumerVo.getUrl() == null))
													&& connectAgent.getType().equals(queueConsumerVo.getType())
														/*&& (connectAgent.getTimeout() == 0 || queueConsumerVo.getDph_Timeout() == 0 || connectAgent.getTimeout() == queueConsumerVo.getDph_Timeout())*/
									) {
										if (connectAgent.getConnectStatus() == false) {
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
													+ ", Ip: " + connectAgent.getIp()
													+ ", Port: " + connectAgent.getPort()
													+ ", Url: " + queueConsumerVo.getUrl()
													+ ", Type: " + queueConsumerVo.getType()
													+ ", Timeout: " + queueConsumerVo.getDph_Timeout()
													+ ", Ip Or Port Or URL Or Type Change"
													);
										}
										
										connectAgent = connectAgentFactory
												.createConnectAgent(queueConsumerVo.getIp(),
														queueConsumerVo.getPort(), queueConsumerVo.getUrl(), queueConsumerVo.getConsumer_Id(), (int)queueConsumerVo.getDph_Timeout().intValue());
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
					consumerGroupNmQueueAgent.updateConnectAgents(connectAgentList);
					connectAgentList.clear();
				}
			}
			
			Set<String> queueAgentMapSet = queueAgentMap.keySet();
			for (String queueName : queueAgentMapSet) {
				if (!queueNameSet.contains(queueName)) {
					QueueAgent consumerGroupNmQueueAgent = queueAgentMap.get(queueName);
					ConcurrentMap<Long, QueueAgent> consumerGroupNmMap = consumerGroupNmQueueAgent.getChildQueueAgentMap();
					consumerGroupNmMap.clear();
					queueAgentMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				} else {
					QueueAgent consumerGroupNmQueueAgent = queueAgentMap.get(queueName);
					ConcurrentMap<Long, QueueAgent> consumerGroupNmMap = consumerGroupNmQueueAgent.getChildQueueAgentMap();
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
								+ ", Ip: " + connectAgent.getIp()
								+ ", Port: " + connectAgent.getPort()
								+ ", Url: " + connectAgent.getUrl()
								+ ", Type: " + connectAgent.getType()
								+ ", Timeout: " + connectAgent.getTimeout()
								);
					}
				}
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {
		
		Set<String> queueAgentMapSet = queueAgentMap.keySet();
		for (String queueName : queueAgentMapSet) {
			QueueAgent consumerGroupNmQueueAgent = queueAgentMap.get(queueName);
			ConcurrentMap<Long, QueueAgent> consumerGroupNmMap = consumerGroupNmQueueAgent.getChildQueueAgentMap();
			consumerGroupNmMap.clear();
		}
		
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
						+ ", Url: " + connectAgent.getUrl()
						+ ", Type: " + connectAgent.getType()
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
