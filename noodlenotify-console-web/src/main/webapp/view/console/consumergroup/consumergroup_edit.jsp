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
				$('#consumerGroup_Nm').attr('disabled', 'true');
				initModel = 'UPDATE';
			} else {
				initModel = 'INSERT';
			}
		}
		
		function check() {
			
			var notNullArray = [
			                    'consumerGroup_Nm', 
			                    'manual_Status'
			                    ];
			
			for (var i=0; i<notNullArray.length; i++) {
				var id = notNullArray[i];
				if ($('#' + id).val() == '') {
					alert($('#' + id + '_Label').text() + "不能为空");
					return false;
				}
			}
			
			if($("#consumerGroup_Nm").val() !== ''){
				var reg = /^(\d|[a-z|A-Z])+$/;
				var r = $("#consumerGroup_Nm").val().match(reg); 
				if(r === null){
					alert('请输入正确的消费者组名称(字母或数字)');
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
				var value = $(this).val();
				if(value !== ""){
					vo[$(this).attr('id')] = $(this).val();
				}
			});
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			if (initModel == 'INSERT') {	
				transaction({
					id: 'INSERT',
					url: '<%=request.getContextPath()%>/console/consumergroup/insert',
					jsonSet: jsonSet
				});
			} else {
				transaction({
					id: 'UPDATE',
					url: '<%=request.getContextPath()%>/console/consumergroup/update',
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
			    	<th><label id="consumerGroup_Nm_Label">消费组名称</label></th>
			    	<td><input type="text" id="consumerGroup_Nm" maxlength="32"/></td>
			    	<th><label id="manual_Status_Label">控制状态</label></th>
			    	<td>
			    		<select id="manual_Status">
			    			<option value="">--select--</option>
			    			<option value="1">有效</option>
			    			<option value="2">无效</option>
			    		</select>
			    	</td>
				</tr>
			</table>
		</div>
	</div>
  </body>
</html>
