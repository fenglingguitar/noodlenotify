package org.fl.noodlenotify.console.web.controller.trace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueTraceStorageService;
import org.fl.noodlenotify.console.vo.QueueTraceStorageVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "trace/queue/tracestorage")
public class QueueTraceStorageController {
	@Autowired
	private QueueTraceStorageService queueTraceStorageService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueTraceStorageVo> queryPage(@NoodleRequestParam QueueTraceStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueTraceStorageService.queryQueueTraceStoragePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueTraceStorageVo> queryIncludePage(@NoodleRequestParam QueueTraceStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueTraceStorageService.queryQueueTraceStorageIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueTraceStorageVo> queryExcludePage(@NoodleRequestParam QueueTraceStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueTraceStorageService.queryQueueTraceStorageExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueTraceStorageVo> queryList(@NoodleRequestParam QueueTraceStorageVo vo) throws Exception {
		return queueTraceStorageService.queryQueueTraceStorageList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueTraceStorageVo> queryQueue(@NoodleRequestParam QueueTraceStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueTraceStorageService.queryQueueByTraceStorage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageService.insertQueueTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageService.insertsQueueTraceStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageService.updateQueueTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageService.updatesQueueTraceStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueTraceStorageVo vo) throws Exception {
		queueTraceStorageService.deleteQueueTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueTraceStorageVo[] vos) throws Exception {
		queueTraceStorageService.deletesQueueTraceStorage(vos);
		return VoidVo.VOID;
	}
}
