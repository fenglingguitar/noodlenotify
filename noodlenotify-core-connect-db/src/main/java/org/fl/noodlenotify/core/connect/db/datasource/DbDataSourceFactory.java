package org.fl.noodlenotify.core.connect.db.datasource;

public interface DbDataSourceFactory {
	public DbDataSource createDataSource(String ip, int port) throws Exception; 
}
