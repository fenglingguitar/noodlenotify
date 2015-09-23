package org.fl.noodlenotify.core.connect.cache.trace.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ConsoleTraceCacheConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsoleTraceCacheConnectManager.class);
	
	public ConsoleTraceCacheConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<TraceStorageVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckTracestorages();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check Tracestorages -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (TraceStorageVo traceStorageVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckTracestorages -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
								+ ", Name: " + traceStorageVo.getName()
								+ ", Ip: " + traceStorageVo.getIp()
								+ ", CheckPort: " + traceStorageVo.getPort()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (TraceStorageVo traceStorageVo : consoleInfoList) {
				connectIdSet.add(traceStorageVo.getTraceStorage_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(traceStorageVo.getTraceStorage_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(traceStorageVo.getIp(),
										traceStorageVo.getPort(), traceStorageVo.getTraceStorage_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(traceStorageVo.getTraceStorage_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
										+ ", Name: " + traceStorageVo.getName()
										+ ", Ip: " + traceStorageVo.getIp()
										+ ", CheckPort: " + traceStorageVo.getPort()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> "
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
											+ ", Name: " + traceStorageVo.getName()
											+ ", Ip: " + traceStorageVo.getIp()
											+ ", CheckPort: " + traceStorageVo.getPort()
											+ ", ConnectAgent Connect -> " + e
											);
							}
						}
					} else {
						if (connectAgent.getIp().equals(traceStorageVo.getIp()) 
								&& connectAgent.getPort() == traceStorageVo.getPort()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
												+ ", Name: " + traceStorageVo.getName()
												+ ", Ip: " + traceStorageVo.getIp()
												+ ", CheckPort: " + traceStorageVo.getPort()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
													+ ", Name: " + traceStorageVo.getName()
													+ ", Ip: " + traceStorageVo.getIp()
													+ ", CheckPort: " + traceStorageVo.getPort()
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
										+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
										+ ", Ip: " + connectAgent.getIp()
										+ ", CheckPort: " + connectAgent.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(traceStorageVo.getIp(),
											traceStorageVo.getPort(), traceStorageVo.getTraceStorage_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(traceStorageVo.getTraceStorage_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + traceStorageVo.getTraceStorage_Id()
											+ ", Name: " + traceStorageVo.getName()
											+ ", Ip: " + traceStorageVo.getIp()
											+ ", CheckPort: " + traceStorageVo.getPort()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + connectAgent.getConnectId()
												+ ", Name: " + traceStorageVo.getName()
												+ ", Ip: " + traceStorageVo.getIp()
												+ ", CheckPort: " + traceStorageVo.getPort()
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
		
		Set<Long> connectIdSet = connectAgentMap.keySet();
		for (long connectId : connectIdSet) {
			ConnectAgent connectAgent = connectAgentMap.get(connectId);
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
