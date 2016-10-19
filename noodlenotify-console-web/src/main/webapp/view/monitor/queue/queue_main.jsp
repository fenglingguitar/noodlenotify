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
		}
    	
		function init() {
			
			$('#query').button().click(function() {
				query();
			});
			
			$("#expander").wijexpander({
				expanded: false,
				beforeExpand: function (e){expandAdjustGrid(248, "form", "expander", "list");},
				afterCollapse: function (e){collapseAdjustGrid(248, "expander", "list");}
			});
			
			$('#list').jqGrid({
		   		url: '<%=request.getContextPath()%>/console/queue/querymonitorpage',
				datatype: 'local',
				mtype: 'post',
			   	colNames: [
					'队列名称', 
					'控制状态',
					'新消息数量',
					'重试消息数量',
					'1分钟接收吞吐量',
					'1分钟接收超时率(%)',
					'1分钟接收错误率(%)',
					'1小时接收吞吐量',
					'1小时接收超时率(%)',
					'1小时接收错误率(%)',
					'1分钟分配吞吐量',
					'1分钟分配超时率(%)',
					'1分钟分配错误率(%)',
					'1小时分配吞吐量',
					'1小时分配超时率(%)',
					'1小时分配错误率(%)',
					'1分钟过期数量',
					'1小时过期数量'
					],
			   	colModel: [
					{name:'queue_Nm', index:'queue_Nm', width:300, align: 'center'},
			   		{name:'manual_Status', index:'manual_Status', width:100, align:'center', formatter:'select', editoptions:{value:'1:有效;2:无效'}},
					{name:'new_Len', index:'new_Len', width:150, align: 'center'},
					{name:'portion_Len', index:'portion_Len', width:150, align: 'center', formatter:portionLenOperator},
					{name:'rev_T_Cnt_Mit', index:'rev_T_Cnt_Mit', width:150, align: 'center'},
					{name:'rev_O_Rate_Mit', index:'rev_O_Rate_Mit', width:150, align: 'center'},
					{name:'rev_E_Rate_Mit', index:'rev_E_Rate_Mit', width:150, align: 'center'},
					{name:'rev_T_Cnt_Hor', index:'rev_T_Cnt_Hor', width:150, align: 'center'},
					{name:'rev_O_Rate_Hor', index:'rev_O_Rate_Hor', width:150, align: 'center'},
					{name:'rev_E_Rate_Hor', index:'rev_E_Rate_Hor', width:150, align: 'center'},
					{name:'dph_T_Cnt_Mit', index:'dph_T_Cnt_Mit', width:150, align: 'center'},
					{name:'dph_O_Rate_Mit', index:'dph_O_Rate_Mit', width:150, align: 'center'},
					{name:'dph_E_Rate_Mit', index:'dph_E_Rate_Mit', width:150, align: 'center'},
					{name:'dph_T_Cnt_Hor', index:'dph_T_Cnt_Hor', width:150, align: 'center'},
					{name:'dph_O_Rate_Hor', index:'dph_O_Rate_Hor', width:150, align: 'center'},
					{name:'dph_E_Rate_Hor', index:'dph_E_Rate_Hor', width:150, align: 'center'},
					{name:'dph_OD_Cnt_Mit', index:'dph_OD_Cnt_Mit', width:150, align: 'center'},
					{name:'dph_OD_Cnt_Hor', index:'dph_OD_Cnt_Hor', width:150, align: 'center'}				
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
			    	repaintGrid(248, "expander", "list");
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
				url: '<%=request.getContextPath()%>/console/queue/querymonitorpage',
				datatype: 'json',
				postData:{'input': jsonToString(vo)}, 
		        page: 1   
		    }).trigger('reloadGrid');
		}
		
		function portionLenOperator(cellvalue, options, rawObject) {
			if (cellvalue > 0) {
				return '<a href="javascript:queryPortionLen(\'' + rawObject['queue_Nm'] + '\')" style="color:#CC3300;">' + cellvalue + '</a>';
			}
			return cellvalue;
		}
		
		function queryPortionLen(queue_Nm) {
			var paramObject = new Object();
			paramObject['queue_Nm'] = queue_Nm;
			paramObject['type'] = 2;
			top.openDialog('消息查询', '<%=request.getContextPath()%>/view/monitor/queue/message_main.jsp', paramObject, 600, 1100, null);			
		}
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" >
	<div>
		<button id="query">查询</button>
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
