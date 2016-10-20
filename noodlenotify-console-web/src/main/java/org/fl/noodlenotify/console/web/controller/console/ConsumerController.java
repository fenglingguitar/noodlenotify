package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodle.common.mvc.vo.VoidVo;
import org.fl.noodlenotify.console.service.ConsumerService;
import org.fl.noodlenotify.console.vo.ConsumerVo;

@Controller
@RequestMapping(value = "console/consumer")
public class ConsumerController {
	@Autowired
	private ConsumerService consumerService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<ConsumerVo> queryPage(@NoodleRequestParam ConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return consumerService.queryConsumerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querypagebyequal")
	@NoodleResponseBody
	public PageVo<ConsumerVo> queryPageByEqual(@NoodleRequestParam ConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return consumerService.queryConsumerPageByEqual(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<ConsumerVo> queryList(@NoodleRequestParam ConsumerVo vo) throws Exception {
		return consumerService.queryConsumerList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam ConsumerVo vo) throws Exception {
		consumerService.insertConsumer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam ConsumerVo[] vos) throws Exception {
		consumerService.insertsConsumer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam ConsumerVo vo) throws Exception {
		consumerService.updateConsumer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam ConsumerVo[] vos) throws Exception {
		consumerService.updatesConsumer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam ConsumerVo vo) throws Exception {
		consumerService.deleteConsumer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam ConsumerVo[] vos) throws Exception {
		consumerService.deletesConsumer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletesgroup")
	@NoodleResponseBody
	public VoidVo deletesgroup(@NoodleRequestParam ConsumerVo[] vos) throws Exception {
		consumerService.deletesgroupConsumer(vos);
		return VoidVo.VOID;
	}
}
