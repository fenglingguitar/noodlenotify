package org.fl.noodlenotify.core.connect.db.datasource;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0DbDataSource implements DbDataSource {

	ComboPooledDataSource comboPooledDataSource;
	
	public C3p0DbDataSource(ComboPooledDataSource comboPooledDataSource) {
		this.comboPooledDataSource = comboPooledDataSource;
	}
	
	@Override
	public DataSource getDataSource() {
		return comboPooledDataSource;
	}

	@Override
	public void close() {
		comboPooledDataSource.close();
	}
}
