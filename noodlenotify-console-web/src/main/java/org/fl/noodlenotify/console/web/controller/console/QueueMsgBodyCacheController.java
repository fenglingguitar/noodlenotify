package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueMsgBodyCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/msgbodycache")
public class QueueMsgBodyCacheController {
	@Autowired
	private QueueMsgBodyCacheService queueMsgBodyCacheService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueMsgBodyCacheVo> queryPage(@NoodleRequestParam QueueMsgBodyCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgBodyCacheService.queryQueueMsgBodyCachePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgBodyCacheVo> queryIncludePage(@NoodleRequestParam QueueMsgBodyCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgBodyCacheService.queryQueueMsgBodyCacheIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgBodyCacheVo> queryExcludePage(@NoodleRequestParam QueueMsgBodyCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgBodyCacheService.queryQueueMsgBodyCacheExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueMsgBodyCacheVo> queryList(@NoodleRequestParam QueueMsgBodyCacheVo vo) throws Exception {
		return queueMsgBodyCacheService.queryQueueMsgBodyCacheList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueMsgBodyCacheVo> queryQueue(@NoodleRequestParam QueueMsgBodyCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgBodyCacheService.queryQueuePageByMsgBodyCache(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheService.insertQueueMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheService.insertsQueueMsgBodyCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheService.updateQueueMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheService.updatesQueueMsgBodyCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueMsgBodyCacheVo vo) throws Exception {
		queueMsgBodyCacheService.deleteQueueMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueMsgBodyCacheVo[] vos) throws Exception {
		queueMsgBodyCacheService.deletesQueueMsgBodyCache(vos);
		return VoidVo.VOID;
	}
}
