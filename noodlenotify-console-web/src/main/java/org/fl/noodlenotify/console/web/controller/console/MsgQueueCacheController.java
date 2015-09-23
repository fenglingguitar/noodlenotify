package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/msgqueuecache")
public class MsgQueueCacheController {
	@Autowired
	private MsgQueueCacheService msgQueueCacheService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<MsgQueueCacheVo> queryPage(@NoodleRequestParam MsgQueueCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return msgQueueCacheService.queryMsgQueueCachePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<MsgQueueCacheVo> queryList(@NoodleRequestParam MsgQueueCacheVo vo) throws Exception {
		return msgQueueCacheService.queryMsgQueueCacheList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheService.insertMsgQueueCache(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam MsgQueueCacheVo[] vos) throws Exception {
		msgQueueCacheService.insertsMsgQueueCache(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheService.updateMsgQueueCache(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam MsgQueueCacheVo[] vos) throws Exception {
		msgQueueCacheService.updatesMsgQueueCache(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam MsgQueueCacheVo vo) throws Exception {
		msgQueueCacheService.deleteMsgQueueCache(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam MsgQueueCacheVo[] vos) throws Exception {
		msgQueueCacheService.deletesMsgQueueCache(vos);
		return VoidVo.VOID;
	}
}
