package org.fl.noodlenotify.monitor.performance.receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.fl.noodle.common.util.json.JsonTranslator;
import org.fl.noodlenotify.monitor.performance.net.UdpServer;
import org.fl.noodlenotify.monitor.performance.net.vo.NetVo;
import org.fl.noodlenotify.monitor.performance.persistence.RedisPersistenceTemplate;
import org.fl.noodlenotify.monitor.performance.storage.vo.KeyVo;

public class ExecuterReceiver {
	
	private final static Logger logger = LoggerFactory.getLogger(ExecuterReceiver.class);
	
	@Autowired
	RedisPersistenceTemplate redisPersistenceTemplate;
	
	@Autowired
	private UdpServer udpServer;
	
	private ExecutorService executorServiceDeal;
	private volatile boolean stopSign = false;
	private CountDownLatch stopCountDownLatch;
	
	private int cacheSize = 100;
	private int threadCountDeal = 1;
	
	public void start() {
		
		stopCountDownLatch = new CountDownLatch(threadCountDeal);
		
		executorServiceDeal = Executors.newFixedThreadPool(threadCountDeal);
		
		for (int i=0; i<threadCountDeal; i++) {
			executorServiceDeal.execute(new Runnable() {
				@Override
				public void run() {
					List<String> recvStrList = new ArrayList<String>(cacheSize);
					while (true) {
						String recvStr = udpServer.get();
						if (recvStr != null) {
							recvStrList.add(recvStr);
							if (recvStrList.size() == cacheSize) {
								save(recvStrList);
							}
						} else {
							if (recvStrList.size() > 0) {
								save(recvStrList);
							}
						}
						if (stopSign) {
							if (recvStrList.size() > 0) {
								save(recvStrList);
							}
							stopCountDownLatch.countDown();
							break;
						}
					}
				}
			});
		}
	}
	
	public void destroy() throws Exception {
		stopSign = true;
		stopCountDownLatch.await();
		executorServiceDeal.shutdown();
	}
	
	private void save(final List<String> recvStrList) {
		
		for (String recvStr : recvStrList) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug(recvStr);
				}
				NetVo netVo = JsonTranslator.fromString(recvStr, NetVo.class);
				KeyVo keyVo = netVo.getKeyVo();
				Object bodyVo = netVo.getBodyVo();
				redisPersistenceTemplate.insert(keyVo.toKeyString(), keyVo.getTimestamp(), bodyVo);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Save -> " + e);
				}
			}
		}
		recvStrList.clear();
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
	
	public void setThreadCountDeal(int threadCountDeal) {
		this.threadCountDeal = threadCountDeal;
	}
}
