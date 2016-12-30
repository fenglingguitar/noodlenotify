package org.fl.noodlenotify.core.connect.db.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.AbstractConnectManager;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;

public class DistributeDbConnectManager extends AbstractConnectManager {
	
	//private final static Logger logger = LoggerFactory.getLogger(DistributeDbConnectManager.class);

	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private Map<String, List<Object>> consoleInfoMap = null;
	
	private List<String> addNodeList = null;
	private List<Object> addConnectList = null;
	private Map<String, List<Object>> addConnectMappingMap = null;
	
	private List<String> reduceNodeList = null;
	private List<ConnectAgent> reduceConnectList = null;
	private Map<String, List<ConnectAgent>> reduceConnectMappingMap = null;
	
	@Override
	protected void updateConnectAgent() {
		
		if (connectClusterMap.isEmpty()) {
			connectClusterMap.put("DEFALT", connectClusterFactoryMap.get("FAILOVER").createConnectCluster(DbConnectAgent.class));
			connectClusterMap.put("ID", connectClusterFactoryMap.get("ID").createConnectCluster(DbConnectAgent.class));
		}
		
		if (connectRouteMap.isEmpty()) {
			connectRouteMap.put("DEFALT", connectRouteFactoryMap.get("RANDOM").createConnectRoute());
		}
		
		queryInfo();
		
		if (consoleInfoMap == null || consoleInfoMap.isEmpty()) { return; }
		
		getAddConnect();
		addConnect();
		getAddNode();
		addNode();
		getAddConnectMapping();
		addConnectMapping();
		
		getReduceConnectMapping();
		reduceConnectMapping();
		getReduceConnect();
		reduceConnect();
		getReduceNode();
		reduceNode();
	}
	
