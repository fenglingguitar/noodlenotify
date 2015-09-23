package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/msgqueuecache")
public class QueueMsgQueueCacheController {
	@Autowired
	private QueueMsgQueueCacheService queueMsgQueueCacheService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueMsgQueueCacheVo> queryPage(@NoodleRequestParam QueueMsgQueueCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgQueueCacheService.queryQueueMsgQueueCachePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgQueueCacheVo> queryIncludePage(@NoodleRequestParam QueueMsgQueueCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgQueueCacheService.queryQueueMsgQueueCacheIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueMsgQueueCacheVo> queryExcludePage(@NoodleRequestParam QueueMsgQueueCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgQueueCacheService.queryQueueMsgQueueCacheExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueMsgQueueCacheVo> queryList(@NoodleRequestParam QueueMsgQueueCacheVo vo) throws Exception {
		return queueMsgQueueCacheService.queryQueueMsgQueueCacheList(vo);
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueMsgQueueCacheVo> queryQueue(@NoodleRequestParam QueueMsgQueueCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueMsgQueueCacheService.queryQueuePageByMsgQueueCache(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheService.insertQueueMsgQueueCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheService.insertsQueueMsgQueueCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheService.updateQueueMsgQueueCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheService.updatesQueueMsgQueueCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueMsgQueueCacheVo vo) throws Exception {
		queueMsgQueueCacheService.deleteQueueMsgQueueCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueMsgQueueCacheVo[] vos) throws Exception {
		queueMsgQueueCacheService.deletesQueueMsgQueueCache(vos);
		return VoidVo.VOID;
	}
}
