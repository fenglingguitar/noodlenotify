package org.fl.noodlenotify.console.web.controller.trace;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.vo.PageVo;
import org.fl.noodlenotify.console.service.TraceStorageService;
import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodle.common.mvc.vo.VoidVo;

@Controller
@RequestMapping(value = "trace/tracestorage")
public class TraceStorageController {
	@Autowired
	private TraceStorageService traceStorageService;

	@RequestMapping(value = "/querypage")
	@NoodleResponseBody
	public PageVo<TraceStorageVo> queryPage(@NoodleRequestParam TraceStorageVo vo, String page, String rows) throws Exception {
		page = page != null && !page.equals("") ? page : "0";
		rows = rows != null && !rows.equals("") ? rows : "0";
		return traceStorageService.queryTraceStoragePage(vo, Integer.parseInt(page), Integer.parseInt(rows));
	}

	@RequestMapping(value = "/querylist")
	@NoodleResponseBody
	public List<TraceStorageVo> queryList(@NoodleRequestParam TraceStorageVo vo) throws Exception {
		return traceStorageService.queryTraceStorageList(vo);
	}

	@RequestMapping(value = "/insert")
	@NoodleResponseBody
	public VoidVo insert(@NoodleRequestParam TraceStorageVo vo) throws Exception {
		traceStorageService.insertTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/inserts")
	@NoodleResponseBody
	public VoidVo inserts(@NoodleRequestParam TraceStorageVo[] vos) throws Exception {
		traceStorageService.insertsTraceStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/update")
	@NoodleResponseBody
	public VoidVo update(@NoodleRequestParam TraceStorageVo vo) throws Exception {
		traceStorageService.updateTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/updates")
	@NoodleResponseBody
	public VoidVo updates(@NoodleRequestParam TraceStorageVo[] vos) throws Exception {
		traceStorageService.updatesTraceStorage(vos);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/delete")
	@NoodleResponseBody
	public VoidVo delete(@NoodleRequestParam TraceStorageVo vo) throws Exception {
		traceStorageService.deleteTraceStorage(vo);
		return VoidVo.VOID;
	}

	@RequestMapping(value = "/deletes")
	@NoodleResponseBody
	public VoidVo deletes(@NoodleRequestParam TraceStorageVo[] vos) throws Exception {
		traceStorageService.deletesTraceStorage(vos);
		return VoidVo.VOID;
	}
}
