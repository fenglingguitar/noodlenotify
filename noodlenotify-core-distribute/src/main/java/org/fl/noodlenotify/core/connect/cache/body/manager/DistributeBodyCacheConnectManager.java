package org.fl.noodlenotify.core.connect.cache.body.manager;

import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.core.connect.cache.body.BodyCacheConnectAgent;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.manager.AbstractConnectManagerRestructure;

public class DistributeBodyCacheConnectManager extends AbstractConnectManagerRestructure {

	//private final static Logger logger = LoggerFactory.getLogger(DistributeBodyCacheConnectManager.class);
	
	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeInfoMap(consoleRemotingInvoke.distributerGetMsgBodyCaches(distributeModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> clusterInfoMapTemp = new HashMap<String, String>();
			clusterInfoMapTemp.put("DEFALT", "FAILOVER");
			clusterInfoMapTemp.put("EITHER", "EITHER");
			clusterInfoMapTemp.put("PARTALL", "PARTALL");
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
		return "MsgBodyCache_Id";
	}

	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get("CACHE_BODY").createConnectAgent(((QueueMsgBodyCacheVo)object).getMsgBodyCache_Id(), ((QueueMsgBodyCacheVo)object).getIp(), ((QueueMsgBodyCacheVo)object).getPort(), null);
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueMsgBodyCacheVo)object).getIp(), ((QueueMsgBodyCacheVo)object).getPort(), null, ConnectAgentType.BODY_CACHE.getCode());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return BodyCacheConnectAgent.class;
	}
	
	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	@Override
	public String getManagerName() {
		return ConnectManagerType.BODY_CACHE.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