	private void queryInfo() {
		consoleInfoMap = null;
		try {
			getConsoleInfoMap(consoleRemotingInvoke.distributerGetMsgStorages(distributeModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getAddNode() {
		addNodeList = new ArrayList<String>();
		for (String name : consoleInfoMap.keySet()) {
			if (!connectNodeMap.containsKey(name)) {
				addNodeList.add(name);
			}
		}
	}
	
	private void addNode() {
		for (String name : addNodeList) {
			connectNodeMap.put(name, new ConnectNodeImpl(name));
		}
	}
	
	private void getAddConnect() {
		addConnectList = new ArrayList<Object>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (List<Object> objectListIt : consoleInfoMap.values()) {
			for (Object objectIt : objectListIt) {
				if (!objectMap.containsKey(getId(objectIt))) {
					objectMap.put(getId(objectIt), objectIt);
				}
			}
		}
		for (Object objectIt : objectMap.values()) {
			if (!connectAgentMap.containsKey(getId(objectIt))) {
				addConnectList.add(objectIt);
			} else {
				ConnectAgent connectAgent = connectAgentMap.get(getId(objectIt));
				if (!isSameConnect(connectAgent, objectIt)) {
					addConnectList.add(objectIt);
				}
			}
		}
	}
	
	private void addConnect() {
		for (Object objectIt : addConnectList) {
			ConnectAgent connectAgent = createConnectAgent(objectIt);
			if (connectAgentMap.containsKey(getId(objectIt))) {
				connectAgentMap.remove(getId(objectIt)).close();
			}
			try {
				connectAgent.connect();
				connectAgentMap.put(getId(objectIt), connectAgent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (ConnectAgent connectAgent : connectAgentMap.values()) {
			if (!connectAgent.isHealthyConnect()) {
				try {
					connectAgent.reconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void getAddConnectMapping() {
		addConnectMappingMap = new HashMap<String, List<Object>>();
		for (String name : consoleInfoMap.keySet()) {
			List<Object> objectList = consoleInfoMap.get(name);
			List<ConnectAgent> connectAgentList = connectNodeMap.get(name).getConnectAgentList();
			for (Object objectIt : objectList) {
				boolean isHave = false;
				for (ConnectAgent connectAgentIt : connectAgentList) {
					if (connectAgentIt.getConnectId() == getId(objectIt)) {
						isHave = true;
					}
				}
				if (!isHave) {
					List<Object> addConnectMappingList = addConnectMappingMap.get(name);
					if (addConnectMappingList == null) {
						addConnectMappingMap.put(name, new ArrayList<Object>());
					}
					addConnectMappingMap.get(name).add(objectIt);
				}
			}
		}
	}
	
	private void addConnectMapping() {
		for (String name : addConnectMappingMap.keySet()) {
			for (Object objectIt : addConnectMappingMap.get(name)) {
				ConnectAgent connectAgent = connectAgentMap.get(getId(objectIt));
				if (connectAgent.isHealthyConnect()) {
					connectNodeMap.get(name).addConnectAgent(connectAgent);
				}
			}
		}
	}
	
	private void getReduceNode() {
		reduceNodeList = new ArrayList<String>();
		for (String name : connectNodeMap.keySet()) {
			if (!consoleInfoMap.containsKey(name)) {
				reduceNodeList.add(name);
			}
		}
	}
	
	private void reduceNode() {
		for (String name : reduceNodeList) {
			connectNodeMap.remove(name);
		}
	}

	private void getReduceConnect() {
		reduceConnectList = new ArrayList<ConnectAgent>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (List<Object> objectListIt : consoleInfoMap.values()) {
			for (Object objectIt : objectListIt) {
				if (!objectMap.containsKey(getId(objectIt))) {
					objectMap.put(getId(objectIt), objectIt);
				}
			}
		}
		for (ConnectAgent connectAgent : connectAgentMap.values()) {
			if (!objectMap.containsKey(connectAgent.getConnectId())) {
				reduceConnectList.add(connectAgent);
			}
		}
	}
	
	private void reduceConnect() {
		for (ConnectAgent connectAgent : reduceConnectList) {
			connectAgentMap.remove(connectAgent.getConnectId()).close();
		}
	}
	
	private void getReduceConnectMapping() {
		reduceConnectMappingMap = new HashMap<String, List<ConnectAgent>>();
		for (String name : consoleInfoMap.keySet()) {
			List<Object> objectList = consoleInfoMap.get(name);
			List<ConnectAgent> connectAgentList = connectNodeMap.get(name).getConnectAgentList();
			for (ConnectAgent connectAgentIt : connectAgentList) {
				boolean isHave = false;
				for (Object objectIt : objectList) {
					if (connectAgentIt.getConnectId() == getId(objectIt)) {
						isHave = true;
					}
				}
				if (!isHave) {
					List<ConnectAgent> reduceConnectMappingList = reduceConnectMappingMap.get(name);
					if (reduceConnectMappingList == null) {
						reduceConnectMappingMap.put(name, new ArrayList<ConnectAgent>());
					}
					reduceConnectMappingMap.get(name).add(connectAgentIt);
				}
			}
		}
	}
	
	private void reduceConnectMapping() {
		for (String name : reduceConnectMappingMap.keySet()) {
			for (ConnectAgent connectAgentIt : reduceConnectMappingMap.get(name)) {
				connectNodeMap.get(name).removeConnectAgent(connectAgentIt);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void getConsoleInfoMap(Object object) {
		consoleInfoMap = (Map<String, List<Object>>) object;
	}
	
	private Long getId(Object object) {
		try {
			return (Long) object.getClass().getMethod("get" + getIdName()).invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getIdName() {
		return "MsgStorage_Id";
	}
	
	private ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get("MYSQL").createConnectAgent(((QueueMsgStorageVo)object).getMsgStorage_Id(), ((QueueMsgStorageVo)object).getIp(), ((QueueMsgStorageVo)object).getPort(), null);
	}
	
	private boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueMsgStorageVo)object).getIp(), ((QueueMsgStorageVo)object).getPort(), null, ConnectAgentType.DB.getCode());
	}
	
	@Override
	protected void destroyConnectAgent() {
		
		connectNodeMap.clear();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
		}
		connectAgentMap.clear();
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
}
