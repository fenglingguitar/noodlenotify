package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/exchanger")
public class ExchangerController {
	@Autowired
	private ExchangerService exchangerService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<ExchangerVo> queryPage(@NoodleRequestParam ExchangerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return exchangerService.queryExchangerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<ExchangerVo> queryList(@NoodleRequestParam ExchangerVo vo) throws Exception {
		return exchangerService.queryExchangerList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam ExchangerVo vo) throws Exception {
		exchangerService.insertExchanger(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam ExchangerVo[] vos) throws Exception {
		exchangerService.insertsExchanger(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam ExchangerVo vo) throws Exception {
		exchangerService.updateExchanger(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam ExchangerVo[] vos) throws Exception {
		exchangerService.updatesExchanger(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam ExchangerVo vo) throws Exception {
		exchangerService.deleteExchanger(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam ExchangerVo[] vos) throws Exception {
		exchangerService.deletesExchanger(vos);
		return VoidVo.VOID;
	}
}
