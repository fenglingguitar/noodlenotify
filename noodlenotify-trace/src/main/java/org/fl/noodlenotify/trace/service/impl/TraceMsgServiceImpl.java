package org.fl.noodlenotify.trace.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.service.TraceStorageService;
import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheConnectAgent;
import org.fl.noodlenotify.core.connect.cache.trace.manager.console.ConsoleTraceCacheConnectManager;
import org.fl.noodlenotify.core.connect.cache.trace.vo.TraceVo;
import org.fl.noodlenotify.trace.service.TraceMsgService;

@Service
public class TraceMsgServiceImpl implements TraceMsgService {

	@Autowired
	private TraceStorageService traceStorageService;

	@Autowired
	private ConsoleTraceCacheConnectManager consoleTraceCacheConnectManager;

	@Override
	public List<TraceVo> traceMsg(String uuid) throws Exception {
		
		List<TraceVo> traceVoResultList = new ArrayList<TraceVo>();
		
		List<TraceStorageVo> tracestorages = traceStorageService.queryCheckTracestorageList();
		if (tracestorages != null && tracestorages.size() > 0) {
			for (TraceStorageVo traceStorage : tracestorages) {
				long traceStorageId = traceStorage.getTraceStorage_Id();
				TraceCacheConnectAgent connectAgent = (TraceCacheConnectAgent) consoleTraceCacheConnectManager.getConnectAgent(traceStorageId);
				if(connectAgent == null){
					continue;
				}
				List<TraceVo> traceVoList = connectAgent.gets(uuid);
				if (traceVoList != null && traceVoList.size() > 0) {
					traceVoResultList.addAll(traceVoList);
				}
			}
		}
		
		Collections.sort(traceVoResultList, new TraceVoComparator());
		
		for (TraceVo traceVo : traceVoResultList) {
			traceVo.setTimestamp(traceVo.getTimestamp() / 1000);
		}
		
		return traceVoResultList;
	}
	
	private class TraceVoComparator implements Comparator<TraceVo> {

		@Override
		public int compare(TraceVo traceVo1, TraceVo traceVo2) {
			long diffTimestamp = traceVo1.getTimestamp() - traceVo2.getTimestamp();
			if (diffTimestamp > 0) {
				return 1;
			} else if (diffTimestamp < 0) {
				return -1;
			} else {
				int diffAction = traceVo1.getAction() - traceVo2.getAction();
				if (diffAction > 0) {
					return 1;
				} else if (diffAction < 0) {
					return -1;
				}
				return 0;
			}
		}
	}
}
