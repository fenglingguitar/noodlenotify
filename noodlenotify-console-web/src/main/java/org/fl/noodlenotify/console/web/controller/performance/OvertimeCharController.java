package org.fl.noodlenotify.console.web.controller.performance;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodle.common.mvc.annotation.NoodleRequestParam;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodlenotify.monitor.performance.persistence.RedisPersistenceTemplate;
import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;
import org.fl.noodlenotify.monitor.performance.vo.OvertimeVo;

@Controller
@RequestMapping(value = "monitor/chart/overtime")
public class OvertimeCharController {
	
	@Autowired
	RedisPersistenceTemplate redisPersistenceTemplate;
	
	@RequestMapping(value = "/getdatetime")
	@NoodleResponseBody
	public OvertimeVo getdatetime() throws Exception {
		
		Date now = new Date();
		OvertimeVo overtimeVo = new OvertimeVo();
		overtimeVo.setTimestamp(now.getTime());
		return overtimeVo;
	}
	
	@RequestMapping(value = "/querychartsinglenow")
	@NoodleResponseBody
	public List<OvertimeVo> queryChartSingleNow(@NoodleRequestParam KeyVo keyVo, String region) throws Exception {
		long regionLong = region != null && !region.equals("") ? Long.valueOf(region) : 60;
		long nowTime = System.currentTimeMillis();
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), nowTime - regionLong * 60000, nowTime, OvertimeVo.class);
	}
	
	@RequestMapping(value = "/querychartbganded")
	@NoodleResponseBody
	public List<OvertimeVo> queryChartBgAndEd(@NoodleRequestParam KeyVo keyVo, @NoodleRequestParam(type = "date") Date beginTime, @NoodleRequestParam(type = "date") Date endTime) throws Exception {
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), beginTime.getTime(), endTime.getTime(), OvertimeVo.class);
	}
	
	@RequestMapping(value = "/querychartsinglenowlast")
	@NoodleResponseBody
	public List<OvertimeVo> queryChartSinglenowlast(@NoodleRequestParam KeyVo keyVo, String intervalLastTime) throws Exception {
		long intervalLastTimeLong = intervalLastTime != null && !intervalLastTime.equals("") ? Long.valueOf(intervalLastTime) : System.currentTimeMillis();
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), intervalLastTimeLong, Long.MAX_VALUE, OvertimeVo.class);
	}
}
