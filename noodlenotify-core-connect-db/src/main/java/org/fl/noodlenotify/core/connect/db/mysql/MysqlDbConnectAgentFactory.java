package org.fl.noodlenotify.core.connect.db.mysql;

import org.fl.noodle.common.connect.agent.AbstractConnectAgentFactory;
import org.fl.noodle.common.connect.agent.ConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSourceFactory;

public class MysqlDbConnectAgentFactory extends AbstractConnectAgentFactory {
	
	private DbDataSourceFactory dbDataSourceFactory;
	
	private DbConnectAgentConfParam dbConnectAgentConfParam = new DbConnectAgentConfParam();
	
	@Override
	public ConnectAgent createConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		
		return new MysqlDbConnectAgent(
				connectId, ip, port, null,
				connectTimeout, readTimeout, encoding, invalidLimitNum,
				connectDistinguish, methodInterceptorList, 
				dbConnectAgentConfParam, dbDataSourceFactory);
	}

	public void setDbDataSourceFactory(DbDataSourceFactory dbDataSourceFactory) {
		this.dbDataSourceFactory = dbDataSourceFactory;
	}

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}
