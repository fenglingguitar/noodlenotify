<%@page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@include file="../../../../global.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <title>NoodleNotify-Child</title>
       
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/rocket/jquery-wijmo.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/jquery-ui/css/jqgrid/jquery-ui-1.9.1.custom.min.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/treeview/assets/skins/sam/treeview.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijsuperpanel.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/themes/wijmo/jquery.wijmo.wijexpander.css" rel="stylesheet" type="text/css" />
    <link href="<%=request.getContextPath()%>/common/css/my.css" rel="stylesheet" type="text/css" />
    
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-1.8.2.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/external/jquery-ui-1.9.1.custom.min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/treeview/yahoo-dom-event.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/treeview/connection-min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/treeview/treeview-min.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/highcharts/js/highcharts.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/highcharts/js/modules/exporting.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/highcharts/js/themes/mytheme.js" type="text/javascript"></script>
	<script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijutil.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijsuperpanel.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/tool/wijmo-open/development-bundle/wijmo/jquery.wijmo.wijexpander.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/js/common.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/common/js/tree.js" type="text/javascript"></script>
    
    <style>
		.filterTableTr{
			height: 32px;
		}
	</style>
    <script type="text/javascript">
    
	    var regionCurrent = 60;
	    var intervalStart = false;
	    var chartInfoMap;
	    var optionsMap;
	    var intervalProcessMap;
	    var intervalLastTimeMap;
	
		function callback(trnId, data, other) {
			
			if (trnId == "CHART_QUERY") {
				
				var options = optionsMap.get(other);
				
				if (options == null) {
					return;
				}
				
				var totalSeriesData = [];
				var errorSeriesData = [];
				var thresholdSeriesData = [];
				
				var i;
				
				for (i=0; i<data.length; i++) {
					totalSeriesData.push([data[i].timestamp, data[i].averageTotalTime]);
					errorSeriesData.push([data[i].timestamp, data[i].averageOvertimeTime]);
					thresholdSeriesData.push([data[i].timestamp, data[i].threshold]);
				}
				
				if (data.length > 0) {
					intervalLastTimeMap.put(other, data[i-1].timestamp);
				}
	
			 	options.series[0].data = totalSeriesData;
	            options.series[1].data = errorSeriesData;
	            options.series[2].data = thresholdSeriesData;
		    
		        new Highcharts.Chart(options);
		         	
			} else if (trnId == "GET_DATETIME") {
				intervalLastTimeMap.put(other, data.timestamp);
			}
		}

		function getQueue_Nm(){
			return $("#queue_Nm").val();
		}

		function init() {
			$('#filterBtn').button().click(function() {
				if(chartInfoMap){
					for(var key in chartInfoMap.getEntry()){
						if ($('#' + key).length > 0) {
							destroyChart({
								'renderTo': key
							});
							chartInfoMap.remove(key);
						}
					}
				}
				treeLeft = new Tree();
				treeLeft.buildTree({
					divId: 'treeDivLeft',
					treeType: 'checkbox-someone',
					rootName: '超时时间',
					rootId: 'OVERTIME',
					rootOther: 'OVERTIME',
					loadNow: false,
					baseUrl: '<%=request.getContextPath()%>/',
					reqUrl: 'monitor/tree/queryfirst',
					highlightFunc: treeHighlightEvent
				});

			});
			
			$('#query').button().click(function() {
				query();
			});
			$('#fullscreen').button().click(function() {
				fullscreen();
			});
			
			$("#expander").wijexpander({expanded: false});
			$("#expander-left").wijexpander({expanded: false});
			
			chartInfoMap = new Map();
			optionsMap = new Map();
		    intervalProcessMap = new Map();
		    intervalLastTimeMap = new Map();
			
			Highcharts.setOptions({
   	            global: {
   	                useUTC: false
   	            }
   	        });
			
			treeLeft = new Tree();
			treeLeft.buildTree({
				divId: 'treeDivLeft',
				treeType: 'checkbox-someone',
				rootName: '超时时间',
				rootId: 'OVERTIME',
				rootOther: 'OVERTIME',
				loadNow: false,
				baseUrl: '<%=request.getContextPath()%>/',
				reqUrl: 'monitor/tree/queryfirst',
				highlightFunc: treeHighlightEvent
			});
			
			var box = document.getElementById('box');
			var left = document.getElementById('left');
			var right = document.getElementById('right');
			var line = document.getElementById('line');
			var treeDiv = document.getElementById('treeDiv');
			var filterDiv = document.getElementById("filterDiv");

			line.onmousedown = function(e) {
				
				var disX = (e || event).clientX;
				line.left = line.offsetLeft;
				
				document.onmousemove = function(e) {  
					
					iT = line.left + ((e || event).clientX - disX);
		            var e=e||window.event,tarnameb=e.target||e.srcElement;
					var maxT = box.clientWidth - 50;
					line.style.margin = 0;
					iT < 50 && (iT = 50);
					iT > maxT && (iT = maxT);
					line.style.left = left.style.width = iT + 'px';
					treeDiv.style.width = iT - 8 + 'px';
					filterDiv.style.width = iT - 7 + 'px';
					right.style.width = box.clientWidth - iT - 3 + 'px';
					return false
				};	

				document.onmouseup = function() {
					document.onmousemove = null;
					document.onmouseup = null;	
					line.releaseCapture && line.releaseCapture()
				};

				line.setCapture && line.setCapture();

				return false
			};

			
			$('#catch_Tm_Start').datepicker({
	            dateFormat: 'yy-mm-dd'
	        });
			$('#catch_Tm_End').datepicker({
	            dateFormat: 'yy-mm-dd'
	        });
			for (var i=0; i<24; i++) {
				var time = i;
				if (time < 10) {
					time = '0' + i;
				}
				$('#catch_Tm_Hour_Start').append('<option value="' + time + '">' + time + '</option>');
			}
			for (var i=0; i<60; i++) {	
				var time = i;
				if (time < 10) {
					time = '0' + i;
				}
				$('#catch_Tm_Minute_Start').append('<option value="' + time + '">' + time + '</option>');
			}
			for (var i=0; i<24; i++) {
				var time = i;
				if (time < 10) {			
					time = '0' + i;
				}
				$('#catch_Tm_Hour_End').append('<option value="' + time + '">' + time + '</option>');
			}
			for (var i=0; i<60; i++) {
				var time = i;
				if (time < 10) {			
					time = '0' + i;
				}
				$('#catch_Tm_Minute_End').append('<option value="' + time + '">' + time + '</option>');
			}
			
			$('#region').append('<option value="">--select--</option>')
			$('#region').append('<option value="10">10分钟</option>');
			$('#region').append('<option value="30">30分钟</option>');
			$('#region').append('<option value="60">1小时</option>');
			$('#region').append('<option value="360">6小时</option>');
			$('#region').append('<option value="720">12小时</option>');
			$('#region').append('<option value="1440">1天</option>');
			$('#region').append('<option value="10080">1周</option>');
			$('#region').append('<option value="43200">1个月(30天)</option>');
						
			if (Sys.ie) {
				
				$('form input[id^="catch_Tm_"]').attr('style', 'width:24%;');
				$('form select[id^="catch_Tm_"]').attr('style', 'width:10%;');
				
			} else if (Sys.chrome) {

				$('form input[id^="catch_Tm_"]').attr('style', 'width:24.3%;');
				$('form select[id^="catch_Tm_"]').attr('style', 'width:9%;');
			}
			
			$('#chart').height($(window).height() - 110);
			$('#treeDiv').height($(window).height() - 110);
		}
		
		function query() {
			
			var keys = chartInfoMap.getKeys();
			
			for(var i=0; i<keys.length; i++) {
				
				var chartInfo = chartInfoMap.get(keys[i]);
				
				cleanChart(chartInfo);
				
				queryChart(chartInfo);
			}
		}
		
		function timeQuery () {
			
			if ($('#region').val() == '') {
				if ($('#catch_Tm_Start').val() != ''
					&& $('#catch_Tm_End').val() != '') {
					query();
				}
			}
		}
		
		function treeHighlightEvent(data, label, node) {
			
			if (data.other == 'EXCHANGE' || data.other == 'DISTRIBUTE') {
				
				data.ppppid = node.parent.parent.parent.parent.data.id;
				var ppppid = data.ppppid.replace(/\W/g, '');
				
				data.pppid = node.parent.parent.data.pid;
				var pppid = data.pppid.replace(/\W/g, '');
				
				data.ppid = node.parent.data.pid;
				var ppid = data.ppid.replace(/\W/g, '');
				
				var pid = data.pid.replace(/\W/g, '');
				var id = data.id.replace(/\W/g, '');
				
				var divId = ppppid + '_' + pppid + '_' + ppid + '_' + pid + '_' + id;
				var title = node.parent.parent.parent.label + '-' + node.parent.parent.label + '-' + node.parent.label + '-' + label;
				
				if (node.highlightState == 1) {
					if ($('#' + divId).length == 0) {			
						buildChart({
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.ppppid,
								monitorModuleName: data.pppid,
								monitorModuleId: data.ppid,
								moduleName: data.pppid,
								moduleId: data.ppid,
								queueName: data.pid,
								monitorName: data.id
							}
						});
						chartInfoMap.put(divId, {
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.ppppid,
								monitorModuleName: data.pppid,
								monitorModuleId: data.ppid,
								moduleName: data.pppid,
								moduleId: data.ppid,
								queueName: data.pid,
								monitorName: data.id
							}
						});
					}					
				} else {
					if ($('#' + divId).length > 0) {
						destroyChart({
							renderTo: divId
						});
						chartInfoMap.remove(divId);
					}
				}
				
			} else if (data.other == 'DB' || data.other == 'BODYCACHE' || data.other == 'QUEUECACHE') {
				
				data.ppppppid = node.parent.parent.parent.parent.parent.parent.data.id;
				var ppppppid = data.ppppppid.replace(/\W/g, '');
				
				data.pppppid = node.parent.parent.parent.parent.parent.data.id;
				var pppppid = data.pppppid.replace(/\W/g, '');
				
				data.ppppid = node.parent.parent.parent.parent.data.id;
				var ppppid = data.ppppid.replace(/\W/g, '');
				
				data.pppid = node.parent.parent.data.pid;
				var pppid = data.pppid.replace(/\W/g, '');
				
				data.ppid = node.parent.data.pid;
				var ppid = data.ppid.replace(/\W/g, '');
				
				var pid = data.pid.replace(/\W/g, '');
				var id = data.id.replace(/\W/g, '');
				
				var divId = ppppppid + '_' + 
							pppppid + '_' + 
							ppppid + '_' + 
							pppid + '_' + 
							ppid + '_' + 
							pid + '_' + 
							id;
				var title = node.parent.parent.parent.parent.parent.label + '-' + 
							node.parent.parent.parent.parent.label + '-' + 
							node.parent.parent.parent.label + '-' + 
							node.parent.parent.label + '-' + 
							node.parent.label + '-' + 
							label;
				
				if (node.highlightState == 1) {
					if ($('#' + divId).length == 0) {			
						buildChart({
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.ppppppid,
								monitorModuleName: data.pppppid,
								monitorModuleId: data.ppppid,
								moduleName: data.pppid,
								moduleId: data.ppid,
								queueName: data.pid,
								monitorName: data.id
							}
						});
						chartInfoMap.put(divId, {
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.ppppppid,
								monitorModuleName: data.pppppid,
								monitorModuleId: data.ppppid,
								moduleName: data.pppid,
								moduleId: data.ppid,
								queueName: data.pid,
								monitorName: data.id
							}
						});
					}					
				} else {
					if ($('#' + divId).length > 0) {
						destroyChart({
							renderTo: divId
						});
						chartInfoMap.remove(divId);
					}
				}
				
			} else if (data.other == 'CUSTOMER') {
				
				data.pppppppid = node.parent.parent.parent.parent.parent.parent.parent.data.id;
				var pppppppid = data.pppppppid.replace(/\W/g, '');
				
				data.ppppppid = node.parent.parent.parent.parent.parent.parent.data.id;
				var ppppppid = data.ppppppid.replace(/\W/g, '');
				
				data.pppppid = node.parent.parent.parent.parent.parent.data.id;
				var pppppid = data.pppppid.replace(/\W/g, '');
				
				data.ppppid = node.parent.parent.parent.parent.data.id;
				var ppppid = data.ppppid.replace(/\W/g, '');
				
				data.pppid = node.parent.parent.data.pid;
				var pppid = data.pppid.replace(/\W/g, '');
				
				data.ppid = node.parent.data.pid;
				var ppid = data.ppid.replace(/\W/g, '');
				
				var pid = data.pid.replace(/\W/g, '');
				var id = data.id.replace(/\W/g, '');
				
				var divId = pppppppid + '_' + 
							ppppppid + '_' + 
							pppppid + '_' + 
							ppppid + '_' + 
							pppid + '_' + 
							ppid + '_' + 
							pid + '_' + 
							id;
				var title = node.parent.parent.parent.parent.parent.parent.label + '-' + 
							node.parent.parent.parent.parent.parent.label + '-' + 
							node.parent.parent.parent.parent.label + '-' + 
							node.parent.parent.parent.label + '-' + 
							node.parent.parent.label + '-' + 
							node.parent.label + '-' + 
							label;
				
				if (node.highlightState == 1) {
					if ($('#' + divId).length == 0) {			
						buildChart({
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.pppppppid,
								monitorModuleName: data.ppppppid,
								monitorModuleId: data.pid,
								moduleName: data.pppppid,
								moduleId: data.ppppid,
								queueName: data.pppid,
								monitorName: data.id
							}
						});
						chartInfoMap.put(divId, {
							containerId: 'chart',
							renderTo: divId,
							title: title,
							input: {
								executerName: data.pppppppid,
								monitorModuleName: data.ppppppid,
								monitorModuleId: data.pid,
								moduleName: data.pppppid,
								moduleId: data.ppppid,
								queueName: data.pppid,
								monitorName: data.id
							}
						});
					}					
				} else {
					if ($('#' + divId).length > 0) {
						destroyChart({
							renderTo: divId
						});
						chartInfoMap.remove(divId);
					}
				}
			}
		}
		
		function buildChart(obj) {
			
			$('#' + obj.containerId).append('<div id="' 
					+ obj.renderTo 
					+ '" style="min-width:400px; width:900px; height:150px; margin:0 auto; margin-top:5px; margin-bottom:6px;"></div>');
			
			transaction({
				id: 'GET_DATETIME',
				url: '<%=request.getContextPath()%>/monitor/chart/overtime/getdatetime',
				async: false,
				other: obj.renderTo
			});
			
			options = {
	            chart: {
	                renderTo: obj.renderTo,
	                type: 'spline',
	                events: {
	                    load: function() {
                        	var options = this;
                        	if (intervalStart == true) {
                        		var intervalProcess = setInterval(function() {
                        			
                        			var intervalProcessNow = intervalProcessMap.get(obj.renderTo);
                        			if (intervalProcessNow != intervalProcess) {
                        				clearInterval(intervalProcess);
                        				return;
                        			}
                        			
    	                			var jsonSet = new JsonSet();
    	                			jsonSet.put('input', obj.input);
    	                			jsonSet.put('intervalLastTime', intervalLastTimeMap.get(obj.renderTo));
    	                						
    	                        	$.ajax({
    	                        		url: '<%=request.getContextPath()%>/monitor/chart/overtime/querychartsinglenowlast',
    	                        		data: jsonSet.toString(),
    	                        		cache: false,
    	                        		dataType: 'json',
    	                        		success: function(data, textStatus, jqXHR) {
    	                        			var i;
    	                        			for (i=0; i<data.length; i++) {

    	                        				var x = data[i].timestamp;
    	                                        var y = data[i].averageTotalTime;
    	                                        options.series[0].addPoint([x, y], true, true);
    	                                        
    	                                        var x = data[i].timestamp;
    	                                        var y = data[i].averageOvertimeTime;
    	                                        options.series[1].addPoint([x, y], true, true);
    	                                        
    	                                        var x = data[i].timestamp;
    	                                        var y = data[i].threshold;
    	                                        options.series[2].addPoint([x, y], true, true);
    	                        			}

    	                    				if (data.length > 0) {
    	                    					intervalLastTimeMap.put(obj.renderTo, data[i-1].timestamp);
    	                        			}
    	                        		}
    	                        	});
    	                		}, 60000);
                            	intervalProcessMap.put(obj.renderTo, intervalProcess);
                        	}
	                    }
	                }
	            },
	            title: {
	                text: obj.title
	            },
	            subtitle: {
	                text: 'OverTime'
	            },
	            xAxis: {
	            	type: 'datetime',
	            	tickPixelInterval: 100
	            },
	            yAxis: {
	                title: {
	                    text: 'Num Of Times'
	                },
	                min: 0
	            },
	            legend: {
	                layout: 'vertical',
	                align: 'right',
	                verticalAlign: 'top',
	                x: 0,
	                y: 45,
	                borderWidth: 1
	            },
	            tooltip: {
	                crosshairs: true,
	                shared: true
	            },
	            plotOptions: {
	            	spline: {
	            		lineWidth: 1,
	            		states: {
	                        hover: {
	                            lineWidth: 1
	                        }
	                    },
	                    shadow: false,
	                	dataLabels: {
	                        enabled: false
	                    },
	                    marker: {
	                    	enabled: false,
	                    	states: {
	                            hover: {
	                                enabled: true,
	                                radius: 4
	                            }
	                        }
	                    }
	                }
	            },
	            series: [{
	                name: 'AgTm',
	                marker: {
	                    symbol: 'square'
	                }
	            }, {
	                name: 'AgOvTm',
	                marker: {
	                    symbol: 'diamond'
	                }
	            }, {
	                name: 'Thd',
	                marker: {
	                    symbol: 'circle'
	                }
	            }]
	        };
			
			optionsMap.put(obj.renderTo, options);
			
			queryChart(obj);
		}
		
		function queryChart(obj) {
			
			var url = '';
			var jsonSet = new JsonSet();
			
			if ($('#region').val() != '') {
				regionCurrent = $('#region').val();
				jsonSet.put('region', regionCurrent);
				intervalStart = true;
				url = '<%=request.getContextPath()%>/monitor/chart/overtime/querychartsinglenow';
			} else {
				if ($('#catch_Tm_Start').val() != ''
						&& $('#catch_Tm_End').val() != '') {
					var catch_Tm_Start = 
							$('#catch_Tm_Start').val() + ' ' +
							$('#catch_Tm_Hour_Start').val() + ':' +
							$('#catch_Tm_Minute_Start').val() + ':' +
							'00';
					jsonSet.put('beginTime', catch_Tm_Start);
					var catch_Tm_End = 
							$('#catch_Tm_End').val() + ' ' +
							$('#catch_Tm_Hour_End').val() + ':' + 
							$('#catch_Tm_Minute_End').val() + ':' +
							'00';
					jsonSet.put('endTime', catch_Tm_End);
					intervalStart = false;
					url = '<%=request.getContextPath()%>/monitor/chart/overtime/querychartbganded';
				} else {
					regionCurrent = 60;
					jsonSet.put('region', regionCurrent);
					intervalStart = true;
					url = '<%=request.getContextPath()%>/monitor/chart/overtime/querychartsinglenow';
				}
			}
			
			jsonSet.put('input', obj.input);
			
			transaction({
				id: 'CHART_QUERY',
				url: url,
				jsonSet: jsonSet,
				other: obj.renderTo
			});
		}
		
		function destroyChart(obj) {

			cleanChart(obj);
			
			intervalLastTimeMap.remove(obj.renderTo);
			optionsMap.remove(obj.renderTo);
			$('#' + obj.renderTo).remove();
		}
		
		function cleanChart(obj) {
			
			var intervalProcess = intervalProcessMap.get(obj.renderTo);
			if (intervalProcess != null) {
				clearInterval(intervalProcess);
				intervalProcessMap.remove(obj.renderTo);
			}
		}
		
		function fullscreen() {
			
			openChildWindowPost(
					'<%=request.getContextPath()%>/view/monitor/overtime/time/overtime_time_chart_fullscreen.jsp', 
					'urlParam',
					jsonToString(chartInfoMap.getEntry())
			);
		}
		
		var iT;
		function resize() {
			
			if (!Sys.ie) {
				
				var box = document.getElementById('box');
				var left = document.getElementById('left');
				var right = document.getElementById('right');
				var line = document.getElementById('line');
				var treeDiv = document.getElementById('treeDiv');
				var filterDiv = document.getElementById("filterDiv");
				
				line.style.left = left.style.width = iT + 'px';
				treeDiv.style.width = iT - 8 + 'px';
				filterDiv.style.width = iT - 7 + 'px';
				right.style.width = box.clientWidth - iT - 3 + 'px';
				
				window.onresize = resize;
			}
		}
		
	</script>

  </head>

  <body onload="init();" onkeydown="onEnterDown(query);" onresize="resize();">
	
	<div id="box">
	    <div id="right" style="width:80%;float:right;">
			<div id="button">
				<button id="query">查询</button>
				<button id="fullscreen">全屏</button>
			</div>
		
			<div id="forms">
			  <div class="navbar">
			  	<div id="expander" class="s_expander">
       	 			<h3>查询条件</h3>
					<div class="container" style="width: auto;">
						<form>
						<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout">
							<colgroup>
								<col width="10%" />
								<col width="15%" />
								<col width="10%" />
								<col width="65%" />
							</colgroup>					
							<tr class="filterTableTr">
						    	<th><label>范围</label></th>
						    	<td><select name="region" id="region" onchange="javascript:query();"></select></td>
						    	<th><label>获取时间</label></th>
						    	<td style="color:#fafafa;">
						    		<input type="text" id="catch_Tm_Start" style="width:24%;" onchange="javascript:timeQuery();"/>
						    		<select name="catch_Tm_Hour_Start" id="catch_Tm_Hour_Start" style="width:10%;" onchange="javascript:timeQuery();"></select>
						    		<select name="catch_Tm_Minute_Start" id="catch_Tm_Minute_Start" style="width:10%;" onchange="javascript:timeQuery();"></select>
						    		~
						    		<input type="text" id="catch_Tm_End" style="width:24%;" onchange="javascript:timeQuery();"/>
						    		<select name="catch_Tm_Hour_End" id="catch_Tm_Hour_End" style="width:10%;" onchange="javascript:timeQuery();"></select>
						    		<select name="catch_Tm_Minute_End" id="catch_Tm_Minute_End" style="width:10%;" onchange="javascript:timeQuery();"></select>
						    	</td>
							</tr>
						</table>
						</form>
					</div>
				</div>
			  </div>
			</div>
		
			<div>
				<div id="chart" style="height:350px; width:100%; overflow:auto; border: 1px solid #e3e3e3; background:#606154;">
				</div>
			</div>
		</div>
	    <div id="left" style="width:20%;">
	    	<button id="filterBtn">刷新</button>
			
			<div id="filterDiv" style="width:97%;">
				<div id="expander-left" class="s_expander">
	        		<h3>查询条件</h3>
	        		<div id="form" style="width: auto;">
						<table width="100%" border="1" cellspacing="0" cellpadding="0" class="s_layout">
							<colgroup>
								<col width="40%" />
								<col width="60%" />
							</colgroup>					
							<tr class="filterTableTr">
						    	<th><label>队列名称</label></th>
						    	<td><input type="text" id="queue_Nm"  /></td>
						    </tr>
						</table>
					</div>
				</div>
			</div>
			<div id="tree">
				<div id="treeDiv" style="background:#606154; border: 1px solid #e3e3e3; height:350px; width:96%; overflow:auto; color:#fafafa;">
					<div id="treeDivLeft" class="whitebg ygtv-checkbox"></div>
				</div>
			</div>
		</div>
		<div id="line" style="position:absolute;top:0;left:19.7%;height:100%;width:8px;overflow:hidden;cursor:w-resize;"></div>
		
	</div>

  </body>
</html>
