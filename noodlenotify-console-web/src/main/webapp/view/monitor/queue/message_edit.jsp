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

    	var contentCoat;
    
		function callback(trnId, data) {
			
			if (trnId == 'SAVE') {
    			if (data.result == 'false') {
    				alert('保存失败');
    			} else {
    				alert('保存成功');
    				top.closeDialogChild(true);
    			}
			}
		}
    
		function init() {
			
			$('#save').button().click(function() {
				save();
			});
			$('#cancel').button().click(function() {
				top.closeDialog(false);
			});
			
			var urlParamObject = getURLParamObject();
			if (urlParamObject != null) {				
				$('#form :input').each(function(i){
					if (urlParamObject[$(this).attr('id')] != null) {	
						if ($(this).attr('id') == 'content') {
							contentCoat = stringToJson(urlParamObject[$(this).attr('id')]);
							$(this).val(contentCoat['content']);
						} else {							
							$(this).val(urlParamObject[$(this).attr('id')]);
						}
					}
				});
				$('#queueName').attr('disabled', 'true');
				$('#uuid').attr('disabled', 'true');
			}
		}
		
		function check() {
			
			var notNullArray = [
			                    'content'
			                    ];
			
			for (var i=0; i<notNullArray.length; i++) {
				var id = notNullArray[i];
				if ($('#' + id).val() == '') {
					alert($('#' + id + '_Label').text() + "不能为空");
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
			vo['queueName'] = $('#queueName').val();
			vo['contentId'] = $('#contentId').val();
			
			if (contentCoat['content'] == $('#content').val()) {
				alert('未作修改');
				return;
			}
			
			contentCoat['content'] = $('#content').val();
			vo['content'] = jsonToString(contentCoat);
			
			vo['db'] = $('#db').val();
			
			var jsonSet = new JsonSet();
			jsonSet.put('input', vo);
			
			transaction({
				id: 'SAVE',
				url: '<%=request.getContextPath()%>/console/message/saveportionmessage',
				jsonSet: jsonSet
			});
		}
		
	</script>
  </head>

  <body onload="init();">
	<div>
		<button id="save">保存</button>
		<button id="cancel">取消</button>
	</div>
	<div>
		<div id="form" style="width: auto;">
			<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout_input">
				<colgroup>
					<col width="20%" />
					<col width="80%" />
				</colgroup>					
			    <tr>
			    	<th><label id="queue_Nm_Label">队列名称</label></th>
			    	<td><input type="text" id="queueName" maxlength="32"/></td>
				</tr>
				<tr>
			    	<th><label id="content_Label">发送内容</label></th>
			    	<td>
			    		<textarea id="content" rows="12" cols="50"></textarea>
			    	</td>
				</tr>
				<tr>
			    	<th><label id="uuid_Label">唯一号</label></th>
			    	<td><input type="text" id="uuid" maxlength="32"/></td>
				</tr>
			</table>
			<input type="hidden" id="db"/>
			<input type="hidden" id="contentId"/>
		</div>
	</div>
  </body>
</html>
