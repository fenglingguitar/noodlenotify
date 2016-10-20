/*package org.fl.noodlenotify.console.web.controller.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.fl.noodlenotify.console.constant.ConsoleConstants;
import org.fl.noodlenotify.console.service.DistributerService;
import org.fl.noodlenotify.console.service.ExchangerService;
import org.fl.noodlenotify.console.service.MsgBodyCacheService;
import org.fl.noodlenotify.console.service.MsgQueueCacheService;
import org.fl.noodlenotify.console.service.MsgStorageService;
import org.fl.noodlenotify.console.service.QueueConsumerGroupService;
import org.fl.noodlenotify.console.service.QueueDistributerService;
import org.fl.noodlenotify.console.service.QueueExchangerService;
import org.fl.noodlenotify.console.service.QueueMsgBodyCacheService;
import org.fl.noodlenotify.console.service.QueueMsgQueueCacheService;
import org.fl.noodlenotify.console.service.QueueMsgStorageService;
import org.fl.noodlenotify.console.vo.DistributerVo;
import org.fl.noodlenotify.console.vo.ExchangerVo;
import org.fl.noodlenotify.console.vo.MsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.MsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.MsgStorageVo;
import org.fl.noodlenotify.console.vo.QueueConsumerGroupVo;
import org.fl.noodlenotify.console.vo.QueueDistributerVo;
import org.fl.noodlenotify.console.vo.QueueExchangerVo;
import org.fl.noodlenotify.console.vo.QueueMsgBodyCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgQueueCacheVo;
import org.fl.noodlenotify.console.vo.QueueMsgStorageVo;
import org.fl.noodle.common.mvc.annotation.NoodleResponseBody;
import org.fl.noodlenotify.console.web.vo.TreeVo;

@Controller
@RequestMapping(value = "monitor/tree")
public class TreeController {

	@Autowired
	ExchangerService exchangerService;
	
	@Autowired
	private DistributerService distributerService;
	
	@Autowired
	MsgStorageService msgStorageService;
	
	@Autowired
	private MsgBodyCacheService msgBodyCacheService;

	@Autowired
	private MsgQueueCacheService msgQueueCacheService;
	
	@Autowired
	QueueExchangerService queueExchangerService;
	
	@Autowired
	private QueueDistributerService queueDistributerService;
	
	@Autowired
	private QueueMsgStorageService queueMsgStorageService;
	
	@Autowired
	private QueueMsgQueueCacheService queueMsgQueueCacheService;
	
	@Autowired
	private QueueMsgBodyCacheService queueMsgBodyCacheService;
	
	@Autowired
	private QueueConsumerGroupService queueConsumerGroupService;
	
	@RequestMapping(value = "/queryfirst")
	@NoodleResponseBody
	public List<TreeVo> queryFirst() throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_EXCHANGE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_EXCHANGE);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/queryexchangebyexchange");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DISTRIBUTE);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/querydistributebydistribute");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DB);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DB);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/querydbbydb");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_QUEUECACHE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_QUEUECACHE);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/queryqueuecachebyqueuecache");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_BODYCACHE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_BODYCACHE);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/querybodycachebybodycache");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_CONSUMER);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_CONSUMER);
		treeVo.setPid("ROOT");
		treeVo.setUrl("monitor/tree/queryconsumerbyconsumer");
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryconsumerbyconsumer")
	@NoodleResponseBody
	public List<TreeVo> queryConsumerByConsumer(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DISTRIBUTE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querydistributebyconsumer");
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangebyexchange")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeByExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		ExchangerVo exchangerVoQuery = new ExchangerVo();
		exchangerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ExchangerVo> exchangerVoList = exchangerService.queryExchangerList(exchangerVoQuery);
		for (ExchangerVo exchangerVo : exchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(exchangerVo.getExchanger_Id()));
			treeVo.setLabel(exchangerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebyexchange");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydistributebydistribute")
	@NoodleResponseBody
	public List<TreeVo> queryDistributeByDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		DistributerVo distributerVoQuery = new DistributerVo();
		distributerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoQuery);
		for (DistributerVo distributerVo : distributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(distributerVo.getDistributer_Id()));
			treeVo.setLabel(distributerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebydistribute");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydistributebyconsumer")
	@NoodleResponseBody
	public List<TreeVo> queryDistributeByConsumer(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		DistributerVo distributerVoQuery = new DistributerVo();
		distributerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoQuery);
		for (DistributerVo distributerVo : distributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(distributerVo.getDistributer_Id()));
			treeVo.setLabel(distributerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebyconsumer");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryqueuebyexchange")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByExchange(String pid,String queue_Nm) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;

		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(Long.valueOf(pid));
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueExchangerVo.setQueue_Nm(queue_Nm);
		List<QueueExchangerVo> qeueueExchangerVoList = queueExchangerService.queryQueuesByExchangerTree(queueExchangerVo);
		for (QueueExchangerVo qeueueExchangerVoResult : qeueueExchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(qeueueExchangerVoResult.getQueue_Nm()));
			treeVo.setLabel(qeueueExchangerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbyexchange");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryqueuebydistribute")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByDistribute(String pid,String queue_Nm) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;

		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(Long.valueOf(pid));
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueDistributerVo.setQueue_Nm(queue_Nm);
		List<QueueDistributerVo> queueDistributerVoVoList = queueDistributerService.queryQueuesByDistributerTree(queueDistributerVo);
		for (QueueDistributerVo queueDistributerVoResult : queueDistributerVoVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueDistributerVoResult.getQueue_Nm()));
			treeVo.setLabel(queueDistributerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbydistribute");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryqueuebyconsumer")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByConsumer(String pid,String queue_Nm) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;

		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(Long.valueOf(pid));
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueDistributerVo.setQueue_Nm(queue_Nm);
		List<QueueDistributerVo> queueDistributerVoVoList = queueDistributerService.queryQueuesByDistributerTree(queueDistributerVo);
		for (QueueDistributerVo queueDistributerVoResult : queueDistributerVoVoList) {
			treeVo = new TreeVo();
			treeVo.setId(queueDistributerVoResult.getQueue_Nm());
			treeVo.setLabel(queueDistributerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querygroupbyconsumer");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querygroupbyconsumer")
	@NoodleResponseBody
	public List<TreeVo> queryGroupByConsumer(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setQueue_Nm(pid);
		queueConsumerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueConsumerGroupVo> queueConsumerGroupVoList = queueConsumerGroupService.queryQueueByConsumerGroupList(queueConsumerGroupVo);
		for (QueueConsumerGroupVo queueConsumerGroupVoResult : queueConsumerGroupVoList) {
			treeVo = new TreeVo();
			treeVo.setId(queueConsumerGroupVoResult.getConsumerGroup_Nm());
			treeVo.setLabel(queueConsumerGroupVoResult.getConsumerGroup_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryconsumerbyconsumergroup");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryconsumerbyconsumergroup")
	@NoodleResponseBody
	public List<TreeVo> queryConsumerByConsumergroup(String pid, String ppid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		QueueConsumerGroupVo queueConsumerGroupVo = new QueueConsumerGroupVo();
		queueConsumerGroupVo.setQueue_Nm(ppid);
		queueConsumerGroupVo.setConsumerGroup_Nm(pid);
		queueConsumerGroupVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<QueueConsumerGroupVo> queueConsumerGroupVoList = queueConsumerGroupService.queryConsumerGroupsByQueue(queueConsumerGroupVo);
		for (QueueConsumerGroupVo queueConsumerGroupVoResult : queueConsumerGroupVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueConsumerGroupVoResult.getConsumer_Id()));
			treeVo.setLabel(queueConsumerGroupVoResult.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbyconsumer");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbyexchange")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_EXCHANGE_RECEIVE);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_EXCHANGE_RECEIVE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_EXCHANGE);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbydistribute")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_NEW_DISPATCH);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_DISTRIBUTE_NEW_DISPATCH);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_DISTRIBUTE_PORTION_DISPATCH);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_DISTRIBUTE_PORTION_DISPATCH);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbyconsumer")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByConsumer(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_CONSUMER_SEND);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_CONSUMER_SEND);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_CONSUMER);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydbbydb")
	@NoodleResponseBody
	public List<TreeVo> queryDbByDb(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		MsgStorageVo msgStorageVoQuery = new MsgStorageVo();
		msgStorageVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgStorageVo> msgStorageVoList = msgStorageService.queryMsgStorageList(msgStorageVoQuery);
		for (MsgStorageVo msgStorageVo : msgStorageVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(msgStorageVo.getMsgStorage_Id()));
			treeVo.setLabel(msgStorageVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryexchangedistributebydb");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryqueuecachebyqueuecache")
	@NoodleResponseBody
	public List<TreeVo> queryDbByQueuecache(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		MsgQueueCacheVo msgQueueCacheVoQuery = new MsgQueueCacheVo();
		msgQueueCacheVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgQueueCacheVo> msgQueueCacheVoList = msgQueueCacheService.queryMsgQueueCacheList(msgQueueCacheVoQuery);
		for (MsgQueueCacheVo msgQueueCacheVo : msgQueueCacheVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(msgQueueCacheVo.getMsgQueueCache_Id()));
			treeVo.setLabel(msgQueueCacheVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryexchangedistributebyqueuecache");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querybodycachebybodycache")
	@NoodleResponseBody
	public List<TreeVo> queryDbByBodycache(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		MsgBodyCacheVo msgBodyCacheVoQuery = new MsgBodyCacheVo();
		msgBodyCacheVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<MsgBodyCacheVo> msgBodyCacheVoList = msgBodyCacheService.queryMsgBodyCacheList(msgBodyCacheVoQuery);
		for (MsgBodyCacheVo msgBodyCacheVo : msgBodyCacheVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(msgBodyCacheVo.getMsgBodyCache_Id()));
			treeVo.setLabel(msgBodyCacheVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryexchangedistributebybodycache");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangedistributebydb")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeDistributeByDb(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_EXCHANGE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_EXCHANGE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/queryexchangebydbexchange");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DISTRIBUTE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querydistributebydbdistribute");
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangedistributebyqueuecache")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeDistributeByQueuecache(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DISTRIBUTE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querydistributebyqueuecachedistribute");
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangedistributebybodycache")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeDistributeByBodycache(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_EXCHANGE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_EXCHANGE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/queryexchangebybodycacheexchange");
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MODULE_ID_DISTRIBUTE);
		treeVo.setLabel(MonitorPerformanceConstant.MODULE_NAME_DISTRIBUTE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querydistributebybodycachedistribute");
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangebydbexchange")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeByDbExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		ExchangerVo exchangerVoQuery = new ExchangerVo();
		exchangerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ExchangerVo> exchangerVoList = exchangerService.queryExchangerList(exchangerVoQuery);
		for (ExchangerVo exchangerVo : exchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(exchangerVo.getExchanger_Id()));
			treeVo.setLabel(exchangerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebydbexchange");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryexchangebybodycacheexchange")
	@NoodleResponseBody
	public List<TreeVo> queryExchangeByBodycacheExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		ExchangerVo exchangerVoQuery = new ExchangerVo();
		exchangerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<ExchangerVo> exchangerVoList = exchangerService.queryExchangerList(exchangerVoQuery);
		for (ExchangerVo exchangerVo : exchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(exchangerVo.getExchanger_Id()));
			treeVo.setLabel(exchangerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebybodycacheexchange");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydistributebydbdistribute")
	@NoodleResponseBody
	public List<TreeVo> queryDistributeByDbDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		DistributerVo distributerVoQuery = new DistributerVo();
		distributerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoQuery);
		for (DistributerVo distributerVo : distributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(distributerVo.getDistributer_Id()));
			treeVo.setLabel(distributerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebydbdistribute");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydistributebyqueuecachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryDistributeByQueuecacheDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		DistributerVo distributerVoQuery = new DistributerVo();
		distributerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoQuery);
		for (DistributerVo distributerVo : distributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(distributerVo.getDistributer_Id()));
			treeVo.setLabel(distributerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebyqueuecachedistribute");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querydistributebybodycachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryDistributeByBodycacheDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		DistributerVo distributerVoQuery = new DistributerVo();
		distributerVoQuery.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		List<DistributerVo> distributerVoList = distributerService.queryDistributerList(distributerVoQuery);
		for (DistributerVo distributerVo : distributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(distributerVo.getDistributer_Id()));
			treeVo.setLabel(distributerVo.getName());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/queryqueuebybodycachedistribute");
			treeVoList.add(treeVo);
		}
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/queryqueuebydbexchange")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByDbExchange(String pid, String pppid,String queue_Nm) throws Exception {
		
		TreeVo treeVo = null;
		
		Map<String, TreeVo> treeVoMapByDb = new HashMap<String, TreeVo>();
		Map<String, TreeVo> treeVoMapByExchange = new HashMap<String, TreeVo>();
		
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setMsgStorage_Id(Long.valueOf(pppid));
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueMsgStorageVo.setQueue_Nm(queue_Nm);
		List<QueueMsgStorageVo> queueMsgStorageVoList = queueMsgStorageService.queryQueueByMsgstorageListTree(queueMsgStorageVo);
		for (QueueMsgStorageVo queueMsgStorageVoResult : queueMsgStorageVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueMsgStorageVoResult.getQueue_Nm()));
			treeVo.setLabel(queueMsgStorageVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbydbexchange");
			treeVoMapByDb.put(queueMsgStorageVoResult.getQueue_Nm(), treeVo);
		}
		
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(Long.valueOf(pid));
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueExchangerVo.setQueue_Nm(queue_Nm);
		List<QueueExchangerVo> qeueueExchangerVoList = queueExchangerService.queryQueuesByExchangerTree(queueExchangerVo);
		for (QueueExchangerVo qeueueExchangerVoResult : qeueueExchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(qeueueExchangerVoResult.getQueue_Nm()));
			treeVo.setLabel(qeueueExchangerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbydbexchange");
			treeVoMapByDb.put(qeueueExchangerVoResult.getQueue_Nm(), treeVo);
			treeVoMapByExchange.put(qeueueExchangerVoResult.getQueue_Nm(), treeVo);
		}
		
		Iterator<String> it = treeVoMapByDb.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!treeVoMapByExchange.containsKey(key)) {
				it.remove();
			}
		}
		
		return new ArrayList<TreeVo>(treeVoMapByDb.values());
	}
	
	@RequestMapping(value = "/queryqueuebybodycacheexchange")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByBodycacheExchange(String pid, String pppid, String queue_Nm) throws Exception {
		
		TreeVo treeVo = null;
		
		Map<String, TreeVo> treeVoMapByDb = new HashMap<String, TreeVo>();
		Map<String, TreeVo> treeVoMapByExchange = new HashMap<String, TreeVo>();
		
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setMsgBodyCache_Id(Long.valueOf(pppid));
		queueMsgBodyCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueMsgBodyCacheVo.setQueue_Nm(queue_Nm);
		List<QueueMsgBodyCacheVo> queueMsgBodyCacheVoList = queueMsgBodyCacheService.queryQueueMsgBodyCacheListTree(queueMsgBodyCacheVo);
		for (QueueMsgBodyCacheVo queueMsgBodyCacheVoResult : queueMsgBodyCacheVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueMsgBodyCacheVoResult.getQueue_Nm()));
			treeVo.setLabel(queueMsgBodyCacheVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbybodycacheexchange");
			treeVoMapByDb.put(queueMsgBodyCacheVoResult.getQueue_Nm(), treeVo);
		}
		
		QueueExchangerVo queueExchangerVo = new QueueExchangerVo();
		queueExchangerVo.setExchanger_Id(Long.valueOf(pid));
		queueExchangerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueExchangerVo.setQueue_Nm(queue_Nm);
		List<QueueExchangerVo> qeueueExchangerVoList = queueExchangerService.queryQueuesByExchangerTree(queueExchangerVo);
		for (QueueExchangerVo qeueueExchangerVoResult : qeueueExchangerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(qeueueExchangerVoResult.getQueue_Nm()));
			treeVo.setLabel(qeueueExchangerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbybodycacheexchange");
			treeVoMapByDb.put(qeueueExchangerVoResult.getQueue_Nm(), treeVo);
			treeVoMapByExchange.put(qeueueExchangerVoResult.getQueue_Nm(), treeVo);
		}
		
		Iterator<String> it = treeVoMapByDb.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!treeVoMapByExchange.containsKey(key)) {
				it.remove();
			}
		}
		
		return new ArrayList<TreeVo>(treeVoMapByDb.values());
	}
	
	
	@RequestMapping(value = "/queryqueuebydbdistribute")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByDbDistribute(String pid, String pppid,String queue_Nm) throws Exception {
		
		TreeVo treeVo = null;
		
		Map<String, TreeVo> treeVoMapByDb = new HashMap<String, TreeVo>();
		Map<String, TreeVo> treeVoMapByExchange = new HashMap<String, TreeVo>();
		
		QueueMsgStorageVo queueMsgStorageVo = new QueueMsgStorageVo();
		queueMsgStorageVo.setMsgStorage_Id(Long.valueOf(pppid));
		queueMsgStorageVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueMsgStorageVo.setQueue_Nm(queue_Nm);
		List<QueueMsgStorageVo> queueMsgStorageVoList = queueMsgStorageService.queryQueueByMsgstorageListTree(queueMsgStorageVo);
		for (QueueMsgStorageVo queueMsgStorageVoResult : queueMsgStorageVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueMsgStorageVoResult.getQueue_Nm()));
			treeVo.setLabel(queueMsgStorageVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbydbdistribute");
			treeVoMapByDb.put(queueMsgStorageVoResult.getQueue_Nm(), treeVo);
		}
		
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(Long.valueOf(pid));
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueDistributerVo.setQueue_Nm(queue_Nm);
		List<QueueDistributerVo> queueDistributerVoList = queueDistributerService.queryQueuesByDistributerTree(queueDistributerVo);
		for (QueueDistributerVo queueDistributerVoResult : queueDistributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueDistributerVoResult.getQueue_Nm()));
			treeVo.setLabel(queueDistributerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbydbdistribute");
			treeVoMapByDb.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
			treeVoMapByExchange.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
		}
		
		Iterator<String> it = treeVoMapByDb.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!treeVoMapByExchange.containsKey(key)) {
				it.remove();
			}
		}
		
		return new ArrayList<TreeVo>(treeVoMapByDb.values());
	}
	
	@RequestMapping(value = "/queryqueuebyqueuecachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByQueuecacheDistribute(String pid, String pppid, String queue_Nm) throws Exception {
		
		TreeVo treeVo = null;
		
		Map<String, TreeVo> treeVoMapByDb = new HashMap<String, TreeVo>();
		Map<String, TreeVo> treeVoMapByExchange = new HashMap<String, TreeVo>();
		
		
		QueueMsgQueueCacheVo queueMsgQueueCacheVo = new QueueMsgQueueCacheVo();
		queueMsgQueueCacheVo.setMsgQueueCache_Id(Long.valueOf(pppid));
		queueMsgQueueCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueMsgQueueCacheVo.setQueue_Nm(queue_Nm);
		List<QueueMsgQueueCacheVo> queueMsgQueueCacheVoList = queueMsgQueueCacheService.queryQueueByMsgQueueCacheListTree(queueMsgQueueCacheVo);
		for (QueueMsgQueueCacheVo queueMsgQueueCacheVoResult : queueMsgQueueCacheVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueMsgQueueCacheVoResult.getQueue_Nm()));
			treeVo.setLabel(queueMsgQueueCacheVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbyqueuecachedistribute");
			treeVoMapByDb.put(queueMsgQueueCacheVoResult.getQueue_Nm(), treeVo);
		}
		
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(Long.valueOf(pid));
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueDistributerVo.setQueue_Nm(queue_Nm);
		List<QueueDistributerVo> queueDistributerVoList = queueDistributerService.queryQueuesByDistributerTree(queueDistributerVo);
		for (QueueDistributerVo queueDistributerVoResult : queueDistributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueDistributerVoResult.getQueue_Nm()));
			treeVo.setLabel(queueDistributerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbyqueuecachedistribute");
			treeVoMapByDb.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
			treeVoMapByExchange.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
		}
		
		Iterator<String> it = treeVoMapByDb.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!treeVoMapByExchange.containsKey(key)) {
				it.remove();
			}
		}
		
		return new ArrayList<TreeVo>(treeVoMapByDb.values());
	}
	
	@RequestMapping(value = "/queryqueuebybodycachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryQueueByBodycacheDistribute(String pid, String pppid, String queue_Nm) throws Exception {
		
		System.out.println(queue_Nm);
		
		TreeVo treeVo = null;
		
		Map<String, TreeVo> treeVoMapByDb = new HashMap<String, TreeVo>();
		Map<String, TreeVo> treeVoMapByExchange = new HashMap<String, TreeVo>();
		
		QueueMsgBodyCacheVo queueMsgBodyCacheVo = new QueueMsgBodyCacheVo();
		queueMsgBodyCacheVo.setMsgBodyCache_Id(Long.valueOf(pppid));
		queueMsgBodyCacheVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueMsgBodyCacheVo.setQueue_Nm(queue_Nm);
		List<QueueMsgBodyCacheVo> queueMsgBodyCacheVoList = queueMsgBodyCacheService.queryQueueByMsgBodyCacheListTree(queueMsgBodyCacheVo);
		for (QueueMsgBodyCacheVo queueMsgBodyCacheVoResult : queueMsgBodyCacheVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueMsgBodyCacheVoResult.getQueue_Nm()));
			treeVo.setLabel(queueMsgBodyCacheVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbybodycachedistribute");
			treeVoMapByDb.put(queueMsgBodyCacheVoResult.getQueue_Nm(), treeVo);
		}
		
		QueueDistributerVo queueDistributerVo = new QueueDistributerVo();
		queueDistributerVo.setDistributer_Id(Long.valueOf(pid));
		queueDistributerVo.setManual_Status(ConsoleConstants.MANUAL_STATUS_VALID);
		queueDistributerVo.setQueue_Nm(queue_Nm);
		List<QueueDistributerVo> queueDistributerVoList = queueDistributerService.queryQueuesByDistributerTree(queueDistributerVo);
		for (QueueDistributerVo queueDistributerVoResult : queueDistributerVoList) {
			treeVo = new TreeVo();
			treeVo.setId(String.valueOf(queueDistributerVoResult.getQueue_Nm()));
			treeVo.setLabel(queueDistributerVoResult.getQueue_Nm());
			treeVo.setPid(pid);
			treeVo.setUrl("monitor/tree/querymonitorbybodycachedistribute");
			treeVoMapByDb.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
			treeVoMapByExchange.put(queueDistributerVoResult.getQueue_Nm(), treeVo);
		}
		
		Iterator<String> it = treeVoMapByDb.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!treeVoMapByExchange.containsKey(key)) {
				it.remove();
			}
		}
		
		return new ArrayList<TreeVo>(treeVoMapByDb.values());
	}
	
	@RequestMapping(value = "/querymonitorbydbexchange")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByDbExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_DB_INSERT);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_DB_INSERT);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_DB);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbybodycacheexchange")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByBodycacheExchange(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_BODYCACHE_SET);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_BODYCACHE_SET);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_BODYCACHE);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbydbdistribute")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByDbDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_DB_UPDATE);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_DB_UPDATE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_DB);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_DB_DELETE);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_DB_DELETE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_DB);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbyqueuecachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByQueuecacheDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_SET);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_QUEUECACHE_NEW_SET);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_QUEUECACHE);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_SET);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_QUEUECACHE_PORTION_SET);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_QUEUECACHE);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_NEW_POP);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_QUEUECACHE_NEW_POP);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_QUEUECACHE);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_QUEUECACHE_PORTION_POP);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_QUEUECACHE_PORTION_POP);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_QUEUECACHE);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querymonitorbybodycachedistribute")
	@NoodleResponseBody
	public List<TreeVo> queryMonitorByBodycacheDistribute(String pid) throws Exception {
		
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		
		TreeVo treeVo = null;
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_BODYCACHE_GET);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_BODYCACHE_GET);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_BODYCACHE);
		treeVoList.add(treeVo);
		
		treeVo = new TreeVo();
		treeVo.setId(MonitorPerformanceConstant.MONITOR_ID_BODYCACHE_REMOVE);
		treeVo.setLabel(MonitorPerformanceConstant.MONITOR_NAME_BODYCACHE_REMOVE);
		treeVo.setPid(pid);
		treeVo.setUrl("monitor/tree/querynull");
		treeVo.setEnableHighlight("true");
		treeVo.setLoad("true");
		treeVo.setOther(MonitorPerformanceConstant.MODULE_ID_BODYCACHE);
		treeVoList.add(treeVo);
		
		return treeVoList;
	}
	
	@RequestMapping(value = "/querynull")
	@NoodleResponseBody
	public List<TreeVo> queryNull() throws Exception {
		List<TreeVo> treeVoList = new ArrayList<TreeVo>();
		return treeVoList;
	}
}
*/