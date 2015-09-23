package org.fl.noodlenotify.monitor.performance.schedule;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.util.PatternMatchUtils;

import org.fl.noodlenotify.monitor.performance.executer.service.PerformanceExecuterService;

public class ExecuterSchedule implements
		InstantiationAwareBeanPostProcessor {

	private final static Logger logger = LoggerFactory.getLogger(ExecuterSchedule.class);
			
	private List<String> mappedNameList;
	
	private int corePoolSize = 1;
	
	private ScheduledExecutorService scheduledExecutorService;
	
	private long delay = 1;

	public void start() throws Exception {
		scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize);
	}
	
	public void destroy() throws Exception {
		scheduledExecutorService.shutdown();
	}
	
	@Override
	public Object postProcessBeforeInitialization(Object bean, final String beanName)
			throws BeansException {
		
		if (this.mappedNameList != null) {
			for (String mappedName : mappedNameList) {
				if (PatternMatchUtils.simpleMatch(mappedName, beanName)) {
					try {
						final PerformanceExecuterService performanceExecuterService = (PerformanceExecuterService) bean;
						long initialDelay = performanceExecuterService.getInitialDelay();
						long delay = performanceExecuterService.getDelay() > 0 ? performanceExecuterService.getDelay() : this.delay;
						if (initialDelay == 0) {
							initialDelay = Math.round(Math.random() * delay);
						}
						scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
							@Override
							public void run() {
								try {
									performanceExecuterService.send();
								} catch (Exception e) {
									if (logger.isErrorEnabled()) {
										logger.error("PostProcessBeforeInitialization -> " 
														+ "BeanName: " + beanName
														+ ", StatusExecuterService Execute Exception -> " + e);
									}
								}
							}
						}, initialDelay, delay, TimeUnit.SECONDS);
						if (logger.isDebugEnabled()) {
							logger.debug("PostProcessBeforeInitialization -> " 
											+ "BeanName: " + beanName
											+ ", InitialDelay: " + initialDelay
											+ ", Delay: " + delay
											+ ", Executer Scheduled");
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("PostProcessBeforeInitialization -> " 
											+ "BeanName: " + beanName
											+ ", Not Match Runnable Type -> " + e);
						}
					}
				}
			}
		}
		
		return bean;
	}
	
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName)
			throws BeansException {
		return null;
	}
	
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		return true;
	}

	@Override
	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {
		return pvs;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	
	public void setMappedNameList(List<String> mappedNameList) {
		this.mappedNameList = mappedNameList;
	}
	
	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}
}
