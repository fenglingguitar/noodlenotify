package org.fl.noodlenotify.core.connect.db.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ConsoleDbConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsoleDbConnectManager.class);
	
	public ConsoleDbConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<MsgStorageVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckMsgStorages();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check MsgStorages -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (MsgStorageVo msgStorageVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckMsgStorages -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
								+ ", Name: " + msgStorageVo.getName()
								+ ", Ip: " + msgStorageVo.getIp()
								+ ", CheckPort: " + msgStorageVo.getPort()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (MsgStorageVo msgStorageVo : consoleInfoList) {
				connectIdSet.add(msgStorageVo.getMsgStorage_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(msgStorageVo.getMsgStorage_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(msgStorageVo.getIp(),
										msgStorageVo.getPort(), msgStorageVo.getMsgStorage_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(msgStorageVo.getMsgStorage_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
										+ ", Name: " + msgStorageVo.getName()
										+ ", Ip: " + msgStorageVo.getIp()
										+ ", CheckPort: " + msgStorageVo.getPort()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> Connect -> "
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
											+ ", Name: " + msgStorageVo.getName()
											+ ", Ip: " + msgStorageVo.getIp()
											+ ", CheckPort: " + msgStorageVo.getPort()
											+ ", ConnectAgent Connect -> " + e
											);
							}
						}
					} else {
						if (connectAgent.getIp().equals(msgStorageVo.getIp()) 
								&& connectAgent.getPort() == msgStorageVo.getPort()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
												+ ", Name: " + msgStorageVo.getName()
												+ ", Ip: " + msgStorageVo.getIp()
												+ ", CheckPort: " + msgStorageVo.getPort()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
													+ ", Name: " + msgStorageVo.getName()
													+ ", Ip: " + msgStorageVo.getIp()
													+ ", CheckPort: " + msgStorageVo.getPort()
													+ ", ConnectAgent Reconnect -> " + e
													);
									}
								}
							}
						} else {
							connectAgent.close();
							connectAgentMap.remove(connectAgent.getConnectId());
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
										+ ", Name: " + msgStorageVo.getName()
										+ ", Ip: " + msgStorageVo.getIp()
										+ ", CheckPort: " + msgStorageVo.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(msgStorageVo.getIp(),
											msgStorageVo.getPort(), msgStorageVo.getMsgStorage_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(msgStorageVo.getMsgStorage_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
											+ ", Name: " + msgStorageVo.getName()
											+ ", Ip: " + msgStorageVo.getIp()
											+ ", CheckPort: " + msgStorageVo.getPort()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + msgStorageVo.getMsgStorage_Id()
												+ ", Name: " + msgStorageVo.getName()
												+ ", Ip: " + msgStorageVo.getIp()
												+ ", CheckPort: " + msgStorageVo.getPort()
												+ ", Ip Or Port Change, ConnectAgent Connect -> " + e
												);
								}
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
						logger.debug("UpdateConnectAgent -> Remove -> " 
								+ "MsgStorageId: " + moduleId
								+ ", ConnectId: " + connectAgent.getConnectId()
								+ ", Ip: " + connectAgent.getIp()
								+ ", CheckPort: " + connectAgent.getPort()
								);
					}
				}
			}
		}
	}

	@Override
	protected void destroyConnectAgent() {
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
			if (logger.isDebugEnabled()) {
				logger.debug("DestroyConnectAgent -> Close -> " 
						+ "ModuleId: " + moduleId
						+ ", ConnectId: " + connectAgent.getConnectId()
						+ ", Ip: " + connectAgent.getIp()
						+ ", CheckPort: " + connectAgent.getPort()
						);
			}
		}
		connectAgentMap.clear();
	}
}
