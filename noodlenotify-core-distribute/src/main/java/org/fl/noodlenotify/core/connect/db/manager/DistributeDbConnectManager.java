package org.fl.noodlenotify.core.connect.db.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConnectManagerAbstract;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbQueueAgent;

public class DistributeDbConnectManager extends ConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeDbConnectManager.class);
	
	public DistributeDbConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		Map<String, List<QueueMsgStorageVo>> consoleInfoMap = null;
		
		try {
			consoleInfoMap = consoleRemotingInvoke.distributerGetMsgStorages(moduleId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Distributer Get MsgStorages -> " + e
							);
			}
		}
		
		if (consoleInfoMap != null) {
			
			if (logger.isDebugEnabled()) {
				Set<String> set = consoleInfoMap.keySet();
				for (String queueName : set) {
					List<QueueMsgStorageVo> list = consoleInfoMap.get(queueName);
					for (QueueMsgStorageVo queueMsgStorageVo : list) {
						logger.debug("UpdateConnectAgent -> DistributeGetMsgStorages -> " 
									+ "ModuleId: " + moduleId
									+ ", QueueName: " + queueName 
									+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
									+ ", Name: " + queueMsgStorageVo.getName()
									+ ", Ip: " + queueMsgStorageVo.getIp()
									+ ", Port: " + queueMsgStorageVo.getPort()
									);
					}
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			List<ConnectAgent> connectAgentList = new ArrayList<ConnectAgent>();
			Set<String> queueNameSet = consoleInfoMap.keySet();
			for (String queueName : queueNameSet) {
				boolean isNewQueue = false;
				QueueAgent queueAgent = queueAgentMap.get(queueName);
				if (queueAgent == null) {
					queueAgent = new DbQueueAgent(queueName);
					queueAgentMap.put(queueName, queueAgent);
					if (logger.isDebugEnabled()) {
						logger.debug("UpdateConnectAgent -> Add Queue -> " 
								+ "ModuleId: " + moduleId
								+ ", QueueName: " + queueName
								);
					}
					isNewQueue = true;
				}
				List<QueueMsgStorageVo> queueMsgStorageVoList = consoleInfoMap.get(queueName);
				for (QueueMsgStorageVo queueMsgStorageVo : queueMsgStorageVoList) {
					if (connectAgentFactory != null) {
						connectIdSet.add(queueMsgStorageVo.getMsgStorage_Id());
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgStorageVo.getMsgStorage_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgStorageVo.getIp(),
											queueMsgStorageVo.getPort(), queueMsgStorageVo.getMsgStorage_Id());
							try {
								connectAgent.connect();
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName 
											+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
											+ ", Name: " + queueMsgStorageVo.getName()
											+ ", Ip: " + queueMsgStorageVo.getIp()
											+ ", Port: " + queueMsgStorageVo.getPort()
											);
								}
								try {
									((DbConnectAgent)connectAgent).createTable(queueName);
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Connect -> CreateTable -> " 
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
												+ ", Name: " + queueMsgStorageVo.getName()
												+ ", Ip: " + queueMsgStorageVo.getIp()
												+ ", Port: " + queueMsgStorageVo.getPort()
												);
									}
									connectAgentList.add(connectAgent);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName 
													+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
													+ ", Name: " + queueMsgStorageVo.getName()
													+ ", Ip: " + queueMsgStorageVo.getIp()
													+ ", Port: " + queueMsgStorageVo.getPort()
													+ ", ConnectAgent Connect CreateTable -> " + e
													);
									}
								}
								connectAgentMap.put(queueMsgStorageVo.getMsgStorage_Id(), connectAgent);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
												+ ", Name: " + queueMsgStorageVo.getName()
												+ ", Ip: " + queueMsgStorageVo.getIp()
												+ ", Port: " + queueMsgStorageVo.getPort()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(queueMsgStorageVo.getIp()) 
									&& connectAgent.getPort() == queueMsgStorageVo.getPort()) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName
													+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
													+ ", Name: " + queueMsgStorageVo.getName()
													+ ", Ip: " + queueMsgStorageVo.getIp()
													+ ", Port: " + queueMsgStorageVo.getPort()
													);
										}
										try {
											((DbConnectAgent)connectAgent).createTable(queueName);
											if (logger.isDebugEnabled()) {
												logger.debug("UpdateConnectAgent -> Reconnect -> CreateTable -> " 
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName 
														+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
														+ ", Name: " + queueMsgStorageVo.getName()
														+ ", Ip: " + queueMsgStorageVo.getIp()
														+ ", Port: " + queueMsgStorageVo.getPort()
														);
											}
											connectAgentList.add(connectAgent);
										} catch (Exception e) {
											if (logger.isErrorEnabled()) {
												logger.error("UpdateConnectAgent -> "
															+ "ModuleId: " + moduleId
															+ ", QueueName: " + queueName
															+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
															+ ", Name: " + queueMsgStorageVo.getName()
															+ ", Ip: " + queueMsgStorageVo.getIp()
															+ ", Port: " + queueMsgStorageVo.getPort()
															+ ", ConnectAgent Reconnect CreateTable -> " + e
															);
											}
										}
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
														+ ", Name: " + queueMsgStorageVo.getName()
														+ ", Ip: " + queueMsgStorageVo.getIp()
														+ ", Port: " + queueMsgStorageVo.getPort()
														+ ", ConnectAgent Reconnect -> " + e
														);
										}
									}
								} else {
									try {
										if (isNewQueue) {
											((DbConnectAgent)connectAgent).createTable(queueName);
											if (logger.isDebugEnabled()) {
												logger.debug("UpdateConnectAgent -> CreateTable -> " 
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName 
														+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
														+ ", Name: " + queueMsgStorageVo.getName()
														+ ", Ip: " + queueMsgStorageVo.getIp()
														+ ", Port: " + queueMsgStorageVo.getPort()
														);
											}
										}
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName
														+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
														+ ", Name: " + queueMsgStorageVo.getName()
														+ ", Ip: " + queueMsgStorageVo.getIp()
														+ ", Port: " + queueMsgStorageVo.getPort()
														+ ", ConnectAgent CreateTable -> " + e
														);
										}
									}
								}
							} else {
								connectAgent.close();
								connectAgentMap.remove(connectAgent.getConnectId());
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Remove Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", QueueName: " + queueName
											+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgStorageVo.getIp(),
												queueMsgStorageVo.getPort(), queueMsgStorageVo.getMsgStorage_Id());
								try {
									connectAgent.connect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Change -> " 
												+ "ModuleId: " + moduleId
												+ ", QueueName: " + queueName 
												+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
												+ ", Name: " + queueMsgStorageVo.getName()
												+ ", Ip: " + queueMsgStorageVo.getIp()
												+ ", Port: " + queueMsgStorageVo.getPort()
												+ ", Ip Or Port Change"
												);
									}
									try {
										((DbConnectAgent)connectAgent).createTable(queueName);
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Change -> CreateTable -> " 
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName 
													+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
													+ ", Name: " + queueMsgStorageVo.getName()
													+ ", Ip: " + queueMsgStorageVo.getIp()
													+ ", Port: " + queueMsgStorageVo.getPort()
													+ ", Ip Or Port Change"
													);
										}
										connectAgentList.add(connectAgent);
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> "
														+ "ModuleId: " + moduleId
														+ ", QueueName: " + queueName 
														+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
														+ ", Name: " + queueMsgStorageVo.getName()
														+ ", Ip: " + queueMsgStorageVo.getIp()
														+ ", Port: " + queueMsgStorageVo.getPort()
														+ ", Ip Or Port Change, ConnectAgent Connect CreateTable -> " + e
														);
										}
									}
									connectAgentMap.put(queueMsgStorageVo.getMsgStorage_Id(), connectAgent);
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", QueueName: " + queueName 
													+ ", ConnectId: " + queueMsgStorageVo.getMsgStorage_Id()
													+ ", Name: " + queueMsgStorageVo.getName()
													+ ", Ip: " + queueMsgStorageVo.getIp()
													+ ", Port: " + queueMsgStorageVo.getPort()
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
