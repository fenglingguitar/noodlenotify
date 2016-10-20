package org.fl.noodlenotify.console.web.controller.console;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.QueueConsumerGroupService;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "console/queue/consumergroup")
public class QueueConsumerGroupController {

	@Autowired
	private QueueConsumerGroupService queueConsumerGroupService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<QueueConsumerGroupVo> queryPage(@NoodleRequestParam QueueConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerGroupService.queryQueueConsumerGroupPage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryincludepage")
	@NoodleResponseBody
	public PageVo<QueueConsumerGroupVo> queryIncludePage(@NoodleRequestParam QueueConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerGroupService.queryQueueConsumerGroupIncludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryexcludepage")
	@NoodleResponseBody
	public PageVo<QueueConsumerGroupVo> queryExcludePage(@NoodleRequestParam QueueConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerGroupService.queryQueueConsumerGroupExcludePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryqueue")
	@NoodleResponseBody
	public PageVo<QueueConsumerGroupVo> queryQueue(@NoodleRequestParam QueueConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerGroupService.queryQueuePageByConsumerGroup(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/queryconsumerforqueue")
	@NoodleResponseBody
	public PageVo<QueueConsumerGroupVo> queryConsumerPageForQueue(@NoodleRequestParam QueueConsumerGroupVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return queueConsumerGroupService.queryConsumerPageByQueueConsumerGroup(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<QueueConsumerGroupVo> queryList(@NoodleRequestParam QueueConsumerGroupVo vo) throws Exception {
		return queueConsumerGroupService.queryQueueConsumerGroupList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupService.insertQueueConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam QueueConsumerGroupVo[] vos) throws Exception {
		if (vos == null || vos.length == 0) {
			return VoidVo.VOID;
		}
		String queueNm = vos[0].getQueue_Nm();
		List<QueueConsumerGroupVo> unuserGroupNumList = queueConsumerGroupService.queryUnuserGroupNumList(queueNm);
		if (unuserGroupNumList == null) {
			return VoidVo.VOID;
		}
		int maxLength = vos.length;
		if (vos.length > unuserGroupNumList.size()) {
			maxLength = unuserGroupNumList.size();
		}
		for (int i = 0; i < maxLength; i++) {
			vos[i].setConsumer_Num(unuserGroupNumList.get(i).getConsumer_Num());
			queueConsumerGroupService.insertQueueConsumerGroup(vos[i]);
		}
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupService.updateQueueConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam QueueConsumerGroupVo[] vos) throws Exception {
		queueConsumerGroupService.updatesQueueConsumerGroup(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam QueueConsumerGroupVo vo) throws Exception {
		queueConsumerGroupService.deleteQueueConsumerGroup(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam QueueConsumerGroupVo[] vos) throws Exception {
		queueConsumerGroupService.deletesQueueConsumerGroup(vos);
		return VoidVo.VOID;
	}
}
