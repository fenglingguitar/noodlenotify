package org.fl.noodlenotify.core.distribute;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.fl.noodle.common.connect.aop.ConnectThreadLocalStorage;
import org.fl.noodle.common.connect.cluster.ConnectCluster;
import org.fl.noodle.common.connect.manager.ConnectManager;
import org.fl.noodle.common.distributedlock.db.DbDistributedLock;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.core.connect.aop.LocalStorageType;
import org.fl.noodlenotify.core.connect.cache.queue.QueueCacheConnectAgent;
import org.fl.noodlenotify.core.connect.db.DbConnectAgent;
import org.fl.noodlenotify.core.connect.db.mysql.MysqlDbConnectAgent;
import org.fl.noodlenotify.core.constant.message.MessageConstant;
import org.fl.noodlenotify.core.distribute.callback.RemovePopMessageCallback;
import org.fl.noodlenotify.core.domain.message.MessageDm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DistributePull {
	
	private final static Logger logger = LoggerFactory.getLogger(DistributePull.class);

	private String queueName;
	
	private long moduleId;
	
	private ConnectManager dbConnectManager;
	private ConnectManager queueCacheConnectManager;
	private long dbId;
	
	private DistributeConfParam distributeConfParam;
	
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	private volatile boolean stopSign = false;
	
	private CountDownLatch stopCountDownLatch;
	private AtomicInteger  stopCountDownLatchCount;
	
	private AtomicLong middleIdFresh = new AtomicLong(0);
	
	private DbDistributedLock dbDistributedLock;
	
	private QueueDistributerVo queueDistributerVo;

	public DistributePull(String queueName,
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
		
		ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
		DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
		
		ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
		try {
			middleIdFresh.set(dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time()));
			
			stopCountDownLatch = new CountDownLatch(4);
			
			dbDistributedLock = new DbDistributedLock();
			dbDistributedLock.setJdbcTemplate(((MysqlDbConnectAgent)dbConnectManager.getConnectAgent(dbId)).getJdbcTemplate());
			dbDistributedLock.setLockId(moduleId);
			dbDistributedLock.setTableName("MSG_" + queueName + "_LK");
			try {
				dbDistributedLock.start();
			} catch (Exception e) {
				e.printStackTrace();
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
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
		}
	}
	
	public void destroy() {
		
		stopSign = true;
		
		if (dbDistributedLock.getStatus() == false) {
			
			if (dbDistributedLock != null) {
				dbDistributedLock.destroy();
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Destroy DB DistributePull Locker -> " 
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
			
			if (dbDistributedLock != null) {
				dbDistributedLock.destroy();
				if (logger.isDebugEnabled()) {
					logger.debug("UpdateConnectAgent -> Destroy DB DistributePull Locker -> " 
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
				
				boolean isNewLocker = dbDistributedLock.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
				DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
				
				long min = 0;
				long max = 0;
				
				if (isNewLocker) {
					ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
					try {
						middleIdFresh.set(dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time()));
						notifySleep();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
					}
				}
				
				ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
				try {
					min = middleIdFresh.get() > 0 ? middleIdFresh.get() + 1 : dbConnectAgent.minId(queueName);
					max = dbConnectAgent.maxIdDelay(queueName, System.currentTimeMillis() - queueDistributerVo.getDph_Delay_Time());
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
				}
				
				if (max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalFresh());
					} catch (InterruptedException e) {
						e.printStackTrace();
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
							ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
							try {
								completeCount = dbConnectAgent.selectCount(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
							} catch (Exception e) {
								e.printStackTrace();
								break;
							} finally {
								ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
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
						
						ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
						try {
							messageDmList = dbConnectAgent.select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						} catch (Exception e) {
							e.printStackTrace();
							break;
						} finally {
							ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
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
								messageDm.setObjectTwo(dbDistributedLock);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(true);
							}
							push("DistributeSetFreshRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
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
							+ ", DB: " + dbId
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
						e.printStackTrace();
					}
					continue;
				}
				
				if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
					try {
						startSleep(distributeConfParam.getSelectLenTimeIntervalFresh());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						startSleep(distributeConfParam.getSelectTimeIntervalFresh());
					} catch (InterruptedException e) {
						e.printStackTrace();
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
				
				boolean isNewLocker = dbDistributedLock.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
				DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
				
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
				
				ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_NEW);
					max = middleIdFresh.get();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalNew());
					} catch (InterruptedException e) {
						e.printStackTrace();
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
						ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
						try {
							messageDmList = dbConnectAgent.select(queueName, start, end, MessageConstant.MESSAGE_STATUS_NEW);
						} catch (Exception e) {
							e.printStackTrace();
							break;
						} finally {
							ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
						}
						
						count += messageDmList.size();
						countDown.addAndGet(messageDmList.size());
						if (messageDmList != null && messageDmList.size() > 0) {
							CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
							for (MessageDm messageDm : messageDmList) {
								messageDm.setObjectOne(countDownLatch);
								messageDm.setObjectTwo(dbDistributedLock);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(true);
							}
							push("DistributeSetNewRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
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
							+ ", DB: " + dbId
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
						e.printStackTrace();
					}
				} else {
					if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
						long sleepTime = 
								distributeConfParam.getSelectLenTimeIntervalNew()
									+ Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectLenTimeIntervalRatioNew());
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetNewRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Len Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}		
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
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
									+ ", DB: " + dbId
									+ ", CountDown Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
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
				
				boolean isNewLocker = dbDistributedLock.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
				DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
				
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
				
				ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_PORTION);
					max = middleIdFresh.get();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalPortion());
					} catch (InterruptedException e) {
						e.printStackTrace();
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
						ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
						try {
							messageDmList = dbConnectAgent.selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_PORTION, System.currentTimeMillis() - queueDistributerVo.getInterval_Time());
						} catch (Exception e) {
							e.printStackTrace();
							break;
						} finally {
							ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
						}
						
						count += messageDmList.size();
						countDown.addAndGet(messageDmList.size());
						if (messageDmList != null && messageDmList.size() > 0) {
							CountDownLatch countDownLatch = new CountDownLatch(messageDmList.size());
							for (MessageDm messageDm : messageDmList) {
								messageDm.setObjectOne(countDownLatch);
								messageDm.setObjectTwo(dbDistributedLock);
								messageDm.setObjectThree(countDown);
								messageDm.setBool(false);
							}
							push("DistributeSetPortionRunnable", queueCacheConnectManager, messageDmList);
							try {
								countDownLatch.await();
							} catch (InterruptedException e) {
								e.printStackTrace();
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
							+ ", DB: " + dbId
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
						e.printStackTrace();
					}
				} else {
					if (len >= distributeConfParam.getQueueCacheCapacityNew()) {
						long sleepTime = 
								distributeConfParam.getSelectLenTimeIntervalPortion()
									+ Math.round(1.0f * countDown.get() * 1000 / distributeConfParam.getSelectLenTimeIntervalRatioPortion());
						if (logger.isDebugEnabled()) {
							logger.debug("DistributeSetPortionRunnable -> " 
									+ "Queue: " + queueName
									+ ", DB: " + dbId
									+ ", Len Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}		
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
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
									+ ", DB: " + dbId
									+ ", CountDown Sleep -> "
									+ "SleepTime: " + sleepTime
									);
						}
						try {
							startSleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
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
				
				boolean isNewLocker = dbDistributedLock.waitLocker();
				
				if (stopSign) {
					break;
				}
				
				ConnectCluster dbConnectCluster = dbConnectManager.getConnectCluster("ID");
				DbConnectAgent dbConnectAgent = (DbConnectAgent) dbConnectCluster.getProxy();
				
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
				
				ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
				try {
					min = dbConnectAgent.minIdByStatus(queueName, MessageConstant.MESSAGE_STATUS_FINISH);
					max = middleIdFresh.get();
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} finally {
					ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetDeleteTimeoutRunnable -> Get Min And Max ID -> " 
							+ "Queue: " + queueName
							+ ", DB: " + dbId
							+ ", Min: " + min
							+ ", Max: " + max
							);
				}
				
				if (min == 0 || max == 0 || min > max) {
					try {
						startSleep(distributeConfParam.getSelectMinMaxTimeIntervalDeleteTimeout());
					} catch (InterruptedException e) {
						e.printStackTrace();
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
					
					ConnectThreadLocalStorage.put(LocalStorageType.CONNECT_ID.getCode(), dbId);
					try {
						messageDmList = dbConnectAgent.selectTimeout(queueName, start, end, MessageConstant.MESSAGE_STATUS_FINISH, System.currentTimeMillis() - distributeConfParam.getSelectDeleteTimeout());
					} catch (Exception e) {
						e.printStackTrace();
						break;
					} finally {
						ConnectThreadLocalStorage.remove(LocalStorageType.CONNECT_ID.getCode());
					}
					
					logCount += messageDmList.size();
					
					if (messageDmList != null && messageDmList.size() > 0) {
						for (MessageDm messageDm : messageDmList) {
							messageDm.addMessageCallback(new RemovePopMessageCallback(messageDm, queueCacheConnectManager));
							try {
								dbConnectAgent.delete(messageDm);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						messageDmList.clear();
					}
					
					min += distributeConfParam.getSelectByIdIntervalDeleteTimeout() + 1;
				}
				
				if (logger.isDebugEnabled()) {
					logger.debug("DistributeSetDeleteTimeoutRunnable -> Actual Operation Delete -> " 
							+ "Queue: " + queueName
							+ ", DB: " + dbId
							+ ", Min: " + logMin
							+ ", Max: " + logMax
							+ ", Region: " + (logMax - logMin + 1)
							+ ", Count: " + logCount
							);
				}
				
				try {
					startSleep(distributeConfParam.getSelectEmptyTimeIntervalDeleteTimeout());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			stopCountDownLatch.countDown();
			if (logger.isDebugEnabled()) {
				logger.debug("DistributePull -> DistributeSetDeleteTimeoutRunnable -> StopCountDownLatchCount CountDown -> "
						+ "QueueName: " + queueName 
						+ ", StopCountDownLatchCount: " + stopCountDownLatchCount.decrementAndGet()
						);
			}
		}
	}
	
	private void push(String queueCacheName,
			ConnectManager queueCacheConnectManager,
			List<MessageDm> messageDmList) {
		
		ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
		for (MessageDm messageDm : messageDmList) {
			try {
				queueCacheConnectAgent.push(messageDm);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//cancelCountDownLatchList(messageDmList);
		
		/*ConnectNode connectNode = queueCacheConnectManager.getConnectNode(queueName);
		if (connectNode != null) {
			QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectNode.getConnectAgent();
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
		}*/
	}
	
	private long queueCacheLen(String queueCacheName, 
			ConnectManager queueCacheConnectManager,
			boolean queueType) {
		
		long len = -1;
		ConnectCluster connectCluster = queueCacheConnectManager.getConnectCluster("DEFALT");
		QueueCacheConnectAgent queueCacheConnectAgent = (QueueCacheConnectAgent) connectCluster.getProxy();
		try {
			len = queueCacheConnectAgent.len(queueName, queueType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*QueueAgent queueAgent = queueCacheConnectManager.getQueueAgent(queueName);
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
		}*/
		return len;
	}
	
	/*private void cancelCountDownLatchList(List<MessageDm> messageDmList) {
		for (MessageDm messageDm : messageDmList) {
			cancelCountDownLatch(messageDm);
		}
	}*/
	
	/*private void cancelCountDownLatch(MessageDm messageDm) {
		if (messageDm.getObjectOne() != null) {					
			CountDownLatch countDownLatch = (CountDownLatch) messageDm.getObjectOne();
			countDownLatch.countDown();
		}
	}*/
	
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
