package org.fl.noodlenotify.core.distribute;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;

public class Distribute {
	
	ConcurrentMap<String, QueueDistributerVo> queueDistributerVoMap = new ConcurrentHashMap<String, QueueDistributerVo>();
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String distributeName;
	private long moduleId;
	private String localIp;
	
	private ModuleRegister distributeModuleRegister;
	
	private ConnectManagerPool connectManagerPool;
	
	public void start() throws Exception {
		
		if (distributeName == null || (distributeName != null && distributeName.equals("hostname"))) {
			distributeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp(): localIp;
		
		moduleId = consoleRemotingInvoke.saveDistributerRegister(localIp, distributeName);
		distributeModuleRegister.setModuleId(moduleId);

		connectManagerPool.start();
	}
	
	public void destroy() throws Exception {
		consoleRemotingInvoke.saveDistributerCancel(moduleId);
		connectManagerPool.destroy();
	}
	
	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setDistributeName(String distributeName) {
		this.distributeName = distributeName;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public void setDistributeModuleRegister(ModuleRegister distributeModuleRegister) {
		this.distributeModuleRegister = distributeModuleRegister;
	}
	
	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}
}
