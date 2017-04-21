package org.fl.noodlenotify.core.connect.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodlenotify.common.pojo.db.MessageDb;
import org.fl.noodlenotify.core.connect.db.AbstractDbConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSource;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSourceFactory;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class MysqlDbConnectAgent extends AbstractDbConnectAgent {
	
	private final static Logger logger = LoggerFactory.getLogger(MysqlDbConnectAgent.class);

	private JdbcTemplate jdbcTemplate;

	private TransactionTemplate transactionTemplate;
	private DbDataSourceFactory dbDataSourceFactory;
	private DbDataSource dbDataSource;
	
	private ConcurrentMap<String, String> insertSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> insertContentSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> updateSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> deleteSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> backupSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> selectSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> selectCountSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> selectFinishTimeoutSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> selectByIdSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> maxIdSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> maxIdDelaySqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> minIdSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> minUnFinishIdSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> minIdByStatusSqlMap = new ConcurrentHashMap<String, String>();

	public MysqlDbConnectAgent(
			long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding,
			int invalidLimitNum, ConnectDistinguish connectDistinguish,
			List<MethodInterceptor> methodInterceptorList,
			DbConnectAgentConfParam dbConnectAgentConfParam,
			DbDataSourceFactory dbDataSourceFactory) {
		super(
			connectId, ip, port, url, connectTimeout, readTimeout, encoding, 
			invalidLimitNum, connectDistinguish, 
			methodInterceptorList, dbConnectAgentConfParam);
		this.dbDataSourceFactory = dbDataSourceFactory;
	}

	@Override
	public void connectDbActual() throws Exception {
		
		try {
			jdbcTemplate = new JdbcTemplate();
			dbDataSource = dbDataSourceFactory.createDataSource(ip, port);
			jdbcTemplate.setDataSource(dbDataSource.getDataSource());
			DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
			dataSourceTransactionManager.setDataSource(dbDataSource.getDataSource());
			transactionTemplate = new TransactionTemplate();
			transactionTemplate.setTransactionManager(dataSourceTransactionManager);
			transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
			Connection connection = dbDataSource.getDataSource().getConnection();
			connection.close();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ConnectDbActual -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Connect -> " + e);
			}
			dbDataSource.close();
			throw new ConnectRefusedException("Connection refused for create mysql db connect agent");
		}
	}
	
	@Override
	public void reconnectDbActual() throws Exception {
		try {
			Connection connection = dbDataSource.getDataSource().getConnection();
			connection.close();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("ReconnectActual -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Reconnect -> " + e);
			}
			throw new ConnectRefusedException("Connection refused for create mysql db connect agent");
		}
	}
	
	@Override
	public void closeDbActual() {
		dbDataSource.close();
	}

	@Override
	public void createTable(String queueName) throws Exception {
		
		StringBuilder sqlContentTableStringBuilder = new StringBuilder();
		sqlContentTableStringBuilder.append("CREATE TABLE IF NOT EXISTS `MSG_");
		sqlContentTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqlContentTableStringBuilder.append("_CT` (");
		sqlContentTableStringBuilder.append("	`ID` bigint(16) NOT NULL AUTO_INCREMENT,");
		sqlContentTableStringBuilder.append("	`CONTENT` longblob NOT NULL,");
		sqlContentTableStringBuilder.append("	PRIMARY KEY (`ID`)");
		sqlContentTableStringBuilder.append(") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;");
		
		StringBuilder sqlTableStringBuilder = new StringBuilder();
		sqlTableStringBuilder.append("CREATE TABLE IF NOT EXISTS `MSG_");
		sqlTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqlTableStringBuilder.append("_IF` (");
		sqlTableStringBuilder.append("	`ID` bigint(16) NOT NULL AUTO_INCREMENT,");
		sqlTableStringBuilder.append("	`UUID` char(32) NOT NULL,");
		sqlTableStringBuilder.append("	`QUEUE_NAME` char(32) NOT NULL,");
		sqlTableStringBuilder.append("	`CONTENT_ID` bigint(16) NOT NULL,");
		sqlTableStringBuilder.append("	`EXECUTE_QUEUE` bigint(8) NOT NULL,");
		sqlTableStringBuilder.append("	`RESULT_QUEUE` bigint(8) NOT NULL,");
		sqlTableStringBuilder.append("	`STATUS` tinyint(1) NOT NULL,");
		sqlTableStringBuilder.append("	`REDIS_ONE` bigint(8) NOT NULL,");
		sqlTableStringBuilder.append("	`REDIS_TWO` bigint(8) NOT NULL,");
		sqlTableStringBuilder.append("	`BEGIN_TIME` bigint(16) NOT NULL,");
		sqlTableStringBuilder.append("	`FINISH_TIME` bigint(16) NOT NULL,");
		sqlTableStringBuilder.append("	`TRACE_KAY` char(32) NOT NULL,");
		sqlTableStringBuilder.append("	`PARENT_KAY` char(32) NOT NULL,");
		sqlTableStringBuilder.append("	PRIMARY KEY (`ID`)");
		sqlTableStringBuilder.append(") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;");
		
		StringBuilder sqlBackupTableStringBuilder = new StringBuilder();
		sqlBackupTableStringBuilder.append("CREATE TABLE IF NOT EXISTS `MSG_");
		sqlBackupTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqlBackupTableStringBuilder.append("_BU` (");
		sqlBackupTableStringBuilder.append("	`ID` bigint(16) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`UUID` char(32) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`QUEUE_NAME` char(32) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`CONTENT_ID` bigint(16) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`EXECUTE_QUEUE` bigint(8) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`RESULT_QUEUE` bigint(8) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`STATUS` tinyint(1) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`BEGIN_TIME` bigint(16) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`FINISH_TIME` bigint(16) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`TRACE_KAY` char(32) NOT NULL,");
		sqlBackupTableStringBuilder.append("	`PARENT_KAY` char(32) NOT NULL,");
		sqlBackupTableStringBuilder.append("	PRIMARY KEY (`ID`)");
		sqlBackupTableStringBuilder.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
		
		StringBuilder sqLockTableStringBuilder = new StringBuilder();
		sqLockTableStringBuilder.append("CREATE TABLE IF NOT EXISTS `MSG_");
		sqLockTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqLockTableStringBuilder.append("_LK` (");
		sqLockTableStringBuilder.append("	`ID` bigint(16) NOT NULL,");
		sqLockTableStringBuilder.append("	`OVERTIME` bigint(16) NOT NULL,");
		sqLockTableStringBuilder.append("	`SET_ID` bigint(16) NOT NULL,");
		sqLockTableStringBuilder.append("	`IP` char(16) NULL,");
		sqLockTableStringBuilder.append("	PRIMARY KEY (`ID`)");
		sqLockTableStringBuilder.append(") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;");
				
		try {
			jdbcTemplate.update(sqlContentTableStringBuilder.toString());
			jdbcTemplate.update(sqlTableStringBuilder.toString());
			jdbcTemplate.update(sqlBackupTableStringBuilder.toString());
			jdbcTemplate.update(sqLockTableStringBuilder.toString());
		} catch (RecoverableDataAccessException e) {
			if (logger.isErrorEnabled()) {
				logger.error("CreateTable -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Create Table -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("CreateTable -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Create Table -> " + e);
			}
			throw e;
		}
	}

	@Override
	public void insertActual(final List<MessageDb> messageDbList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					
					KeyHolder keyHolder = new GeneratedKeyHolder();  
					
					for (final MessageDb messageDb : messageDbList) {
						
						String queueName =  messageDb.getQueueName();
						String contentsql = insertContentSqlMap.get(queueName);
						if (contentsql == null) {
							contentsql = "INSERT INTO MSG_" + queueName.toUpperCase().replace(".", "_") 
									+ "_CT (CONTENT) VALUES (?)";
							insertContentSqlMap.putIfAbsent(queueName, contentsql);
						}
						final String contentsqlFinal = contentsql;
						try {
							jdbcTemplate.update(new PreparedStatementCreator() {
								public PreparedStatement createPreparedStatement(Connection connection)
										throws SQLException {
						             			PreparedStatement preparedStatement = 
						                        		connection.prepareStatement(contentsqlFinal, Statement.RETURN_GENERATED_KEYS);
						                        preparedStatement.setBytes(1, messageDb.getContent());
						                        return preparedStatement;
						                  	}
						            	}, keyHolder);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("InsertActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDb.getUuid()
										+ ", Insert Message Content -> " + e);
							}
							messageDb.setResult(false);
							messageDb.setException(e);
							continue;
						}
						
						final long contentKey = keyHolder.getKey().longValue();
						
						String sql = insertSqlMap.get(queueName);
						if (sql == null) {
							sql = "INSERT INTO MSG_" + queueName.toUpperCase().replace(".", "_") 
									+ "_IF (UUID, QUEUE_NAME, CONTENT_ID, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME, TRACE_KAY, PARENT_KAY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							insertSqlMap.putIfAbsent(queueName, sql);
						}
						final String sqlFinal = sql;
						try {
							jdbcTemplate.update(new PreparedStatementCreator() {
								public PreparedStatement createPreparedStatement(Connection connection)
										throws SQLException {
						             			PreparedStatement preparedStatement = 
						                        		connection.prepareStatement(sqlFinal, Statement.RETURN_GENERATED_KEYS);
						             			preparedStatement.setString(1, messageDb.getUuid());
						             			preparedStatement.setString(2, messageDb.getQueueName());
						                        preparedStatement.setLong(3, contentKey);
						                        preparedStatement.setLong(4, messageDb.getExecuteQueue());
						                        preparedStatement.setLong(5, messageDb.getResultQueue());
						                        preparedStatement.setLong(6, messageDb.getStatus());
						                        preparedStatement.setLong(7, messageDb.getRedisOne());
						                        preparedStatement.setLong(8, messageDb.getRedisTwo());
						                        preparedStatement.setLong(9, messageDb.getBeginTime());
						                        preparedStatement.setLong(10, messageDb.getFinishTime());
						                        preparedStatement.setString(11, messageDb.getTraceKey());
						                        preparedStatement.setString(12, messageDb.getParentKey());
						                        return preparedStatement;
						                  	}
						            	}, keyHolder);
							messageDb.setResult(true);
							messageDb.setDb(connectId);
							messageDb.setId(keyHolder.getKey().longValue());
							messageDb.setContentId(contentKey);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("InsertActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDb.getUuid()
										+ ", Insert Message Info-> " + e);
							}
							messageDb.setResult(false);
							messageDb.setException(e);
						}
					}
				}
			});
		} catch (CannotCreateTransactionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("InsertActual -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Transaction -> " + e);
			}
			for (MessageDb messageDb : messageDbList) {
				messageDb.setResult(false);
				messageDb.setException(new ConnectResetException("Connection reset for insert by db connect agent"));
			}
		}
	}

	@Override
	public void updateActual(final List<MessageDb> messageDbList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (final MessageDb messageDb : messageDbList) {						
						String queueName =  messageDb.getQueueName();
						String sql = updateSqlMap.get(queueName);
						if (sql == null) {
							sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF SET EXECUTE_QUEUE = ?, RESULT_QUEUE = ?, STATUS = ?, FINISH_TIME = ? WHERE ID = ?";
							updateSqlMap.putIfAbsent(queueName, sql);
						}
						final String sqlFinal = sql;
						try {
							
							jdbcTemplate.update(sqlFinal, new Object[] {
									messageDb.getExecuteQueue(),
									messageDb.getResultQueue(),
									messageDb.getStatus(),
									messageDb.getFinishTime(),
									messageDb.getId()
							});
							messageDb.setResult(true);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDb.getUuid()
										+ ", Update Message -> " + e);
							}
							messageDb.setResult(false);
							messageDb.setException(e);
						}
					}
				}
			});
		} catch (CannotCreateTransactionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("UpdateActual -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Transaction -> " + e);
			}
			for (MessageDb messageDb : messageDbList) {
				messageDb.setResult(false);
				messageDb.setException(new ConnectResetException("Connection reset for update by db connect agent"));
			}
		}
	}
	
	@Override
	protected void deleteActual(final List<MessageDb> messageDbList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (final MessageDb messageDb : messageDbList) {
						String queueName =  messageDb.getQueueName();
						String sqlBackup = backupSqlMap.get(queueName);
						if (sqlBackup == null) {
							sqlBackup = "INSERT INTO MSG_" + queueName.toUpperCase().replace(".", "_") 
											+ "_BU SELECT ID, UUID, QUEUE_NAME, CONTENT_ID, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, BEGIN_TIME, FINISH_TIME, TRACE_KAY, PARENT_KAY FROM MSG_" 
											+ queueName.toUpperCase().replace(".", "_") 
											+ "_IF WHERE ID = ?";
							backupSqlMap.putIfAbsent(queueName, sqlBackup);
						}
						final String sqlBackupFinal = sqlBackup;
						try {
							jdbcTemplate.update(sqlBackupFinal, new Object[] {
									messageDb.getId()
							});
						} catch (Exception e) {	
							if (logger.isErrorEnabled()) {
								logger.error("DeleteActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDb.getUuid()
										+ ", Backup Message -> " + e);
							}
						}
						String sqlDelete = deleteSqlMap.get(queueName);
						if (sqlDelete == null) {
							sqlDelete = "DELETE FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE ID = ?";
							deleteSqlMap.putIfAbsent(queueName, sqlDelete);
						}
						final String sqlDeleteFinal = sqlDelete;
						try {
							jdbcTemplate.update(sqlDeleteFinal, new Object[] {
									messageDb.getId()
							});
						} catch (Exception e) {	
							if (logger.isErrorEnabled()) {
								logger.error("DeleteActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDb.getUuid()
										+ ", Delete Message -> " + e);
							}
						}
					}
				}
			});
		} catch (CannotCreateTransactionException e) {
			if (logger.isErrorEnabled()) {
				logger.error("DeleteActual -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Transaction -> " + e);
			}
		}
	}
	
	@Override
	public List<MessageDb> select(String queueName, long start, long end, byte status) throws Exception {
		
		String sql = selectSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT ID, UUID, CONTENT_ID, QUEUE_NAME, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME, TRACE_KAY, PARENT_KAY FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
					+ "_IF WHERE ID >= ? AND ID <= ? AND STATUS = ?";
			selectSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		
		List<MessageDb> messageDbList = null;
		try {
			messageDbList = jdbcTemplate.query(sqlFinal, new Object[] {
					start,
					end,
					status
			}, new RowMapper<MessageDb>() {
				@Override
				public MessageDb mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDb messageDb = new MessageDb();
					messageDb.setId(resultSet.getLong("ID"));
					messageDb.setUuid(resultSet.getString("UUID"));
					messageDb.setQueueName(resultSet.getString("QUEUE_NAME"));
					messageDb.setContentId(resultSet.getLong("CONTENT_ID"));
					messageDb.setExecuteQueue(resultSet.getLong("EXECUTE_QUEUE"));
					messageDb.setResultQueue(resultSet.getLong("RESULT_QUEUE"));
					messageDb.setStatus(resultSet.getByte("STATUS"));
					messageDb.setDb(connectId);
					messageDb.setRedisOne(resultSet.getLong("REDIS_ONE"));
					messageDb.setRedisTwo(resultSet.getLong("REDIS_TWO"));
					messageDb.setBeginTime(resultSet.getLong("BEGIN_TIME"));
					messageDb.setFinishTime(resultSet.getLong("FINISH_TIME"));
					messageDb.setTraceKey(resultSet.getString("TRACE_KAY"));
					messageDb.setParentKey(resultSet.getString("PARENT_KAY"));
					return messageDb;
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("Select -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("Select -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw e;
		}
		return messageDbList;
	}
	
	@Override
	public long selectCount(String queueName, long start, long end, byte status) throws Exception {
		
		String sql = selectCountSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT COUNT(*) FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
					+ "_IF WHERE ID >= ? AND ID <= ? AND STATUS = ?";
			selectCountSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		
		Long count;
		try {
			count = jdbcTemplate.queryForObject(sqlFinal, Long.class, new Object[] {
					start,
					end,
					status
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("Select -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("Select -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw e;
		}
		return count != null ? count : 0;
	}
	
	@Override
	public List<MessageDb> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception {
		
		String sql = selectFinishTimeoutSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT ID, UUID, CONTENT_ID, QUEUE_NAME, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME, TRACE_KAY, PARENT_KAY FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
					+ "_IF WHERE ID >= ? AND ID <= ? AND STATUS = ? AND FINISH_TIME < ?";
			selectFinishTimeoutSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		
		List<MessageDb> messageDbList = null;
		try {
			messageDbList = jdbcTemplate.query(sqlFinal, new Object[] {
					start,
					end,
					status,
					timeout
			}, new RowMapper<MessageDb>() {
				@Override
				public MessageDb mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDb messageDb = new MessageDb();
					messageDb.setId(resultSet.getLong("ID"));
					messageDb.setUuid(resultSet.getString("UUID"));
					messageDb.setQueueName(resultSet.getString("QUEUE_NAME"));
					messageDb.setContentId(resultSet.getLong("CONTENT_ID"));
					messageDb.setExecuteQueue(resultSet.getLong("EXECUTE_QUEUE"));
					messageDb.setResultQueue(resultSet.getLong("RESULT_QUEUE"));
					messageDb.setStatus(resultSet.getByte("STATUS"));
					messageDb.setDb(connectId);
					messageDb.setRedisOne(resultSet.getLong("REDIS_ONE"));
					messageDb.setRedisTwo(resultSet.getLong("REDIS_TWO"));
					messageDb.setBeginTime(resultSet.getLong("BEGIN_TIME"));
					messageDb.setFinishTime(resultSet.getLong("FINISH_TIME"));
					messageDb.setTraceKey(resultSet.getString("TRACE_KAY"));
					messageDb.setParentKey(resultSet.getString("PARENT_KAY"));
					return messageDb;
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("SelectFinishTimeout -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("SelectFinishTimeout -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select -> " + e);
			}
			throw e;
		}
		return messageDbList;
	}

	@Override
	public MessageDb selectById(String queueName, long id) throws Exception {
		
		String sql = selectByIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT CONTENT FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT WHERE ID = ?";
			selectByIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		List<MessageDb> messageDbList = null;
		try {
			messageDbList = jdbcTemplate.query(sqlFinal, new Object[] {
					id
			}, new RowMapper<MessageDb>() {
				@Override
				public MessageDb mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDb messageDb = new MessageDb();
					messageDb.setContent(resultSet.getBytes("CONTENT"));
					return messageDb;
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("SelectById -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select By Id -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("SelectById -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Select By Id -> " + e);
			}
			throw e;
		}
		if (messageDbList != null && messageDbList.size() > 0) {
			return messageDbList.get(0);
		}
		return null;
	}
	
	@Override
	public long maxId(String queueName) throws Exception {
		
		String sql = maxIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MAX(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF";
			maxIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		Long maxId;
		try {
			maxId = jdbcTemplate.queryForObject(sqlFinal, Long.class);
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MaxId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Max Id -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MaxId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Max Id -> " + e);
			}
			throw e;
		}
		return maxId != null ? maxId : 0;
	}
	
	@Override
	public long maxIdDelay(String queueName, long delay) throws Exception {
		
		String sql = maxIdDelaySqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MAX(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE BEGIN_TIME < ?";
			maxIdDelaySqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		Long maxId;
		try {
			maxId = jdbcTemplate.queryForObject(sqlFinal, Long.class, delay);
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MaxIdDelay -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Max Id -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("maxIdDelay -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Max Id -> " + e);
			}
			throw e;
		}
		return maxId != null ? maxId : 0;
	}

	@Override
	public long minId(String queueName) throws Exception {
		
		String sql = minIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF";
			minIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		Long minId;
		try {
			minId = jdbcTemplate.queryForObject(sqlFinal, Long.class);
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Min Id -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Min Id -> " + e);
			}
			throw e;
		} 
		return minId != null ? minId : 0;
	}
	
	@Override
	public long minUnFinishId(String queueName) throws Exception {
		
		String sql = minUnFinishIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS <> ?";
			minUnFinishIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		Long minUnFinishId;
		try {
			minUnFinishId = jdbcTemplate.queryForObject(sqlFinal, Long.class, new Object[] {
					MessageConstant.MESSAGE_STATUS_FINISH
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinUnFinishId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Min Finish Id -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinUnFinishId -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Min Finish Id -> " + e);
			}
			throw e;
		}
		return minUnFinishId != null ? minUnFinishId : 0;
	}
	
	@Override
	public long minIdByStatus(String queueName, byte status) throws Exception {
		
		String sql = minIdByStatusSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?";
			minIdByStatusSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		Long minId;
		try {
			minId = jdbcTemplate.queryForObject(sqlFinal, Long.class, new Object[] {
					status
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinIdByStatus -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + status
						+ ", Get Min Id By Status -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("MinIdByStatus -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + status
						+ ", Get Min Id By Status -> " + e);
			}
			throw e;
		}
		return minId != null ? minId : 0;
	}
	
	public void setDbDataSourceFactory(DbDataSourceFactory dbDataSourceFactory) {
		this.dbDataSourceFactory = dbDataSourceFactory;
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return DbConnectAgent.class;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
