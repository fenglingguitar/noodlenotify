package org.fl.noodlenotify.core.connect.net.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodlenotify.console.vo.CustomerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ConsumerConsoleNetConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ConsumerConsoleNetConnectManager.class);
	
	Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	public ConsumerConsoleNetConnectManager() {
	}
	
	public ConsumerConsoleNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<CustomerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckCustomers();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check Customers -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (CustomerVo customerVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckCustomers -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + customerVo.getCustomer_Id()
								+ ", Name: " + customerVo.getName()
								+ ", Ip: " + customerVo.getIp()
								+ ", CheckPort: " + customerVo.getCheck_Port()
								+ ", CheckUrl: " + customerVo.getCheck_Url()
								+ ", CheckType: " + customerVo.getCheck_Type()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (CustomerVo customerVo : consoleInfoList) {
				connectIdSet.add(customerVo.getCustomer_Id());
				if (connectAgentFactoryMap != null) {
					ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(customerVo.getCheck_Type());
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(customerVo.getCustomer_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(customerVo.getIp(),
											customerVo.getCheck_Port(), customerVo.getCheck_Url(), customerVo.getCustomer_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(customerVo.getCustomer_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + customerVo.getCustomer_Id()
											+ ", Name: " + customerVo.getName()
											+ ", Ip: " + customerVo.getIp()
											+ ", CheckPort: " + customerVo.getCheck_Port()
											+ ", CheckUrl: " + customerVo.getCheck_Url()
											+ ", CheckType: " + customerVo.getCheck_Type()
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + customerVo.getCustomer_Id()
												+ ", Name: " + customerVo.getName()
												+ ", Ip: " + customerVo.getIp()
												+ ", CheckPort: " + customerVo.getCheck_Port()
												+ ", CheckUrl: " + customerVo.getCheck_Url()
												+ ", CheckType: " + customerVo.getCheck_Type()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(customerVo.getIp()) 
									&& connectAgent.getPort() == customerVo.getCheck_Port()
										&& (connectAgent.getUrl() != null && customerVo.getCheck_Url() != null && connectAgent.getUrl().equals(customerVo.getCheck_Url()) || (connectAgent.getUrl() == null && customerVo.getCheck_Url() == null))
										 	&& connectAgent.getType().equals(customerVo.getCheck_Type())
							) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + customerVo.getCustomer_Id()
													+ ", Name: " + customerVo.getName()
													+ ", Ip: " + customerVo.getIp()
													+ ", CheckPort: " + customerVo.getCheck_Port()
													+ ", CheckUrl: " + customerVo.getCheck_Url()
													+ ", CheckType: " + customerVo.getCheck_Type()
													);
										}
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> Reconnect -> "
														+ "ModuleId: " + moduleId
														+ ", ConnectId: " + customerVo.getCustomer_Id()
														+ ", Name: " + customerVo.getName()
														+ ", Ip: " + customerVo.getIp()
														+ ", CheckPort: " + customerVo.getCheck_Port()
														+ ", CheckUrl: " + customerVo.getCheck_Url()
														+ ", CheckType: " + customerVo.getCheck_Type()
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
											+ ", ConnectId: " + customerVo.getCustomer_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", CheckPort: " + connectAgent.getPort()
											+ ", CheckUrl: " + customerVo.getCheck_Url()
											+ ", CheckType: " + customerVo.getCheck_Type()
											+ ", Ip Or Port Or URL Or Type Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(customerVo.getIp(),
												customerVo.getCheck_Port(), customerVo.getCheck_Url(), customerVo.getCustomer_Id());
								try {
									connectAgent.connect();
									connectAgentMap.put(customerVo.getCustomer_Id(), connectAgent);
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Connect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + customerVo.getCustomer_Id()
												+ ", Name: " + customerVo.getName()
												+ ", Ip: " + customerVo.getIp()
												+ ", CheckPort: " + customerVo.getCheck_Port()
												+ ", CheckUrl: " + customerVo.getCheck_Url()
												+ ", CheckType: " + customerVo.getCheck_Type()
												+ ", Ip Or Port Or URL Or Type Change"
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> Connect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + customerVo.getCustomer_Id()
													+ ", Name: " + customerVo.getName()
													+ ", Ip: " + customerVo.getIp()
													+ ", CheckPort: " + customerVo.getCheck_Port()
													+ ", CheckUrl: " + customerVo.getCheck_Url()
													+ ", CheckType: " + customerVo.getCheck_Type()
													+ ", Ip Or Port Or URL Or Type Change, ConnectAgent Connect -> " + e
													);
									}
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
								+ ", CheckUrl: " + connectAgent.getUrl()
								+ ", CheckType: " + connectAgent.getType()
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
						+ ", CheckUrl: " + connectAgent.getUrl()
						+ ", CheckType: " + connectAgent.getType()
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
