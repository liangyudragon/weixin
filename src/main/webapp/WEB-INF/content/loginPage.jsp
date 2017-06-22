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
    <meta name="keywords" content="wechat4j后台管理" />
    <meta name="description" content="Version:1.0" />
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="format-detection" content="telephone=no">
    <link rel="Shortcut Icon" href="/favicon.ico" />
    <!-- load css -->
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/common/layui/css/layui.css" media="all">
    <link rel="stylesheet" type="text/css" href="<%=basePath%>/resources/backstage/css/login.css" media="all">
</head>
<body>
<div class="larry-canvas" id="canvas"></div>
<div class="layui-layout layui-layout-login">
    <h1>
        <strong>wechat4j管理系统后台</strong>
        <em>Management System</em>
    </h1>
    <form class="layui-form" action=""  method="post">
        <div class="layui-user-icon larry-login">
            <input type="text" placeholder="账号" name="mobile" id="mobile" class="login_txtbx"/>
        </div>
        <div class="layui-pwd-icon larry-login">
            <input type="password" placeholder="密码" name="password" id="password" class="login_txtbx"/>
        </div>
        <%--<div class="layui-val-icon larry-login">
            <div class="layui-code-box">
                <input type="text" id="code" name="code" placeholder="验证码" maxlength="4" class="login_txtbx">
                <a><img style="right: 32px;width: 38%;" src="authority/login/getcode.htm" name="numberp" alt="" class="verifyImg" id="verifyImg" onclick="javascript:changeImage();">
                </a>
            </div>
        </div>--%>
        <div class="layui-submit larry-login">
            <a class="layui-btn" lay-submit="" id="login" style="padding: 0 131px;height: 43px;" href="javaScript:void(0);" lay-filter="demo1">立即登录</a>
        </div>
    </form>
</div>
<script type="text/javascript" src="<%=basePath%>/resources/common/layui/layui.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/common/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/resources/common/jsplugin/jparticle.jquery.js"></script>
<script type="text/javascript">
    layui.use(['jquery','form'],function(){
        window.jQuery = window.$ = layui.jquery;
        var form = layui.form();
        //监听提交
        form.on('submit(demo1)', function(data){
            $.ajax({
                type:'post',
                url:'index/login.do',
                data:data.field,
                dataType:'json',//服务器返回的数据类型
                success:function(data){
                    if(data.code==200){
                        window.location.href="index/index.do";
                    }else{
                        layer.msg(data.msg);
                        //changeImage();
                    }
                },
                error:function(){
                    //changeImage();
                }
            });
        });
    });
    $(function(){
        $(".layui-canvs").jParticle({
            background: "#141414",
            color: "#E6E6E6"
        });
        $("canvas:first").css('background', '');

    });
    /**
     *刷新验证码
     **/
    /*function changeImage(){
        document.forms[0].numberp.src="authority/login/getcode.htm?r="+Math.random();
        //$("#verifyImg").attr('src','authority/login/getcode.htm');
    }*/
    document.onkeydown=keyListener;
    function keyListener(e){
        e = e ? e : event;
        if(e.keyCode == 13){
            $("#login").click();
        }
    }
</script>
</body>
</html>