package org.fl.noodlenotify.core.connect.cache.queue.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.AbstractConnectManagerTemplate;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.route.ConnectRoute;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.CheckActiveLockChangeHandler;
import org.fl.noodlenotify.core.distribute.locker.cache.queue.QueueCacheDistributeSetLocker;

public class DistributeQueueCacheConnectManager extends AbstractConnectManagerTemplate {

	//private final static Logger logger = LoggerFactory.getLogger(DistributeQueueCacheConnectManager.class);
	
	private ModuleRegister distributeModuleRegister;
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private ConcurrentMap<String, QueueCacheDistributeSetLocker> queueCacheDistributeSetLockerMap = new ConcurrentHashMap<String, QueueCacheDistributeSetLocker>();
	
	private List<String> addLockerList = null;
	private List<String> reduceLockerList = null;
	
	@Override
	protected void addComponent() {
		if (connectAndNodeInfoMap != null) {
			getAddNode();
			addNode();
			getAddConnect();
			addConnect();
			getAddConnectMapping();
			addConnectMapping();
			getAddLocker();
			addLocker();
		}
		
		if (clusterInfoMap != null) {
			getAddCluster();
			addCluster();
		}
		
		if (routeInfoMap != null) {
			getAddRoute();
			addRoute();
		}
	}
	
	@Override
	protected void reduceComponent() {
		if (connectAndNodeInfoMap != null) {
			getReduceLocker();
			reduceLocker();
			getReduceConnectMapping();
			reduceConnectMapping();
			getReduceConnect();
			reduceConnect();
			getReduceNode();
			reduceNode();
		}
		
		if (clusterInfoMap != null) {
			getReduceCluster();
			reduceCluster();
		}
		
		if (routeInfoMap != null) {
			getReduceRoute();
			reduceRoute(); 
		}
	}

	private void getAddLocker() {
		addLockerList = new ArrayList<String>();
		for (String name : connectAndNodeInfoMap.keySet()) {
			if (!queueCacheDistributeSetLockerMap.containsKey(name)) {
				addLockerList.add(name);
			}
		}
	}
	
	private void addLocker() {
		for (String name : addLockerList) {
			QueueCacheDistributeSetLocker queueCacheDistributeSetLocker = new QueueCacheDistributeSetLocker(name, distributeModuleRegister.getModuleId(), this);
			CheckActiveLockChangeHandler checkActiveLockChangeHandler = new CheckActiveLockChangeHandler(name, this);
			queueCacheDistributeSetLocker.setLockChangeHandler(checkActiveLockChangeHandler);
			try {
				queueCacheDistributeSetLocker.start();
				queueCacheDistributeSetLockerMap.put(name, queueCacheDistributeSetLocker);
				checkActiveLockChangeHandler.waitActiveReady();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void getReduceLocker() {
		reduceLockerList = new ArrayList<String>();
		for (String name : queueCacheDistributeSetLockerMap.keySet()) {
			if (!connectAndNodeInfoMap.containsKey(name)) {
				reduceLockerList.add(name);
			}
		}
	}
	
	private void reduceLocker() {
		for (String name : reduceLockerList) {
			queueCacheDistributeSetLockerMap.remove(name).destroy();
		}
	}
	
	@Override
	protected void queryInfo() {
		try {
			getConnectAndNodeInfoMap(consoleRemotingInvoke.distributerGetMsgQueueCaches(distributeModuleRegister.getModuleId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			Map<String, String> clusterInfoMapTemp = new HashMap<String, String>();
			clusterInfoMapTemp.put("DEFALT", "MASTER");
			clusterInfoMapTemp.put("OTHER", "OTHER");
			clusterInfoMapTemp.put("ALL", "ALL");
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
		return "MsgQueueCache_Id";
	}

	@Override
	protected ConnectAgent createConnectAgent(Object object) {
		return connectAgentFactoryMap.get("CACHE_QUEUE").createConnectAgent(((QueueMsgQueueCacheVo)object).getMsgQueueCache_Id(), ((QueueMsgQueueCacheVo)object).getIp(), ((QueueMsgQueueCacheVo)object).getPort(), null);
	}
	
	@Override
	protected boolean isSameConnect(ConnectAgent connectAgent, Object object) {
		return connectAgent.isSameConnect(((QueueMsgQueueCacheVo)object).getIp(), ((QueueMsgQueueCacheVo)object).getPort(), null, ConnectAgentType.QUEUE_CACHE.getCode());
	}
	
	@Override
	protected Class<?> getConnectAgentClass() {
		return QueueCacheConnectAgent.class;
	}
	
	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}
	
	@Override
	public String getManagerName() {
		return ConnectManagerType.QUEUE_CACHE.getCode();
	}
	
	@Override
	public ConnectRoute getConnectRoute(String routeName) {
		return connectRouteMap.get("DEFALT");
	}
}
