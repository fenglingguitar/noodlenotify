package org.fl.noodlenotify.core.connect.net.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class DistributeConsoleNetConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeConsoleNetConnectManager.class);
	
	public DistributeConsoleNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<DistributerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckDistributers();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check Distributers -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (DistributerVo distributerVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> GetDistributers -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + distributerVo.getDistributer_Id()
								+ ", Name: " + distributerVo.getName()
								+ ", Ip: " + distributerVo.getIp()
								+ ", CheckPort: " + distributerVo.getCheck_Port()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (DistributerVo distributerVo : consoleInfoList) {
				connectIdSet.add(distributerVo.getDistributer_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(distributerVo.getDistributer_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(distributerVo.getIp(),
										distributerVo.getCheck_Port(), distributerVo.getDistributer_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(distributerVo.getDistributer_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + distributerVo.getDistributer_Id()
										+ ", Name: " + distributerVo.getName()
										+ ", Ip: " + distributerVo.getIp()
										+ ", CheckPort: " + distributerVo.getCheck_Port()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> Connect -> "
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + distributerVo.getDistributer_Id()
											+ ", Name: " + distributerVo.getName()
											+ ", Ip: " + distributerVo.getIp()
											+ ", CheckPort: " + distributerVo.getCheck_Port()
											+ ", ConnectAgent Connect -> " + e
											);
							}
						}
					} else {
						if (connectAgent.getIp().equals(distributerVo.getIp()) 
								&& connectAgent.getPort() == distributerVo.getCheck_Port()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + distributerVo.getDistributer_Id()
												+ ", Name: " + distributerVo.getName()
												+ ", Ip: " + distributerVo.getIp()
												+ ", CheckPort: " + distributerVo.getCheck_Port()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> Reconnect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + distributerVo.getDistributer_Id()
													+ ", Name: " + distributerVo.getName()
													+ ", Ip: " + distributerVo.getIp()
													+ ", CheckPort: " + distributerVo.getCheck_Port()
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
										+ ", ConnectId: " + distributerVo.getDistributer_Id()
										+ ", Ip: " + connectAgent.getIp()
										+ ", CheckPort: " + connectAgent.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(distributerVo.getIp(),
											distributerVo.getCheck_Port(), distributerVo.getDistributer_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(distributerVo.getDistributer_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + distributerVo.getDistributer_Id()
											+ ", Name: " + distributerVo.getName()
											+ ", Ip: " + distributerVo.getIp()
											+ ", CheckPort: " + distributerVo.getCheck_Port()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + distributerVo.getDistributer_Id()
												+ ", Name: " + distributerVo.getName()
												+ ", Ip: " + distributerVo.getIp()
												+ ", CheckPort: " + distributerVo.getCheck_Port()
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
