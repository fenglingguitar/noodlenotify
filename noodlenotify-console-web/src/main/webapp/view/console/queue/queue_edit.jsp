<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>NoodleNotify-Child</title>
       
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/jqgrid/css/ui.jqgrid.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/css/my.css" rel="stylesheet" type="text/css" />
    
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-ui-1.9.1.custom.min.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/i18n/grid.locale-en.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/jqgrid/js/jquery.jqGrid.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>
	
    <script type="text/javascript">
		
    	var initModel;
    
		function callback(trnId, data) {
			
			if (trnId == 'INSERT') {
    			if (data.result == 'false') {
    				alert('新增失败');
    			} else {
    				alert('新增成功');
    				top.closeDialog(true);
    			}
			} else if (trnId == 'UPDATE') {
    			if (data.result == 'false') {
    				alert('修改失败');
    			} else {
    				alert('修改成功');
    				top.closeDialog(true);
    			}
			}
		}
    
		function init() {
			
			$('#save').button().click(function() {
				save()
			});
			$('#cancel').button().click(function() {
				top.closeDialog(false);
			});
			
			var urlParamObject = getURLParamObject();
			if (urlParamObject != null) {				
				$('#form :input').each(function(i){
					if (urlParamObject[$(this).attr('id')] != null) {					
						$(this).val(urlParamObject[$(this).attr('id')]);
					}
				});
				$('#queue_Nm').attr('disabled', 'true');
				initModel = 'UPDATE';
			} else {
				initModel = 'INSERT';
			}
		}
		
		function check() {
			
			var notNullArray = [
			                    'queue_Nm', 
			                    'is_Repeat',
			                    'expire_Time',
			                    'interval_Time',
			                    'dph_Delay_Time',
			                    'dph_Timeout',
			                    'new_Pop_ThreadNum',
			                    'new_Exe_ThreadNum',
			                    'portion_Pop_ThreadNum',
			                    'portion_Exe_ThreadNum',
			                    'manual_Status',
			                    ];
			
			for (var i=0; i<notNullArray.length; i++) {
				var id = notNullArray[i];
				if ($('#' + id).val() == '') {
					alert($('#' + id + '_Label').text() + "不能为空");
					return false;
				}
			}
			if($("#queue_Nm").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/;
				var r = $("#queue_Nm").val().match(reg); 
				if(r === null){
					alert('请输入正确的队列名称(字母或数字或".")');
					return false;
				}
			}
			
			return true;
		}
		
		function save() {
			
			if (!check()) {
				return;
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				vo[$(this).attr('id')] = $(this).val();
			});
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			
			if (initModel == 'INSERT') {				
				transaction({
					id: 'INSERT',
					url: '<%=request.getContextPath()%>/console/queue/insert',
					jsonSet: jsonSet
				});
			} else {
				transaction({
					id: 'UPDATE',
					url: '<%=request.getContextPath()%>/console/queue/update',
					jsonSet: jsonSet
				});				
			}
		}
		
	</script>
  </head>

  <body onload="init();" onkeydown="onEnterDown(save);" >
	<div>
		<button id="save">保存</button>
		<button id="cancel">取消</button>
	</div>
	<div>
		<div id="form" style="width: auto;">
			<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout_input">
				<colgroup>
					<col width="20%" />
					<col width="30%" />
					<col width="20%" />
					<col width="30%" />
				</colgroup>					
			    <tr>
			    	<th><label id="queue_Nm_Label">队列名称</label></th>
			    	<td><input type="text" id="queue_Nm" maxlength="32"/></td>
			    	<th><label id="is_Repeat_Label">是否重发</label></th>
			    	<td>
			    		<select id="is_Repeat">
			    			<option value="">--select--</option>
			    			<option value="1">是</option>
			    			<option value="0">否</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="expire_Time_Label">过期时间</label></th>
			    	<td>
			    		<select id="expire_Time">
			    			<option value="">--select--</option>
			    			<option value="0">永不</option>
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
			    	<th><label id="interval_Time_Label">间隔时间</label></th>
					<td>
			    		<select id="interval_Time">
			    			<option value="">--select--</option>
			    			<option value="10000">10秒</option>
			    			<option value="30000">30秒</option>
			    			<option value="60000">1分钟</option>
			    			<option value="120000">2分钟</option>
			    			<option value="180000">3分钟</option>
			    			<option value="300000">5分钟</option>
			    			<option value="600000">10分钟</option>
			    			<option value="900000">15分钟</option>
			    			<option value="1800000">30分钟</option>
			    			<option value="3600000">1小时</option>
			    			<option value="10800000">3小时</option>
			    			<option value="21600000">6小时</option>
			    			<option value="43200000">12小时</option>
			    			<option value="86400000">1天</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="dph_Delay_Time_Label">推送延迟时间</label></th>
			    	<td>
			    		<select id="dph_Delay_Time">
			    			<option value="">--select--</option>
			    			<option value="0">不延迟</option>
			    			<option value="1000">1秒</option>
			    			<option value="2000">2秒</option>
			    			<option value="3000">3秒</option>
			    			<option value="5000">5秒</option>
			    			<option value="10000">10秒</option>
			    			<option value="30000">30秒</option>
			    			<option value="60000">1分钟</option>
			    			<option value="120000">2分钟</option>
			    			<option value="180000">3分钟</option>
			    			<option value="300000">5分钟</option>
			    			<option value="600000">10分钟</option>
			    			<option value="900000">15分钟</option>
			    			<option value="1800000">30分钟</option>
			    			<option value="3600000">1小时</option>
			    			<option value="10800000">3小时</option>
			    			<option value="21600000">6小时</option>
			    			<option value="43200000">12小时</option>
			    			<option value="86400000">1天</option>
			    		</select>
			    	</td>
			    	<th><label id="dph_Timeout_Label">推送超时时间</label></th>
					<td>
			    		<select id="dph_Timeout">
			    			<option value="">--select--</option>
			    			<option value="1000">1秒</option>
			    			<option value="2000">2秒</option>
			    			<option value="3000">3秒</option>
			    			<option value="5000">5秒</option>
			    			<option value="10000">10秒</option>
			    			<option value="30000">30秒</option>
			    			<option value="60000">1分钟</option>
			    			<option value="120000">2分钟</option>
			    			<option value="180000">3分钟</option>
			    			<option value="300000">5分钟</option>
			    			<option value="600000">10分钟</option>
			    			<option value="900000">15分钟</option>
			    			<option value="1800000">30分钟</option>
			    			<option value="3600000">1小时</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="new_Pop_ThreadNum_Label">新消息POP线程数量</label></th>
			    	<td>
			    		<select id="new_Pop_ThreadNum">
			    			<option value="">--select--</option>
			    			<option value="1">1</option>
			    			<option value="2">2</option>
			    			<option value="3">3</option>
			    			<option value="3">3</option>
			    			<option value="4">4</option>
			    			<option value="5">5</option>
			    			<option value="6">6</option>
			    			<option value="7">7</option>
			    			<option value="8">8</option>
			    			<option value="9">9</option>
			    			<option value="10">10</option>
			    			<option value="15">15</option>
			    			<option value="20">20</option>
			    			<option value="25">25</option>
			    			<option value="30">30</option>
			    			<option value="35">35</option>
			    			<option value="40">40</option>
			    			<option value="45">45</option>
			    			<option value="50">50</option>
			    			<option value="60">60</option>
			    			<option value="70">70</option>
			    			<option value="80">80</option>
			    			<option value="90">90</option>
			    			<option value="100">100</option>
			    			<option value="100">100</option>
			    			<option value="300">300</option>
			    			<option value="100">100</option>
			    			<option value="500">500</option>
			    		</select>
			    	</td>
			    	<th><label id="new_Exe_ThreadNum_Label">新消息EXE线程数量</label></th>
			    	<td>
			    		<select id="new_Exe_ThreadNum">
			    			<option value="">--select--</option>
			    			<option value="1">1</option>
			    			<option value="2">2</option>
			    			<option value="3">3</option>
			    			<option value="3">3</option>
			    			<option value="4">4</option>
			    			<option value="5">5</option>
			    			<option value="6">6</option>
			    			<option value="7">7</option>
			    			<option value="8">8</option>
			    			<option value="9">9</option>
			    			<option value="10">10</option>
			    			<option value="15">15</option>
			    			<option value="20">20</option>
			    			<option value="25">25</option>
			    			<option value="30">30</option>
			    			<option value="35">35</option>
			    			<option value="40">40</option>
			    			<option value="45">45</option>
			    			<option value="50">50</option>
			    			<option value="60">60</option>
			    			<option value="70">70</option>
			    			<option value="80">80</option>
			    			<option value="90">90</option>
			    			<option value="100">100</option>
			    			<option value="100">100</option>
			    			<option value="300">300</option>
			    			<option value="100">100</option>
			    			<option value="500">500</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="portion_Pop_ThreadNum_Label">未完成消息POP线程数量</label></th>
			    	<td>
			    		<select id="portion_Pop_ThreadNum">
			    			<option value="">--select--</option>
			    			<option value="1">1</option>
			    			<option value="2">2</option>
			    			<option value="3">3</option>
			    			<option value="3">3</option>
			    			<option value="4">4</option>
			    			<option value="5">5</option>
			    			<option value="6">6</option>
			    			<option value="7">7</option>
			    			<option value="8">8</option>
			    			<option value="9">9</option>
			    			<option value="10">10</option>
			    			<option value="15">15</option>
			    			<option value="20">20</option>
			    			<option value="25">25</option>
			    			<option value="30">30</option>
			    			<option value="35">35</option>
			    			<option value="40">40</option>
			    			<option value="45">45</option>
			    			<option value="50">50</option>
			    			<option value="60">60</option>
			    			<option value="70">70</option>
			    			<option value="80">80</option>
			    			<option value="90">90</option>
			    			<option value="100">100</option>
			    			<option value="100">100</option>
			    			<option value="300">300</option>
			    			<option value="100">100</option>
			    			<option value="500">500</option>
			    		</select>
			    	</td>
			    	<th><label id="portion_Exe_ThreadNum_Label">未完成消息EXE线程数量</label></th>
			    	<td>
			    		<select id="portion_Exe_ThreadNum">
			    			<option value="">--select--</option>
			    			<option value="1">1</option>
			    			<option value="2">2</option>
			    			<option value="3">3</option>
			    			<option value="3">3</option>
			    			<option value="4">4</option>
			    			<option value="5">5</option>
			    			<option value="6">6</option>
			    			<option value="7">7</option>
			    			<option value="8">8</option>
			    			<option value="9">9</option>
			    			<option value="10">10</option>
			    			<option value="15">15</option>
			    			<option value="20">20</option>
			    			<option value="25">25</option>
			    			<option value="30">30</option>
			    			<option value="35">35</option>
			    			<option value="40">40</option>
			    			<option value="45">45</option>
			    			<option value="50">50</option>
			    			<option value="60">60</option>
			    			<option value="70">70</option>
			    			<option value="80">80</option>
			    			<option value="90">90</option>
			    			<option value="100">100</option>
			    			<option value="100">100</option>
			    			<option value="300">300</option>
			    			<option value="100">100</option>
			    			<option value="500">500</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="manual_Status_Label">控制状态</label></th>
			    	<td>
			    		<select id="manual_Status">
			    			<option value="">--select--</option>
			    			<option value="1">有效</option>
			    			<option value="2">无效</option>
			    		</select>
			    	</td>
			    	<th>&nbsp;</th>
					<td>&nbsp;</td>
				</tr>
			</table>
		</div>
	</div>
  </body>
</html>
