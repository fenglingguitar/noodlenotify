package org.fl.noodlenotify.core.connect.db.datasource;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class C3p0DbDataSourceFactory implements DbDataSourceFactory {
	
	private String driverClass = "com.mysql.jdbc.Driver";
	private String jdbcUrlMysql = "jdbc:mysql://127.0.0.1:3306/noodlenotify?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
	private String dbName = "noodlenotify";
	private String user = "noodlenotify";
	private String password = "noodlenotify";
	private int minPoolSize = 20;
	private int maxPoolSize = 50;
	private int initialPoolSize = 20;
	private int maxIdleTime = 1800;
	private int acquireIncrement = 10;
	private int acquireRetryAttempts = 30;
	private int acquireRetryDelay = 1000;
	private boolean testConnectionOnCheckin = true;
	private int idleConnectionTestPeriod = 60;
	private int checkoutTimeout = 10000;
	private int maxStatements = 0;
	private int maxStatementsPerConnection = 0;

	@Override
	public DbDataSource createDataSource(String ip, int port) throws Exception {
		
		ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

		comboPooledDataSource.setDriverClass(driverClass);
		
		if (driverClass.equals("com.mysql.jdbc.Driver")) {
			jdbcUrlMysql = "jdbc:mysql://"
					+ ip
					+ ":"
					+ String.valueOf(port)
					+ "/"
					+ dbName
					+ "?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true";
			comboPooledDataSource.setJdbcUrl(jdbcUrlMysql);
		}
		
		comboPooledDataSource.setUser(user);
		comboPooledDataSource.setPassword(password);
		
		comboPooledDataSource.setMinPoolSize(minPoolSize);
		comboPooledDataSource.setMaxPoolSize(maxPoolSize);
		comboPooledDataSource.setInitialPoolSize(initialPoolSize);
		comboPooledDataSource.setMaxIdleTime(maxIdleTime);
		comboPooledDataSource.setAcquireIncrement(acquireIncrement);
		comboPooledDataSource.setAcquireRetryAttempts(acquireRetryAttempts);
		comboPooledDataSource.setAcquireRetryDelay(acquireRetryDelay);
		comboPooledDataSource.setTestConnectionOnCheckin(testConnectionOnCheckin);
		comboPooledDataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
		comboPooledDataSource.setCheckoutTimeout(checkoutTimeout);
		comboPooledDataSource.setMaxStatements(maxStatements);
		comboPooledDataSource.setMaxStatementsPerConnection(maxStatementsPerConnection);
		return new C3p0DbDataSource(comboPooledDataSource);
	}
	
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setMinPoolSize(int minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public void setInitialPoolSize(int initialPoolSize) {
		this.initialPoolSize = initialPoolSize;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public void setAcquireIncrement(int acquireIncrement) {
		this.acquireIncrement = acquireIncrement;
	}

	public void setAcquireRetryAttempts(int acquireRetryAttempts) {
		this.acquireRetryAttempts = acquireRetryAttempts;
	}

	public void setAcquireRetryDelay(int acquireRetryDelay) {
		this.acquireRetryDelay = acquireRetryDelay;
	}

	public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
		this.testConnectionOnCheckin = testConnectionOnCheckin;
	}

	public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
		this.idleConnectionTestPeriod = idleConnectionTestPeriod;
	}

	public void setCheckoutTimeout(int checkoutTimeout) {
		this.checkoutTimeout = checkoutTimeout;
	}

	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}

	public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
		this.maxStatementsPerConnection = maxStatementsPerConnection;
	}
}
