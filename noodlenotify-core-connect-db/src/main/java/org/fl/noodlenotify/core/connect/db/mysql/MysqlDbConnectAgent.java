package org.fl.noodlenotify.core.connect.db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.aopalliance.intercept.MethodInterceptor;
import org.fl.noodle.common.connect.distinguish.ConnectDistinguish;
import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodle.common.connect.exception.ConnectResetException;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.AbstractDbConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgentConfParam;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSource;
import org.fl.noodlenotify.core.connect.db.datasource.DbDataSourceFactory;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.fl.noodlenotify.core.domain.message.MessageVo;
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
	private ConcurrentMap<String, String> getAliveSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> keepAliveSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> releaseAliveSqlMap = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> checkLenSqlMap = new ConcurrentHashMap<String, String>();

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
		
		/*StringBuilder sqLocklInsertTableStringBuilder = new StringBuilder();
		sqLocklInsertTableStringBuilder.append("INSERT INTO MSG_");
		sqLocklInsertTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqLocklInsertTableStringBuilder.append("_LK ");
		sqLocklInsertTableStringBuilder.append("SELECT 1 AS ID, 0 AS OVERTIME, 0 AS SET_ID FROM DUAL ");
		sqLocklInsertTableStringBuilder.append("WHERE (SELECT COUNT(*) FROM MSG_");
		sqLocklInsertTableStringBuilder.append(queueName.toUpperCase().replace(".", "_"));
		sqLocklInsertTableStringBuilder.append("_LK) = 0");*/
				
		try {
			jdbcTemplate.update(sqlContentTableStringBuilder.toString());
			jdbcTemplate.update(sqlTableStringBuilder.toString());
			jdbcTemplate.update(sqlBackupTableStringBuilder.toString());
			jdbcTemplate.update(sqLockTableStringBuilder.toString());
			//jdbcTemplate.update(sqLocklInsertTableStringBuilder.toString());
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
	public void insertActual(final List<MessageDm> messageDmList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					
					KeyHolder keyHolder = new GeneratedKeyHolder();  
					
					for (final MessageDm messageDm : messageDmList) {
						
						String queueName =  messageDm.getQueueName();
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
						                        preparedStatement.setBytes(1, messageDm.getContent());
						                        return preparedStatement;
						                  	}
						            	}, keyHolder);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("InsertActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDm.getUuid()
										+ ", Insert Message Content -> " + e);
							}
							messageDm.setResult(false);
							messageDm.setException(e);
							continue;
						}
						
						final long contentKey = keyHolder.getKey().longValue();
						
						String sql = insertSqlMap.get(queueName);
						if (sql == null) {
							sql = "INSERT INTO MSG_" + queueName.toUpperCase().replace(".", "_") 
									+ "_IF (UUID, QUEUE_NAME, CONTENT_ID, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
							insertSqlMap.putIfAbsent(queueName, sql);
						}
						final String sqlFinal = sql;
						try {
							jdbcTemplate.update(new PreparedStatementCreator() {
								public PreparedStatement createPreparedStatement(Connection connection)
										throws SQLException {
						             			PreparedStatement preparedStatement = 
						                        		connection.prepareStatement(sqlFinal, Statement.RETURN_GENERATED_KEYS);
						             			preparedStatement.setString(1, messageDm.getUuid());
						             			preparedStatement.setString(2, messageDm.getQueueName());
						                        preparedStatement.setLong(3, contentKey);
						                        preparedStatement.setLong(4, messageDm.getExecuteQueue());
						                        preparedStatement.setLong(5, messageDm.getResultQueue());
						                        preparedStatement.setLong(6, messageDm.getStatus());
						                        preparedStatement.setLong(7, messageDm.getRedisOne());
						                        preparedStatement.setLong(8, messageDm.getRedisTwo());
						                        preparedStatement.setLong(9, messageDm.getBeginTime());
						                        preparedStatement.setLong(10, messageDm.getFinishTime());
						                        return preparedStatement;
						                  	}
						            	}, keyHolder);
							messageDm.setResult(true);
							messageDm.setDb(connectId);
							messageDm.setId(keyHolder.getKey().longValue());
							messageDm.setContentId(contentKey);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("InsertActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDm.getUuid()
										+ ", Insert Message Info-> " + e);
							}
							messageDm.setResult(false);
							messageDm.setException(e);
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
			for (MessageDm messageDm : messageDmList) {
				messageDm.setResult(false);
				messageDm.setException(new ConnectResetException("Connection reset for insert by db connect agent"));
			}
		}
	}

	@Override
	public void updateActual(final List<MessageDm> messageDmList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (final MessageDm messageDm : messageDmList) {						
						String queueName =  messageDm.getQueueName();
						String sql = updateSqlMap.get(queueName);
						if (sql == null) {
							sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF SET EXECUTE_QUEUE = ?, RESULT_QUEUE = ?, STATUS = ?, FINISH_TIME = ? WHERE ID = ?";
							updateSqlMap.putIfAbsent(queueName, sql);
						}
						final String sqlFinal = sql;
						try {
							
							jdbcTemplate.update(sqlFinal, new Object[] {
									messageDm.getExecuteQueue(),
									messageDm.getResultQueue(),
									messageDm.getStatus(),
									messageDm.getFinishTime(),
									messageDm.getId()
							});
							messageDm.setResult(true);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("UpdateActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDm.getUuid()
										+ ", Update Message -> " + e);
							}
							messageDm.setResult(false);
							messageDm.setException(e);
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
			for (MessageDm messageDm : messageDmList) {
				messageDm.setResult(false);
				messageDm.setException(new ConnectResetException("Connection reset for update by db connect agent"));
			}
		}
	}
	
	@Override
	protected void deleteActual(final List<MessageDm> messageDmList) {
		
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					for (final MessageDm messageDm : messageDmList) {
						String queueName =  messageDm.getQueueName();
						String sqlBackup = backupSqlMap.get(queueName);
						if (sqlBackup == null) {
							sqlBackup = "INSERT INTO MSG_" + queueName.toUpperCase().replace(".", "_") 
											+ "_BU SELECT ID, UUID, QUEUE_NAME, CONTENT_ID, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, BEGIN_TIME, FINISH_TIME FROM MSG_" 
											+ queueName.toUpperCase().replace(".", "_") 
											+ "_IF WHERE ID = ?";
							backupSqlMap.putIfAbsent(queueName, sqlBackup);
						}
						final String sqlBackupFinal = sqlBackup;
						try {
							jdbcTemplate.update(sqlBackupFinal, new Object[] {
									messageDm.getId()
							});
						} catch (Exception e) {	
							if (logger.isErrorEnabled()) {
								logger.error("DeleteActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDm.getUuid()
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
									messageDm.getId()
							});
						} catch (Exception e) {	
							if (logger.isErrorEnabled()) {
								logger.error("DeleteActual -> " 
										+ "DB: " + connectId
										+ ", Ip: " + ip
										+ ", Port: " + port
										+ ", UUID: " + messageDm.getUuid()
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
	public List<MessageDm> select(String queueName, long start, long end, byte status) throws Exception {
		
		String sql = selectSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT ID, UUID, CONTENT_ID, QUEUE_NAME, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
					+ "_IF WHERE ID >= ? AND ID <= ? AND STATUS = ?";
			selectSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		
		List<MessageDm> messageDmList = null;
		try {
			messageDmList = jdbcTemplate.query(sqlFinal, new Object[] {
					start,
					end,
					status
			}, new RowMapper<MessageDm>() {
				@Override
				public MessageDm mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDm messageDm = new MessageDm();
					messageDm.setId(resultSet.getLong("ID"));
					messageDm.setUuid(resultSet.getString("UUID"));
					messageDm.setQueueName(resultSet.getString("QUEUE_NAME"));
					messageDm.setContentId(resultSet.getLong("CONTENT_ID"));
					messageDm.setExecuteQueue(resultSet.getLong("EXECUTE_QUEUE"));
					messageDm.setResultQueue(resultSet.getLong("RESULT_QUEUE"));
					messageDm.setStatus(resultSet.getByte("STATUS"));
					messageDm.setDb(connectId);
					messageDm.setRedisOne(resultSet.getLong("REDIS_ONE"));
					messageDm.setRedisTwo(resultSet.getLong("REDIS_TWO"));
					messageDm.setBeginTime(resultSet.getLong("BEGIN_TIME"));
					messageDm.setFinishTime(resultSet.getLong("FINISH_TIME"));
					return messageDm;
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
		return messageDmList;
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
		
		long count = 0;
		try {
			count = jdbcTemplate.queryForLong(sqlFinal, new Object[] {
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
		return count;
	}
	
	@Override
	public List<MessageDm> selectTimeout(String queueName, long start, long end, byte status, long timeout) throws Exception {
		
		String sql = selectFinishTimeoutSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT ID, UUID, CONTENT_ID, QUEUE_NAME, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, REDIS_ONE, REDIS_TWO, BEGIN_TIME, FINISH_TIME FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
					+ "_IF WHERE ID >= ? AND ID <= ? AND STATUS = ? AND FINISH_TIME < ?";
			selectFinishTimeoutSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		
		List<MessageDm> messageDmList = null;
		try {
			messageDmList = jdbcTemplate.query(sqlFinal, new Object[] {
					start,
					end,
					status,
					timeout
			}, new RowMapper<MessageDm>() {
				@Override
				public MessageDm mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDm messageDm = new MessageDm();
					messageDm.setId(resultSet.getLong("ID"));
					messageDm.setUuid(resultSet.getString("UUID"));
					messageDm.setQueueName(resultSet.getString("QUEUE_NAME"));
					messageDm.setContentId(resultSet.getLong("CONTENT_ID"));
					messageDm.setExecuteQueue(resultSet.getLong("EXECUTE_QUEUE"));
					messageDm.setResultQueue(resultSet.getLong("RESULT_QUEUE"));
					messageDm.setStatus(resultSet.getByte("STATUS"));
					messageDm.setDb(connectId);
					messageDm.setRedisOne(resultSet.getLong("REDIS_ONE"));
					messageDm.setRedisTwo(resultSet.getLong("REDIS_TWO"));
					messageDm.setBeginTime(resultSet.getLong("BEGIN_TIME"));
					messageDm.setFinishTime(resultSet.getLong("FINISH_TIME"));
					return messageDm;
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
		return messageDmList;
	}

	@Override
	public MessageDm selectById(String queueName, long id) throws Exception {
		
		String sql = selectByIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT CONTENT FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT WHERE ID = ?";
			selectByIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		List<MessageDm> messageDmList = null;
		try {
			messageDmList = jdbcTemplate.query(sqlFinal, new Object[] {
					id
			}, new RowMapper<MessageDm>() {
				@Override
				public MessageDm mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageDm messageDm = new MessageDm();
					messageDm.setContent(resultSet.getBytes("CONTENT"));
					return messageDm;
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
		if (messageDmList != null && messageDmList.size() > 0) {
			return messageDmList.get(0);
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
		long maxId = 0;
		try {
			maxId = jdbcTemplate.queryForLong(sqlFinal);
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
		return maxId;
	}
	
	@Override
	public long maxIdDelay(String queueName, long delay) throws Exception {
		
		String sql = maxIdDelaySqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MAX(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE BEGIN_TIME < ?";
			maxIdDelaySqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long maxId = 0;
		try {
			maxId = jdbcTemplate.queryForLong(sqlFinal, delay);
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
		return maxId;
	}

	@Override
	public long minId(String queueName) throws Exception {
		
		String sql = minIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF";
			minIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long minId = 0;
		try {
			minId = jdbcTemplate.queryForLong(sqlFinal);
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
		return minId;
	}
	
	@Override
	public long minUnFinishId(String queueName) throws Exception {
		
		String sql = minUnFinishIdSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS <> ?";
			minUnFinishIdSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long minUnFinishId = 0;
		try {
			minUnFinishId = jdbcTemplate.queryForLong(sqlFinal, new Object[] {
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
		return minUnFinishId;
	}
	
	@Override
	public long minIdByStatus(String queueName, byte status) throws Exception {
		
		String sql = minIdByStatusSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT MIN(ID) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?";
			minIdByStatusSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long minId = 0;
		try {
			minId = jdbcTemplate.queryForLong(sqlFinal, new Object[] {
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
		return minId;
	}

	@Override
	public long getDiffTime() throws Exception {
		
		final String sqlFinal = "SELECT CURRENT_TIMESTAMP FROM DUAL";
		List<Long> diffTimeList = null;
		long diffTime = 0;
		try {
			diffTimeList = jdbcTemplate.query(sqlFinal, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet resultSet, int index)
						throws SQLException {
					return resultSet.getTimestamp(1).getTime();
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("GetDiffTime -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Diff Time -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("GetDiffTime -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Diff Time -> " + e);
			}
			throw e;
		}
		if (diffTimeList != null && diffTimeList.size() > 0) {
			diffTime = diffTimeList.get(0);
		}
		diffTime = System.currentTimeMillis() - diffTime;
		return diffTime;
	}

	@Override
	public boolean getAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception {
		
		String sql = getAliveSqlMap.get(queueName);
		if (sql == null) {
			sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_LK SET SET_ID = ?, OVERTIME = ? WHERE OVERTIME < ? AND ID = 1";
			getAliveSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long now = System.currentTimeMillis() + diffTime;
		try {
			if (jdbcTemplate.update(sqlFinal, new Object[] {
					id,
					now + intervalTime,
					now
			}) == 1) {
				return true;
			}
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Alive -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("GetAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Alive -> " + e);
			}
			throw e;
		}
		return false;
	}

	@Override
	public boolean keepAlive(String queueName, long id, long diffTime, long intervalTime) throws Exception {
		
		String sql = keepAliveSqlMap.get(queueName);
		if (sql == null) {
			sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_LK SET SET_ID = ?, OVERTIME = ? WHERE SET_ID = ? AND ID = 1";
			keepAliveSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long now = System.currentTimeMillis() + diffTime;
		try {
			if (jdbcTemplate.update(sqlFinal, new Object[] {
					id,
					now + intervalTime,
					id
			}) == 1) {
				return true;
			}
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Keep Alive -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("KeepAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Keep Alive -> " + e);
			}
			throw e;
		}
		return false;
	}

	@Override
	public void releaseAlive(String queueName, long id) throws Exception {
		
		String sql = releaseAliveSqlMap.get(queueName);
		if (sql == null) {
			sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_LK SET SET_ID = ?, OVERTIME = ? WHERE SET_ID = ? AND ID = 1";
			releaseAliveSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		try {
			jdbcTemplate.update(sqlFinal, new Object[] {
					0,
					0,
					id
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("ReleaseAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Release Alive -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("ReleaseAlive -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Release Alive -> " + e);
			}
			throw e;
		}
	}
	
	@Override
	public void checkHealth() throws Exception {
		
		final String sqlFinal = "SELECT CURRENT_TIMESTAMP FROM DUAL";
		try {
			jdbcTemplate.query(sqlFinal, new RowMapper<Long>() {
				@Override
				public Long mapRow(ResultSet resultSet, int index)
						throws SQLException {
					return resultSet.getTimestamp(1).getTime();
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Diff Time -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("CheckHealth -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Get Diff Time -> " + e);
			}
			throw e;
		}
	}
	
	@Override
	public long checkNewLen(String queueName) throws Exception {
		
		String sql = checkLenSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT COUNT(*) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?";
			checkLenSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long len = 0;
		try {
			len = jdbcTemplate.queryForLong(sqlFinal, new Object[] {
					MessageConstant.MESSAGE_STATUS_NEW
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("CheckNewLen -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + MessageConstant.MESSAGE_STATUS_NEW
						+ ", Get Count -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("CheckNewLen -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + MessageConstant.MESSAGE_STATUS_NEW
						+ ", Get Count -> " + e);
			}
			throw e;
		}
		
		return len;
	}

	@Override
	public long checkPortionLen(String queueName) throws Exception {
		
		String sql = checkLenSqlMap.get(queueName);
		if (sql == null) {
			sql = "SELECT COUNT(*) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?";
			checkLenSqlMap.putIfAbsent(queueName, sql);
		}
		final String sqlFinal = sql;
		long len = 0;
		try {
			len = jdbcTemplate.queryForLong(sqlFinal, new Object[] {
					MessageConstant.MESSAGE_STATUS_PORTION
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("checkPortionLen -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + MessageConstant.MESSAGE_STATUS_PORTION
						+ ", Get Count -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("checkPortionLen -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", Status: " + MessageConstant.MESSAGE_STATUS_PORTION
						+ ", Get Count -> " + e);
			}
			throw e;
		}
		
		return len;
	}
	

	@Override
	public List<MessageVo> queryPortionMessage(String queueName, String uuid, Long region, String content, Integer page, Integer rows) throws Exception {
		
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(MessageConstant.MESSAGE_STATUS_PORTION);		
		
		String sql = "SELECT i.ID, UUID, CONTENT_ID, QUEUE_NAME, EXECUTE_QUEUE, RESULT_QUEUE, STATUS, BEGIN_TIME, FINISH_TIME, CONVERT(CONTENT USING utf8) CONTENT FROM MSG_" + queueName.toUpperCase().replace(".", "_") 
				+ "_IF i LEFT JOIN MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT c ON i.CONTENT_ID = c.ID WHERE STATUS = ? ";
				
		if (uuid != null && !uuid.isEmpty()) {
			sql += "AND UUID = ? ";
			objectList.add(uuid);
		}
		
		if (region != null && region > 0) {
			sql += "AND BEGIN_TIME > ? ";
			objectList.add(System.currentTimeMillis() - region);
		}
		
		if (content != null && !content.isEmpty()) {
			sql += "AND CONVERT(CONTENT USING utf8) LIKE ? ";
			objectList.add("%" + content + "%");
		}
				
		final String sqlFinal = sql + "ORDER BY i.ID DESC LIMIT ?,?";
		
		objectList.add(page);
		objectList.add(rows);
		
		List<MessageVo> messageDmList = null;
		try {
			messageDmList = jdbcTemplate.query(sqlFinal, objectList.toArray(), new RowMapper<MessageVo>() {
				@Override
				public MessageVo mapRow(ResultSet resultSet, int index)
						throws SQLException {
					MessageVo messageVo = new MessageVo();
					messageVo.setId(resultSet.getLong("ID"));
					messageVo.setUuid(resultSet.getString("UUID"));
					messageVo.setQueueName(resultSet.getString("QUEUE_NAME"));
					messageVo.setContentId(resultSet.getLong("CONTENT_ID"));
					messageVo.setExecuteQueue(resultSet.getLong("EXECUTE_QUEUE"));
					messageVo.setResultQueue(resultSet.getLong("RESULT_QUEUE"));
					messageVo.setStatus(resultSet.getByte("STATUS"));
					messageVo.setDb(connectId);
					messageVo.setBeginTime(new Date(resultSet.getLong("BEGIN_TIME")));
					messageVo.setFinishTime(new Date(resultSet.getLong("FINISH_TIME")));
					messageVo.setContent(resultSet.getString("CONTENT"));
					return messageVo;
				}
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("queryPortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", query portion message -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("queryPortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", query portion message -> " + e);
			}
			throw e;
		}
		return messageDmList;
	}
	
	@Override
	public void savePortionMessage(String queueName, Long contentId, String content) throws Exception {
		
		String sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT SET CONTENT = ? WHERE ID = ?";
				
		final String sqlFinal = sql;
		
		try {
			jdbcTemplate.update(sqlFinal, new Object[]{
					content,
					contentId,
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("savePortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", save portion message -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("savePortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", save portion message -> " + e);
			}
			throw e;
		}
	}
	
	@Override
	public void deletePortionMessage(String queueName, Long id) throws Exception {
		
		String sql = "UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF SET STATUS = ? WHERE ID = ?";
				
		final String sqlFinal = sql;
		
		try {
			jdbcTemplate.update(sqlFinal, new Object[]{
					MessageConstant.MESSAGE_STATUS_FINISH,
					id
			});
		} catch (RecoverableDataAccessException e) {	
			if (logger.isErrorEnabled()) {
				logger.error("deletePortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", delete portion message -> " + e);
			}
			throw new ConnectResetException("Connection reset for create mysql db connect agent");
		} catch (Exception e) {	
			if (logger.isErrorEnabled()) {
				logger.error("deletePortionMessage -> " 
						+ "DB: " + connectId
						+ ", Ip: " + ip
						+ ", Port: " + port
						+ ", delete portion message -> " + e);
			}
			throw e;
		}
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
