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
				initModel = 'UPDATE';
			} else {
				initModel = 'INSERT';
			}
		}
		
		function check() {
			
			var notNullArray = [
			                    'name', 
			                    'manual_Status', 
			                    'ip',
			                    'port',
			                    'check_Port'
			                    ];
			for (var i=0; i<notNullArray.length; i++) {
				var id = notNullArray[i];
				if ($('#' + id).val() == '') {
					alert($('#' + id + '_Label').text() + "不能为空");
					return false;
				}
			}
			
			if($("#ip").val() !== ''){
				var reg = /^(\d|[a-z|A-Z]|\.)+$/; 
				var r = $("#ip").val().match(reg); 
				if(r === null){
					alert('请输入正确的IP');
					return false;
				}
			}
			
			if($("#port").val() !== '' && isNaN($("#port").val())){
				alert('请输入正确的PORT');
				return false;
			}
			
			if($("#check_Port").val() !== '' && isNaN($("#check_Port").val())){
				alert('请输入正确的监测PORT');
				return false;
			}
			return true;
		}
		
		function save() {
			
			if (!check()) {
				return;
			}
			
			var vo = new Object();
			$('#form :input').each(function(i){
				var value = $(this).val();
				if(value !== ""){
					vo[$(this).attr('id')] = $(this).val();
				}
			});
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			if (initModel == 'INSERT') {	
				vo['system_Status'] = '2';
				transaction({
					id: 'INSERT',
					url: '<%=request.getContextPath()%>/console/exchanger/insert',
					jsonSet: jsonSet
				});
			} else {
				if (vo['manual_Status'] == '2') {
					vo['system_Status'] = '2';
				}
				transaction({
					id: 'UPDATE',
					url: '<%=request.getContextPath()%>/console/exchanger/update',
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
			<input type="hidden" id="exchanger_Id"/>
			<input type="hidden" id="system_Status"/>
			<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout_input">
				<colgroup>
					<col width="20%" />
					<col width="30%" />
					<col width="20%" />
					<col width="30%" />
				</colgroup>					
			    <tr>
			    	<th><label id="name_Label">交换中心名称</label></th>
			    	<td><input type="text" id="name" maxlength="32"/></td>
			    	<th><label id="manual_Status_Label">控制状态</label></th>
			    	<td>
			    		<select id="manual_Status">
			    			<option value="">--select--</option>
			    			<option value="1">有效</option>
			    			<option value="2">无效</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="ip_Label">IP</label></th>
			    	<td><input type="text" id="ip" maxlength="64"/></td>
			    	<th><label id="port_Label">PORT</label></th>
			    	<td><input type="text" id="port" maxlength="5"/></td>
				</tr>
				<tr>
			    	<th><label id="url_Label">URL</label></th>
			    	<td><input type="text" id="url" maxlength="512"/></td>
			    	<th><label id="type_Label">类型</label></th>
			    	<td>
			    		<select id="type">
			    			<option value="">--select--</option>
			    			<option value="NETTY">NETTY</option>
			    			<option value="HTTP">HTTP</option>
			    		</select>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="check_Port_Label">监测PORT</label></th>
			    	<td><input type="text" id="check_Port" maxlength="11"/></td>
			    	<th>&nbsp;</th>
					<td>&nbsp;</td>
				</tr>
			</table>
		</div>
	</div>
  </body>
</html>
