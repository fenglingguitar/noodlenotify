package org.fl.noodlenotify.trace.service;

import java.util.List;

import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;

public interface TraceMsgService {
	public List<TraceVo> traceMsg(String uuid) throws Exception;
}
