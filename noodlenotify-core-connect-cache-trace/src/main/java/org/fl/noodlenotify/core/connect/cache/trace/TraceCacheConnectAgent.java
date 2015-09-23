package org.fl.noodlenotify.core.connect.cache.trace;

import java.util.List;

import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;

public interface TraceCacheConnectAgent {
	public void set(TraceVo traceVo) throws Exception;
	public List<TraceVo> gets(String uuid) throws Exception;
	public void remove(String uuid) throws Exception;
}
