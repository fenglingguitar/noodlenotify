package org.fl.noodlenotify.core.distribute.manager;

import java.util.HashMap;
import java.util.Map;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.AbstractConnectManagerTemplate;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;

public class DistributeDbConnectManager extends AbstractConnectManagerTemplate {
	
	//private final static Logger logger = LoggerFactory.getLogger(DistributeDbConnectManager.class);

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;

	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeInfoMap(consoleRemotingInvoke.distributerGetDb(distributeModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> clusterInfoMapTemp = new HashMap<String, String>();
			clusterInfoMapTemp.put("DEFALT", "FAILOVER");
			clusterInfoMapTemp.put("ID", "ID");
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
		return "Db_Id";
	}
	
	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get("MYSQL").createConnectAgent(((QueueDbVo)object).getDb_Id(), ((QueueDbVo)object).getIp(), ((QueueDbVo)object).getPort(), null);
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueDbVo)object).getIp(), ((QueueDbVo)object).getPort(), null, ConnectAgentType.DB.getCode());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return DbConnectAgent.class;
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
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
	
	@Override
	protected void addConnectMapping() {
		for (String name : addConnectMappingMap.keySet()) {
			for (Object objectIt : addConnectMappingMap.get(name)) {
				ConnectAgent connectAgent = connectAgentMap.get(getId(objectIt));
				if (connectAgent != null && connectAgent.isHealthyConnect()) {
					connectNodeMap.get(name).addConnectAgent(connectAgent);
					try {
						((DbConnectAgent)connectAgent).createTable(name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
