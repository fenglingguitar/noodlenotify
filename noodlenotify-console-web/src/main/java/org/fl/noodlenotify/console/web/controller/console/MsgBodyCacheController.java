package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/msgbodycache")
public class MsgBodyCacheController {
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<MsgBodyCacheVo> queryPage(@NoodleRequestParam MsgBodyCacheVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return msgBodyCacheService.queryMsgBodyCachePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<MsgBodyCacheVo> queryList(@NoodleRequestParam MsgBodyCacheVo vo) throws Exception {
		return msgBodyCacheService.queryMsgBodyCacheList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheService.insertMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam MsgBodyCacheVo[] vos) throws Exception {
		msgBodyCacheService.insertsMsgBodyCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheService.updateMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam MsgBodyCacheVo[] vos) throws Exception {
		msgBodyCacheService.updatesMsgBodyCache(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam MsgBodyCacheVo vo) throws Exception {
		msgBodyCacheService.deleteMsgBodyCache(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam MsgBodyCacheVo[] vos) throws Exception {
		msgBodyCacheService.deletesMsgBodyCache(vos);
		return VoidVo.VOID;
	}
}
