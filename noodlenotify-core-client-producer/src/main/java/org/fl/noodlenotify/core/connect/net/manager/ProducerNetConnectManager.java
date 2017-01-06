package org.fl.noodlenotify.core.connect.net.manager;

import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.AbstractConnectManagerTemplate;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;

public class ProducerNetConnectManager extends AbstractConnectManagerTemplate {
	
	//private final static Logger logger = LoggerFactory.getLogger(ProducerNetConnectManager.class);
	
	private ModuleRegister producerModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeInfoMap(consoleRemotingInvoke.producerGetExchangers(producerModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> clusterInfoMapTemp = new HashMap<String, String>();
			clusterInfoMapTemp.put("DEFALT", "FAILOVER");
			getClusterInfoMap(clusterInfoMapTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> routeInfoMapTemp = new HashMap<String, String>();
			routeInfoMapTemp.put("DEFALT", "RANDOM");
			getRouteInfoMap(routeInfoMapTemp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected String getIdName() {
		return "Exchanger_Id";
	}

	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get(((QueueExchangerVo)object).getType()).createConnectAgent(((QueueExchangerVo)object).getExchanger_Id(), ((QueueExchangerVo)object).getIp(), ((QueueExchangerVo)object).getPort(), ((QueueExchangerVo)object).getUrl());
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueExchangerVo)object).getIp(), ((QueueExchangerVo)object).getPort(), ((QueueExchangerVo)object).getUrl(), ((QueueExchangerVo)object).getType());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return NetConnectAgent.class;
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setProducerModuleRegister(ModuleRegister producerModuleRegister) {
		this.producerModuleRegister = producerModuleRegister;
	}

	@Override
	public String getManagerName() {
		return ConnectManagerType.NET.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
