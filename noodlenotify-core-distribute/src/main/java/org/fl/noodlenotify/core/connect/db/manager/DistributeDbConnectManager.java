package org.fl.noodlenotify.core.connect.db.manager;

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
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DistributeDbConnectManager extends AbstractConnectManager {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeDbConnectManager.class);

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	protected void updateConnectAgent() {
		
		long moduleId = distributeModuleRegister.getModuleId();
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("DEFALT", connectClusterFactoryMap.get("FAILOVER").createConnectCluster(DbConnectAgent.class));
			connectClusterMap.put("ID", connectClusterFactoryMap.get("ID").createConnectCluster(DbConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("DEFALT", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
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
					isNewQueue = true;
				}
				List<QueueMsgStorageVo> queueMsgStorageVoList = consoleInfoMap.get(queueName);
				for (QueueMsgStorageVo queueMsgStorageVo : queueMsgStorageVoList) {
					ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get("MYSQL");
					if (connectAgentFactory != null) {
						connectIdSet.add(queueMsgStorageVo.getMsgStorage_Id());
						ConnectAgent connectAgent = connectAgentMap.get(queueMsgStorageVo.getMsgStorage_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(queueMsgStorageVo.getMsgStorage_Id(), queueMsgStorageVo.getIp(),
											queueMsgStorageVo.getPort(), null);
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
									((DbConnectAgent)connectAgent.getProxy()).createTable(queueName);
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
							if (connectAgent.isSameConnect(queueMsgStorageVo.getIp(), queueMsgStorageVo.getPort(), null, ConnectAgentType.DB.getCode())) {
								if (!connectAgent.isHealthyConnect()) {
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
											//+ ", Ip: " + connectAgent.getIp()
											//+ ", Port: " + connectAgent.getPort()
											+ ", Ip Or Port Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(queueMsgStorageVo.getMsgStorage_Id(), queueMsgStorageVo.getIp(),
												queueMsgStorageVo.getPort(), null);
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
	protected String getManagerName() {
		return ConnectManagerType.DB.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
