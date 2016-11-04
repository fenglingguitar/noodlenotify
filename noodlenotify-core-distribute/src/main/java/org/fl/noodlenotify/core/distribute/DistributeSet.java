package org.fl.noodlenotify.core.distribute;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.ConnectAgent;
import org.fl.noodlenotify.core.connect.ConnectManager;
import org.fl.noodlenotify.core.connect.QueueAgent;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.exception.ConnectionUnableException;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.distribute.locker.DistributeSetLocker;
import org.fl.noodlenotify.core.distribute.locker.db.DbDistributeSetLocker;
import org.fl.noodlenotify.core.domain.message.MessageDm;


public class DistributeSet {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributeSet.class);

	private String queueName;
	
	private long moduleId;
	
	private org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private long dbId;
	
	private DistributeConfParam distributeConfParam;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;
	
	private CountDownLatch stopCountDownLatch;
	private AtomicInteger  stopCountDownLatchCount;
	
	private AtomicLong middleIdFresh = new AtomicLong(0);
	
	private DistributeSetLocker dbDistributeSetLocker;
	
	private QueueDistributerVo queueDistributerVo;

	public DistributeSet(String queueName,
							long moduleId,
							org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager,
							ConnectManager queueCacheConnectManager,
							DistributeConfParam distributeConfParam,
							QueueDistributerVo queueDistributerVo,
							long dbId) {
		this.queueName = queueName;
		this.moduleId = moduleId;
		this.dbConnectManager = dbConnectManager;
		this.queueCacheConnectManager = queueCacheConnectManager;
		this.distributeConfParam = distributeConfParam;
		this.queueDistributerVo = queueDistributerVo;
		this.dbId = dbId;
	}
	
	public void start() {
		
		org.fl.noodle.common.connect.agent.ConnectAgent connectAgent = dbConnectManager.getConnectAgent(dbId);
		if(connectAgent != null) {
			
			DbConnectAgent dbConnectAgent = (DbConnectAgent) connectAgent;
			
			try {
				middleIdFresh.set(dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time()));
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Start -> " + "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Fresh Get Max ID -> " + e);
				}
			}
			
			stopCountDownLatch = new CountDownLatch(4);
			if (logger.isDebugEnabled()) {
				stopCountDownLatchCount = new AtomicInteger(4);
				logger.debug("Start -> New StopCountDownLatchCount -> " 
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.get()
						);
			}
			
			dbDistributeSetLocker = new DbDistributeSetLocker(queueName, moduleId, dbConnectManager, dbId);
			dbDistributeSetLocker.start();
			if (logger.isDebugEnabled()) {
				logger.debug("UpdateConnectAgent -> Start DB DistributeSet Locker -> " 
						+ "QueueName: " + queueName 
						+ ", DB: " + dbId 
						);
			}
			
			Thread distributeSetFreshThread = new Thread(new DistributeSetFreshRunnable());
			distributeSetFreshThread.setPriority(distributeConfParam.getSetThreadPriorityFresh());
			executorService.execute(distributeSetFreshThread);
			
			Thread distributeSetNewThread = new Thread(new DistributeSetNewRunnable());
			distributeSetNewThread.setPriority(distributeConfParam.getSetThreadPriorityNew());
			executorService.execute(distributeSetNewThread);
			
			Thread distributeSetPortionThread = new Thread(new DistributeSetPortionRunnable());
			distributeSetPortionThread.setPriority(distributeConfParam.getSetThreadPriorityPortion());
			executorService.execute(distributeSetPortionThread);
			
			Thread distributeSetDeleteTimeoutThread = new Thread(new DistributeSetDeleteTimeoutRunnable());
			distributeSetDeleteTimeoutThread.setPriority(distributeConfParam.getSetThreadPriorityDeleteTimeout());
			executorService.execute(distributeSetDeleteTimeoutThread);
		}
	}
	
	public void destroy() {
		
		stopSign = true;
		
		if (dbDistributeSetLocker.getStatus() == false) {
			
			if (dbDistributeSetLocker != null) {
				dbDistributeSetLocker.destroy();
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Destroy DB DistributeSet Locker -> " 
							+ "QueueName: " + queueName 
							+ ", DB: " + dbId 
							);
				}
			}
			
			notifySleep();
			
			if (stopCountDownLatch != null) {
				try {
					stopCountDownLatch.await();
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Destroy -> "
								+ "Queue: " + queueName 
								+ ", CountDownLatch Await -> " + e);
					}
				}
			}
			
		} else {
			
			notifySleep();
			
			if (stopCountDownLatch != null) {
				try {
					stopCountDownLatch.await();
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Destroy -> "
								+ "Queue: " + queueName 
								+ ", CountDownLatch Await -> " + e);
					}
				}
			}
			
			if (dbDistributeSetLocker != null) {
				dbDistributeSetLocker.destroy();
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Destroy DB DistributeSet Locker -> " 
							+ "QueueName: " + queueName 
							+ ", DB: " + dbId 
							);
				}
			}
		}
	}
	
	private class DistributeSetFreshRunnable implements Runnable {
		
		@Override
		public void run() {
			
			int skipCount = 0;
			
			while (true) {
				
				boolean isNewLocker = dbDistributeSetLocker.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				org.fl.noodle.common.connect.agent.ConnectAgent connectAgent = dbConnectManager.getConnectAgent(dbId);
				if(connectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetFreshRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + dbId
								+ ", Get DB ConnectAgent -> NULL");
					}
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Get DB ConnectAgent Sleep -> " + e);
						}
					}
					continue;
				}
				
				DbConnectAgent dbConnectAgent = (DbConnectAgent) connectAgent;
				
				long min = 0;
				long max = 0;
				
				if (isNewLocker) {
					try {
						middleIdFresh.set(dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time()));
						notifySleep();
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Fresh Get Max ID -> " + e);
						}
					}
				}
				
				try {
					min = middleIdFresh.get() > 0 ? middleIdFresh.get() + 1 : dbConnectAgent.minId(queueName);
					max = dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time());
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetFreshRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + connectAgent.getConnectId()
								+ ", Get Min Adn Max ID -> " + e);
					}
					continue;
				}
				
				if (max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalFresh());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", (max == 0 || min > max) Select Time Interval -> " + e);
						}
					}
					continue;
				}
				
				long len = 0;
				long count = 0;
				AtomicLong countDown = new AtomicLong(0);
				long logMin = min;
				long logMax = max;
				
				boolean skip = false;
				
				while (min <= max) {
					
					if (stopSign) {
						break;
					}
					
					long start = min;
					long end = min + distributeConfParam.getSelectByIdIntervalFresh();
					end = end <= max ? end : max;
					
					List<MessageDm> messageDmList = null;
					
					len = queueCacheLen("DistributeSetFreshRunnable", queueCacheConnectManager, true);
					if (len >= 0 && len < distributeConfParam.getQueueCacheCapacityNew()) {
						long expect = end - start + 1;
						if (skipCount > 0) {
							long completeCount = 0;
							try {
								completeCount = dbConnectAgent.selectCount(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetFreshRunnable -> "
											+ "Queue: " + queueName 
											+ ", DB: " + connectAgent.getConnectId()
											+ ", Select Massage -> " + e);
								}
								break;
							}
							if (completeCount < expect) {
								if (skipCount <= 20) {
									middleIdFresh.set(min - 1); 
									skipCount++;
									skip = true;
									break;
								} 
							}
						}
						try {
							messageDmList = dbConnectAgent.select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetFreshRunnable -> "
										+ "Queue: " + queueName 
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Select Massage -> " + e);
							}
							break;
						}
						if (messageDmList.size() < expect) {
							if (skipCount <= 20) {
								middleIdFresh.set(min - 1); 
								skipCount++;
								skip = true;
								break;
							} else {
								skipCount = 0;
							}
						} else {
							skipCount = 0;
						}
						count += messageDmList.size();
						countDown.addAndGet(messageDmList.size());
						if (messageDmList != null && messageDmList.size() > 0) {
							CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
							for (MessageDm messageDm : messageDmList) {
								messageDm.setObjectOne(countDownLatch);
								messageDm.setObjectTwo(dbDistributeSetLocker);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(true);
							}
							push("DistributeSetFreshRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetFreshRunnable -> "
											+ "Queue: " + queueName
											+ ", DB: " + connectAgent.getConnectId()
											+ ", Push Massage CountDownLatch Wait -> " + e);
								}
							}
							messageDmList.clear();
						}
						middleIdFresh.set(end); 
					} else {
						break;
					}
					
					min += distributeConfParam.getSelectByIdIntervalFresh() + 1;					
				} 
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetFreshRunnable -> " 
							+ "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Get Min Adn Max ID -> "
							+ "Min: " + logMin
							+ ", Max: " + logMax
							+ ", Region: " + (logMax - logMin + 1)
							+ ", Count: " + count
							+ ", CountDown Subtract: " + (count - countDown.get())
							);
				}
				
				if(skip == true) {
					try {
						startSleep(distributeConfParam.getSelectTimeIntervalFresh());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Select Time Interval -> " + e);
						}
					}
					continue;
				}
				
				if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
					try {
						startSleep(distributeConfParam.getSelectLenTimeIntervalFresh());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Len Select Time Interval -> " + e);
						}
					}
				} else {
					try {
						startSleep(distributeConfParam.getSelectTimeIntervalFresh());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetFreshRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Select Time Interval -> " + e);
						}
					}
				}
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug("DistributeSetFreshRunnable -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private class DistributeSetNewRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				boolean isNewLocker = dbDistributeSetLocker.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				org.fl.noodle.common.connect.agent.ConnectAgent connectAgent = dbConnectManager.getConnectAgent(dbId);
				if(connectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetNewRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + dbId
								+ ", Get DB ConnectAgent -> NULL");
					}
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Get DB ConnectAgent Sleep -> " + e);
						}
					}
					continue;
				}
				
				DbConnectAgent dbConnectAgent = (DbConnectAgent) connectAgent;
				
				long min = 0;
				long max = 0;
				
				if (isNewLocker) {
					try {
						startSleep(30000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", IsNewLocker Sleep -> " + e);
						}
					}
				}
				
				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_NEW);
					max = middleIdFresh.get();
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetNewRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + connectAgent.getConnectId()
								+ ", Get Min Adn Max ID -> " + e);
					}
					continue;
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalNew());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", (min == 0 || max == 0 || min > max) Select Time Interval -> " + e);
						}
					}
					continue;
				}
				
				boolean isNewEmpty = true;
				long len = 0;
				long count = 0;
				AtomicLong countDown = new AtomicLong(0);
				long logMin = min;
				long logMax = max;
				
				while (min <= max) {
					
					if (stopSign) {
						break;
					}
					
					long start = min;
					long end = min + distributeConfParam.getSelectByIdIntervalNew();
					end = end <= max ? end : max;
					
					List<MessageDm> messageDmList = null;
					
					len = queueCacheLen("DistributeSetNewRunnable", queueCacheConnectManager, true);
					if (len >= 0 && len < distributeConfParam.getQueueCacheCapacityNew()) {
						try {
							messageDmList = dbConnectAgent.select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetNewRunnable -> "
										+ "Queue: " + queueName 
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Select Massage -> " + e);
							}
							break;
						}
						count += messageDmList.size();
						countDown.addAndGet(messageDmList.size());
						if (messageDmList != null && messageDmList.size() > 0) {
							CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
							for (MessageDm messageDm : messageDmList) {
								messageDm.setObjectOne(countDownLatch);
								messageDm.setObjectTwo(dbDistributeSetLocker);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(true);
							}
							push("DistributeSetNewRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetNewRunnable -> "
											+ "Queue: " + queueName
											+ ", DB: " + connectAgent.getConnectId()
											+ ", Push Massage CountDownLatch Wait -> " + e);
								}
							}
							messageDmList.clear();
							isNewEmpty = false;
						}
					} else {
						break;
					}

					min += distributeConfParam.getSelectByIdIntervalNew() + 1;
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetNewRunnable -> " 
							+ "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Get Min Adn Max ID -> "
							+ "Min: " + logMin
							+ ", Max: " + logMax
							+ ", Region: " + (logMax - logMin + 1)
							+ ", Count: " + count
							+ ", CountDown Subtract: " + (count - countDown.get())
							);
				}
				
				if (isNewEmpty) {
					try {
						startSleep(distributeConfParam.getSelectEmptyTimeIntervalNew());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", New Empty -> Select Time Interval -> " + e);
						}
					}
				} else {
					if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
						long sleepTime = 
								distributeConfParam.getSelectLenTimeIntervalNew()
									+ Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectLenTimeIntervalRatioNew());
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Len Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}		
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetNewRunnable -> " 
										+ "Queue: " + queueName
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Len Select Time Interval -> " + e);
							}
						}
					} else {
						long sleepTime = 
								Math.round(1.0f * countDown.get() / count * 
										(Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectCountDownTimeIntervalRatioNew()) 
											+ distributeConfParam.getSelectCountDownTimeIntervalNew()))
												+ distributeConfParam.getSelectCountDownTimeIntervalMinNew();
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", CountDown Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetNewRunnable -> " 
										+ "Queue: " + queueName
										+ ", DB: " + connectAgent.getConnectId()
										+ ", CountDown Select Time Interval -> " + e);
							}
						}
					}
				}				
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug("DistributeSetNewRunnable -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private class DistributeSetPortionRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				boolean isNewLocker = dbDistributeSetLocker.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				org.fl.noodle.common.connect.agent.ConnectAgent connectAgent = dbConnectManager.getConnectAgent(dbId);
				if(connectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetNewRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + dbId
								+ ", Get DB ConnectAgent -> NULL");
					}
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Get DB ConnectAgent Sleep -> " + e);
						}
					}
					continue;
				}
				
				DbConnectAgent dbConnectAgent = (DbConnectAgent) connectAgent;
				
				long min = 0;
				long max = 0;
				
				if (isNewLocker) {
					try {
						startSleep(30000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", IsNewLocker Sleep -> " + e);
						}
					}
				}
				
				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_PORTION);
					max = middleIdFresh.get();
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetPortionRunnable -> " 
								+ "Queue: " + queueName
								+ ", DB: " + connectAgent.getConnectId()
								+ ", Get Min Adn Max ID -> " + e);
					}
					continue;
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalPortion());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetPortionRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", (min == 0 || max == 0 || min > max) Select Time Interval -> " + e);
						}
					}
					continue;
				}
												
				boolean isPortionEmpty = true;
				long len = 0;
				long count = 0;
				AtomicLong countDown = new AtomicLong(0);
				long logMin = min;
				long logMax = max;
				
				while (min <= max) {
					
					if (stopSign) {
						break;
					}
					
					long start = min;
					long end = min + distributeConfParam.getSelectByIdIntervalPortion();
					end = end <= max ? end : max;
					
					List<MessageDm> messageDmList = null;
					
					len = queueCacheLen("DistributeSetPortionRunnable", queueCacheConnectManager, false);
					if (len >= 0 && len < distributeConfParam.getQueueCacheCapacityPortion()) {
						try {
							messageDmList = dbConnectAgent.selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_PORTION, System.currentTimeMillis() - queueDistributerVo.getInterval_Time());
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetPortionRunnable -> "
										+ "Queue: " + queueName 
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Select Massage -> " + e);
							}
							break;
						}
						count += messageDmList.size();
						countDown.addAndGet(messageDmList.size());
						if (messageDmList != null && messageDmList.size() > 0) {
							CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
							for (MessageDm messageDm : messageDmList) {
								messageDm.setObjectOne(countDownLatch);
								messageDm.setObjectTwo(dbDistributeSetLocker);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(false);
							}
							push("DistributeSetPortionRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetPortionRunnable -> Portion Massage -> "
											+ "Queue: " + queueName
											+ ", DB: " + connectAgent.getConnectId()
											+ ", Push Massage CountDownLatch Wait -> " + e);
								}
							}
							messageDmList.clear();
							isPortionEmpty = false;
						}
					} else {
						break;
					}
					
					min += distributeConfParam.getSelectByIdIntervalPortion() + 1;					
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetPortionRunnable -> " 
							+ "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Get Min Adn Max ID -> "
							+ "Min: " + logMin
							+ ", Max: " + logMax
							+ ", Region: " + (logMax - logMin + 1)
							+ ", Count: " + count
							+ ", CountDown Subtract: " + (count - countDown.get())
							);
				}
				
				if (isPortionEmpty) {
					try {
						startSleep(distributeConfParam.getSelectEmptyTimeIntervalPortion());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetPortionRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Portion Empty Select Time Interval -> " + e);
						}
					}
				} else {
					if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
						long sleepTime = 
								distributeConfParam.getSelectLenTimeIntervalPortion()
									+ Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectLenTimeIntervalRatioPortion());
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetPortionRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Len Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}		
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetPortionRunnable -> " 
										+ "Queue: " + queueName
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Len Select Time Interval -> " + e);
							}
						}
					} else {
						long timeIntervalMin = distributeConfParam.getSelectCountDownTimeIntervalMinPortion();
						if (queueDistributerVo.getInterval_Time() < distributeConfParam.getSelectCountDownTimeIntervalMinPortion()) {
							timeIntervalMin = queueDistributerVo.getInterval_Time();
						}
						long sleepTime = 
								Math.round(1.0f * countDown.get() / count * 
										(Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectCountDownTimeIntervalRatioPortion()) 
											+ distributeConfParam.getSelectCountDownTimeIntervalPortion()))
												+ timeIntervalMin;
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetPortionRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", CountDown Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetPortionRunnable -> " 
										+ "Queue: " + queueName
										+ ", DB: " + connectAgent.getConnectId()
										+ ", CountDown Select Time Interval -> " + e);
							}
						}
					}
				}				
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug("DistributeSetPortionRunnable -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private class DistributeSetDeleteTimeoutRunnable implements Runnable {
		
		@Override
		public void run() {
			
			while (true) {
				
				boolean isNewLocker = dbDistributeSetLocker.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				org.fl.noodle.common.connect.agent.ConnectAgent connectAgent = dbConnectManager.getConnectAgent(dbId);
				if(connectAgent == null) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetDeleteTimeoutRunnable -> Get DB ConnectAgent -> " 
								+ "Queue: " + queueName
								+ ", DB: " + dbId
								+ ", Exception -> Get DB ConnectAgent Return Null");
					}
					try {
						startSleep(1000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> Get DB ConnectAgent -> StartSleep -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Exception -> " + e);
						}
					}
					continue;
				}
				
				DbConnectAgent dbConnectAgent = (DbConnectAgent) connectAgent;
				
				long min = 0;
				long max = 0;
				
				if (isNewLocker) {
					try {
						startSleep(30000);
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetNewRunnable -> IsNewLocker -> StartSleep -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Exception -> " + e);
						}
					}
				}

				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_FINISH);
					max = middleIdFresh.get();
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetDeleteTimeoutRunnable -> Get Min And Max ID -> " 
								+ "Queue: " + queueName
								+ ", DB: " + connectAgent.getConnectId()
								+ ", Exception -> " + e);
					}
					continue;
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetDeleteTimeoutRunnable -> Get Min And Max ID -> " 
							+ "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Min: " + min
							+ ", Max: " + max
							);
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalDeleteTimeout());
					} catch (InterruptedException e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetDeleteTimeoutRunnable -> (Min == 0 || Max == 0 || Min > Max) -> StartSleep -> " 
									+ "Queue: " + queueName
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Exception -> " + e);
						}
					}
					continue;
				}
				
				long logCount = 0;
				long logMin = min;
				long logMax = max;
				
				while (min <= max) {
					
					if (stopSign) {
						break;
					}
					
					long start = min;
					long end = min + distributeConfParam.getSelectByIdIntervalDeleteTimeout();
					end = end <= max ? end : max;
					
					List<MessageDm> messageDmList = null;
					
					try {
						messageDmList = dbConnectAgent.selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - distributeConfParam.getSelectDeleteTimeout());
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("DistributeSetDeleteTimeoutRunnable -> DB Select Timeout Finish Massage -> "
									+ "Queue: " + queueName 
									+ ", DB: " + connectAgent.getConnectId()
									+ ", Exception -> " + e);
						}
						break;
					}
					
					logCount += messageDmList.size();
					
					if (messageDmList != null && messageDmList.size() > 0) {
						QueueAgent queueCacheQueueAgent = queueCacheConnectManager.getQueueAgent(queueName);
						if (queueCacheQueueAgent != null) {
							List<ConnectAgent> queueCacheConnectAgentList = queueCacheQueueAgent.getConnectAgentAll();
							if (queueCacheConnectAgentList.size() > 0) {
								for (MessageDm messageDm : messageDmList) {
									for (ConnectAgent queueCacheConnectAgentIt : queueCacheConnectAgentList) {
										QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) queueCacheConnectAgentIt;
										try {
											queueCacheConnectAgent.removePop(messageDm);
										} catch (ConnectionUnableException e) {
											queueCacheConnectManager.startUpdateConnectAgent();
											if (logger.isErrorEnabled()) {
												logger.error("DistributeSetDeleteTimeoutRunnable -> Queue Cache Remove Pop -> "
														+ "Queue: " + queueName
														+ ", UUID: " + messageDm.getUuid()
														+ ", QueueCache: " + connectAgent.getConnectId()
														+ ", Exception -> " + e);
											}
										} catch (Exception e) {
											if (logger.isErrorEnabled()) {
												logger.error("DistributeSetDeleteTimeoutRunnable -> Queue Cache Remove Pop -> "
														+ "Queue: " + queueName
														+ ", UUID: " + messageDm.getUuid()
														+ ", QueueCache: " + connectAgent.getConnectId()
														+ ", Exception -> " + e);
											}
										}
									}
								}
							} else {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetDeleteTimeoutRunnable -> Queue Cache Remove Pop -> "
											+ "QUEUE: " + queueName 
											+ ", Exception -> Get Connect Agent List Is Empty");
								}
							}
						} else {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetDeleteTimeoutRunnable -> Queue Cache Remove Pop -> "
										+ "QUEUE: " + queueName 
										+ ", Exception -> Get Queue Agent Return Null");
							}
						}
						
						CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
						for (MessageDm messageDm : messageDmList) {
							messageDm.setObjectOne(countDownLatch);
						}
						
						for (MessageDm messageDm : messageDmList) {
							try {
								dbConnectAgent.delete(messageDm);
							} catch (Exception e) {
								if (logger.isErrorEnabled()) {
									logger.error("DistributeSetDeleteTimeoutRunnable -> DB Delete Finish Message -> "
											+ "Queue: " + queueName
											+ ", UUID: " + messageDm.getUuid()
											+ ", DB: " + ((ConnectAgent)dbConnectAgent).getConnectId()
											+ ", Exception -> " + e);
								}
							}
						}
						
						try {
							countDownLatch.await();
						} catch (InterruptedException e) {
							if (logger.isErrorEnabled()) {
								logger.error("DistributeSetDeleteTimeoutRunnable -> CountDownLatch Await -> "
										+ "Queue: " + queueName
										+ ", DB: " + connectAgent.getConnectId()
										+ ", Exception -> " + e);
							}
						}
						
						messageDmList.clear();
					}
					
					min += distributeConfParam.getSelectByIdIntervalDeleteTimeout() + 1;
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetDeleteTimeoutRunnable -> Actual Operation Delete -> " 
							+ "Queue: " + queueName
							+ ", DB: " + connectAgent.getConnectId()
							+ ", Min: " + logMin
							+ ", Max: " + logMax
							+ ", Region: " + (logMax - logMin + 1)
							+ ", Count: " + logCount
							);
				}
				
				try {
					startSleep(distributeConfParam.getSelectEmptyTimeIntervalDeleteTimeout());
				} catch (InterruptedException e) {
					if (logger.isErrorEnabled()) {
						logger.error("DistributeSetDeleteTimeoutRunnable -> DB Select Timeout Finish Massage -> StartSleep -> " 
								+ "Queue: " + queueName
								+ ", DB: " + connectAgent.getConnectId()
								+ ", Exception -> " + e);
					}
				}
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug("DistributeSet -> DistributeSetDeleteTimeoutRunnable -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private void push(String queueCacheName,
			ConnectManager queueCacheConnectManager,
			List<MessageDm> messageDmList) {
		
		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) queueAgent.getConnectAgent();
			if (queueCacheConnectAgent != null) {
				for (MessageDm messageDm : messageDmList) {
					try {
						queueCacheConnectAgent.push(messageDm);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error(queueCacheName + " -> Push -> "
									+ "QUEUE: " + queueName
									+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
									+ ", Push Massage -> " + e);
						}
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> Push -> "
							+ "QUEUE: " + queueName
							+ ", Push Massage -> Get Redis Connect Agent -> Null");
				}
				cancelCountDownLatchList(messageDmList);
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(queueCacheName + " -> Push -> "
						+ "QUEUE: " + queueName 
						+ ", Push Massage -> Get Queue Agent -> Null");
			}
			cancelCountDownLatchList(messageDmList);
		}
	}
	
	private long queueCacheLen(String queueCacheName, 
			ConnectManager queueCacheConnectManager,
			boolean queueType) {
		
		long len = -1;
		QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
		if (queueAgent != null) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) queueAgent.getConnectAgent();
			if (queueCacheConnectAgent != null) {
				try {
					len = queueCacheConnectAgent.len(queueName, queueType);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error(queueCacheName + " -> GetLen -> "
								+ "QUEUE: " + queueName
								+ ", Connect: " + ((ConnectAgent)queueCacheConnectAgent).getConnectId()
								+ ", Get Len -> " + e);
					}
				}
			} else {
				if (logger.isErrorEnabled()) {
					logger.error(queueCacheName + " -> GetLen -> "
							+ "QUEUE: " + queueName
							+ ", Get Len -> Get Redis Connect Agent -> Null");
				}
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(queueCacheName + " -> GetLen -> "
						+ "QUEUE: " + queueName 
						+ ", Get Len -> Get Queue Agent -> Null");
			}
		}
		return len;
	}
	
	private void cancelCountDownLatchList(List<MessageDm> messageDmList) {
		for (MessageDm messageDm : messageDmList) {
			cancelCountDownLatch(messageDm);
		}
	}
	
	private void cancelCountDownLatch(MessageDm messageDm) {
		if (messageDm.getObjectOne() != null) {					
			CountDownLatch countDownLatch = (CountDownLatch) messageDm.getObjectOne();
			countDownLatch.countDown();
		}
	}
	
	private synchronized void startSleep(long suspendTime) throws InterruptedException {
		if (!stopSign && suspendTime > 0) {
			wait(suspendTime);
		}
	}
	
	private synchronized void notifySleep() {
		notifyAll();
	}
	
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	
	public void setDbConnectManager(org.fl.noodle.common.connect.manager.ConnectManager dbConnectManager) {
		this.dbConnectManager = dbConnectManager;
	}
	
	public void setQueueCacheConnectManagerNew(ConnectManager queueCacheConnectManager) {
		this.queueCacheConnectManager = queueCacheConnectManager;
	}
	
	public void setQueueDistributerVo(QueueDistributerVo queueDistributerVo) {
		this.queueDistributerVo = queueDistributerVo;
	}

	public void setDbId(long dbId) {
		this.dbId = dbId;
	}
}
