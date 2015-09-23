package org.fl.noodlenotify.core.connect.db.datasource;

import javax.sql.DataSource;

public interface DbDataSource {
	public DataSource getDataSource();
	public void close();
}
