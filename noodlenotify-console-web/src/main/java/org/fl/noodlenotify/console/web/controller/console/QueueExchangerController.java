package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/exchanger")
public class QueueExchangerController {
	@Autowired
	private QueueExchangerService queueExchangerService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueExchangerVo> queryPage(@NoodleRequestParam QueueExchangerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueExchangerService.queryQueueExchangerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueExchangerVo> queryIncludePage(@NoodleRequestParam QueueExchangerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueExchangerService.queryQueueExchangerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueExchangerVo> queryExcludePage(@NoodleRequestParam QueueExchangerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueExchangerService.queryQueueExchangerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueExchangerVo> queryList(@NoodleRequestParam QueueExchangerVo vo) throws Exception {
		return queueExchangerService.queryQueueExchangerList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueExchangerVo> queryQueue(@NoodleRequestParam QueueExchangerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueExchangerService.queryQueuePageByExchanger(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueExchangerVo vo) throws Exception {
		queueExchangerService.insertQueueExchanger(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueExchangerVo[] vos) throws Exception {
		queueExchangerService.insertsQueueExchanger(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueExchangerVo vo) throws Exception {
		queueExchangerService.updateQueueExchanger(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueExchangerVo[] vos) throws Exception {
		queueExchangerService.updatesQueueExchanger(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueExchangerVo vo) throws Exception {
		queueExchangerService.deleteQueueExchanger(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueExchangerVo[] vos) throws Exception {
		queueExchangerService.deletesQueueExchanger(vos);
		return VoidVo.VOID;
	}
}
