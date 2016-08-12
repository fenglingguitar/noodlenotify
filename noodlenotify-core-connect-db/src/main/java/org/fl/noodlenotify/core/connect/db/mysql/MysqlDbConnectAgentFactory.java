package org.fl.noodlenotify.core.connect.db.mysql;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.db.DbConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSourceFactory;

public class MysqlDbConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private DbDataSourceFactory dbDataSourceFactory;
	
	private DbConnectAgentConfParam dbConnectAgentConfParam = new DbConnectAgentConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		
		MysqlDbConnectAgent mysqlDbConnectAgent = new MysqlDbConnectAgent(ip, port, connectId, dbConnectAgentConfParam, dbDataSourceFactory);	
		return mysqlDbConnectAgent;
	}

	public void setDbDataSourceFactory(DbDataSourceFactory dbDataSourceFactory) {
		this.dbDataSourceFactory = dbDataSourceFactory;
	}

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}
