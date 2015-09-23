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
				beforeExpand: function (e){expandAdjustGrid(230, "form", "expander", "list");},
				afterCollapse: function (e){collapseAdjustGrid(230, "expander", "list");}
			});
			
			$('#list').jqGrid({
		   		url: '<%=request.getContextPath()%>/trace/msg/querylist',
		   		datatype: 'local',
				mtype: 'post',
			   	colNames: [
			   	        '唯一号',
						'时间',
						'动作类型', 
						'结果',
						'跟踪模块',
						'跟踪模块编号',
						'处理模块',
						'处理模块编号'
					],
			   	colModel: [
					{name:'uuid', index:'uuid', width:260, align: 'center'},
					{name:'timestamp', index:'timestamp', width:200, align: 'center', formatter:'date', 
						formatoptions:{srcformat:'u', newformat:'Y-m-d H:i:s'}},
					{name:'action', index:'action', width:200, align: 'center', formatter:'select', 
						editoptions:{value:'1:数据库插入;2:交换中心接收;3:发送消费者;4:分配中心分派;5:分配中心再次分派;'}},
					{name:'result', index:'result', width:100, align: 'center', formatter:'select', 
						editoptions:{value:'1:成功;0:失败;2:部分成功;3:消息已过期;4:更新数据库失败;'}},
					{name:'traceModuleType', index:'traceModuleType', width:100, align: 'center', formatter:'select', 
						editoptions:{value:'1:交换中心;2:分配中心;3:数据库;4:消费者;5:消费者组;'}},
					{name:'traceModuleId', index:'traceModuleId', width:120, align: 'center'},
					{name:'dealModuleType', index:'dealModuleType', width:100, align: 'center', formatter:'select', 
						editoptions:{value:'1:交换中心;2:分配中心;3:数据库;4:消费者;5:消费者组;'}},
					{name:'dealModuleId', index:'dealModuleId', width:120, align: 'center'}
			   	],
			   	rowNum: 1537228672809129,
			   	pager: '#pager',
			   	sortname: 'id',
			    viewrecords: true,
			    autowidth: true,
			    shrinkToFit: false,
			    height: 230,
			    sortorder: 'desc',
			    multiselect: false,
			    jsonReader: {
					repeatitems : false
				},
				ondblClickRow: false,
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
		
		function check() {
			
			var notNullArray = [
			                    'uuid'
			                   ];
			
			for (var i=0; i<notNullArray.length; i++) {
				var id = notNullArray[i];
				if ($('#' + id).val() == '') {
					alert($('#' + id + '_Label').text() + "不能为空");
					return false;
				}
			}
			if($("#uuid").val() !== ''){
				var reg = /^(\d|[a-z|A-Z])+$/;
				var r = $("#uuid").val().match(reg); 
				if(r === null){
					alert('请输入正确的唯一号(字母或数字)');
					return false;
				}
			}
			
			return true;
		}
		
		function query() {
			
			if (!check()) {
				return;
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				if ($(this).val() != '') {					
					vo[$(this).attr('id')] = $(this).val();
				}
			 });
			
			$('#list').jqGrid('setGridParam', {   
				url: '<%=request.getContextPath()%>/trace/msg/querylist',
				datatype: 'json',
				postData:{'input': jsonToString(vo)}, 
				page: 1   
		    }).trigger('reloadGrid');
		}
		
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" >
	<div>
		<button id="query">查询</button>
	</div>
	<div>
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
				    	<th><label id="uuid_Label">唯一号</label></th>
				    	<td colspan=3><input type="text" id="uuid" maxlength="32"/></td>
				    	<th><label>&nbsp;</label></th>
				    	<td>&nbsp;</td>
				    	<th><label>&nbsp;</label></th>
				    	<td>&nbsp;</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div id="list_div" style="width:auto;">
		<table id="list"></table>
		<div id="pager"></div>
	</div>
  </body>
</html>
