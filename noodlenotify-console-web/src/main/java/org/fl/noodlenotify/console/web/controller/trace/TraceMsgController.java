package org.fl.noodlenotify.console.web.controller.trace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;
import org.fl.noodlenotify.trace.service.TraceMsgService;

@Controller
@RequestMapping(value = "trace/msg")
public class TraceMsgController {

	@Autowired
	private TraceMsgService traceMsgService;

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public PageVo<TraceVo> queryList(@NoodleRequestParam TraceVo traceVo) throws Exception {
		PageVo<TraceVo> pageVo = new PageVo<TraceVo>();
		List<TraceVo> traceVoList = traceMsgService.traceMsg(traceVo.getUuid());
		pageVo.setData(traceVoList);
		pageVo.setTotalCount(traceVoList.size());
		pageVo.setPageSize(traceVoList.size() > 0 ? traceVoList.size() : 1);
		return pageVo;
	}
}
