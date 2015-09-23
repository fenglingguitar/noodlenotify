package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.ProducerService;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/producer")
public class ProducerController {
	@Autowired
	private ProducerService producerService;
	
	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<ProducerVo> queryPage(@NoodleRequestParam ProducerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return producerService.queryProducerPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}
	
	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<ProducerVo> queryList(@NoodleRequestParam ProducerVo vo) throws Exception {
		return producerService.queryProducerList(vo);
	}
	
	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam ProducerVo vo) throws Exception {
		producerService.insertProducer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam ProducerVo[] vos) throws Exception {
		producerService.insertsProducer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam ProducerVo vo) throws Exception {
		producerService.updateProducer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam ProducerVo[] vos) throws Exception {
		producerService.updatesProducer(vos);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam ProducerVo vo) throws Exception {
		producerService.deleteProducer(vo);
		return VoidVo.VOID;
	}
	
	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam ProducerVo[] vos) throws Exception {
		producerService.deletesProducer(vos);
		return VoidVo.VOID;
	}
}
