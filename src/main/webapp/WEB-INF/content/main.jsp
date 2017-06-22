<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <base href="<%=basePath%>">
    <meta charset="UTF-8">
	<title>wechat4j后台管理</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">	
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">	
	<meta name="apple-mobile-web-app-capable" content="yes">	
	<meta name="format-detection" content="telephone=no">	
	<link rel="Shortcut Icon" href="/favicon.ico" />
	<!-- load css -->
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/common/bootstrap/css/bootstrap.min.css" media="all">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/common/layui/css/layui.css" media="all">
	<!-- <link rel="stylesheet" type="text/css" href="/common/css/larry.css" media="all"> 基于flex布局 未完待续-->
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/common/css/global.css" media="all">
	<link rel="stylesheet" type="text/css" href="http://at.alicdn.com/t/font_bmgv5kod196q1tt9.css">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/backstage/css/common.css" media="all">
	<link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/backstage/css/main.css" media="all">
</head>
<body>
<div class="larry-grid larry-wrapper">
    <div class="row shortcut" id="shortcut">

    </div>
    <!-- 首页信息 -->
    <div class="row system">
    </div>

</div>
<!-- 加载js文件 -->
<script type="text/javascript" src="<%=basePath%>/resources/common/layui/layui.js"></script> 
<script type="text/javascript" src="<%=basePath%>/resources/common/js/jquery-3.2.0.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/common/bootstrap/js/bootstrap.min.js"></script>  
<script type="text/javascript" src="<%=basePath%>/resources/common/jsplugin/jquery.leoweather.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/common/jsplugin/echarts.min.js"></script>
<!-- 引入当前页面js文件 -->
<script type="text/javascript" src="<%=basePath%>/resources/backstage/js/common.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/backstage/js/main.js"></script>
</body>
</html>