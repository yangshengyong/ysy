<%@ page language="java" import="java.util.*" pageEncoding="utf-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>emplist</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" type="text/css" href="css/style.css" />
		<meta charset="UTF-8">
		<title>动态时间显示</title>
		<script language="javascript">
            var t = null;
            t = setTimeout(time,1000);//开始执行
            function time()
            {
                clearTimeout(t);//清除定时器
                dt = new Date();
                var y=dt.getYear()+1900;
                var mm=dt.getMonth()+1;
                var d=dt.getDate();
                var weekday=["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
                var day=dt.getDay();
                var h=dt.getHours();
                var m=dt.getMinutes();
                var s=dt.getSeconds();
                if(h<10){h="0"+h;}
                if(m<10){m="0"+m;}
                if(s<10){s="0"+s;}
                document.getElementById("timeShow").innerHTML =  weekday[day]+""+h+":"+m+":"+s+"";
                t = setTimeout(time,1000); //设定定时器，循环执行
            }
		</script>
	</head>
	<body>
		<div id="wrap">

			<div id="top_content">
				<div id="header">

					<div id="rightheader" style=" text-align:center;">
						<h1 id="timeShow"></h1><%--<samp id="timeShow"></samp>--%>
					</div>
					<div id="topheader" style=" text-align:center;">

						<h1 >唐诗检索系统</h1>
					</div>

				</div>
				<div id="content">
					<p id="whereami">
					</p>
					<table class="table">
						<tr class="table_header">
							<body>
							<form action="${pageContext.request.contextPath}/ts/search" method="post">
								<input type="text" name="name">
								作者：<input type="radio"name="type" value="author">
								题目：<input type="radio"name="type" value="title">
								内容：<input type="radio"name="type" checked="checked" value="content">
								<input type="submit" value="查询">
							</form>
							</body>
							<td>
								Name
							</td>
							<td>
								Content
							</td>
							<td>
								Author
							</td>

						</tr>
						<c:forEach items="${sessionScope.poetries}" var="sd">
						<tr class="row1">
							<td>
								${sd.title }
							</td>
							<td>
								${sd.content }
							</td>
							<td>
								${sd.poetId.name }
							</td>

						</tr>
						</c:forEach>
					</table>
					<p>
							<s:if test="${sessionScope.nowPage >1}">
								<a href="${pageContext.request.contextPath}/ts/search?nowPage=${sessionScope.nowPage -1}&name=${sessionScope.tsname}&type=${sessionScope.tstype}">上一页</a>
							</s:if>

							<s:if test=" ${sessionScope.nowPage < sessionScope.pages} ">
								<a href="${pageContext.request.contextPath}/ts/search?nowPage=${sessionScope.nowPage +1}&name=${sessionScope.tsname}&type=${sessionScope.tstype}">下一页</a>
							</s:if>

						【共${sessionScope.count}条】【当前${sessionScope.nowPage}页】【共${sessionScope.pages}页】
						<a href="${pageContext.request.contextPath}/ts/search?nowPage=1&name=${sessionScope.tsname}&type=${sessionScope.tstype}">【首页】</a>
						<a href="${pageContext.request.contextPath}/ts/search?nowPage=${sessionScope.pages}&name=${sessionScope.tsname}&type=${sessionScope.tstype}">【尾页】</a>
						<%--【尾页${sessionScope.pages}】--%>
					</p>

				</div>
			</div>
			<div id="footer">
				<div id="footer_bg">
				ABC@126.com
				</div>
			</div>
		</div>
	</body>
</html>
