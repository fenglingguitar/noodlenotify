<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../../../global.jsp"%>
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
	    	if (trnId == 'INSERTS') {
    			if (data.result == 'false') {
    				alert('新增失败');
    			} else {
    				alert('新增成功');
    				query();
    			}
			} else if (trnId == 'DELETES') {
				if (data.result == 'false') {
					alert('删除失败');
				} else {
					alert('删除成功');
					query();
				}
			}
		}
    
		function init() {
			
			$('#query_left').button().click(function() {
				queryLeft();
			});
			$('#close_left').button().click(function() {
				top.closeDialog(false);
			});
			$('#query_right').button().click(function() {
				queryRight();
			});
			$('#close_right').button().click(function() {
				top.closeDialog(false);
			});
			$('#inserts').button().click(function() {
				inserts();
			});
			$('#deletes').button().click(function() {
				deletes();
			});
			
			var urlParamObject = getURLParamObject();
			if (urlParamObject != null) {				
				$('#form_left :input').each(function(i){
					if (urlParamObject[$(this).attr('id')] != null) {					
						$(this).val(urlParamObject[$(this).attr('id')]);
					}
				});
				$('#queue_Nm').attr('disabled', 'true');
			}
			
			var paramObject = {'queue_Nm': urlParamObject.queue_Nm};
			
			$("#expander_left").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(247, "form_left", "expander_left", "list_left");},
				afterCollapse: function (e){collapseAdjustGrid(247, "expander_left", "list_left");}
			});
			$("#expander_right").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(247, "form_right", "expander_right", "list_right");},
				afterCollapse: function (e){collapseAdjustGrid(247, "expander_right", "list_right");}
			});
			
			$('#list_left').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/queue/msgstorage/queryincludepage',
				datatype: 'json',
				mtype: 'post',
				postData:{'input': jsonToString(paramObject)}, 
			   	colNames: [
					'队列名称', 
					'数据库编号',
					'数据库名称', 
					'IP',
					'PORT',
					'系统状态',
					'控制状态'
					],
			   	colModel: [
					{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
					{name:'msgStorage_Id', index:'msgStorage_Id', width:100, align: 'center'},
					{name:'name', index:'name', width:300, align: 'center'},
					{name:'ip', index:'ip', width:130, align: 'center'},
					{name:'port', index:'port', width:100, align: 'center'},
			   		{name:'system_Status', index:'system_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:在线;2:离线'}},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效;3:只读'}}
			   	],
			   	rowNum: 10,
			   	rowList: [10,20,30,40,50,100],
			   	pager: '#pager_left',
			   	sortname: 'id',
			    viewrecords: true,
			    autowidth: true,
			    shrinkToFit: false,
			    height: 247,
			    sortorder: 'desc',
			    multiselect: true,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
			    caption: '查询结果',
			    gridComplete:function(){
			    	repaintGrid(247, "expander_left", "list_left");
			    }
			});
				
			$('#list_left').jqGrid('navGrid', '#pager_left', {search:false, edit:false, add:false, del:false});
			
			$('#list_right').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/queue/msgstorage/queryexcludepage',
				datatype: 'json',
				mtype: 'post',
				postData:{'input': jsonToString(paramObject)}, 
			   	colNames: [
						'数据库编号',
						'数据库名称', 
						'IP',
						'PORT',
						'系统状态',
						'控制状态'
					],
			   	colModel: [
					{name:'msgStorage_Id', index:'msgStorage_Id', width:100, align: 'center'},
					{name:'name', index:'name', width:300, align: 'center'},
					{name:'ip', index:'ip', width:130, align: 'center'},
					{name:'port', index:'port', width:100, align: 'center'},
			   		{name:'system_Status', index:'system_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:在线;2:离线'}},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效;3:只读'}}
			   	],
			   	rowNum: 10,
			   	rowList: [10,20,30,40,50,100],
			   	pager: '#pager_right',
			   	sortname: 'id',
			    viewrecords: true,
			    autowidth: true,
			    shrinkToFit: false,
			    height: 247,
			    sortorder: 'desc',
			    multiselect: true,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
			    caption: '查询结果',
			    gridComplete:function(){
			    	repaintGrid(247, "expander_right", "list_right");
			    }
			});
				
			$('#list_right').jqGrid('navGrid', '#pager_right', {search:false, edit:false, add:false, del:false});
			
			$(window).resize(function(){ 
				$("#list_left").setGridWidth(Number($(window).width()) * (45 / 100));
				$("#list_right").setGridWidth(Number($(window).width()) * (45 / 100));
				repaintGrid(247, "expander_right", "list_right");
				repaintGrid(247, "expander_left", "list_left");
			});
		}
		
		function query() {
			
			queryLeft();
			queryRight();
		}
		
		function queryLeft() {
			
			if($("#ip_left").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/;
				var r = $("#ip_left").val().match(reg); 
				if(r === null){
					alert('左侧请输入正确的IP');
					return;
				}
			}
			if($("#port_left").val() !== '' && isNaN($("#port_left").val())){
				alert('左侧请输入正确的PORT');
				return;
			}
			
			var vo = new Object();
			$('#form_left :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id').replace(/_left/, '')] = $(this).val();
				}
			 });
			
			$('#list_left').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/console/queue/msgstorage/queryincludepage',
				postData:{'input': jsonToString(vo)}, 
		        page: 1   
		    }).trigger('reloadGrid');
		}
		
		function queryRight() {
			
			if($("#ip").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/;
				var r = $("#ip").val().match(reg); 
				if(r === null){
					alert('右侧请输入正确的IP');
					return;
				}
			}
			if($("#port").val() !== '' && isNaN($("#port").val())){
				alert('右侧请输入正确的PORT');
				return;
			}
			
			var vo = new Object();
			$('#form_right :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id')] = $(this).val();
				}
			 });
			
			vo['queue_Nm'] = $('#queue_Nm').val();
			
			$('#list_right').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/console/queue/msgstorage/queryexcludepage',
				postData:{'input': jsonToString(vo)}, 
		        page: 1   
		    }).trigger('reloadGrid');
		}
		
		function inserts() {
			
			var index = jQuery('#list_right').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			
			var retArray = new Array();
			var indexArray = index.toString().split(',');
			for (var i=0; i<indexArray.length; i++) {				
				var ret = jQuery('#list_right').jqGrid('getRowData', indexArray[i]);
				ret['queue_Nm'] = $('#queue_Nm').val();
				retArray.push(ret);
			}
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', retArray);
			
			transaction({
				id: 'INSERTS',
				url: '<%=request.getContextPath()%>/console/queue/msgstorage/inserts',
				jsonSet: jsonSet
			});	
		}
		
		function deletes() {
			
			var index = jQuery('#list_left').jqGrid('getGridParam', 'selarrrow');
			if (index.toString() == '') {
				alert('请选择');
				return;
			}
			
			var retArray = new Array();
			var indexArray = index.toString().split(',');
			for (var i=0; i<indexArray.length; i++) {				
				var ret = jQuery('#list_left').jqGrid('getRowData', indexArray[i]);
				retArray.push(ret);
			}
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', retArray);
			
			transaction({
				id: 'DELETES',
				url: '<%=request.getContextPath()%>/console/queue/msgstorage/deletes',
				jsonSet: jsonSet
			});	
		}
		
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" >
	<div style="width: auto;">
		<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
			<colgroup>
				<col width="45%" />
				<col width="10%" />
				<col width="45%" />
			</colgroup>
			<tr>
				<td valign="top">
					<div>
						<button id="query_left">查询</button>
						<button id="close_left">关闭</button>
					</div>
					<div id="expander_left" class="s_expander">
        				<h3>查询条件</h3>
						<div id="form_left" style="width: auto;">
							<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout">
								<colgroup>
									<col width="20%" />
									<col width="30%" />
									<col width="20%" />
									<col width="30%" />
								</colgroup>					
							    <tr>
							    	<th><label>队列名称</label></th>
							    	<td><input type="text" id="queue_Nm" maxlength="32"/></td>
							    	<th><label>数据库名称</label></th>
							    	<td><input type="text" id="name_left" maxlength="32"/></td>
								</tr>
								<tr>
							    	<th><label>系统状态</label></th>
							    	<td>
							    		<select id="system_Status_left">
							    			<option value="">--all--</option>
							    			<option value="1">在线</option>
							    			<option value="2">离线</option>
							    		</select>
							    	</td>
							    	<th><label>控制状态</label></th>
							    	<td>
							    		<select id="manual_Status_left">
							    			<option value="">--all--</option>
							    			<option value="1">有效</option>
							    			<option value="2">无效</option>
							    			<option value="3">只读</option>
							    		</select>
							    	</td>
							    </tr>
							    <tr>
							    	<th><label>IP</label></th>
							    	<td><input type="text" id="ip_left" maxlength="64"/></td>
							    	<th><label>PORT</label></th>
							    	<td><input type="text" id="port_left" maxlength="5"/></td>
								</tr>
							</table>
						</div>
					</div>
					<div id="list_left_div" style="width:auto;">
						<table id="list_left"></table>
						<div id="pager_left"></div>
					</div>
				</td>
				<td align="center">
					<div>
						<button id="inserts">&nbsp;新增&nbsp;</button>
					</div>
					<br/>
					<div>
						<button id="deletes">&nbsp;删除&nbsp;</button>
					</div>
				</td>
				<td valign="top">
					<div>
						<button id="query_right">查询</button>
						<button id="close_right">关闭</button>
					</div>
					<div id="expander_right" class="s_expander">
        				<h3>查询条件</h3>
						<div id="form_right" style="width: auto;">
							<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout">
								<colgroup>
									<col width="10%" />
									<col width="15%" />
									<col width="10%" />
									<col width="15%" />
								</colgroup>					
							    <tr>
							    	<th><label>数据库名称</label></th>
							    	<td><input type="text" id="name" maxlength="32"/></td>
							    	<th><label>系统状态</label></th>
							    	<td>
							    		<select id="system_Status">
							    			<option value="">--all--</option>
							    			<option value="1">在线</option>
							    			<option value="2">离线</option>
							    		</select>
							    	</td>
							    </tr>
							    <tr>
								    <th><label>控制状态</label></th>
							    	<td>
							    		<select id="manual_Status">
							    			<option value="">--all--</option>
							    			<option value="1">有效</option>
							    			<option value="2">无效</option>
							    			<option value="3">只读</option>
							    		</select>
							    	</td>
							    	<th><label>IP</label></th>
							    	<td><input type="text" id="ip" maxlength="64"/></td>
								</tr>
							    <tr>
							    	<th><label>PORT</label></th>
							    	<td><input type="text" id="port" maxlength="5"/></td>
							    	<th><label>&nbsp;</label></th>
							    	<td>&nbsp;</td>
								</tr>
							</table>
						</div>
					</div>
					<div id="list_right_div"  style="width:auto;">
						<table id="list_right"></table>
						<div id="pager_right"></div>
					</div>
				</td>
			</tr>
		</table>
	</div>
  </body>
</html>
