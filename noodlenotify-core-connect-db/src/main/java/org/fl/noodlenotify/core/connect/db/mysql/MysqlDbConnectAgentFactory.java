package org.fl.noodlenotify.core.connect.db.mysql;

import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectAgentFactoryAbstract;
import org.fl.noodlenotify.core.connect.db.DbConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSourceFactory;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.OvertimePerformanceExecuterService;
import org.fl.noodlenotify.monitor.performance.executer.service.impl.SuccessPerformanceExecuterService;

public class MysqlDbConnectAgentFactory extends ConnectAgentFactoryAbstract {
	
	private DbDataSourceFactory dbDataSourceFactory;
	
	private DbConnectAgentConfParam dbConnectAgentConfParam = new DbConnectAgentConfParam();
	
	@Autowired(required=false)
	private OvertimePerformanceExecuterService overtimePerformanceExecuterService;
	
	@Autowired(required=false)
	private SuccessPerformanceExecuterService successPerformanceExecuterService;
	
	@Override
	public ConnectAgent createConnectAgent(String ip, int port, long connectId) {
		
		MysqlDbConnectAgent mysqlDbConnectAgent = new MysqlDbConnectAgent(ip, port, connectId, dbConnectAgentConfParam, dbDataSourceFactory);
		mysqlDbConnectAgent.setOvertimePerformanceExecuterService(overtimePerformanceExecuterService);
		mysqlDbConnectAgent.setSuccessPerformanceExecuterService(successPerformanceExecuterService);		
		return mysqlDbConnectAgent;
	}

	public void setDbDataSourceFactory(DbDataSourceFactory dbDataSourceFactory) {
		this.dbDataSourceFactory = dbDataSourceFactory;
	}

	public void setDbConnectAgentConfParam(DbConnectAgentConfParam dbConnectAgentConfParam) {
		this.dbConnectAgentConfParam = dbConnectAgentConfParam;
	}
}
