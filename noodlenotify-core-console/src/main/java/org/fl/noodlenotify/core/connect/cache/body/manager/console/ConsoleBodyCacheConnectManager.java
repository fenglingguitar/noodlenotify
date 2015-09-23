package org.fl.noodlenotify.core.connect.cache.body.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ConsoleBodyCacheConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsoleBodyCacheConnectManager.class);
	
	public ConsoleBodyCacheConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<MsgBodyCacheVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckMsgBodyCaches();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check MsgBodyCaches -> " + e);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (MsgBodyCacheVo msgBodyCacheVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckMsgBodyCaches -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
								+ ", Name: " + msgBodyCacheVo.getName()
								+ ", Ip: " + msgBodyCacheVo.getIp()
								+ ", CheckPort: " + msgBodyCacheVo.getPort()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (MsgBodyCacheVo msgBodyCacheVo : consoleInfoList) {
				connectIdSet.add(msgBodyCacheVo.getMsgBodyCache_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(msgBodyCacheVo.getMsgBodyCache_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(msgBodyCacheVo.getIp(),
										msgBodyCacheVo.getPort(), msgBodyCacheVo.getMsgBodyCache_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(msgBodyCacheVo.getMsgBodyCache_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
										+ ", Name: " + msgBodyCacheVo.getName()
										+ ", Ip: " + msgBodyCacheVo.getIp()
										+ ", CheckPort: " + msgBodyCacheVo.getPort()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> "
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
											+ ", Name: " + msgBodyCacheVo.getName()
											+ ", Ip: " + msgBodyCacheVo.getIp()
											+ ", CheckPort: " + msgBodyCacheVo.getPort()
											+ ", ConnectAgent Connect -> " + e);
							}
						}
					} else {
						if (connectAgent.getIp().equals(msgBodyCacheVo.getIp()) 
								&& connectAgent.getPort() == msgBodyCacheVo.getPort()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
												+ ", Name: " + msgBodyCacheVo.getName()
												+ ", Ip: " + msgBodyCacheVo.getIp()
												+ ", CheckPort: " + msgBodyCacheVo.getPort()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
													+ ", Name: " + msgBodyCacheVo.getName()
													+ ", Ip: " + msgBodyCacheVo.getIp()
													+ ", CheckPort: " + msgBodyCacheVo.getPort()
													+ ", ConnectAgent Reconnect -> " + e);
									}
								}
							}
						} else {
							connectAgent.close();
							connectAgentMap.remove(connectAgent.getConnectId());
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Remove -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
										+ ", Ip: " + connectAgent.getIp()
										+ ", CheckPort: " + connectAgent.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(msgBodyCacheVo.getIp(),
											msgBodyCacheVo.getPort(), msgBodyCacheVo.getMsgBodyCache_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(msgBodyCacheVo.getMsgBodyCache_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
											+ ", Name: " + msgBodyCacheVo.getName()
											+ ", Ip: " + msgBodyCacheVo.getIp()
											+ ", CheckPort: " + msgBodyCacheVo.getPort()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + msgBodyCacheVo.getMsgBodyCache_Id()
												+ ", Name: " + msgBodyCacheVo.getName()
												+ ", Ip: " + msgBodyCacheVo.getIp()
												+ ", CheckPort: " + msgBodyCacheVo.getPort()
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
								+ "ModuleId: " + moduleId
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
