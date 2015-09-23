package org.fl.noodlenotify.core.connect.cache.trace.vo;

public class TraceVo {
	
	private String uuid;
	private int action;
	private long timestamp;
	private byte result;
	private byte traceModuleType;
	private long traceModuleId;
	private byte dealModuleType;
	private long dealModuleId;
	
	public TraceVo() {
	}
	
	public TraceVo(
			String uuid,
			int action,
			long timestamp,
			byte result,
			byte traceModuleType,
			long traceModuleId,
			byte dealModuleType,
			long dealModuleId
			) {
		this.uuid = uuid;
		this.action = action;
		this.timestamp = timestamp;
		this.result = result;
		this.setTraceModuleType(traceModuleType);
		this.setTraceModuleId(traceModuleId);
		this.dealModuleType = dealModuleType;
		this.dealModuleId = dealModuleId;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public byte getResult() {
		return result;
	}

	public void setResult(byte result) {
		this.result = result;
	}
	
	public byte getTraceModuleType() {
		return traceModuleType;
	}

	public void setTraceModuleType(byte traceModuleType) {
		this.traceModuleType = traceModuleType;
	}

	public long getTraceModuleId() {
		return traceModuleId;
	}

	public void setTraceModuleId(long traceModuleId) {
		this.traceModuleId = traceModuleId;
	}

	public byte getDealModuleType() {
		return dealModuleType;
	}

	public void setDealModuleType(byte dealModuleType) {
		this.dealModuleType = dealModuleType;
	}

	public long getDealModuleId() {
		return dealModuleId;
	}

	public void setDealModuleId(long dealModuleId) {
		this.dealModuleId = dealModuleId;
	}
}
