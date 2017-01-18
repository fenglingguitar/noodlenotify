package org.fl.noodlenotify.core.exchange;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.exception.ConnectInvokeException;
import org.fl.noodle.common.connect.manager.ConnectManagerPool;
import org.fl.noodle.common.connect.register.ModuleRegister;
import org.fl.noodle.common.connect.server.ConnectServer;
import org.fl.noodle.common.util.net.NetAddressUtil;
import org.fl.noodlenotify.console.remoting.ConsoleRemotingInvoke;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.constent.ConnectManagerType;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.net.NetConnectReceiver;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.core.exchange.manager.ExchangeConnectManager;

public class Exchange implements NetConnectReceiver {

	//private final static Logger logger = LoggerFactory.getLogger(Exchange.class);
	
	private ConsoleRemotingInvoke consoleRemotingInvoke;
	
	private String exchangeName;
	private long moduleId;
	private String localIp;
	private int localPort;
	private String url;
	private String type;
	
	private long sizeLimit = 8192;
	
	private ModuleRegister exchangeModuleRegister;
	
	private ConnectManagerPool connectManagerPool;
	
	private ConnectServer connectServer;
	
	public void start() throws Exception {
		
		if (exchangeName == null || 
				(exchangeName != null && exchangeName.equals("hostname"))) {
			exchangeName = NetAddressUtil.getLocalHostName();
		}
		localIp = localIp == null ? NetAddressUtil.getLocalIp() : localIp;
		
		connectServer.start();
		
		moduleId = consoleRemotingInvoke.saveExchangerRegister(localIp, localPort, url, type, exchangeName);
		exchangeModuleRegister.setModuleId(moduleId);

		connectManagerPool.start();
	}
	
	public void destroy() throws Exception {
		consoleRemotingInvoke.saveExchangerCancel(moduleId);
		connectServer.destroy();
		connectManagerPool.destroy();
	}
	
	@Override
	public void receive(Message message) throws Exception {
		
		MessageDm messageDm = new MessageDm(
				message.getQueueName(), 
				message.getUuid(), 
				message.getContent().getBytes("UTF-8")
				);
		
		if (messageDm.getContent().length > sizeLimit) {
			throw new ConnectInvokeException("Message body bigger then max limit: " + sizeLimit);
		}
		
		Long queueConsumerGroupNum = ((ExchangeConnectManager) connectManagerPool.getConnectManager(ConnectManagerType.EXCHANGE.getCode())).getQueueConsumerGroupNumMap().get(messageDm.getQueueName());
		if (queueConsumerGroupNum != null) {
			messageDm.setExecuteQueue(queueConsumerGroupNum);
			messageDm.setStatus(MessageConstant.MESSAGE_STATUS_NEW);
		} else {
			connectManagerPool.getConnectManager(ConnectManagerType.EXCHANGE.getCode()).runUpdate();
			throw new ConnectInvokeException("Set execute queue error, can not get queue consumer group num");
		}
		
		ConnectCluster connectCluster = connectManagerPool.getConnectManager(ConnectManagerType.DB.getCode()).getConnectCluster("DEFALT");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) connectCluster.getProxy();
		
		messageDm.setBeginTime(System.currentTimeMillis());
		ConnectThreadLocalStorage.put(LocalStorageType.MESSAGE_DM.getCode(), messageDm);
		try {
			dbConnectAgent.insert(messageDm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.MESSAGE_DM.getCode());
		}
		
	}

	public void setConsoleRemotingInvoke(ConsoleRemotingInvoke consoleRemotingInvoke) {
		this.consoleRemotingInvoke = consoleRemotingInvoke;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public void setLocalIp(String localIp) {
		this.localIp = localIp;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public void setSizeLimit(long sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public void setExchangeModuleRegister(ModuleRegister exchangeModuleRegister) {
		this.exchangeModuleRegister = exchangeModuleRegister;
	}

	public void setConnectManagerPool(ConnectManagerPool connectManagerPool) {
		this.connectManagerPool = connectManagerPool;
	}

	public void setConnectServer(ConnectServer connectServer) {
		this.connectServer = connectServer;
	}
}
