package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueDistributerService;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/distributer")
public class QueueDistributerController {
	@Autowired
	private QueueDistributerService queueDistributerService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueDistributerVo> queryPage(@NoodleRequestParam QueueDistributerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueDistributerService.queryQueueDistributerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueDistributerVo> queryIncludePage(@NoodleRequestParam QueueDistributerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueDistributerService.queryQueueDistributerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueDistributerVo> queryExcludePage(@NoodleRequestParam QueueDistributerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueDistributerService.queryQueueDistributerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueDistributerVo> queryList(@NoodleRequestParam QueueDistributerVo vo) throws Exception {
		return queueDistributerService.queryQueueDistributerList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueDistributerVo> queryQueue(@NoodleRequestParam QueueDistributerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueDistributerService.queryQueuePageByDistributer(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueDistributerVo vo) throws Exception {
		queueDistributerService.insertQueueDistributer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueDistributerVo[] vos) throws Exception {
		queueDistributerService.insertsQueueDistributer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueDistributerVo vo) throws Exception {
		queueDistributerService.updateQueueDistributer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueDistributerVo[] vos) throws Exception {
		queueDistributerService.updatesQueueDistributer(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueDistributerVo vo) throws Exception {
		queueDistributerService.deleteQueueDistributer(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueDistributerVo[] vos) throws Exception {
		queueDistributerService.deletesQueueDistributer(vos);
		return VoidVo.VOID;
	}
}
