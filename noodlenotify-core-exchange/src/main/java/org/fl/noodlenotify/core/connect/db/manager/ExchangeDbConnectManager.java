package org.fl.noodlenotify.core.connect.db.manager;

import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.manager.AbstractConnectManagerRestructure;

public class ExchangeDbConnectManager extends AbstractConnectManagerRestructure {
	
	//private final static Logger logger = LoggerFactory.getLogger(ExchangeDbConnectManager.class);
	
	private ModuleRegister exchangeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeInfoMap(consoleRemotingInvoke.exchangerGetMsgStorages(exchangeModuleRegister.getModuleId()));
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
		return "MsgStorage_Id";
	}
	
	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get("MYSQL").createConnectAgent(((QueueMsgStorageVo)object).getMsgStorage_Id(), ((QueueMsgStorageVo)object).getIp(), ((QueueMsgStorageVo)object).getPort(), null);
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueMsgStorageVo)object).getIp(), ((QueueMsgStorageVo)object).getPort(), null, ConnectAgentType.DB.getCode());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return DbConnectAgent.class;
	}
	
	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	@Override
	public String getManagerName() {
		return ConnectManagerType.DB.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
