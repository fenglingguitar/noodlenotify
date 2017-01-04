package org.fl.noodlenotify.core.manager;

import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.agent.ConnectAgentFactory;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.cluster.ConnectClusterFactory;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.connect.node.ConnectNode;
import org.fl.noodle.common.connect.node.ConnectNodeImpl;
import org.fl.noodle.common.connect.performance.ConnectPerformanceInfo;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodle.common.connect.route.ConnectRouteFactory;
import org.fl.noodle.common.connect.serialize.ConnectSerializeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodle.common.util.thread.ExecutorThreadFactory;
import org.fl.noodle.common.util.thread.Stopping;

public abstract class AbstractConnectManagerRestructure implements ConnectManager {
	
	private final static Logger logger = LoggerFactory.getLogger(AbstractConnectManagerRestructure.class);
	
	private long suspendTime = 60000;
	
	private long calculateAvgTimeInterval = 5000;
	
	protected Map<String, ConnectAgentFactory> connectAgentFactoryMap;
	protected Map<String, ConnectClusterFactory> connectClusterFactoryMap;
	protected Map<String, ConnectRouteFactory> connectRouteFactoryMap;
	protected Map<String, ConnectSerializeFactory> connectSerializeFactoryMap;
	
	protected ConcurrentMap<String, ConnectNode> connectNodeMap = new ConcurrentHashMap<String, ConnectNode>();
	protected ConcurrentMap<Long, ConnectAgent> connectAgentMap = new ConcurrentHashMap<Long, ConnectAgent>();
	protected ConcurrentMap<String, ConnectCluster> connectClusterMap = new ConcurrentHashMap<String, ConnectCluster>();
	protected ConcurrentMap<String, ConnectRoute> connectRouteMap = new ConcurrentHashMap<String, ConnectRoute>();
	
	protected ConcurrentMap<String, ConnectPerformanceInfo> connectPerformanceInfoMap = new ConcurrentHashMap<String, ConnectPerformanceInfo>();

	protected ExecutorService executorService = Executors.newSingleThreadExecutor(new ExecutorThreadFactory(this.getClass().getName()));
	
	protected volatile boolean stopSign = false;
	private Stopping stopping = new Stopping();
	
	private long bootPriority;
	
	protected Map<String, List<Object>> connectAndNodeInfoMap = null;
	protected Map<String, Object> clusterInfoMap = null;
	protected Map<String, Object> routeInfoMap = null;
	
	protected List<String> addNodeList = null;
	protected List<Object> addConnectList = null;
	protected Map<String, List<Object>> addConnectMappingMap = null;
	protected List<String> addClusterList = null;
	protected List<String> addRouteList = null;
	
	protected List<String> reduceNodeList = null;
	protected List<ConnectAgent> reduceConnectList = null;
	protected Map<String, List<ConnectAgent>> reduceConnectMappingMap = null;
	protected List<String> reduceClusterList = null;
	protected List<String> reduceRouteList = null;
	
