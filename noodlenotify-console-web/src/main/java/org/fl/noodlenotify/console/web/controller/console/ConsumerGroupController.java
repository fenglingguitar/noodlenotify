package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.ConsumerGroupService;
import org.fl.noodlenotify.console.vo.ConsumerGroupVo;
import org.fl.noodlenotify.console.vo.ConsumerVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/consumergroup")
public class ConsumerGroupController {
	
	@Autowired
	private ConsumerGroupService consumerGroupService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<ConsumerGroupVo> queryPage(@NoodleRequestParam ConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return consumerGroupService.queryConsumerGroupPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<ConsumerVo> queryIncludePage(@NoodleRequestParam ConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return consumerGroupService.queryConsumerIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<ConsumerVo> queryExcludePage(@NoodleRequestParam ConsumerVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return consumerGroupService.queryConsumerExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<ConsumerGroupVo> queryList(@NoodleRequestParam ConsumerGroupVo vo) throws Exception {
		return consumerGroupService.queryConsumerGroupList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam ConsumerGroupVo vo) throws Exception {
		consumerGroupService.insertConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam ConsumerGroupVo[] vos) throws Exception {
		consumerGroupService.insertsConsumerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam ConsumerGroupVo vo) throws Exception {
		consumerGroupService.updateConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam ConsumerGroupVo[] vos) throws Exception {
		consumerGroupService.updatesConsumerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam ConsumerGroupVo vo) throws Exception {
		consumerGroupService.deleteConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam ConsumerGroupVo[] vos) throws Exception {
		consumerGroupService.deletesConsumerGroup(vos);
		return VoidVo.VOID;
	}
}
