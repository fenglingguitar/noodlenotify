package org.fl.noodlenotify.monitor.status.console.executer;

import java.util.Date;

import org.fl.noodle.common.monitor.executer.AbstractExecuter;
import org.fl.noodlenotify.console.service.ProducerService;
import org.fl.noodlenotify.console.vo.ProducerVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProducerStatusExecuter extends AbstractExecuter {

	private final static Logger logger = LoggerFactory.getLogger(ProducerStatusExecuter.class);
	
	@Autowired
	private ProducerService producerService;
	
	private long maxInterval = 10 * 1000;

	@Override
	public void execute() throws Exception {
		
		ProducerVo producerVo = new ProducerVo();
		producerVo.setBeat_Time(new Date(((new Date()).getTime() - maxInterval)));
		
		try {
			producerService.updateClientOnline(producerVo);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> clientService.updateClientOnline -> {} -> Exception:{}", producerVo, e.getMessage());
			}
		}
		
		try {
			producerService.updateClientOffline(producerVo);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute -> clientService.updateClientOffline -> {} -> Exception:{}", producerVo, e.getMessage());
			}
		}
	}
}
