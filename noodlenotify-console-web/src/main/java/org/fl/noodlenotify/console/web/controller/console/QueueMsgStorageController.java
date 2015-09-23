package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/msgstorage")
public class QueueMsgStorageController {
	@Autowired
	private QueueMsgStorageService queueMsgStorageService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueMsgStorageVo> queryPage(@NoodleRequestParam QueueMsgStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgStorageService.queryQueueMsgStoragePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgStorageVo> queryIncludePage(@NoodleRequestParam QueueMsgStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgStorageService.queryQueueMsgStorageIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgStorageVo> queryExcludePage(@NoodleRequestParam QueueMsgStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgStorageService.queryQueueMsgStorageExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueMsgStorageVo> queryList(@NoodleRequestParam QueueMsgStorageVo vo) throws Exception {
		return queueMsgStorageService.queryQueueMsgStorageList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueMsgStorageVo> queryQueue(@NoodleRequestParam QueueMsgStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgStorageService.queryQueueByMsgStorage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageService.insertQueueMsgStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageService.insertsQueueMsgStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageService.updateQueueMsgStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageService.updatesQueueMsgStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueMsgStorageVo vo) throws Exception {
		queueMsgStorageService.deleteQueueMsgStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueMsgStorageVo[] vos) throws Exception {
		queueMsgStorageService.deletesQueueMsgStorage(vos);
		return VoidVo.VOID;
	}
}
