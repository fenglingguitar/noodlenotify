package org.fl.noodlenotify.core.connect.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fl.noodle.common.connect.exception.ConnectRefusedException;
import org.fl.noodlenotify.core.connect.constent.ConnectAgentType;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.domain.message.MessageVo;
import org.fl.noodlenotify.core.status.AbstractStatusChecker;

public class MysqlDbStatusChecker extends AbstractStatusChecker implements DbStatusChecker {
	
	//private final static Logger logger = LoggerFactory.getLogger(MysqlDbStatusChecker.class);

	private Connection conn = null;
	private Statement stmt = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;

	private String dbUrl;
	private String dbName;
	private String user;
	private String password;
	
	public MysqlDbStatusChecker(long connectId, String ip, int port, String url, 
			int connectTimeout, int readTimeout, String encoding, String dbName, String user, String password) {
		super(connectId, ip, port, url, ConnectAgentType.DB.getCode(), connectTimeout, readTimeout, encoding);
		this.dbUrl = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?useUnicode=true&characterEncoding=utf8";
		this.dbName = dbName;
		this.user = user;
		this.password = password;
	}

	@Override
	public void connect() throws Exception {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbUrl, user, password);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConnectRefusedException("Connection refused for create mysql db connect agent");
		}
	}
	
	@Override
	public void close() {
		try {
			if(rs != null) { rs.close(); }
		} catch(SQLException e){
			e.printStackTrace();
		}
		try {
			if(stmt != null) { stmt.close(); }
		} catch(SQLException e){
			e.printStackTrace();
		}
		try {
			if(pstmt != null) { pstmt.close(); }
		} catch(SQLException e){
			e.printStackTrace();
		}
		try {
			if(conn != null) { conn.close(); }
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void checkHealth() throws Exception {
		stmt = conn.createStatement();
		rs = stmt.executeQuery("SELECT CURRENT_TIMESTAMP FROM DUAL");
		rs.first();
	}
	
	private boolean isHaveTable(String queueName) throws Exception {
		pstmt = conn.prepareStatement("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?");
		pstmt.setString(1, dbName);
		pstmt.setString(2, "MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF");
		rs = pstmt.executeQuery();
		return rs.first();
	}
	
	@Override
	public long checkNewLen(String queueName) throws Exception {
		if (isHaveTable(queueName)) {
			pstmt = conn.prepareStatement("SELECT COUNT(*) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?");
			pstmt.setByte(1, MessageConstant.MESSAGE_STATUS_NEW);
			rs = pstmt.executeQuery();
			if (rs.first()) {
				return rs.getLong(1);
			}
		}
		return 0L;
	}

	@Override
	public long checkPortionLen(String queueName) throws Exception {
		if (isHaveTable(queueName)) {
			pstmt = conn.prepareStatement("SELECT COUNT(*) FROM MSG_" + queueName.toUpperCase().replace(".", "_") + "_IF WHERE STATUS = ?");
			pstmt.setByte(1, MessageConstant.MESSAGE_STATUS_PORTION);
			rs = pstmt.executeQuery();
			if (rs.first()) {
				return rs.getLong(1);
			}
		}
		return 0L;
	}
	
	@Override
	public List<MessageVo> queryPortionMessage(String queueName, String uuid, Long region, String content, Integer page, Integer rows) throws Exception {
		
		List<MessageVo> messageDmList = new ArrayList<MessageVo>();
		
		if (isHaveTable(queueName)) {
			
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
					
			sql += "ORDER BY i.ID DESC LIMIT ?,?";
			
			objectList.add(page);
			objectList.add(rows);
			
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			for (Object objectIt : objectList) {
				pstmt.setObject(i++, objectIt);
			}
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MessageVo messageVo = new MessageVo();
				messageVo.setId(rs.getLong("ID"));
				messageVo.setUuid(rs.getString("UUID"));
				messageVo.setQueueName(rs.getString("QUEUE_NAME"));
				messageVo.setContentId(rs.getLong("CONTENT_ID"));
				messageVo.setExecuteQueue(rs.getLong("EXECUTE_QUEUE"));
				messageVo.setResultQueue(rs.getLong("RESULT_QUEUE"));
				messageVo.setStatus(rs.getByte("STATUS"));
				messageVo.setDb(connectId);
				messageVo.setBeginTime(new Date(rs.getLong("BEGIN_TIME")));
				messageVo.setFinishTime(new Date(rs.getLong("FINISH_TIME")));
				messageVo.setContent(rs.getString("CONTENT"));
				messageDmList.add(messageVo);
			}
		}
		return messageDmList;
	}
	
	@Override
	public void savePortionMessage(String queueName, Long contentId, String content) throws Exception {
		if (isHaveTable(queueName)) {
			pstmt = conn.prepareStatement("UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT SET CONTENT = ? WHERE ID = ?");
			pstmt.setString(1, content);
			pstmt.setLong(2, contentId);
			pstmt.execute();
		}
	}
	
	@Override
	public void deletePortionMessage(String queueName, Long id) throws Exception {
		if (isHaveTable(queueName)) {
			pstmt = conn.prepareStatement("UPDATE MSG_" + queueName.toUpperCase().replace(".", "_") + "_CT SET CONTENT = ? WHERE ID = ?");
			pstmt.setByte(1, MessageConstant.MESSAGE_STATUS_FINISH);
			pstmt.setLong(2, id);
			pstmt.execute();
		}
	}

	@Override
	protected Class<?> getServiceInterfaces() {
		return DbStatusChecker.class;
	}
}
