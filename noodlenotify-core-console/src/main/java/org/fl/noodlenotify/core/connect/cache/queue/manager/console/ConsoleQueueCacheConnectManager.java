package org.fl.noodlenotify.core.connect.cache.queue.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ConsoleQueueCacheConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsoleQueueCacheConnectManager.class);
	
	public ConsoleQueueCacheConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<MsgQueueCacheVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckMsgQueueCaches();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> GetMsgQueueCaches -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check MsgQueueCaches -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (MsgQueueCacheVo msgQueueCacheVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckMsgQueueCaches -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
								+ ", Name: " + msgQueueCacheVo.getName()
								+ ", Ip: " + msgQueueCacheVo.getIp()
								+ ", CheckPort: " + msgQueueCacheVo.getPort()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (MsgQueueCacheVo msgQueueCacheVo : consoleInfoList) {
				connectIdSet.add(msgQueueCacheVo.getMsgQueueCache_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(msgQueueCacheVo.getMsgQueueCache_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(msgQueueCacheVo.getIp(),
										msgQueueCacheVo.getPort(), msgQueueCacheVo.getMsgQueueCache_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(msgQueueCacheVo.getMsgQueueCache_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
										+ ", Name: " + msgQueueCacheVo.getName()
										+ ", Ip: " + msgQueueCacheVo.getIp()
										+ ", CheckPort: " + msgQueueCacheVo.getPort()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> Connect -> "
											+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
											+ ", Name: " + msgQueueCacheVo.getName()
											+ ", Ip: " + msgQueueCacheVo.getIp()
											+ ", CheckPort: " + msgQueueCacheVo.getPort()
											+ ", ConnectAgent Connect -> " + e
											);
							}
						}
					} else {
						if (connectAgent.getIp().equals(msgQueueCacheVo.getIp()) 
								&& connectAgent.getPort() == msgQueueCacheVo.getPort()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
												+ ", Name: " + msgQueueCacheVo.getName()
												+ ", Ip: " + msgQueueCacheVo.getIp()
												+ ", CheckPort: " + msgQueueCacheVo.getPort()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> Reconnect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
													+ ", Name: " + msgQueueCacheVo.getName()
													+ ", Ip: " + msgQueueCacheVo.getIp()
													+ ", CheckPort: " + msgQueueCacheVo.getPort()
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
										+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
										+ ", Ip: " + connectAgent.getIp()
										+ ", CheckPort: " + connectAgent.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(msgQueueCacheVo.getIp(),
											msgQueueCacheVo.getPort(), msgQueueCacheVo.getMsgQueueCache_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(msgQueueCacheVo.getMsgQueueCache_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
											+ ", Name: " + msgQueueCacheVo.getName()
											+ ", Ip: " + msgQueueCacheVo.getIp()
											+ ", CheckPort: " + msgQueueCacheVo.getPort()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ ", ConnectId: " + msgQueueCacheVo.getMsgQueueCache_Id()
												+ ", Name: " + msgQueueCacheVo.getName()
												+ ", Ip: " + msgQueueCacheVo.getIp()
												+ ", CheckPort: " + msgQueueCacheVo.getPort()
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
								+ "ModuleId: " + connectId
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
