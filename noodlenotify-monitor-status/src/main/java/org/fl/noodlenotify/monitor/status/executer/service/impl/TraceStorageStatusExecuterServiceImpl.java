package org.fl.noodlenotify.monitor.status.executer.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.TraceStorageService;
import org.fl.noodlenotify.console.vo.TraceStorageVo;
import org.fl.noodlenotify.core.connect.cache.trace.TraceCacheStatusChecker;
import org.fl.noodlenotify.core.connect.cache.trace.manager.console.ConsoleTraceCacheConnectManager;
import org.fl.noodlenotify.monitor.status.executer.service.ExecuterServiceAbstract;

@Service("traceStorageStatusExecuterService")
public class TraceStorageStatusExecuterServiceImpl extends ExecuterServiceAbstract {

	private final static Logger logger = LoggerFactory.getLogger(TraceStorageStatusExecuterServiceImpl.class);
	
	@Autowired
	private TraceStorageService traceStorageService;

	@Autowired
	ConsoleTraceCacheConnectManager consoleTraceCacheConnectManager;

	@Override
	public void execute() throws Exception {
		
		List<TraceStorageVo> traceStorageVoList = traceStorageService.queryCheckTracestorageList();
		for (TraceStorageVo traceStorageVo : traceStorageVoList) {
			byte systemStatus = traceStorageVo.getSystem_Status();
			byte currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_OFF_LINE;
			TraceCacheStatusChecker traceCacheStatusChecker = (TraceCacheStatusChecker) consoleTraceCacheConnectManager.getConnectAgent(traceStorageVo.getTraceStorage_Id());
			if (traceCacheStatusChecker != null) {
				try {
					traceCacheStatusChecker.checkHealth();
					currentSysTemStatus = ConsoleConstants.SYSTEM_STATUS_ON_LINE;
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.error("CheckHealth -> " + e);
					}
				}
			}
			if (systemStatus != currentSysTemStatus) {
				TraceStorageVo currentTraceStorageVo = new TraceStorageVo();
				currentTraceStorageVo.setTraceStorage_Id(traceStorageVo.getTraceStorage_Id());
				currentTraceStorageVo.setSystem_Status(currentSysTemStatus);
				traceStorageService.updatesTraceStorageSystemStatus(currentTraceStorageVo);
			}
		}
	}
}
