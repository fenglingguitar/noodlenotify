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
		   		url: '<%=request.getContextPath()%>/console/msgqueuecache/querypage',
				datatype: 'json',
				mtype: 'post',
			   	colNames: [
			   	        '编号',
						'队列缓存名称', 
						'IP',
						'PORT',
						'系统状态',
						'控制状态'
					],
			   	colModel: [
					{name:'msgQueueCache_Id', index:'msgqueuecache_Id', width:100, align: 'center'},
					{name:'name', index:'name', width:300, align: 'center'},
					{name:'ip', index:'ip', width:130, align: 'center'},
					{name:'port', index:'port', width:100, align: 'center'},
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
					vo['msgQueueCache_Id'] = ret['msgQueueCache_Id'];
					jQuery("#"+subgrid_table_id).jqGrid({
						url:'<%=request.getContextPath()%>/console/queue/msgqueuecache/queryqueue',
						datatype: "json",
						mtype: 'post',
						postData:{'input': jsonToString(vo)}, 
						colNames: [
						           	'队列名称', 
									'控制状态',
									'是否为活动状态',
									'新消息数量',
									'未完成消息数量'
						],
						colModel: [
									{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
							   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}},
									{name:'is_Active', index:'is_Active', width:100, align:'center', formatter:'checkbox', editoptions:{value:'1:0'}},
									{name:'new_Len', index:'new_Len', width:100, align: 'center'},
									{name:'portion_Len', index:'portion_Len', width:100, align: 'center'}
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
			
			if($("#port").val() !== '' && isNaN($("#port").val())){
				alert('请输入正确的PORT');
				return;
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
				url: '<%=request.getContextPath()%>/console/msgqueuecache/querypage',
				postData:{'input': jsonToString(vo)}, 
				page: 1   
		    }).trigger('reloadGrid');
		}
		
		function insert() {
			top.openDialog('队列缓存新增', '<%=request.getContextPath()%>/view/console/msgqueuecache/msgqueuecache_edit.jsp', null, 260, 700, query);
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
			top.openDialog('队列缓存修改', '<%=request.getContextPath()%>/view/console/msgqueuecache/msgqueuecache_edit.jsp', ret, 260, 700, query);					
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
				url: '<%=request.getContextPath()%>/console/msgqueuecache/deletes',
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
			    	<td><input type="text" id="msgQueueCache_Id" maxlength="20" onkeyup="this.value=this.value.replace(/\D/g,'')"/></td>
			    	<th><label>队列缓存名称</label></th>
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
			    	<th><label>PORT</label></th>
			    	<td><input type="text" id="port" maxlength="5"/></td>
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
