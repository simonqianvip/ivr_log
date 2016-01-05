<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
</head>
<script type="text/javascript" src="js/ajax-pushlet-client.js"></script>

<body onload="init()">
	<h1 align="center">
		<font style="color: blue;">IVR日志查询结果页面</font>
	</h1>
	<h2 align="center">
		ip:<input type="text" id="input1" name="input1" readonly="readonly"></input>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;name:<input
			type="text" id="input2" value=""  readonly="readonly"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;rbusNo:<input
			type="text" id="input3" value=""  readonly="readonly"/>
	</h2>
	<h3 align="center">
		<font style="color: red">${requestScope.ivr_msg }</font>
	</h3>
	<div id="demo" style="background-color:#D3D3D3;"></div>
	<div>
		<h2 align="center">
			<font style="color: red;text-align: center;">友情提示：如果页面超过5分钟没有信息刷新，有可能已产生新日志，请重新查询...</font>
		</h2>
	</div>
</body>
<script type="text/javascript"> 

var caller = '<%=request.getParameter("caller")%>';
var findPath = '<%=request.getParameter("findPath")%>';
var session = '<%=request.getParameter("session")%>';

var ip = '<%=request.getParameter("ip")%>';
var name = '<%=request.getParameter("name")%>';
var rbusNo = '<%=request.getParameter("rbusNo")%>';

	document.getElementById("input1").value = ip;
	document.getElementById("input2").value = name;
	document.getElementById("input3").value = rbusNo;

	var xmlHttpRequest;
	function createXmlHttpRequest() {
		if (window.ActiveXObject) { //如果是IE浏览器  
			return new ActiveXObject("Microsoft.XMLHTTP");
		} else if (window.XMLHttpRequest) { //非IE浏览器  
			return new XMLHttpRequest();
		}
	}

	function init() {
		var url = "/IVR_log/client/IvrResultServlet?caller=" + caller
				+ "&findPath=" + findPath + "&ip=" + ip + "&session=" + session;
		//1.创建XMLHttpRequest组建  
		xmlHttpRequest = createXmlHttpRequest();
		//2.设置回调函数  
		xmlHttpRequest.onreadystatechange = zswFun;
		//3.初始化XMLHttpRequest组建  
		xmlHttpRequest.open("POST", url, true);
		//4.发送请求  
		xmlHttpRequest.send(null);
	}

	function zswFun() {
		if (xmlHttpRequest.readyState == 4 && xmlHttpRequest.status == 200) {
			var b = xmlHttpRequest.responseText;
			if (b == "true") {
				/*  alert("登录成功！");   */
			} else {
				/*  alert("登录失败！"); */
			}
		}
	}

	//对pushlet的初始化，触发web.xml中的servlet。
	PL._init();
	//这里的监听的主题，必须在sources.properties中配置的对象中声明这个主题。
	//sources.properties配置着事件源（EventSources），在服务器启动时会自动激活。
	//可以通过服务器的启动记录查看得到。可以将这个文件放到WEB-INF目录下面或者classess目录下面都可以。
	PL.joinListen('/system/test');
	function onData(event) {
		var log = event.get("log");

		var array = new Array();
		array = log.split("$");
		/*   alert(array.length); */

		if (array.length > 1) {
			for (var i = 0; i < array.length - 1; i++) {
				newEl = document.createElement("pre");
				newText = document.createTextNode(array[i]);
				newEl.appendChild(newText);

				x = document.getElementById("demo");
				x.appendChild(newEl);
			}
		}
	}
</script>

</html>
