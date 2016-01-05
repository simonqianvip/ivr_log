<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>IVR日志--号码查询</title>
<link href="css/style.css" type="text/css" rel="stylesheet">
	<meta http-equiv="x-ua-compatible" content="ie=7" />
</head>
<body>
	<div class="top"></div>
	<!-- <div class="logo">手机定位手机归属查询</div> -->
	<div class="search">
		<form method="post" action="" name="memberform" target="_blank">
			手机号:
			<input type="text" name="m" id="m" maxlength="100"/> 
			大会议:
			<input type="text" id="meeting" name="meeting"></input>
			<span class="btn_wr">
			<input type="submit" value="监  控" id="btnQuery" class="btn" onmousedown="this.className='btn btn_h'"
					onmouseout="this.className='btn'" onClick="send_action(this)">
			</span>
		</form>
		<script type="text/javascript">
			document.getElementById("m").focus();
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
			function send_action() {
				var caller = document.memberform.m.value;
				var meeting = document.memberform.meeting.value;
				if (caller == '') {
					alert("\请输入待查询的主叫手机号码!!");
					return;
				}
				memberform.action = "/IVR_log/client/IvrSearchServlet?caller="
						+ caller+"&spid="+meeting;
			}

		</script>
		
		
	</div>
	<div class="banner"></div>
	<div class="foot">
		<font color="red" size="10">${msg }</font>
	</div>
</body>
</html>
