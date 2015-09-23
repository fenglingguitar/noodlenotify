package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueCustomerGroupService;
import org.fl.noodlenotify.console.vo.QueueCustomerGroupVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/customergroup")
public class QueueCustomerGroupController {

	@Autowired
	private QueueCustomerGroupService queueCustomerGroupService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueCustomerGroupVo> queryPage(@NoodleRequestParam QueueCustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerGroupService.queryQueueCustomerGroupPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueCustomerGroupVo> queryIncludePage(@NoodleRequestParam QueueCustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerGroupService.queryQueueCustomerGroupIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueCustomerGroupVo> queryExcludePage(@NoodleRequestParam QueueCustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerGroupService.queryQueueCustomerGroupExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueCustomerGroupVo> queryQueue(@NoodleRequestParam QueueCustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerGroupService.queryQueuePageByCustomerGroup(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querycustomerforqueue")
	@NoodleResponseBody
	public PageVo<QueueCustomerGroupVo> queryCustomerPageForQueue(@NoodleRequestParam QueueCustomerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueCustomerGroupService.queryCustomerPageByQueueCustomerGroup(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueCustomerGroupVo> queryList(@NoodleRequestParam QueueCustomerGroupVo vo) throws Exception {
		return queueCustomerGroupService.queryQueueCustomerGroupList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupService.insertQueueCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueCustomerGroupVo[] vos) throws Exception {
		if (vos == null || vos.length == 0) {
			return VoidVo.VOID;
		}
		String queueNm = vos[0].getQueue_Nm();
		List<QueueCustomerGroupVo> unuserGroupNumList = queueCustomerGroupService.queryUnuserGroupNumList(queueNm);
		if (unuserGroupNumList == null) {
			return VoidVo.VOID;
		}
		int maxLength = vos.length;
		if (vos.length > unuserGroupNumList.size()) {
			maxLength = unuserGroupNumList.size();
		}
		for (int i = 0; i < maxLength; i++) {
			vos[i].setCustomer_Num(unuserGroupNumList.get(i).getCustomer_Num());
			queueCustomerGroupService.insertQueueCustomerGroup(vos[i]);
		}
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupService.updateQueueCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueCustomerGroupVo[] vos) throws Exception {
		queueCustomerGroupService.updatesQueueCustomerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueCustomerGroupVo vo) throws Exception {
		queueCustomerGroupService.deleteQueueCustomerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueCustomerGroupVo[] vos) throws Exception {
		queueCustomerGroupService.deletesQueueCustomerGroup(vos);
		return VoidVo.VOID;
	}
}
