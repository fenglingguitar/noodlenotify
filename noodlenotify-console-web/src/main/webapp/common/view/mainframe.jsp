<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>NoodleNotify 配管中心</title>
    
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijsuperpanel.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijmenu.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijdialog.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijtabs.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijsplitter.css" rel="stylesheet" type="text/css" />

	<script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-ui-1.9.1.custom.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery.mousewheel.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery.bgiframe-2.1.3-pre.js" type="text/javascript"></script>
    
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijutil.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijsuperpanel.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijmenu.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijdialog.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijtabs.js" type="text/javascript"></script>  
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijsplitter.js" type="text/javascript"></script>  
    <script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>    
    

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
		}
		.container
	    {
	    	width: 100%;
	    	height: 100%;
	    }
	    .ui-widget 
		{ 
			font-size: 0.8em; 
		}
		.header
		{
			margin:0;
			padding:5px 0 5px 10px;
		}
    	.menuer
		{
		}
		.wijmo-wijmenu
		{
		    margin-bottom: 8px;
		}
		#tabs
		{
			margin-top: 0em;
		}
		#tabs li .ui-icon-close
		{
			float: left;
			margin: 0.2em 0.2em 0 0;
			cursor: pointer;
		}
		#add_tab
		{
			cursor: pointer;
		}
        #splitter
        {
            width: 100%;
        }
        h2
        {
        	
        }
        #splitter .ui-widget-content 
        {
        	border: 0px;
        	background: #606154;
        }
        .ui-tabs
		{
		    position: relative;
		    padding: .1em;
		    zoom: 1;
		    border: 0px;
		    background: #606154;
		}
		.ui-tabs .ui-tabs-nav
		{
		    margin: 0;
		    padding: .1em .1em 0;
		}
		.ui-tabs .ui-tabs-nav li a
		{
		    float: left;
		    padding: .2em 1em;
		    text-decoration: none;
		}
		.ui-tabs .ui-tabs-panel
		{
		    display: block;
		    border-width: 0;
		    padding: 0em 0em;
		    background: none;
		}
        .wijmo-wijmenu 
        {
		    margin-bottom: 3px;
		    padding: 0.2em 0.3em 0.2em 0.3em;
		}
		.wijmo-wijmenu-horizontal .wijmo-wijmenu-link 
		{
			padding: 0.3em 0.4em;
		}
		.menuer ul li:first-child 
		{
			padding-right:15px;
		}
	</style>
    <script type="text/javascript">
    	
    	var $tabs;
    	
    	var iframeHeight;
    	var treeIframeHeight;
    	
	    $(document).ready(function() {
	    	
	        $("#menu").wijmenu();
	        
	    	var windowHeight = Number($(window).height());
	        //var headerHeight = Number($(".header").css("height").slice(0, -2));
	        var headerHeight = 0;
	        var menuerHeight = Number($(".menuer").css("height").slice(0, -2))
	        var splitterHeight = windowHeight - headerHeight - menuerHeight - 3;
	        $("#splitter").css("height", splitterHeight + "px");
	        
	        $("#splitter").wijsplitter({ 
	        	orientation: "vertical", 
	        	fullSplit: false,
	        	splitterDistance: 210,
	        	panel1: {
					collapsed: true
				},
	        	expanded: function () { 
		        	$("#tree-iframe").css("width", "100%");
	        		$("#tree-iframe").css("width", $("#tree-iframe").parent().css("width"));
	        		$("#tree-iframe").css("height", treeIframeHeight + "px");
	        	}
	        });
	        
	        iframeHeight = splitterHeight - 30;
	        if (Sys.ie) {
	        	iframeHeight = iframeHeight - 3;
	        } else {
	        	iframeHeight = iframeHeight - 0;
	        }
	        $("iframe").css("height", iframeHeight + "px");
	        
	        treeIframeHeight = splitterHeight;
	        if (Sys.ie) {
	        	treeIframeHeight = splitterHeight - 6;
	        } else {
	        	treeIframeHeight = splitterHeight - 2;
	        }
	        $("#tree-iframe").css("height", treeIframeHeight + "px");
			
			$("#tabs").wijtabs({ scrollable: false });
			
			$tabs = $('#tabs').wijtabs({
				tabTemplate: '<li><a href="#' + '{href}">#' + '{label}</a><span class="ui-icon ui-icon-close">Remove Tab</span></li>'
			});
			
			$('#tabs span.ui-icon-close').live('click', function () {
				var index = $('li', $tabs).index($(this).parent());
				$tabs.wijtabs('remove', index);
			});
	    });
	    
		function addTab(tabId, title, url) {
			var tabId = '#tab-' + tabId;
			if ($(tabId).size() == 0) {
				$tabs.wijtabs('add', tabId, title);
				$(tabId).html('<iframe src="' + url + '" width="100%" height="' + iframeHeight + '" frameborder="0"></iframe>');
				$tabs.find('[href="' + tabId + '"]').click();
			} else {
				$tabs.find('[href="' + tabId + '"]').click();
			}
		}
		
		var dialogCallback = null;
		
		function openDialog(title, url, urlParam, height, width, callback) {
			height = height != null && height != '' ? height : window.screen.height - 190;
			width = width != null && width != '' ? width : window.screen.width - 300;
			if (urlParam != null && urlParam != '') {
				var strParam = jsonToString(urlParam);
				url = appendURLParam(url, 'urlParam', strParam);
			}
			$('#dialog').wijdialog({
				showStatus: false,
				showControlBox: false,
				autoOpen: false,
				modal: true,
				height: height,
				width: width,
				title: title,
				buttons: {
					'Cancel': function () {
						$(this).wijdialog('close');
					}
				},
				close: function () {
					$('#dialog').empty();
				}
			});
			$('#dialog').append('<iframe src="'+ url +'" width="100%" height="100%" frameborder="0"></iframe>');
			$('#dialog').wijdialog('open');
			dialogCallback = callback;
		}
		
		function closeDialog(isCallback) {
			$('#dialog').wijdialog('close');
			if (isCallback) {
				if (dialogCallback != null) {
					dialogCallback();
				}
			}
		}
		
		var dialogCallbackChild = null;
		
		function openDialogChild(title, url, urlParam, height, width, callback) {
			height = height != null && height != '' ? height : window.screen.height - 190;
			width = width != null && width != '' ? width : window.screen.width - 300;
			if (urlParam != null && urlParam != '') {
				var strParam = jsonToString(urlParam);
				url = appendURLParam(url, 'urlParam', strParam);
			}
			$('#dialogChild').wijdialog({
				showStatus: false,
				showControlBox: false,
				autoOpen: false,
				modal: true,
				height: height,
				width: width,
				title: title,
				buttons: {
					'Cancel': function () {
						$(this).wijdialog('close');
					}
				},
				close: function () {
					$('#dialogChild').empty();
				}
			});
			$('#dialogChild').append('<iframe src="'+ url +'" width="100%" height="100%" frameborder="0"></iframe>');
			$('#dialogChild').wijdialog('open');
			dialogCallbackChild = callback;
		}
		
		function closeDialogChild(isCallback, param) {
			$('#dialogChild').wijdialog('close');
			if (isCallback) {
				if (dialogCallbackChild != null) {
					dialogCallbackChild(param);
				}
			}
		}
	    
	</script>
  </head>
  <body>
	<div class="container">
		<!-- <div class="header">
            <h2>NoodleNotify</h2>
        </div> -->
		<div class="menuer">
            <ul id="menu">
                <li><a href="#" onclick="javascript:addTab('mainframe_home', 'Home', '<%=request.getContextPath()%>/common/view/welcome.jsp');"><h2>NoodleNotify</h2></a></li>
                <li><a href="#">系统配置</a>
                    <ul>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_1', '系统-队列', '<%=request.getContextPath()%>/view/console/queue/queue_main.jsp');">系统-队列</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_2', '生产者', '<%=request.getContextPath()%>/view/console/producer/producer_main.jsp');">生产者</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_3', '交换中心', '<%=request.getContextPath()%>/view/console/exchanger/exchanger_main.jsp');">交换中心</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_4', '分配中心', '<%=request.getContextPath()%>/view/console/distributer/distributer_main.jsp');">分配中心</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_5', '数据库', '<%=request.getContextPath()%>/view/console/db/db_main.jsp');">数据库</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_6', '队列缓存', '<%=request.getContextPath()%>/view/console/msgqueuecache/msgqueuecache_main.jsp');">队列缓存</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_7', '消息体缓存', '<%=request.getContextPath()%>/view/console/msgbodycache/msgbodycache_main.jsp');">消息体缓存</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_8', '消费者组', '<%=request.getContextPath()%>/view/console/consumergroup/consumergroup_main.jsp');">消费者组</a></li>
	                    <li><a href="#" onclick="javascript:addTab('mainframe_1_9', '消费者', '<%=request.getContextPath()%>/view/console/consumer/consumer_main.jsp');">消费者</a></li>
                   </ul>
                </li>
                <li><a href="#">系统监控</a>
                    <ul>
		                <li><a href="#" onclick="javascript:addTab('mainframe_2_1_1', '监控-队列', '<%=request.getContextPath()%>/view/monitor/queue/queue_main.jsp');">监控-队列</a></li>
                    	<li><a href="#">超时</a>
                    		<ul>
		                        <li><a href="#" onclick="javascript:addTab('mainframe_2_2_1', '超时次数', '<%=request.getContextPath()%>/view/monitor/overtime/count/overtime_count_chart.jsp');">超时次数</a></li>
		                        <li><a href="#" onclick="javascript:addTab('mainframe_2_2_2', '超时时间', '<%=request.getContextPath()%>/view/monitor/overtime/time/overtime_time_chart.jsp');">超时时间</a></li>
                    		</ul>
                		</li>
                		<li><a href="#">错误</a>
                    		<ul>
								<li><a href="#" onclick="javascript:addTab('mainframe_2_3_1', '错误次数', '<%=request.getContextPath()%>/view/monitor/success/count/success_count_chart.jsp');">错误次数</a></li>
                    		</ul>
                		</li>
                    </ul>
                </li>
            </ul>
        </div>
		<div id="splitter">
			<div>
				<iframe id="tree-iframe"
					src="<%=request.getContextPath()%>/common/view/menutree.jsp"
					width="100%" height="480px" frameborder="0"></iframe>
			</div>
			<div>
				<div id="tabs">
					<ul>
						<li><a href="#tab-mainframe_home">Home</a></li>
					</ul>
					<div id="tab-mainframe_home">
						<iframe
							src="<%=request.getContextPath()%>/common/view/welcome.jsp"
							width="100%" height="480px" frameborder="0"></iframe>
					</div>
				</div>
			</div>
		</div>
		<div id="dialog" style="width:100%;"></div>
		<div id="dialogChild" style="width:100%;"></div>
	</div>
  </body>
</html>
