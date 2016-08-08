<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Monitor-Child</title>
       
    <link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/common/css/my.css" />
    
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
  	<script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>

    <script type="text/javascript">
    	
    	function callback(trnId, data, other) {
    	}
    	
    	function init() {
    	}
		
	</script>
  </head>

  <body onload="init();" >
	<div class="page-header">
	    <h2>Welcome</h2>
	</div>
	<div class="page-list">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<colgroup>
				<col width="15%" />
				<col width="85%" />
			</colgroup>					
		    <tr>
		    	<td valign="top">
			    	<ul>
						<li>1、系统配置
							<ul>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_1', '系统-队列', '<%=request.getContextPath()%>/view/console/queue/queue_main.jsp');">系统-队列</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_2', '生产者', '<%=request.getContextPath()%>/view/console/producer/producer_main.jsp');">生产者</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_3', '交换中心', '<%=request.getContextPath()%>/view/console/exchanger/exchanger_main.jsp');">交换中心</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_4', '分配中心', '<%=request.getContextPath()%>/view/console/distributer/distributer_main.jsp');">分配中心</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_5', '数据库', '<%=request.getContextPath()%>/view/console/msgstorage/msgstorage_main.jsp');">数据库</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_6', '队列缓存', '<%=request.getContextPath()%>/view/console/msgqueuecache/msgqueuecache_main.jsp');">队列缓存</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_7', '消息体缓存', '<%=request.getContextPath()%>/view/console/msgbodycache/msgbodycache_main.jsp');">消息体缓存</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_8', '消费者组', '<%=request.getContextPath()%>/view/console/customergroup/customergroup_main.jsp');">消费者组</a></li>
				               <li><a href="#" onclick="javascript:top.addTab('mainframe_1_9', '消费者', '<%=request.getContextPath()%>/view/console/customer/customer_main.jsp');">消费者</a></li>
							</ul>
						</li>
				    </ul>
		    	</td>
		    	<td valign="top">
			    	<ul>
						<li>2、系统监控
							<ul>
				               	<li><a href="#" onclick="javascript:top.addTab('mainframe_2_1_1', '监控-队列', '<%=request.getContextPath()%>/view/monitor/queue/queue_main.jsp');">监控-队列</a></li>
								<li>超时
									<ul>
					              		<li><a href="#" onclick="javascript:top.addTab('mainframe_2_2_1', '超时次数', '<%=request.getContextPath()%>/view/monitor/overtime/count/overtime_count_chart.jsp');">超时次数</a></li>
					               		<li><a href="#" onclick="javascript:top.addTab('mainframe_2_2_2', '超时时间', '<%=request.getContextPath()%>/view/monitor/overtime/time/overtime_time_chart.jsp');">超时时间</a></li>
									</ul>
								</li>
								<li>错误
									<ul>
					              		<li><a href="#" onclick="javascript:top.addTab('mainframe_2_3_1', '错误次数', '<%=request.getContextPath()%>/view/monitor/success/count/success_count_chart.jsp');">错误次数</a></li>
									</ul>
								</li>
						    </ul>
					    </li>
				    </ul>
		    	</td>
			</tr>
		</table>
  	</div>
  </body>
</html>
