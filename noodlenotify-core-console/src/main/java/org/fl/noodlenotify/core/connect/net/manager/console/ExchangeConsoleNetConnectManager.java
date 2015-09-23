package org.fl.noodlenotify.core.connect.net.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ExchangeConsoleNetConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ExchangeConsoleNetConnectManager.class);
	
	public ExchangeConsoleNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<ExchangerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckExchangers();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check Exchangers -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (ExchangerVo exchangerVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckExchangers -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + exchangerVo.getExchanger_Id()
								+ ", Name: " + exchangerVo.getName()
								+ ", Ip: " + exchangerVo.getIp()
								+ ", CheckPort: " + exchangerVo.getCheck_Port()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (ExchangerVo exchangerVo : consoleInfoList) {
				connectIdSet.add(exchangerVo.getExchanger_Id());
				if (connectAgentFactory != null) {
					ConnectAgent connectAgent = connectAgentMap.get(exchangerVo.getExchanger_Id());
					if (connectAgent == null) {
						connectAgent = connectAgentFactory
								.createConnectAgent(exchangerVo.getIp(),
										exchangerVo.getCheck_Port(), exchangerVo.getExchanger_Id());
						try {
							connectAgent.connect();
							connectAgentMap.put(exchangerVo.getExchanger_Id(), connectAgent);
							if (logger.isDebugEnabled()) {
								logger.debug("UpdateConnectAgent -> Connect -> " 
										+ "ModuleId: " + moduleId
										+ ", ConnectId: " + exchangerVo.getExchanger_Id()
										+ ", Name: " + exchangerVo.getName()
										+ ", Ip: " + exchangerVo.getIp()
										+ ", CheckPort: " + exchangerVo.getCheck_Port()
										);
							}
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateConnectAgent -> Connect -> "
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + exchangerVo.getExchanger_Id()
											+ ", Name: " + exchangerVo.getName()
											+ ", Ip: " + exchangerVo.getIp()
											+ ", CheckPort: " + exchangerVo.getCheck_Port()
											+ ", ConnectAgent Connect -> " + e
											);
							}
						}
					} else {
						if (connectAgent.getIp().equals(exchangerVo.getIp()) 
								&& connectAgent.getPort() == exchangerVo.getCheck_Port()) {
							if (connectAgent.getConnectStatus() == false) {
								try {
									connectAgent.reconnect();
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Reconnect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + exchangerVo.getExchanger_Id()
												+ ", Name: " + exchangerVo.getName()
												+ ", Ip: " + exchangerVo.getIp()
												+ ", CheckPort: " + exchangerVo.getCheck_Port()
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> Reconnect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + exchangerVo.getExchanger_Id()
													+ ", Name: " + exchangerVo.getName()
													+ ", Ip: " + exchangerVo.getIp()
													+ ", CheckPort: " + exchangerVo.getCheck_Port()
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
										+ ", ConnectId: " + exchangerVo.getExchanger_Id()
										+ ", Ip: " + connectAgent.getIp()
										+ ", CheckPort: " + connectAgent.getPort()
										+ ", Ip Or Port Change"
										);
							}
							
							connectAgent = connectAgentFactory
									.createConnectAgent(exchangerVo.getIp(),
											exchangerVo.getCheck_Port(), exchangerVo.getExchanger_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(exchangerVo.getExchanger_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + exchangerVo.getExchanger_Id()
											+ ", Name: " + exchangerVo.getName()
											+ ", Ip: " + exchangerVo.getIp()
											+ ", CheckPort: " + exchangerVo.getCheck_Port()
											+ ", Ip Or Port Change"
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + exchangerVo.getExchanger_Id()
												+ ", Name: " + exchangerVo.getName()
												+ ", Ip: " + exchangerVo.getIp()
												+ ", CheckPort: " + exchangerVo.getCheck_Port()
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
