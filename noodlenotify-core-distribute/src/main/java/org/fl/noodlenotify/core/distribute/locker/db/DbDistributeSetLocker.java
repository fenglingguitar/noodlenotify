package org.fl.noodlenotify.core.distribute.locker.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.distribute.locker.DistributeSetLockerAbstract;

public class DbDistributeSetLocker extends DistributeSetLockerAbstract {
	
	private final static Logger logger = LoggerFactory.getLogger(DbDistributeSetLocker.class);

	private ConnectManager dbConnectManager;
	private long dbId;
	
	public DbDistributeSetLocker(String queueName, long distributeId, ConnectManager dbConnectManager, long dbId) {
		super(queueName, distributeId);
		this.dbConnectManager = dbConnectManager;
		this.dbId = dbId;
	}
	
	public DbDistributeSetLocker(String queueName, long distributeId, long sleepTime, long delayTime, ConnectManager dbConnectManager, long dbId) {
		super(queueName, distributeId, sleepTime, delayTime);
		this.dbConnectManager = dbConnectManager;
		this.dbId = dbId;
	}

	@Override
	protected long getDiffTime() {
		
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(dbId);
		if (dbConnectAgent == null) {
			return 0;
		}
		
		try {
			return dbConnectAgent.getDiffTime();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetDiffTime -> " 
							+ "Queue: " + queueName
							+ ", DistributeId: " + distributeId
							+ ", DB: " + ((ConnectAgent) dbConnectAgent).getConnectId()
							+ "DB Get Diff Time -> " + e);
			}
			return 0;
		}
	}

	@Override
	protected boolean getAlive() {
		
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(dbId);
		if (dbConnectAgent == null) {
			return false;
		}
		
		long intervalTime = sleepTime + delayTime;
		try {
			boolean isAlive = dbConnectAgent.getAlive(queueName, distributeId, diffTime, intervalTime);
			if (isAlive == true) {
				if (logger.isDebugEnabled()) {
					logger.debug("GetAlive -> " 
								+ "Queue: " + queueName
								+ ", DistributeId: " + distributeId
								+ ", DB: " + ((ConnectAgent) dbConnectAgent).getConnectId()
								+ ", Become DB Set Queue Master");
				}
			}
			return isAlive;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> " 
							+ "Queue: " + queueName
							+ ", DistributeId: " + distributeId
							+ ", DB: " + ((ConnectAgent) dbConnectAgent).getConnectId()
							+ ", IntervalTime: " + intervalTime
							+ ", DB Get Alive -> " + e);
			}
			return false;
		}
	}

	@Override
	protected boolean keepAlive() {
		
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(dbId);
		if (dbConnectAgent == null) {
			return false;
		}
		
		long intervalTime = sleepTime + delayTime;
		try {
			return dbConnectAgent.keepAlive(queueName, distributeId, diffTime, intervalTime);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> " 
							+ "Queue: " + queueName
							+ ", DistributeId: " + distributeId
							+ ", DB: " + ((ConnectAgent) dbConnectAgent).getConnectId()
							+ ", IntervalTime: " + intervalTime
							+ "DB Keep Alive -> " + e);
			}
			return false;
		}
	}

	@Override
	protected void releaseAlive() {
		
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectManager.getConnectAgent(dbId);
		if (dbConnectAgent == null) {
			return;
		}
		
		try {
			dbConnectAgent.releaseAlive(queueName, distributeId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReleaseAlive -> " 
							+ "Queue: " + queueName
							+ ", DistributeId: " + distributeId
							+ ", DB: " + ((ConnectAgent) dbConnectAgent).getConnectId()
							+ "DB Release Alive -> " + e);
			}
		}
	}
}
