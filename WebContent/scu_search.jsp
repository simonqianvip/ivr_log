<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>scu日志查询</title>
<!-- <link href="css/style.css" type="text/css" rel="stylesheet"> -->
<meta http-equiv="x-ua-compatible" content="ie=7" />


<link href="<%=request.getContextPath()%>/css/bootstrap.min.css"
	rel="stylesheet" media="screen">
	<link
		href="<%=request.getContextPath()%>/css/bootstrap-datetimepicker.css"
		rel="stylesheet" media="screen">
		<link
			href="<%=request.getContextPath()%>/css/bootstrap-datetimepicker.less"
			rel="stylesheet" media="screen">
</head>
<body>
	<div class="top"></div>
	<div class="search">
		<form method="post" action="" name="memberform" target="show_page"
			style="">
			<br /> <br />
			<table align="center">
				<tr>
					<td>主叫: <input type="text" name="calling" id="calling"
						maxlength="100" />
					</td>
					<td>被叫: <input type="text" id="called" name="called"></input>
					</td>
					<td>大会议: <input type="text" id="meeting" name="meeting"></input>
					</td>
					<td>开始时间:</td>
					<td>

						<div class="controls input-append date form_datetime" data-date=""
							data-date-format="yyyy-mm-dd hh:ii:ss"
							data-link-field="start_time">
							<input size="16" type="text" value="" style="width: 150;">
								<span class="add-on"><i class="icon-remove"></i></span> <span
								class="add-on"><i class="icon-th"></i></span>
						</div> <input type="hidden" id="start_time" value="" />
					</td>
					<td>结束时间:</td>
					<td>

						<div class="controls input-append date form_datetime" data-date=""
							data-date-format="yyyy-mm-dd hh:ii:ss" data-link-field="end_time">
							<input size="16" type="text" value="" style="width: 150;">
								<span class="add-on"><i class="icon-remove"></i></span> <span
								class="add-on"><i class="icon-th"></i></span>
						</div> <input type="hidden" id="end_time" value="" />
					</td>
					<td><input type="submit" value="查询" id="btnQuery" class="btn"
						onmousedown="this.className='btn btn_h'"
						onmouseout="this.className='btn'" onClick="send_action(this)"></td>
					<!-- <span class="btn_wr"> -->
					<!-- </span> -->
				</tr>
			</table>

		</form>
		<div>
			<c:if test="${requestScope.scuList != null}">
				<table border="1" align="center" bgcolor="silver" width="80%">
					<tr>
						<td align="center">主叫</td>
						<td align="center">被叫</td>
						<td align="center">呼入时间</td>
						<td align="center">呼叫状态</td>
						<td align="center">查看日志</td>
					</tr>
					<c:forEach items="${requestScope.scuList}" var="scu">
						<tr>
							<td  align="center">${scu.caller}</td>
							<td  align="center">${scu.called}</td>
							<td  align="center">${scu.stime}</td>
							<td  align="center">${scu.callstate}</td>
							<td  align="center"><a
								href="/IVR_log/client/ScuResultServlet?operation=showLog&spid=${scu.spid}&scuname=${scu.scuname}&session=${scu.session}&ip=${scu.ip}&startTime=${scu.stime}&caller=${scu.caller}&called=${scu.called}"
								style='text-decoration:none;' target="_blank">查看</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</div>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/js/jquery-1.8.3.min.js"
			charset="UTF-8"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/js/bootstrap-datetimepicker.js"
			charset="UTF-8"></script>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/js/bootstrap-datetimepicker.fr.js"
			charset="UTF-8"></script>
		<script type="text/javascript">
			document.getElementById("calling").focus();
			//定位焦点。
			function chk(){ 
				var obj=document.getElementsByName('meeting'); //选择所有name="'test'"的对象，返回数组 
				//取到对象数组后，我们来循环检测它是不是被选中 
				var s=''; 
				for(var i=0; i<obj.length; i++){ 
				if(obj[i].checked) s+=obj[i].value; //如果选中，将value添加到变量s中 
				} 
				//那么现在来检测s的值就知道选中的复选框的值了 
				//alert(s==''?'你还没有选择任何内容！':s); 
				return s;
			} 
		</script>
		<script type="text/javascript">
		 $('.form_datetime').datetimepicker({
		        language:  'zh',
		        todayBtn:  1,
				autoclose: 1,
				todayHighlight: 1,
				forceParse: 0,
		        showMeridian: 1
		    });
			function send_action() {
				var caller = document.memberform.calling.value;
				var called = document.memberform.called.value;
				var stime = document.memberform.start_time.value;
				var etime = document.memberform.end_time.value;
				var meeting = document.memberform.meeting.value;
				if (caller == '') {
					alert("请输入待查询的主叫手机号码!!");
					return;
				}
				if (stime == '') {
					alert("请输入待查询的时间!!");
					return;
				}
				memberform.action = "/IVR_log/client/ScuSearchServlet?caller="
						+ caller+"&called="+called+"&stime="+stime+"&etime="+etime+"&meeting="+meeting;
			}

		</script>


	</div>
	<div class="banner"></div>
	<br />
	<br />
	<br />
	<div class="foot" align="center">
		<font color="red" size="10">${msg }</font>
	</div>
</body>
</html>
