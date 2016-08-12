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
import org.fl.noodlenotify.monitor.performance.vo.SuccessVo;

@Controller
@RequestMapping(value = "monitor/chart/success")
public class SuccessCharController {
	
	@Autowired(required = false)
	RedisPersistenceTemplate redisPersistenceTemplate;
	
	@RequestMapping(value = "/getdatetime")
	@NoodleResponseBody
	public SuccessVo getdatetime() throws Exception {
		
		Date now = new Date();
		SuccessVo successVo = new SuccessVo();
		successVo.setTimestamp(now.getTime());
		return successVo;
	}
	
	@RequestMapping(value = "/querychartsinglenow")
	@NoodleResponseBody
	public List<SuccessVo> queryChartSingleNow(@NoodleRequestParam KeyVo keyVo, String region) throws Exception {
		long regionLong = region != null && !region.equals("") ? Long.valueOf(region) : 60;
		long nowTime = System.currentTimeMillis();
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), nowTime - regionLong * 60000, nowTime, SuccessVo.class);
	}
	
	@RequestMapping(value = "/querychartbganded")
	@NoodleResponseBody
	public List<SuccessVo> queryChartBgAndEd(@NoodleRequestParam KeyVo keyVo, @NoodleRequestParam(type = "date") Date beginTime, @NoodleRequestParam(type = "date") Date endTime) throws Exception {
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), beginTime.getTime(), endTime.getTime(), SuccessVo.class);
	}
	
	@RequestMapping(value = "/querychartsinglenowlast")
	@NoodleResponseBody
	public List<SuccessVo> queryChartSinglenowlast(@NoodleRequestParam KeyVo keyVo, String intervalLastTime) throws Exception {
		long intervalLastTimeLong = intervalLastTime != null && !intervalLastTime.equals("") ? Long.valueOf(intervalLastTime) : System.currentTimeMillis();
		return redisPersistenceTemplate.queryList(keyVo.toKeyString(), intervalLastTimeLong, Long.MAX_VALUE, SuccessVo.class);
	}
}
