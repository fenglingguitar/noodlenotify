package org.fl.noodlenotify.core.connect.net.manager.console;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactory;
import org.fl.noodlenotify.core.connect.ConsoleConnectManagerAbstract;

public class ProducerConsoleNetConnectManager extends ConsoleConnectManagerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(ProducerConsoleNetConnectManager.class);
	
	Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	
	public ProducerConsoleNetConnectManager() {
	}
	
	public ProducerConsoleNetConnectManager(ConnectAgentFactory connectAgentFactory) {
		super(connectAgentFactory);
	}

	@Override
	protected void updateConnectAgent() {
		
		List<ProducerVo> consoleInfoList = null;
		
		try {
			consoleInfoList = consoleRemotingInvoke.queryCheckProducers();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateConnectAgent -> "
							+ "ModuleId: " + moduleId
							+ ", Query Check Producers -> " + e
							);
			}
		}
		
		if (consoleInfoList != null) {
			
			if (logger.isDebugEnabled()) {
				for (ProducerVo producerVo : consoleInfoList) {
					logger.debug("UpdateConnectAgent -> QueryCheckProducers -> " 
								+ "ModuleId: " + moduleId
								+ ", ConnectId: " + producerVo.getProducer_Id()
								+ ", Name: " + producerVo.getName()
								+ ", Ip: " + producerVo.getIp()
								+ ", CheckPort: " + producerVo.getCheck_Port()
								+ ", CheckUrl: " + producerVo.getCheck_Url()
								+ ", CheckType: " + producerVo.getCheck_Type()
								);
				}
			}
			
			Set<Long> connectIdSet = new HashSet<Long>();

			for (ProducerVo producerVo : consoleInfoList) {
				connectIdSet.add(producerVo.getProducer_Id());
				if (connectAgentFactoryMap != null) {
					ConnectAgentFactory connectAgentFactory = connectAgentFactoryMap.get(producerVo.getCheck_Type());
					if (connectAgentFactory != null) {
						ConnectAgent connectAgent = connectAgentMap.get(producerVo.getProducer_Id());
						if (connectAgent == null) {
							connectAgent = connectAgentFactory
									.createConnectAgent(producerVo.getIp(),
											producerVo.getCheck_Port(), producerVo.getCheck_Url(), producerVo.getProducer_Id());
							try {
								connectAgent.connect();
								connectAgentMap.put(producerVo.getProducer_Id(), connectAgent);
								if (logger.isDebugEnabled()) {
									logger.debug("UpdateConnectAgent -> Connect -> " 
											+ "ModuleId: " + moduleId
											+ ", ConnectId: " + producerVo.getProducer_Id()
											+ ", Name: " + producerVo.getName()
											+ ", Ip: " + producerVo.getIp()
											+ ", CheckPort: " + producerVo.getCheck_Port()
											+ ", CheckUrl: " + producerVo.getCheck_Url()
											+ ", CheckType: " + producerVo.getCheck_Type()
											);
								}
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("UpdateConnectAgent -> Connect -> "
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + producerVo.getProducer_Id()
												+ ", Name: " + producerVo.getName()
												+ ", Ip: " + producerVo.getIp()
												+ ", CheckPort: " + producerVo.getCheck_Port()
												+ ", CheckUrl: " + producerVo.getCheck_Url()
												+ ", CheckType: " + producerVo.getCheck_Type()
												+ ", ConnectAgent Connect -> " + e
												);
								}
							}
						} else {
							if (connectAgent.getIp().equals(producerVo.getIp()) 
									&& connectAgent.getPort() == producerVo.getCheck_Port()
										&& (connectAgent.getUrl() != null && producerVo.getCheck_Url() != null && connectAgent.getUrl().equals(producerVo.getCheck_Url()) || (connectAgent.getUrl() == null && producerVo.getCheck_Url() == null))
											&& connectAgent.getType().equals(producerVo.getCheck_Type())
							) {
								if (connectAgent.getConnectStatus() == false) {
									try {
										connectAgent.reconnect();
										if (logger.isDebugEnabled()) {
											logger.debug("UpdateConnectAgent -> Reconnect -> " 
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + producerVo.getProducer_Id()
													+ ", Name: " + producerVo.getName()
													+ ", Ip: " + producerVo.getIp()
													+ ", CheckPort: " + producerVo.getCheck_Port()
													+ ", CheckUrl: " + producerVo.getCheck_Url()
													+ ", CheckType: " + producerVo.getCheck_Type()
													);
										}
									} catch (Exception e) {
										if (logger.isErrorEnabled()) {
											logger.error("UpdateConnectAgent -> Reconnect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + producerVo.getProducer_Id()
													+ ", Name: " + producerVo.getName()
													+ ", Ip: " + producerVo.getIp()
													+ ", CheckPort: " + producerVo.getCheck_Port()
													+ ", CheckUrl: " + producerVo.getCheck_Url()
													+ ", CheckType: " + producerVo.getCheck_Type()
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
											+ ", ConnectId: " + producerVo.getProducer_Id()
											+ ", Ip: " + connectAgent.getIp()
											+ ", CheckPort: " + connectAgent.getPort()
											+ ", CheckUrl: " + producerVo.getCheck_Url()
											+ ", CheckType: " + producerVo.getCheck_Type()
											+ ", Ip Or Port Or URL Or Type Change"
											);
								}
								
								connectAgent = connectAgentFactory
										.createConnectAgent(producerVo.getIp(),
												producerVo.getCheck_Port(), producerVo.getCheck_Url(), producerVo.getProducer_Id());
								try {
									connectAgent.connect();
									connectAgentMap.put(producerVo.getProducer_Id(), connectAgent);
									if (logger.isDebugEnabled()) {
										logger.debug("UpdateConnectAgent -> Connect -> " 
												+ "ModuleId: " + moduleId
												+ ", ConnectId: " + producerVo.getProducer_Id()
												+ ", Name: " + producerVo.getName()
												+ ", Ip: " + producerVo.getIp()
												+ ", CheckPort: " + producerVo.getCheck_Port()
												+ ", CheckUrl: " + producerVo.getCheck_Url()
												+ ", CheckType: " + producerVo.getCheck_Type()
												+ ", Ip Or Port Or URL Or Type Change"
												);
									}
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("UpdateConnectAgent -> Connect -> "
													+ "ModuleId: " + moduleId
													+ ", ConnectId: " + producerVo.getProducer_Id()
													+ ", Name: " + producerVo.getName()
													+ ", Ip: " + producerVo.getIp()
													+ ", CheckPort: " + producerVo.getCheck_Port()
													+ ", CheckUrl: " + producerVo.getCheck_Url()
													+ ", CheckType: " + producerVo.getCheck_Type()
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
