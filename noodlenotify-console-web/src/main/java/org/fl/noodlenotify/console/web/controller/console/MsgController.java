package org.fl.noodlenotify.console.web.controller.console;

import java.util.ArrayList;
import java.util.List;

import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;
import org.fl.noodlenotify.common.pojo.console.MessageVo;
import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.QueueDbService;
import org.fl.noodlenotify.console.vo.QueueDbVo;
import org.fl.noodlenotify.core.connect.db.DbStatusChecker;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbStatusCheckerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "console/message")
public class MsgController {
	
	@Autowired
	private QueueDbService queueDbService;

	@Autowired(required = false)
	private MysqlDbStatusCheckerFactory mysqlDbStatusCheckerFactory;
	
	@RequestMapping(value = "/queryportionmessage")
	@NoodleResponseBody(type = "json-millisecond")
	public List<MessageVo> queryPortionMessage(@NoodleRequestParam MessageVo vo, String page, String rows) throws Exception {
		
		int pageInt = page != null && !page.equals("") ? Integer.parseInt(page) : 0;
		int rowsInt = rows != null && !rows.equals("") ? Integer.parseInt(rows) : 50;
		
		List<MessageVo> result = new ArrayList<MessageVo>();
		
		QueueDbVo queueDbVo = new QueueDbVo();
		queueDbVo.setQueue_Nm(vo.getQueueName());
		queueDbVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueDbVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
		List<QueueDbVo> queueDbVoList = queueDbService.queryDbByQueueExclude(queueDbVo);
		for (QueueDbVo queueDbVoIt : queueDbVoList) {
			DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(queueDbVoIt.getDb_Id(), queueDbVoIt.getIp(), queueDbVoIt.getPort(), null);
			result.addAll(dbStatusChecker.queryPortionMessage(queueDbVoIt.getQueue_Nm(), vo.getUuid(), vo.getRegion(), vo.getContent(), pageInt * rowsInt, rowsInt));
		}
		
		return result;
	}
	
	@RequestMapping(value = "/saveportionmessage")
	@NoodleResponseBody
	public VoidVo savePortionMessage(@NoodleRequestParam MessageVo vo) throws Exception {
		
		QueueDbVo queueDbVo = new QueueDbVo();
		queueDbVo.setQueue_Nm(vo.getQueueName());
		queueDbVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
		queueDbVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
		List<QueueDbVo> queueDbVoList = queueDbService.queryDbByQueueExclude(queueDbVo);
		for (QueueDbVo queueDbVoIt : queueDbVoList) {
			if (queueDbVoIt.getDb_Id() == vo.getDb()) {
				DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(queueDbVoIt.getDb_Id(), queueDbVoIt.getIp(), queueDbVoIt.getPort(), null);
				dbStatusChecker.savePortionMessage(queueDbVoIt.getQueue_Nm(), vo.getContentId(), vo.getContent());
			}
		}
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletesportionmessage")
	@NoodleResponseBody
	public VoidVo deletesPortionMessage(@NoodleRequestParam MessageVo[] vos) throws Exception {
		
		for (MessageVo vo : vos) {
			QueueDbVo queueDbVo = new QueueDbVo();
			queueDbVo.setQueue_Nm(vo.getQueueName());
			queueDbVo.setSystem_Status(ConsoleConstants.SYSTEM_STATUS_ON_LINE);
			queueDbVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_INVALID);
			List<QueueDbVo> queueDbVoList = queueDbService.queryDbByQueueExclude(queueDbVo);
			for (QueueDbVo queueDbVoIt : queueDbVoList) {
				if (queueDbVoIt.getDb_Id() == vo.getDb()) {
					DbStatusChecker dbStatusChecker = (DbStatusChecker) mysqlDbStatusCheckerFactory.createStatusChecker(queueDbVoIt.getDb_Id(), queueDbVoIt.getIp(), queueDbVoIt.getPort(), null);
					dbStatusChecker.deletePortionMessage(queueDbVoIt.getQueue_Nm(), vo.getId());
				}
			}
		}
		return VoidVo.VOID;
	}
}
