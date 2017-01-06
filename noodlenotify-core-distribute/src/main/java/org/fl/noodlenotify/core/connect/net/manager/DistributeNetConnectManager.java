package org.fl.noodlenotify.core.connect.net.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.net.NetConnectAgent;
import org.fl.noodlenotify.core.connect.net.cluster.LayerConnectCluster;
import org.fl.noodlenotify.core.manager.AbstractConnectManagerTemplate;

public class DistributeNetConnectManager extends AbstractConnectManagerTemplate {
	
	//private final static Logger logger = LoggerFactory.getLogger(DistributeNetConnectManager.class);
	
	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	protected Map<String, Map<Long, List<Object>>> connectAndNodeAndChildInfoMap = null;
	
	protected Map<String, List<Long>> addChildNodeMap = null;
	protected Map<String, Map<Long, List<Object>>> addChildConnectMappingMap = null;
	
	protected Map<String, Map<Long, List<ConnectAgent>>> reduceChildConnectMappingMap = null;
	protected Map<String, List<Long>> reduceChildNodeMap = null;
	
	@Override
	protected void destroyConnectAgent() {
		
		for (String name : connectNodeMap.keySet()) {
			ConnectNode connectNode = connectNodeMap.get(name);
			ConcurrentMap<Long, ConnectNode> childConnectNodeMap = connectNode.getChildConnectNodeMap();
			childConnectNodeMap.clear();
		}
		connectNodeMap.clear();
		
		for (long key : connectAgentMap.keySet()) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
		}
		connectAgentMap.clear();
	}
	
	@Override
	protected synchronized void updateConnectAgent() {
		
		connectAndNodeAndChildInfoMap = null;
		clusterInfoMap = null;
		routeInfoMap = null;
		
		queryInfo();
		
		if (connectAndNodeAndChildInfoMap != null && !connectAndNodeAndChildInfoMap.isEmpty()) {
			getAddConnect();
			addConnect();
			getAddNode();
			addNode();
			getAddNodeChild();
			addNodeChild();
			getAddConnectMapping();
			addConnectMapping();
			
			getReduceConnectMapping();
			reduceConnectMapping();
			getReduceConnect();
			reduceConnect();
			getReduceNodeChild();
			reduceNodeChild();
			getReduceNode();
			reduceNode();
		}
		
		if (clusterInfoMap != null && !clusterInfoMap.isEmpty()) {
			getAddCluster();
			addCluster();
			getReduceCluster();
			reduceCluster();
		}
		
		if (routeInfoMap != null && !routeInfoMap.isEmpty()) {
			getAddRoute();
			addRoute();
			getReduceRoute();
			reduceRoute(); 
		}
	}

	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeAndChildInfoMap(consoleRemotingInvoke.distributerGetQueueConsumerGroups(distributeModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> clusterInfoMapTemp = new HashMap<String, String>();
			clusterInfoMapTemp.put("DEFALT", "LAYER");
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
	protected void getAddConnect() {
		addConnectList = new ArrayList<Object>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (Map<Long, List<Object>> child : connectAndNodeAndChildInfoMap.values()) {
			for (List<Object> objectListIt : child.values()) {
				for (Object objectIt : objectListIt) {
					if (!objectMap.containsKey(getId(objectIt))) {
						objectMap.put(getId(objectIt), objectIt);
					}
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
	
	@Override
	protected void getAddNode() {
		addNodeList = new ArrayList<String>();
		for (String name : connectAndNodeAndChildInfoMap.keySet()) {
			if (!connectNodeMap.containsKey(name)) {
				addNodeList.add(name);
			}
		}
	}
	
	private void getAddNodeChild() {
		addChildNodeMap = new HashMap<String, List<Long>>();
		for (String name : connectAndNodeAndChildInfoMap.keySet()) {
			for (long childId : connectAndNodeAndChildInfoMap.get(name).keySet()) {
				if (!connectNodeMap.get(name).isContainsChildConnectNode(childId)) {
					List<Long> childList = addChildNodeMap.get(name);
					if (childList == null) {
						addChildNodeMap.put(name, new ArrayList<Long>());
					}
					addChildNodeMap.get(name).add(childId);
				}
			}
		}
	}

	private void addNodeChild() {
		for (String name : addChildNodeMap.keySet()) {
			for (long childId : addChildNodeMap.get(name)) {
				connectNodeMap.get(name).addChildConnectNode(childId, new ConnectNodeImpl(name));
			}
		}
	}
	
	@Override
	protected void getAddConnectMapping() {
		addChildConnectMappingMap = new HashMap<String, Map<Long, List<Object>>>();
		for (String name : connectAndNodeAndChildInfoMap.keySet()) {
			for (long childId : connectAndNodeAndChildInfoMap.get(name).keySet()) {
				for (Object objectIt : connectAndNodeAndChildInfoMap.get(name).get(childId)) {
					boolean isHave = false;
					for (ConnectAgent connectAgentIt : connectNodeMap.get(name).getChildConnectNode(childId).getAllConnectAgentList()) {
						if (connectAgentIt.getConnectId() == getId(objectIt)) {
							isHave = true;
						}
					}
					if (!isHave) {
						Map<Long, List<Object>> addChildMappingMap = addChildConnectMappingMap.get(name);
						if (addChildMappingMap == null) {
							addChildConnectMappingMap.put(name, new HashMap<Long, List<Object>>());
						}
						List<Object> addConnectMappingList = addChildConnectMappingMap.get(name).get(childId);
						if (addConnectMappingList == null) {
							addChildConnectMappingMap.get(name).put(childId, new ArrayList<Object>());
						}
						addChildConnectMappingMap.get(name).get(childId).add(objectIt);
					}
				}
			}
		}
	}
	
	@Override
	protected void addConnectMapping() {
		for (String name : addChildConnectMappingMap.keySet()) {
			for (long childId : addChildConnectMappingMap.get(name).keySet()) {
				for (Object objectIt : addChildConnectMappingMap.get(name).get(childId)) {
					ConnectAgent connectAgent = connectAgentMap.get(getId(objectIt));
					if (connectAgent.isHealthyConnect()) {
						connectNodeMap.get(name).getChildConnectNode(childId).addConnectAgent(connectAgent);
					}
				}
			}
		}
	}
	
	@Override
	protected void getReduceConnectMapping() {
		reduceChildConnectMappingMap = new HashMap<String, Map<Long, List<ConnectAgent>>>();
		for (String name : connectNodeMap.keySet()) {
			for (long childId : connectNodeMap.get(name).getChildConnectNodeMap().keySet()) {
				for (ConnectAgent connectAgentIt : connectNodeMap.get(name).getChildConnectNode(childId).getAllConnectAgentList()) {
					boolean isHave = false;
					for (Object objectIt : connectAndNodeAndChildInfoMap.get(name).get(childId)) {
						if (connectAgentIt.getConnectId() == getId(objectIt)) {
							isHave = true;
						}
					}
					if (!isHave) {
						Map<Long, List<ConnectAgent>> reduceChildMappingMap = reduceChildConnectMappingMap.get(name);
						if (reduceChildMappingMap == null) {
							reduceChildConnectMappingMap.put(name, new HashMap<Long, List<ConnectAgent>>());
						}
						List<ConnectAgent> reduceConnectMappingList = reduceChildConnectMappingMap.get(name).get(childId);
						if (reduceConnectMappingList == null) {
							reduceChildConnectMappingMap.get(name).put(childId, new ArrayList<ConnectAgent>());
						}
						reduceChildConnectMappingMap.get(name).get(childId).add(connectAgentIt);
					}
				}
			}
			
		}
	}
	
	@Override
	protected void reduceConnectMapping() {
		for (String name : reduceChildConnectMappingMap.keySet()) {
			for (long childId : reduceChildConnectMappingMap.get(name).keySet()) {
				for (ConnectAgent connectAgentIt : reduceChildConnectMappingMap.get(name).get(childId)) {
					connectNodeMap.get(name).getChildConnectNode(childId).removeConnectAgent(connectAgentIt);
				}
			}
		}
	}
	
	@Override
	protected void getReduceConnect() {
		reduceConnectList = new ArrayList<ConnectAgent>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (Map<Long, List<Object>> objectListMapIt : connectAndNodeAndChildInfoMap.values()) {
			for (List<Object> objectListIt : objectListMapIt.values()) {
				for (Object objectIt : objectListIt) {
					if (!objectMap.containsKey(getId(objectIt))) {
						objectMap.put(getId(objectIt), objectIt);
					}
				}
			}
		}
		
		for (ConnectAgent connectAgent : connectAgentMap.values()) {
			if (!objectMap.containsKey(connectAgent.getConnectId())) {
				reduceConnectList.add(connectAgent);
			}
		}
	}
	
	@Override
	protected void getReduceNode() {
		reduceNodeList = new ArrayList<String>();
		for (String name : connectNodeMap.keySet()) {
			if (!connectAndNodeAndChildInfoMap.containsKey(name)) {
				reduceNodeList.add(name);
			}
		}
	}
	
	private void getReduceNodeChild() {
		reduceChildNodeMap = new HashMap<String, List<Long>>();
		for (String name : connectNodeMap.keySet()) {
			for (long childId : connectNodeMap.get(name).getChildConnectNodeMap().keySet()) {
				if (!connectAndNodeAndChildInfoMap.get(name).containsKey(childId)) {
					if (!reduceChildNodeMap.containsKey(name)) {
						reduceChildNodeMap.put(name, new ArrayList<Long>());
					}
					reduceChildNodeMap.get(name).add(childId);
				}
			}
			
		}
	}

	private void reduceNodeChild() {
		for (String name : reduceChildNodeMap.keySet()) {
			for (long childId : reduceChildNodeMap.get(name)) {
				connectNodeMap.get(name).removeChildConnectNode(childId);
			}
		}
	}
	
	@Override
	protected void addCluster() {
		for (String name : addClusterList) {
			connectClusterMap.put(name, connectClusterFactoryMap.get(clusterInfoMap.get(name)).createConnectCluster(getConnectAgentClass()));
			((LayerConnectCluster)connectClusterMap.get(name)).setConnectCluster(connectClusterFactoryMap.get("LAYERFAILOVER").createConnectCluster(getConnectAgentClass()));
		}
	}
	
	@Override
	protected String getIdName() {
		return "Consumer_Id";
	}

	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get(((QueueConsumerVo)object).getType()).createConnectAgent(((QueueConsumerVo)object).getConsumer_Id(), ((QueueConsumerVo)object).getIp(), ((QueueConsumerVo)object).getPort(), ((QueueConsumerVo)object).getUrl());
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueConsumerVo)object).getIp(), ((QueueConsumerVo)object).getPort(), ((QueueConsumerVo)object).getUrl(), ((QueueConsumerVo)object).getType());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return NetConnectAgent.class;
	}
	
	@SuppressWarnings("unchecked")
	protected void getConnectAndNodeAndChildInfoMap(Object object) {
		connectAndNodeAndChildInfoMap = (Map<String, Map<Long, List<Object>>>) object;
	}
	
	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
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
