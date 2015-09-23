package org.fl.noodlenotify.core.connect.net.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueCustomerVo;
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
		
		Map<String, Map<Long, List<QueueCustomerVo>>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.distributerGetQueueCustomerGroups(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Distributer Get QueueCustomerGroups -> " + e);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				for (String queueName : consoleInfoMap.keySet()) {
					Map<Long, List<QueueCustomerVo>> customerGroupNmMap = consoleInfoMap.get(queueName);
					for (long customerGroupNm : customerGroupNmMap.keySet()) {
						List<QueueCustomerVo> list = customerGroupNmMap.get(customerGroupNm);
						for (QueueCustomerVo queueCustomerVo : list) {
							logger.debug("UpdateConnectAgent -> DistributerGetQueueCustomerGroups -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", CustomerNum: " + customerGroupNm
									+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
									+ ", Name: " + queueCustomerVo.getName()
									+ ", Ip: " + queueCustomerVo.getIp()
									+ ", Port: " + queueCustomerVo.getPort()
									+ ", Url: " + queueCustomerVo.getUrl()
									+ ", Type: " + queueCustomerVo.getType()
									+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
									);
						}
					}
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();
			Set<Long> customerGroupNmSet = new HashSet<Long>();
			
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
				Map<Long, List<QueueCustomerVo>> customerGroupNmMap = consoleInfoMap.get(queueName);
				for (long customerGroupNm : customerGroupNmMap.keySet()) {
					customerGroupNmSet.add(customerGroupNm);
					QueueAgent customerGroupNmQueueAgent = queueAgent.getChildQueueAgent(customerGroupNm);
					if (customerGroupNmQueueAgent == null) {
						customerGroupNmQueueAgent = new NetQueueAgent(queueName);
						queueAgent.addChildQueueAgent(customerGroupNm, customerGroupNmQueueAgent);
						if (logger.isDebugEnabled()) {
							logger.debug("UpdateConnectAgent -> Add Group -> " 
									+ "ModuleId: " + moduleId
									+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
									);
						}
					}
					List<QueueCustomerVo> queueCustomerVoList = customerGroupNmMap.get(customerGroupNm);
					for (QueueCustomerVo queueCustomerVo : queueCustomerVoList) {
						connectIdSet.add(queueCustomerVo.getCustomer_Id());						
						if (connectAgentFactoryMap != null) {
							ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(queueCustomerVo.getType());
							if (connectAgentFactory != null) {
								ConnectAgent connectAgent = connectAgentMap.get(queueCustomerVo.getCustomer_Id());
								if (connectAgent == null) {
									connectAgent = connectAgentFactory
											.createConnectAgent(queueCustomerVo.getIp(),
													queueCustomerVo.getPort(), queueCustomerVo.getUrl(), queueCustomerVo.getCustomer_Id(), (int)queueCustomerVo.getDph_Timeout());
									try {
										connectAgent.connect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Connect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
													+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
													+ ", Name: " + queueCustomerVo.getName()
													+ ", Ip: " + queueCustomerVo.getIp()
													+ ", Port: " + queueCustomerVo.getPort()
													+ ", Url: " + queueCustomerVo.getUrl()
													+ ", Type: " + queueCustomerVo.getType()
													+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
													);
										}
										connectAgentMap.put(queueCustomerVo.getCustomer_Id(), connectAgent);
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> Connect -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
														+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
														+ ", Name: " + queueCustomerVo.getName()
														+ ", Ip: " + queueCustomerVo.getIp()
														+ ", Port: " + queueCustomerVo.getPort()
														+ ", Url: " + queueCustomerVo.getUrl()
														+ ", Type: " + queueCustomerVo.getType()
														+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
														+ ", ConnectAgent Connect -> " + e
														);
										}
									}
								} else {
									if (connectAgent.getIp().equals(queueCustomerVo.getIp()) 
											&& connectAgent.getPort() == queueCustomerVo.getPort()
												&& (connectAgent.getUrl() != null && queueCustomerVo.getUrl() != null && connectAgent.getUrl().equals(queueCustomerVo.getUrl()) || (connectAgent.getUrl() == null && queueCustomerVo.getUrl() == null))
													&& connectAgent.getType().equals(queueCustomerVo.getType())
														&& (connectAgent.getTimeout() == 0 || queueCustomerVo.getDph_Timeout() == 0 || connectAgent.getTimeout() == queueCustomerVo.getDph_Timeout())
									) {
										if (connectAgent.getConnectStatus() == false) {
											try {
												connectAgent.reconnect();
												if (logger.isDebugEnabled()) {
													logger.debug("UpdateConnectAgent -> Reconnect -> " 
															+ "ModuleId: " + moduleId
															+ ", QueueName: " + queueName
															+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
															+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
															+ ", Name: " + queueCustomerVo.getName()
															+ ", Ip: " + queueCustomerVo.getIp()
															+ ", Port: " + queueCustomerVo.getPort()
															+ ", Url: " + queueCustomerVo.getUrl()
															+ ", Type: " + queueCustomerVo.getType()
															+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
															);
												}
												connectAgentList.add(connectAgent);
											} catch (Exception e) {
												if (logger.isErrorEnabled()) {
													logger.error("UpdateConnectAgent -> "
																+ "ModuleId: " + moduleId
																+ ", QueueName: " + queueName
																+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
																+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
																+ ", Name: " + queueCustomerVo.getName()
																+ ", Ip: " + queueCustomerVo.getIp()
																+ ", Port: " + queueCustomerVo.getPort()
																+ ", Url: " + queueCustomerVo.getUrl()
																+ ", Type: " + queueCustomerVo.getType()
																+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
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
													+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
													+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
													+ ", Ip: " + connectAgent.getIp()
													+ ", Port: " + connectAgent.getPort()
													+ ", Url: " + queueCustomerVo.getUrl()
													+ ", Type: " + queueCustomerVo.getType()
													+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
													+ ", Ip Or Port Or URL Or Type Change"
													);
										}
										
										connectAgent = connectAgentFactory
												.createConnectAgent(queueCustomerVo.getIp(),
														queueCustomerVo.getPort(), queueCustomerVo.getUrl(), queueCustomerVo.getCustomer_Id(), (int)queueCustomerVo.getDph_Timeout());
										try {
											connectAgent.connect();
											if (logger.isDebugEnabled()) {
												logger.debug("UpdateConnectAgent -> Connect -> " 
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
														+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
														+ ", Name: " + queueCustomerVo.getName()
														+ ", Ip: " + queueCustomerVo.getIp()
														+ ", Port: " + queueCustomerVo.getPort()
														+ ", Url: " + queueCustomerVo.getUrl()
														+ ", Type: " + queueCustomerVo.getType()
														+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
														+ ", Ip Or Port Or URL Or Type Change"
														);
											}
											connectAgentMap.put(queueCustomerVo.getCustomer_Id(), connectAgent);
											connectAgentList.add(connectAgent);
										} catch (Exception e) {
											if (logger.isErrorEnabled()) {
												logger.error("UpdateConnectAgent -> Connect -> "
															+ "ModuleId: " + moduleId
															+ ", QueueName: " + queueName
															+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
															+ ", ConnectId: " + queueCustomerVo.getCustomer_Id()
															+ ", Name: " + queueCustomerVo.getName()
															+ ", Ip: " + queueCustomerVo.getIp()
															+ ", Port: " + queueCustomerVo.getPort()
															+ ", Url: " + queueCustomerVo.getUrl()
															+ ", Type: " + queueCustomerVo.getType()
															+ ", Timeout: " + queueCustomerVo.getDph_Timeout()
															+ ", Ip Or Port Or URL Or Type Change, ConnectAgent Connect -> " + e
															);
											}
										}
									}
								}
							}
						}
					}
					customerGroupNmQueueAgent.updateConnectAgents(connectAgentList);
					connectAgentList.clear();
				}
			}
			
			Set<String> queueAgentMapSet = queueAgentMap.keySet();
			for (String queueName : queueAgentMapSet) {
				if (!queueNameSet.contains(queueName)) {
					QueueAgent customerGroupNmQueueAgent = queueAgentMap.get(queueName);
					ConcurrentMap<Long, QueueAgent> customerGroupNmMap = customerGroupNmQueueAgent.getChildQueueAgentMap();
					customerGroupNmMap.clear();
					queueAgentMap.remove(queueName);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Remove Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
				} else {
					QueueAgent customerGroupNmQueueAgent = queueAgentMap.get(queueName);
					ConcurrentMap<Long, QueueAgent> customerGroupNmMap = customerGroupNmQueueAgent.getChildQueueAgentMap();
					for (long customerGroupNm : customerGroupNmMap.keySet()) {
						if (!customerGroupNmSet.contains(customerGroupNm)) {
							customerGroupNmMap.remove(customerGroupNm);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove Group -> "
										+ "ModuleId: " + moduleId
										+ ", GroupNm: " + Long.toBinaryString(customerGroupNm)
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
			QueueAgent customerGroupNmQueueAgent = queueAgentMap.get(queueName);
			ConcurrentMap<Long, QueueAgent> customerGroupNmMap = customerGroupNmQueueAgent.getChildQueueAgentMap();
			customerGroupNmMap.clear();
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
