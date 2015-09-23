package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueService;
import org.fl.noodlenotify.console.vo.QueueVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;
import org.fl.noodlenotify.core.connect.net.pojo.Message;
import org.fl.noodlenotify.core.pclient.ProducerClientImpl;


@Controller
@RequestMapping(value = "console/queue")
public class QueueController {
	
	@Autowired
	private QueueService queueService;
	
	@Autowired
	private ProducerClientImpl producerClient;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueVo> queryPage(@NoodleRequestParam QueueVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueService.queryQueuePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueVo> queryList(@NoodleRequestParam QueueVo vo) throws Exception {
		return queueService.queryQueueList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueVo vo) throws Exception {
		queueService.insertQueue(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueVo[] vos) throws Exception {
		queueService.insertsQueue(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueVo vo) throws Exception {
		queueService.updateQueue(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueVo[] vos) throws Exception {
		queueService.updatesQueue(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueVo vo) throws Exception {
		queueService.deleteQueue(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueVo[] vos) throws Exception {
		queueService.deletesQueue(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/querymonitorpage")
	@NoodleResponseBody
	public PageVo<QueueVo> queryMonitorPage(@NoodleRequestParam QueueVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueService.queryQueueMonitorPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/send")
	@NoodleResponseBody
	public Message send(@NoodleRequestParam Message message, String content) throws Exception {
		return new Message(message.getQueueName(), producerClient.send(message.getQueueName(), message.getContent()), null);
	}
}
