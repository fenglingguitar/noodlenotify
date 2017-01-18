<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>Monitor-Child</title>
       
	<link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
	<link href="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/wijcheckbox/jquery.wijmo.wijcheckbox.css" rel="stylesheet" type="text/css" />
	<link href="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/wijtree/jquery.wijmo.wijtree-my.css" rel="stylesheet" type="text/css" />

	<script src="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/external/jquery-1.9.1.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/external/jquery-ui-1.10.1.custom.min.js" type="text/javascript"></script>
		
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/wijutil/jquery.wijmo.wijutil.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/Base/jquery.wijmo.widget.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-pro/Wijmo/wijtree/jquery.wijmo.wijtree.js" type="text/javascript"></script>
	
	<style type="text/css">
    	html
    	{
			margin:0; 
			padding:0;
		}
		*{
			margin:0;
			padding:0;
		}
		body
		{
			margin:0;
			padding:0;
			height: 100%;
			width: 100%;
		}
		.container
		{
			margin:0;
			padding:0;
		}
		.ui-widget 
		{ 
			font-size: 0.8em; 
		}
		.ui-widget-content 
		{
			background: #606154;
			color: #f1f1f1;
		}
		.wijmo-wijtree .wijmo-wijtree-list .wijmo-wijtree-node {
		    float: none;
		}
		.ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
		    background: #9eca38 url(images/ui-bg_highlight-soft_15_9eca38_1x100.png) 50% 50% repeat-x;
		    border: 1px solid #9eca38;
		    color: #304915;
		    -moz-box-shadow: inset 0px 1px 0 rgba(255, 255, 255, 0.15);
		    -webkit-box-shadow: inset 0px 1px 0 rgba(255, 255, 255, 0.15);
		    box-shadow: inset 0px 1px 0px rgba(255,255,255,0.15);
		    text-shadow: 1px 1px 0px rgba(255,255,255,0.5);
		}
	</style>
    <script type="text/javascript">
    	 $(document).ready(function () {
             $("#tree").wijtree();
         });
	</script>
  </head>

  <body>
  	<div class="container">
		<ul id="tree">
           	<li><a href="#">系统配置</a>
               <ul>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_1', '系统-队列', '<%=request.getContextPath()%>/view/console/queue/queue_main.jsp');">系统-队列</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_2', '生产者', '<%=request.getContextPath()%>/view/console/producer/producer_main.jsp');">生产者</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_3', '交换中心', '<%=request.getContextPath()%>/view/console/exchanger/exchanger_main.jsp');">交换中心</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_4', '分配中心', '<%=request.getContextPath()%>/view/console/distributer/distributer_main.jsp');">分配中心</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_5', '数据库', '<%=request.getContextPath()%>/view/console/db/db_main.jsp');">数据库</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_6', '队列缓存', '<%=request.getContextPath()%>/view/console/msgqueuecache/msgqueuecache_main.jsp');">队列缓存</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_7', '消息体缓存', '<%=request.getContextPath()%>/view/console/msgbodycache/msgbodycache_main.jsp');">消息体缓存</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_8', '消费者组', '<%=request.getContextPath()%>/view/console/consumergroup/consumergroup_main.jsp');">消费者组</a></li>
                <li><a href="#" onclick="javascript:top.addTab('mainframe_1_9', '消费者', '<%=request.getContextPath()%>/view/console/consumer/consumer_main.jsp');">消费者</a></li>
              </ul>
           </li>
           <li><a href="#">系统监控</a>
              <ul>
             	<li><a href="#" onclick="javascript:top.addTab('mainframe_2_1_1', '监控-队列', '<%=request.getContextPath()%>/view/monitor/queue/queue_main.jsp');">监控-队列</a></li>
               	<li><a href="#">超时</a>
               		<ul>
                     <li><a href="#" onclick="javascript:top.addTab('mainframe_2_2_1', '超时次数', '<%=request.getContextPath()%>/view/monitor/overtime/count/overtime_count_chart.jsp');">超时次数</a></li>
                     <li><a href="#" onclick="javascript:top.addTab('mainframe_2_2_2', '超时时间', '<%=request.getContextPath()%>/view/monitor/overtime/time/overtime_time_chart.jsp');">超时时间</a></li>
               		</ul>
           		</li>
           		<li><a href="#">错误</a>
               		<ul>
				<li><a href="#" onclick="javascript:top.addTab('mainframe_2_3_1', '错误次数', '<%=request.getContextPath()%>/view/monitor/success/count/success_count_chart.jsp');">错误次数</a></li>
               		</ul>
           		</li>
               </ul>
           </li>
      </ul>
	</div>
  </body>
</html>
