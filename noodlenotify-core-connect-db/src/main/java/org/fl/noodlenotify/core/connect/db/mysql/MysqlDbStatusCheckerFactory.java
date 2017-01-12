package org.fl.noodlenotify.core.connect.db.mysql;

import org.fl.noodlenotify.core.status.AbstractStatusCheckerFactory;
import org.fl.noodlenotify.core.status.StatusChecker;

public class MysqlDbStatusCheckerFactory extends AbstractStatusCheckerFactory {
	
	private String dbName;
	private String user;
	private String password;
	
	@Override
	public StatusChecker createStatusChecker(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout) {
		return new MysqlDbStatusChecker(
				connectId, ip, port, null,
				connectTimeout, readTimeout, encoding,
				dbName, user, password);
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
}
