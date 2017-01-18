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
			
			$("#expander").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(230, "form", "expander", "list");},
				afterCollapse: function (e){collapseAdjustGrid(230, "expander", "list");}
			});
			
			$('#list').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/distributer/querypage',
				datatype: 'json',
				mtype: 'post',
			   	colNames: [
			   	        '编号',
						'分配中心名称', 
						'IP',
						'系统状态',
						'控制状态'
					],
			   	colModel: [
					{name:'distributer_Id', index:'distributer_Id', width:100, align: 'center'},
					{name:'name', index:'name', width:300, align: 'center'},
					{name:'ip', index:'ip', width:130, align: 'center'},
			   		{name:'system_Status', index:'system_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:在线;2:离线'}},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}}
			   	],
			   	rowNum: 10,
			   	rowList: [10,20,30,40,50,100],
			   	pager: '#pager',
			   	sortname: 'id',
			    viewrecords: true,
			    autowidth: true,
			    shrinkToFit: false,
			    height: 230,
			    sortorder: 'desc',
			    multiselect: true,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
				subGrid: true,
				subGridRowExpanded: function(subgrid_id, row_id) {
					var subgrid_table_id, pager_id;
					subgrid_table_id = subgrid_id + "_t";
					pager_id = "p_" + subgrid_table_id;
					$("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table><div id='" + pager_id + "' class='scroll'></div>");
					var ret = jQuery('#list').jqGrid('getRowData', row_id);
					var vo = new Object();
					vo['distributer_Id'] = ret['distributer_Id'];
					jQuery("#"+subgrid_table_id).jqGrid({
						url:'<%=request.getContextPath()%>/console/queue/distributer/queryqueue',
						datatype: "json",
						mtype: 'post',
						postData:{'input': jsonToString(vo)}, 
						colNames: [
						           	'队列名称', 
									'控制状态',
									'是否重发',
									'过期时间',
									'间隔时间',
									'推送延迟时间',
									'推送超时时间',
									'新消息POP线程数量',
									'新消息EXE线程数量',
									'未完成消息POP线程数量',
									'未完成消息EXE线程数量'
						],
						colModel: [
									{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
							   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}},
									{name:'is_Repeat', index:'is_Repeat', width:100, align:'center', formatter:'checkbox', editoptions:{value:'1:0'}},
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
									{name:'new_Pop_ThreadNum', index:'new_Pop_ThreadNum', width:150, align:'center', formatter:'select', 
										editoptions:{value:'1:1;3:3;5:5;10:10;30:30;50:50;100:100;300:300;500:500;1000:1000;3000:3000;5000:5000;'}
									},
									{name:'new_Exe_ThreadNum', index:'new_Exe_ThreadNum', width:150, align:'center', formatter:'select', 
										editoptions:{value:'1:1;3:3;5:5;10:10;30:30;50:50;100:100;300:300;500:500;1000:1000;3000:3000;5000:5000;'}
									},
									{name:'portion_Pop_ThreadNum', index:'portion_Pop_ThreadNum', width:150, align:'center', formatter:'select', 
										editoptions:{value:'1:1;3:3;5:5;10:10;30:30;50:50;100:100;300:300;500:500;1000:1000;3000:3000;5000:5000;'}
									},
									{name:'portion_Exe_ThreadNum', index:'portion_Exe_ThreadNum', width:150, align:'center', formatter:'select', 
										editoptions:{value:'1:1;3:3;5:5;10:10;30:30;50:50;100:100;300:300;500:500;1000:1000;3000:3000;5000:5000;'}
									}
						],
					   	rowNum:10,
					   	rowList: [10,20,30,40,50,100],
					   	pager: pager_id,
					   	sortname: 'queue_Nm',
					    sortorder: "desc",
					    height: '100%',
					    jsonReader: {
							repeatitems : false
						}
					});
					jQuery("#"+subgrid_table_id).jqGrid('navGrid', "#" + pager_id,{search:false,edit:false,add:false,del:false})
				},
			    caption: '查询结果',
			    gridComplete:function(){
			    	repaintGrid(230, "expander", "list");
			    }
			});
				
			$('#list').jqGrid('navGrid', '#pager', {search:false, edit:false, add:false, del:false});
			
			$(window).resize(function(){ 
				$("#list").setGridWidth($(window).width() - 14);
			});
		}
		
		function query() {
			
			if($("#ip").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/;
				var r = $("#ip").val().match(reg); 
				if(r === null){
					alert('请输入正确的IP');
					return;
				}
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id')] = $(this).val();
				}
			 });
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			
			$('#list').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/console/distributer/querypage',
				postData:{'input': jsonToString(vo)}, 
				page: 1   
		    }).trigger('reloadGrid');
		}
		
		function insert() {
			top.openDialog('分配中心新增', '<%=request.getContextPath()%>/view/console/distributer/distributer_edit.jsp', null, 250, 700, query);
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
			top.openDialog('分配中心修改', '<%=request.getContextPath()%>/view/console/distributer/distributer_edit.jsp', ret, 250, 700, query);					
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
				url: '<%=request.getContextPath()%>/console/distributer/deletes',
				jsonSet: jsonSet
			});	
		}
		
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" >
	<div>
		<button id="query">查询</button>
		<button id="insert">新增</button>
		<button id="update">修改</button>
		<button id="deletes">删除</button>
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
			    	<th><label>编号</label></th>
			    	<td><input type="text" id="distributer_Id" maxlength="20" onkeyup="this.value=this.value.replace(/\D/g,'')"/></td>
			    	<th><label>分配中心名称</label></th>
			    	<td><input type="text" id="name" maxlength="32"/></td>
			    	<th><label>系统状态</label></th>
			    	<td>
			    		<select id="system_Status">
			    			<option value="">--all--</option>
			    			<option value="1">在线</option>
			    			<option value="2">离线</option>
			    		</select>
			    	</td>
			    	<th><label>控制状态</label></th>
			    	<td>
			    		<select id="manual_Status">
			    			<option value="">--all--</option>
			    			<option value="1">有效</option>
			    			<option value="2">无效</option>
			    		</select>
			    	</td>
			    </tr>
			    <tr>
			    	<th><label>IP</label></th>
			    	<td><input type="text" id="ip" maxlength="64"/></td>
			    	<th><label>&nbsp;</label></th>
			    	<td>&nbsp;</td>
			    	<th><label>&nbsp;</label></th>
			    	<td>&nbsp;</td>
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