	@Override
	public void start() {
		
		stopping.stopInit(1);
		
		runUpdateNow();
		
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				while (!stopSign) {
					suspendUpdate();
					updateConnectAgent();
				}
				destroyConnectAgent();
				stopping.stopDo();
			}
		});
		
		(new Timer(true)).schedule(new TimerTask() {
			public void run() {
				for (Entry<Long, ConnectAgent> entry : connectAgentMap.entrySet()) {
					entry.getValue().calculate();
				}
			}
		}, calculateAvgTimeInterval, calculateAvgTimeInterval);
	}
	
	@Override
	public void destroy() {
		stopSign = true;
		do {				
			runUpdate();
		} while (!stopping.stopWait(1000));
		executorService.shutdown();
	}
	
	protected synchronized void suspendUpdate() {
		try {
			wait(suspendTime);
		} catch (InterruptedException e) {
			logger.error("suspendUpdateConnectAgent -> wait -> Exception:{}", e.getMessage());
		}
	}
	
	@Override
	public synchronized void runUpdate() {
		notifyAll();
	}
	
	@Override
	public synchronized void runUpdateNow() {
		updateConnectAgent();
	}
	
	protected synchronized void updateConnectAgent() {
		
		connectAndNodeInfoMap = null;
		clusterInfoMap = null;
		routeInfoMap = null;
		
		queryInfo();
		
		if (connectAndNodeInfoMap != null && !connectAndNodeInfoMap.isEmpty()) {
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
	
	protected void destroyConnectAgent() {
		
		connectNodeMap.clear();
		
		Set<Long> connectAgentKeySet = connectAgentMap.keySet();
		for (long key : connectAgentKeySet) {
			ConnectAgent connectAgent = connectAgentMap.get(key);
			connectAgent.close();
		}
		connectAgentMap.clear();
	}
	
	protected void getAddNode() {
		addNodeList = new ArrayList<String>();
		for (String name : connectAndNodeInfoMap.keySet()) {
			if (!connectNodeMap.containsKey(name)) {
				addNodeList.add(name);
			}
		}
	}
	
	protected void addNode() {
		for (String name : addNodeList) {
			connectNodeMap.put(name, new ConnectNodeImpl(name));
		}
	}
	
	protected void getAddConnect() {
		addConnectList = new ArrayList<Object>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (List<Object> objectListIt : connectAndNodeInfoMap.values()) {
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
	
	protected void addConnect() {
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
	
	protected void getAddConnectMapping() {
		addConnectMappingMap = new HashMap<String, List<Object>>();
		for (String name : connectAndNodeInfoMap.keySet()) {
			List<Object> objectList = connectAndNodeInfoMap.get(name);
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
	
	protected void addConnectMapping() {
		for (String name : addConnectMappingMap.keySet()) {
			for (Object objectIt : addConnectMappingMap.get(name)) {
				ConnectAgent connectAgent = connectAgentMap.get(getId(objectIt));
				if (connectAgent.isHealthyConnect()) {
					connectNodeMap.get(name).addConnectAgent(connectAgent);
				}
			}
		}
	}
	
	protected void getReduceNode() {
		reduceNodeList = new ArrayList<String>();
		for (String name : connectNodeMap.keySet()) {
			if (!connectAndNodeInfoMap.containsKey(name)) {
				reduceNodeList.add(name);
			}
		}
	}
	
	protected void reduceNode() {
		for (String name : reduceNodeList) {
			connectNodeMap.remove(name);
		}
	}

	protected void getReduceConnect() {
		reduceConnectList = new ArrayList<ConnectAgent>();
		Map<Long, Object> objectMap = new HashMap<Long, Object>();
		for (List<Object> objectListIt : connectAndNodeInfoMap.values()) {
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
	
	protected void reduceConnect() {
		for (ConnectAgent connectAgent : reduceConnectList) {
			connectAgentMap.remove(connectAgent.getConnectId()).close();
		}
	}
	
	protected void getReduceConnectMapping() {
		reduceConnectMappingMap = new HashMap<String, List<ConnectAgent>>();
		for (String name : connectAndNodeInfoMap.keySet()) {
			List<Object> objectList = connectAndNodeInfoMap.get(name);
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
	
	protected void reduceConnectMapping() {
		for (String name : reduceConnectMappingMap.keySet()) {
			for (ConnectAgent connectAgentIt : reduceConnectMappingMap.get(name)) {
				connectNodeMap.get(name).removeConnectAgent(connectAgentIt);
			}
		}
	}
	
	protected void getAddCluster() {
		addClusterList = new ArrayList<String>();
		for (String name : clusterInfoMap.keySet()) {
			if (!connectClusterMap.containsKey(name) || !connectClusterMap.get(name).getType().equals(clusterInfoMap.get(name))) {
				addClusterList.add(name);
			}
		}
	}
	
	protected void addCluster() {
		for (String name : addClusterList) {
			connectClusterMap.put(name, connectClusterFactoryMap.get(clusterInfoMap.get(name)).createConnectCluster(getConnectAgentClass()));
		}
	}

	protected void getReduceCluster() {
		reduceClusterList = new ArrayList<String>();
		for (String name : connectClusterMap.keySet()) {
			if (!clusterInfoMap.containsKey(name)) {
				reduceClusterList.add(name);
			}
		}
	}
	
	protected void reduceCluster() {
		for (String name : reduceClusterList) {
			connectClusterMap.remove(name);
		}
	}
	
	protected void getAddRoute() {
		addRouteList = new ArrayList<String>();
		for (String name : routeInfoMap.keySet()) {
			if (!connectRouteMap.containsKey(name) || !connectRouteMap.get(name).getType().equals(routeInfoMap.get(name))) {
				addRouteList.add(name);
			}
		}
	}

	protected void addRoute() {
		for (String name : addRouteList) {
			connectRouteMap.put(name, connectRouteFactoryMap.get(routeInfoMap.get(name)).createConnectRoute());
		}
	}
	
	protected void getReduceRoute() {
		reduceRouteList = new ArrayList<String>();
		for (String name : connectRouteMap.keySet()) {
			if (!routeInfoMap.containsKey(name)) {
				reduceRouteList.add(name);
			}
		}
	}

	protected void reduceRoute() {
		for (String name : reduceRouteList) {
			connectRouteMap.remove(name);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void getConnectAndNodeInfoMap(Object object) {
		connectAndNodeInfoMap = (Map<String, List<Object>>) object;
	}
	
	@SuppressWarnings("unchecked")
	protected void getClusterInfoMap(Object object) {
		clusterInfoMap = (Map<String, Object>) object;
	}
	
	@SuppressWarnings("unchecked")
	protected void getRouteInfoMap(Object object) {
		routeInfoMap = (Map<String, Object>) object;
	}
	
	protected Long getId(Object object) {
		try {
			return (Long) object.getClass().getMethod("get" + getIdName()).invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public ConnectNode getConnectNode(String nodeName) {
		return connectNodeMap.get(nodeName);
	}

	@Override
	public ConnectAgent getConnectAgent(long connectId) {
		return connectAgentMap.get(connectId);
	}
	
	@Override
	public ConnectCluster getConnectCluster(String clusterName) {
		return connectClusterMap.get(clusterName);
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get(routeName);
	}
	
	@Override
	public ConnectPerformanceInfo getConnectPerformanceInfo(String methodKey) {
		return connectPerformanceInfoMap.get(methodKey);
	}
	
	protected abstract void queryInfo();
	protected abstract String getIdName();
	protected abstract ConnectAgent createConnectAgent(Object object);
	protected abstract boolean isSameConnect(ConnectAgent connectAgent, Object object);
	protected abstract Class<?> getConnectAgentClass();

	public void setSuspendTime(long suspendTime) {
		this.suspendTime = suspendTime;
	}
	
	public void setCalculateAvgTimeInterval(long calculateAvgTimeInterval) {
		this.calculateAvgTimeInterval = calculateAvgTimeInterval;
	}

	public void setConnectAgentFactoryMap(Map<String, ConnectAgentFactory> connectAgentFactoryMap) {
		this.connectAgentFactoryMap = connectAgentFactoryMap;
	}

	public void setConnectClusterFactoryMap(Map<String, ConnectClusterFactory> connectClusterFactoryMap) {
		this.connectClusterFactoryMap = connectClusterFactoryMap;
	}

	public void setConnectRouteFactoryMap(Map<String, ConnectRouteFactory> connectRouteFactoryMap) {
		this.connectRouteFactoryMap = connectRouteFactoryMap;
	}

	public void setConnectSerializeFactoryMap(Map<String, ConnectSerializeFactory> connectSerializeFactoryMap) {
		this.connectSerializeFactoryMap = connectSerializeFactoryMap;
	}

	@Override
	public long getBootPriority() {
		return bootPriority;
	}

	public void setBootPriority(long bootPriority) {
		this.bootPriority = bootPriority;
	}
}
