package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/msgstorage")
public class MsgStorageController {
	@Autowired
	private MsgStorageService msgStorageService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<MsgStorageVo> queryPage(@NoodleRequestParam MsgStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return msgStorageService.queryMsgStoragePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<MsgStorageVo> queryList(@NoodleRequestParam MsgStorageVo vo) throws Exception {
		return msgStorageService.queryMsgStorageList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam MsgStorageVo vo) throws Exception {
		msgStorageService.insertMsgStorage(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam MsgStorageVo[] vos) throws Exception {
		msgStorageService.insertsMsgStorage(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam MsgStorageVo vo) throws Exception {
		msgStorageService.updateMsgStorage(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam MsgStorageVo[] vos) throws Exception {
		msgStorageService.updatesMsgStorage(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam MsgStorageVo vo) throws Exception {
		msgStorageService.deleteMsgStorage(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam MsgStorageVo[] vos) throws Exception {
		msgStorageService.deletesMsgStorage(vos);
		return VoidVo.VOID;
	}
}
