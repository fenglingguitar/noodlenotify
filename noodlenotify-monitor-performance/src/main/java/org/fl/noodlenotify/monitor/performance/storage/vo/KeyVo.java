package org.fl.noodlenotify.monitor.performance.storage.vo;

import org.fl.noodlenotify.monitor.performance.persistence.vo.BaseVo;

public class KeyVo extends BaseVo {
	
	private String executerName;
	private String moduleName;
	private long moduleId;
	private String monitorModuleName;
	private long monitorModuleId;
	private String queueName;
	private String monitorName;
	
	public String getExecuterName() {
		return executerName;
	}
	public void setExecuterName(String executerName) {
		this.executerName = executerName;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	public String getMonitorModuleName() {
		return monitorModuleName;
	}
	public void setMonitorModuleName(String monitorModuleName) {
		this.monitorModuleName = monitorModuleName;
	}
	public long getMonitorModuleId() {
		return monitorModuleId;
	}
	public void setMonitorModuleId(long monitorModuleId) {
		this.monitorModuleId = monitorModuleId;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public String getMonitorName() {
		return monitorName;
	}
	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}
	
	public String toKeyString () {
		return new StringBuilder()
					.append("KEY-")
					.append(executerName)
					.append("-")
					.append(monitorModuleName)
					.append("-")
					.append(monitorModuleId)
					.append("-")
					.append(moduleName)
					.append("-")
					.append(moduleId)
					.append("-")
					.append(queueName)
					.append("-")
					.append(monitorName)
					.toString();
	}
}
