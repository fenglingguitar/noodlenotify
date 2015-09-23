package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueCustomerService;
import org.fl.noodlenotify.console.vo.QueueCustomerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/customer")
public class QueueCustomerController {
	@Autowired
	private QueueCustomerService queueCustomerService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueCustomerVo> queryPage(@NoodleRequestParam QueueCustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerService.queryQueueCustomerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueCustomerVo> queryIncludePage(@NoodleRequestParam QueueCustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerService.queryQueueCustomerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueCustomerVo> queryExcludePage(@NoodleRequestParam QueueCustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerService.queryQueueCustomerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueCustomerVo> queryList(@NoodleRequestParam QueueCustomerVo vo) throws Exception {
		return queueCustomerService.queryQueueCustomerList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueCustomerVo> queryQueue(@NoodleRequestParam QueueCustomerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerService.queryQueuePageByCustomer(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueCustomerVo vo) throws Exception {
		queueCustomerService.insertQueueCustomer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueCustomerVo[] vos) throws Exception {
		queueCustomerService.insertsQueueCustomer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueCustomerVo vo) throws Exception {
		queueCustomerService.updateQueueCustomer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueCustomerVo[] vos) throws Exception {
		queueCustomerService.updatesQueueCustomer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueCustomerVo vo) throws Exception {
		queueCustomerService.deleteQueueCustomer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueCustomerVo[] vos) throws Exception {
		queueCustomerService.deletesQueueCustomer(vos);
		return VoidVo.VOID;
	}
}
