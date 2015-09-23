package org.fl.noodlenotify.monitor.performance.net.vo;

import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;

public class NetVo {
	
	private KeyVo keyVo;
	private Object bodyVo;

	public KeyVo getKeyVo() {
		return keyVo;
	}

	public void setKeyVo(KeyVo keyVo) {
		this.keyVo = keyVo;
	}

	public Object getBodyVo() {
		return bodyVo;
	}

	public void setBodyVo(Object bodyVo) {
		this.bodyVo = bodyVo;
	}
}
