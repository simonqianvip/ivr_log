<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title></title>
</head>

<script type="text/javascript">
var sculog = '<%=request.getAttribute("scuLog")%>';
	function onData() {

		var array = new Array();
		array = sculog.split("$");

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


<body onload="onData()">
	<h2 align="center">
		<font style="color: blue;">SCU日志查询结果页面</font>
	</h2>
	<br />
	<br />
	<h1 align="center">
		<font style="color: red">${requestScope.scu_msg}</font>
	</h1>
	<c:if test="${requestScope.scu_msg == null}">
	
		<div id="demo" style="background-color:#D3D3D3;"></div>
		<br />
		<h2>
			<a
				href="/IVR_log/client/ScuResultServlet?operation=exportexcle&startTime=${requestScope.stime}&caller=${requestScope.caller}&called=${requestScope.called}"
				style="float: right;">导出到TXT文件</a>
		</h2>
	</c:if>
	<br />
	<br />

</body>

</html>
