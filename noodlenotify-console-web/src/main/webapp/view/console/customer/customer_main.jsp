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
				beforeExpand: function (e){expandAdjustGrid(248, "form", "expander", "list");},
				afterCollapse: function (e){collapseAdjustGrid(248, "expander", "list");}
			});
			
			$('#list').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/customer/querypage',
				datatype: 'json',
				mtype: 'post',
			   	colNames: [
			   	        '编号',
						'消费者名称', 
						'IP',
						'PORT',
						'URL',
						'类型',
						'监测PORT',
						'监测URL',
						'监测类型',
						'系统状态',
						'控制状态',
						'消费组名称',
						'心跳时间'
					],
			   	colModel: [
					{name:'customer_Id', index:'customer_Id', width:100, align: 'center'},
					{name:'name', index:'name', width:300, align: 'center'},
					{name:'ip', index:'ip', width:130, align: 'center'},
					{name:'port', index:'port', width:100, align: 'center'},
					{name:'url', index:'url', width:200, align: 'left'},
					{name:'type', index:'type', width:100, align: 'center', formatter:'select', editoptions:{value:'NETTY:NETTY;HTTP:HTTP'}},
					{name:'check_Port', index:'check_Port', width:100, align: 'center'},
					{name:'check_Url', index:'check_Url', width:200, align: 'left'},
					{name:'check_Type', index:'check_Type', width:100, align:'center', formatter:'select', editoptions:{value:'NETTY:NETTY;HTTP:HTTP'}},
			   		{name:'system_Status', index:'system_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:在线;2:离线'}},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}},
			   		{name:'customerGroup_Nm', index:'customerGroup_Nm', width:400, align: 'center'},
			   		{name:'beat_Time', index:'beat_Time', width:200, align:'center', formatter:'date', formatoptions:{srcformat:'Y-m-d H:i:s', newformat:'Y-m-d H:i:s'}}
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
				subGrid: true,
				subGridRowExpanded: function(subgrid_id, row_id) {
					var subgrid_table_id, pager_id;
					subgrid_table_id = subgrid_id + "_t";
					pager_id = "p_" + subgrid_table_id;
					$("#" + subgrid_id).html("<table id='" + subgrid_table_id + "' class='scroll'></table><div id='" + pager_id + "' class='scroll'></div>");
					var ret = jQuery('#list').jqGrid('getRowData', row_id);
					var vo = new Object();
					vo['customer_Id'] = ret['customer_Id'];
					jQuery("#"+subgrid_table_id).jqGrid({
						url:'<%=request.getContextPath()%>/console/queue/customer/queryqueue',
						datatype: "json",
						mtype: 'post',
						postData:{'input': jsonToString(vo)}, 
						colNames: [
						           	'队列名称', 
									'控制状态'
						],
						colModel: [
									{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
							   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}}
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
			    	repaintGrid(248, "expander", "list");
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
			
			if($("#check_Port").val() !== '' && isNaN($("#check_Port").val())){
				alert('请输入正确的监测PORT');
				return;
			}
			
			if($("#customerGroup_Nm").val() !== ''){
				var reg = /^(\d|[a-z|A-Z])+$/;
				var r = $("#customerGroup_Nm").val().match(reg); 
				if(r === null){
					alert('请输入正确的消费者组名称(字母或数字)');
					return;
				}
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id')] = $(this).val();
				}
			 });
			
			$('#list').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/console/customer/querypage',
				postData:{'input': jsonToString(vo)}, 
				page: 1   
		    }).trigger('reloadGrid');
		}
		
		function insert() {
			top.openDialog('消费中心新增', '<%=request.getContextPath()%>/view/console/customer/customer_edit.jsp', null, 335, 700, query);
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
			top.openDialog('消费中心修改', '<%=request.getContextPath()%>/view/console/customer/customer_edit.jsp', ret, 335, 700, query);					
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
				url: '<%=request.getContextPath()%>/console/customer/deletes',
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
			    	<td><input type="text" id="customer_Id" maxlength="20" onkeyup="this.value=this.value.replace(/\D/g,'')"/></td>
			    	<th><label>消费者名称</label></th>
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
			    	<th><label>类型</label></th>
			    	<td>
			    		<select id="type">
			    			<option value="">--all--</option>
			    			<option value="NETTY">NETTY</option>
			    			<option value="HTTP">HTTP</option>
			    		</select>
			    	</td>
			    	<th><label>消费组名称</label></th>
			    	<td><input type="text" id="customerGroup_Nm" maxlength="32"/></td>
				</tr>
				<tr>
			    	<th><label>监测PORT</label></th>
			    	<td><input type="text" id="check_Port" maxlength="11" /></td>
			    	<th><label>监测类型</label></th>
			    	<td>
			    		<select id="check_Type">
			    			<option value="">--all--</option>
			    			<option value="NETTY">NETTY</option>
			    			<option value="HTTP">HTTP</option>
			    		</select>
			    	</td>
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
