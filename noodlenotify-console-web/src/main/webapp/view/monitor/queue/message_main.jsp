<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>NoodleNotify-Child</title>
       
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijexpander.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/jqgrid/css/ui.jqgrid.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/css/my.css" rel="stylesheet" type="text/css" />
    
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-ui-1.9.1.custom.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijexpander.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>
	
    <script type="text/javascript">
    	
    	var queue_Nm;
    	var type;
    	var page = 0;
    	var rows = 50;
    	
		function callback(trnId, data) {
			
			if (trnId == 'QUERY') {
				var index = page * rows + 1
				for(var i=0; i<=data.length; i++) {
					jQuery("#list").jqGrid('addRowData', i+index, data[i]);
				}
				if (data.length > 0) {
					page++;
				} 
			}
			
			if (trnId == 'DELETES') {
				if (data.result == 'false') {
					alert('删除失败');
				} else {
					alert('删除成功');
					querystart();
				}
			}
		}
    
		function init() {
			
			$('#query').button().click(function() {
				querystart();
			});
			$('#edit').button().click(function() {
				edit();
			});
			$('#deletes').button().click(function() {
				deletes();
			});
			$('#cancel').button().click(function() {
				top.closeDialog(false);
			});
			
			$("#expander").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(358, "form", "expander", "list");},
				afterCollapse: function (e){collapseAdjustGrid(358, "expander", "list");}
			});
			
			var urlParamObject = getURLParamObject();
			if (urlParamObject != null) {				
				$('#form :input').each(function(i){
					if (urlParamObject[$(this).attr('id')] != null) {					
						$(this).val(urlParamObject[$(this).attr('id')]);
					}
				});
			}
			
			queue_Nm = urlParamObject['queue_Nm'];
			type = urlParamObject['type'];
			
			$('#list').jqGrid({
				datatype: 'local',
			   	colNames: [
					'队列名称',
					'唯一号',
					'消息内容',
					'开始时间',
					'结束时间',
					'DB',
					'Id',
					'ContentId'
					],
			   	colModel: [
					{name:'queueName', index:'queueName', width:180, align: 'center'},
					{name:'uuid', index:'uuid', width:260, align: 'center'},
					{name:'content', index:'content', width:800, align: 'left'},
					{name:'beginTime', index:'begin_Time', width:200, align: 'center'},
					{name:'finishTime', index:'finish_Time', width:200, align: 'center'},
					{name:'db', index:'db', width:60, align: 'center'},
					{name:'id', index:'id', width:60, align: 'center', hidden:true},
					{name:'contentId', index:'contentId', width:60, align: 'center', hidden:true},
				],
			    autowidth: true,
			    shrinkToFit: false,
			    height: 358,
			    multiselect: true,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
			    caption: '查询结果',
			    gridComplete:function(){
			    	repaintGrid(358, "expander", "list");
			    }
			});
			
			$(window).resize(function(){ 
				repaintGrid(358, "expander", "list");
				$("#list").setGridWidth($(window).width() - 14);
				
			});
			
			$(".ui-jqgrid-bdiv").css({'overflow-y':'scroll'});
			$(".ui-jqgrid-bdiv").scroll(function() {
				var scrollLen = Number($(".ui-jqgrid-bdiv").scrollTop());
				var divHeight = Number($(".ui-jqgrid-bdiv").height());
				var tableHeight = Number($("#list").height());
				if (scrollLen > 0 && scrollLen + divHeight - 17 >= tableHeight) {
					query(page);
				}
			});
			
			querystart();
		}
		
		function querystart() {
			page = 0;
			jQuery("#list").clearGridData();
			query(page)
		}
		
		function query(page) {
			
			var vo = new Object();
			$('#form :input').each(function(i){
				vo[$(this).attr('id')] = $(this).val();
			});
			
			vo['queueName'] = queue_Nm;
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			
			var url;
			
			if (type == 2) {
				url = '<%=request.getContextPath()%>/console/message/queryportionmessage?page=' + page + '&rows=' + rows;
			}
			
			transaction({
				id: 'QUERY',
				url: url,
				jsonSet: jsonSet
			});
		}
		
		function edit() {
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
			top.openDialogChild('详细消息', '<%=request.getContextPath()%>/view/monitor/queue/message_edit.jsp', ret, 400, 700, querystart);					
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
				url: '<%=request.getContextPath()%>/console/message/deletesportionmessage',
				jsonSet: jsonSet
			});	
		}
	</script>
  </head>

  <body onload="init();">
	<div>
		<button id="query">查询</button>
		<button id="edit">详细</button>
		<button id="deletes">删除</button>
		<button id="cancel">取消</button>
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
			    	<th><label>唯一号</label></th>
			    	<td><input type="text" id="uuid" maxlength="32"/></td>
			    	<th><label>时间区间</label></th>
			    	<td>
			    		<select id="region">
			    			<option value="">--select--</option>
							<option value="600000">10分钟</option>
							<option value="1800000">30分钟</option>
							<option value="3600000">1小时</option>
							<option value="21600000">6小时</option>
							<option value="43200000">12小时</option>
							<option value="86400000">1天</option>
							<option value="604800000">1周</option>
							<option value="2592000000">1个月(30天)</option>
			    		</select>
			    	</td>
			    	<th><label>消息内容</label></th>
			    	<td><input type="text" id="content" maxlength="128"/></td>
			    	<th><label>&nbsp;</label></th>
			    	<td>&nbsp;</td>
				</tr>
			</table>
		</div>
	</div>
	<div id="list_div" style="width:auto;">
		<table id="list"></table>
		<!-- <div id="pager"></div> -->
	</div>
  </body>
</html>
