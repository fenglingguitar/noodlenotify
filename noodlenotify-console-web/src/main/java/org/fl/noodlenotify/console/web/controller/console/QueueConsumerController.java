package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueConsumerService;
import org.fl.noodlenotify.console.vo.QueueConsumerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/consumer")
public class QueueConsumerController {
	@Autowired
	private QueueConsumerService queueConsumerService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueConsumerVo> queryPage(@NoodleRequestParam QueueConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerService.queryQueueConsumerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueConsumerVo> queryIncludePage(@NoodleRequestParam QueueConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerService.queryQueueConsumerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueConsumerVo> queryExcludePage(@NoodleRequestParam QueueConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerService.queryQueueConsumerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueConsumerVo> queryList(@NoodleRequestParam QueueConsumerVo vo) throws Exception {
		return queueConsumerService.queryQueueConsumerList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueConsumerVo> queryQueue(@NoodleRequestParam QueueConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerService.queryQueuePageByConsumer(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueConsumerVo vo) throws Exception {
		queueConsumerService.insertQueueConsumer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueConsumerVo[] vos) throws Exception {
		queueConsumerService.insertsQueueConsumer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueConsumerVo vo) throws Exception {
		queueConsumerService.updateQueueConsumer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueConsumerVo[] vos) throws Exception {
		queueConsumerService.updatesQueueConsumer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueConsumerVo vo) throws Exception {
		queueConsumerService.deleteQueueConsumer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueConsumerVo[] vos) throws Exception {
		queueConsumerService.deletesQueueConsumer(vos);
		return VoidVo.VOID;
	}
}
