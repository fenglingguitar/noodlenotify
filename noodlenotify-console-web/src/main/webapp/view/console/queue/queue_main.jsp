<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>NoodleNotify-Child</title>
       
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/jqgrid/css/ui.jqgrid.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijsuperpanel.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijexpander.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/css/my.css" rel="stylesheet" type="text/css" />
    
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-ui-1.9.1.custom.min.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijutil.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijsuperpanel.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijexpander.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>
	
    <script type="text/javascript">
    
	    function callback(trnId, data) {
	    	
			if (trnId == 'DELETES') {
				if (data.result == 'false') {
					alert('删除失败');
				} else {
					alert('删除成功');
					query();
				}
			}
		}
    	
		function init() {
			
			$('#query').button().click(function() {
				query();
			});
			$('#insert').button().click(function() {
				insert();
			});
			$('#update').button().click(function() {
				update();
			});
			$('#deletes').button().click(function() {
				deletes();
			});
			$('#exchange').button().click(function() {
				setExchange();
			});
			$('#distributer').button().click(function() {
				setDistributer();
			});
			$('#db').button().click(function() {
				setDb();
			});
			$('#msgqueuecache').button().click(function() {
				setMsgqueuecache();
			});
			$('#msgbodycache').button().click(function() {
				setMsgbodycache();
			});
			$('#consumergroup').button().click(function() {
				setConsumerGroup();
			});
			$('#consumer').button().click(function() {
				setConsumer();
			});
			$('#send').button().click(function() {
				send();
			});
			
			$("#expander").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(248, "form", "expander", "list", "query_div");},
				afterCollapse: function (e){collapseAdjustGrid(248, "expander", "list", "query_div");}
			});
			
			$('#list').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/queue/querypage',
				datatype: 'local',
				mtype: 'post',
			   	colNames: [
					'队列名称', 
					'控制状态',
					'是否重发',
					'过期时间',
					'间隔时间',
					'推送延迟时间',
					'推送超时时间',
					'新消息POP线程数',
					'新消息EXE线程数',
					'未完成消息POP线程数',
					'未完成消息EXE线程数',
					'创建时间'
					],
			   	colModel: [
					{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}},
					{name:'is_Repeat', index:'is_Repeat', width:70, align:'center', formatter:'checkbox', editoptions:{value:'1:0'}},
					{name:'expire_Time', index:'expire_Time', width:100, align:'center', formatter:'select', 
						editoptions:{value:'0:永不;600000:10分钟;1800000:30分钟;3600000:1小时;21600000:6小时;43200000:12小时;86400000:1天;604800000:1周;2592000000:1个月(30天);'}
					},
					{name:'interval_Time', index:'interval_Time', width:100, align:'center', formatter:'select', 
						editoptions:{value:'10000:10秒;30000:30秒;60000:1分钟;120000:2分钟;180000:3分钟;300000:5分钟;600000:10分钟;900000:15分钟;1800000:30分钟;3600000:1小时;10800000:3小时;21600000:6小时;43200000:12小时;86400000:1天;'}
					},
					{name:'dph_Delay_Time', index:'dph_Delay_Time', width:100, align:'center',formatter:'select', 
						editoptions:{value:'0:不延迟;1000:1秒;2000:2秒;3000:3秒;5000:5秒;10000:10秒;30000:30秒;60000:1分钟;120000:2分钟;180000:3分钟;300000:5分钟;600000:10分钟;900000:15分钟;1800000:30分钟;3600000:1小时;10800000:3小时;21600000:6小时;43200000:12小时;86400000:1天;'}
					},
					{name:'dph_Timeout', index:'dph_Timeout', width:100, align:'center',formatter:'select', 
						editoptions:{value:'0:;1000:1秒;2000:2秒;3000:3秒;5000:5秒;10000:10秒;30000:30秒;60000:1分钟;120000:2分钟;180000:3分钟;300000:5分钟;600000:10分钟;900000:15分钟;1800000:30分钟;3600000:1小时;'}
					},
					{name:'new_Pop_ThreadNum', index:'new_Pop_ThreadNum', width:140, align:'center', formatter:'select', 
						editoptions:{value:'1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;15:15;20:20;25:25;30:30;35:35;40:40;45:45;50:50;60:60;70:70;80:80;90:90;100:100;200:200;300:300;400:400;500:500;'}
					},
					{name:'new_Exe_ThreadNum', index:'new_Exe_ThreadNum', width:140, align:'center', formatter:'select', 
						editoptions:{value:'1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;15:15;20:20;25:25;30:30;35:35;40:40;45:45;50:50;60:60;70:70;80:80;90:90;100:100;200:200;300:300;400:400;500:500;'}
					},
					{name:'portion_Pop_ThreadNum', index:'portion_Pop_ThreadNum', width:140, align:'center', formatter:'select', 
						editoptions:{value:'1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;15:15;20:20;25:25;30:30;35:35;40:40;45:45;50:50;60:60;70:70;80:80;90:90;100:100;200:200;300:300;400:400;500:500;'}
					},
					{name:'portion_Exe_ThreadNum', index:'portion_Exe_ThreadNum', width:140, align:'center', formatter:'select', 
						editoptions:{value:'1:1;2:2;3:3;4:4;5:5;6:6;7:7;8:8;9:9;10:10;15:15;20:20;25:25;30:30;35:35;40:40;45:45;50:50;60:60;70:70;80:80;90:90;100:100;200:200;300:300;400:400;500:500;'}
					},
					{name:'create_Tm', index:'create_Tm', width:200, align:'center', formatter:'date', formatoptions:{srcformat:'Y-m-d H:i:s.sss', newformat:'Y-m-d H:i:s'}}
				],
			   	rowNum: 10,
			   	rowList: [10,20,30,40,50,100],
			   	pager: '#pager',
			   	sortname: 'id',
			    viewrecords: true,
			    autowidth: true,
			    shrinkToFit: false,
			    height: 248,
			    sortorder: 'desc',
			    multiselect: true,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
			    caption: '查询结果',
			    gridComplete:function(){
			    	repaintGrid(248, "expander", "list", "query_div");
			    }
			});
				
			$('#list').jqGrid('navGrid', '#pager', {search:false, edit:false, add:false, del:false});
			
			$(window).resize(function(){ 
				$("#list").setGridWidth($(window).width() - 14);
			});
			
			query();
		}
		
		function query() {
			
			if($("#queue_Nm").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/;
				var r = $("#queue_Nm").val().match(reg); 
				if(r === null){
					alert('请输入正确的队列名称(字母或数字或".")');
					return false;
				}
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id')] = $(this).val();
				}
			 });
			
			$('#list').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/console/queue/querypage',
				datatype: 'json',
				postData:{'input': jsonToString(vo)}, 
		        page: 1   
		    }).trigger('reloadGrid');
		}
		
		function insert() {
			top.openDialog('队列新增', '<%=request.getContextPath()%>/view/console/queue/queue_edit.jsp', null, 350, 700, query);
		}
		
		function update() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('队列修改', '<%=request.getContextPath()%>/view/console/queue/queue_edit.jsp', ret, 350, 700, query);					
		}
		
		function deletes() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			
			if(!window.confirm('你确定要删除吗？')){
				return false;
            }
			
			var retArray = new Array();
			var indexArray = index.toString().split(',');
			for (var i=0; i<indexArray.length; i++) {				
				var ret = jQuery('#list').jqGrid('getRowData', indexArray[i]);
				retArray.push(ret);
			}
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', retArray);
			
			transaction({
				id: 'DELETES',
				url: '<%=request.getContextPath()%>/console/queue/deletes',
				jsonSet: jsonSet
			});	
		}
		
		function setExchange() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置交换中心', '<%=request.getContextPath()%>/view/console/queue/queue-exchange/queue_exchange_main.jsp', ret, 640, 1200, null);					
		}
		
		function setDistributer() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置分配中心', '<%=request.getContextPath()%>/view/console/queue/queue-distributer/queue_distributer_main.jsp', ret, 610, 1200, null);					
		}
		
		function setDb() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置数据库', '<%=request.getContextPath()%>/view/console/queue/queue-db/queue_db_main.jsp', ret, 610, 1200, null);					
		}
		
		function setMsgqueuecache() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置队列缓存', '<%=request.getContextPath()%>/view/console/queue/queue-msgqueuecache/queue_msgqueuecache_main.jsp', ret, 610, 1200, null);					
		}
		
		function setMsgbodycache() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置消息体缓存', '<%=request.getContextPath()%>/view/console/queue/queue-msgbodycache/queue_msgbodycache_main.jsp', ret, 610, 1200, null);					
		}
		
		function setConsumerGroup() {
			
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置消费者组', '<%=request.getContextPath()%>/view/console/queue/queue-consumergroup/queue_consumergroup_main.jsp', ret, 580, 1200, null);					
		}
		
		function setConsumer() {
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('设置消费者', '<%=request.getContextPath()%>/view/console/queue/queue-consumer/queue_consumer_main.jsp', ret, 670, 1200, null);					
		}
		
		function send() {
			var index = jQuery('#list').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			var indexArray = index.toString().split(',');
			if (indexArray.length > 1) {
				alert('只能选择一行');
				return;
			}
			var ret = jQuery('#list').jqGrid('getRowData', index);
			top.openDialog('发送消息', '<%=request.getContextPath()%>/view/console/queue/queue_send.jsp', ret, 335, 500, null);					
		}
		
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" >
	<div id="query_div">
		<button id="query">查询</button>
		<button id="insert">新增</button>
		<button id="update">修改</button>
		<button id="deletes">删除</button>
		<button id="exchange">设置交换中心</button>
		<button id="distributer">设置分配中心</button>
		<button id="db">设置数据库</button>
		<button id="msgqueuecache">设置队列缓存</button>
		<button id="msgbodycache">设置消息体缓存</button>
		<button id="consumergroup">设置消费者组</button>
		<button id="consumer">设置消费者</button>
		<button id="send">发送消息</button>
	</div>
	<div id="expander" class="s_expander">
        <h3>查询条件</h3>
		<div id="form" style="width: auto;">
			<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout">
				<colgroup>
					<col width="10%" />
					<col width="15%" />
					<col width="10%" />
					<col width="15%" />
					<col width="10%" />
					<col width="15%" />
					<col width="10%" />
					<col width="15%" />
				</colgroup>					
			    <tr>
			    	<th><label>队列名称</label></th>
			    	<td><input type="text" id="queue_Nm" maxlength="32"/></td>
			    	<th><label>队列状态</label></th>
			    	<td>
			    		<select id="manual_Status">
			    			<option value="">--all--</option>
			    			<option value="1">有效</option>
			    			<option value="2">无效</option>
			    		</select>
			    	</td>
			    	<th><label>是否重发</label></th>
			    	<td>
			    		<select id="is_Repeat">
			    			<option value="">--all--</option>
			    			<option value="1">是</option>
			    			<option value="0">否</option>
			    		</select>
			    	</td>
			    	<th><label>&nbsp;</label></th>
			    	<td>&nbsp;</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="list_div" style="width:auto;">
		<table id="list"></table>
		<div id="pager"></div>
	</div>
  </body>
</html>
