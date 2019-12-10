<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/init.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>重庆大学智慧党建云平台</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" type="text/css" href="${basePath }/cqu/css/common.min.css"/>
    <link rel="stylesheet" type="text/css" href="${basePath }/cqu/css/login.min.css"/>
</head>
<div class="login_page full_screen">
    <div class="user_info_container">
        <div class="title_container"></div>
        <div class="info_container">
            <div class="info_title">
                <span class="active">普通登录</span>
                <span>统一身份认证</span>
            </div>
            <form class="login_form" action="" method="post">
                <div class="user_container">
                    <i class="input_icon"></i>
                    <input type="text" maxlength="20" id="username"/>
                </div>
                <div class="pwd_container">
                    <i class="input_icon"></i>
                    <input type="password" maxlength="20" id="password"/>
                </div>
                <input type="button" class="submit_btn" value="登录" />
            </form>
        </div>
    </div>
</div>
<portlet:resourceURL id="/hg/ajaxLogin" var="ajaxLogin"/>

<script>
    //tab切换
    $(function () {
        $('.info_title span').click(function (e) {
            if (e.target.className === "active") return;
            $('.info_title span').removeClass("active");
            e.target.className = 'active';
        });

        function ajaxLogin() {
            var userName = $("#username").val();
            var password = $("#password").val();
            var url = "${ajaxLogin }";
            $.ajax({
                url: url,
                data: {
                    "<portlet:namespace/>userName": userName,
                    "<portlet:namespace/>password": password,
                },
                type: "post",
                dataType: "text",
                async: false,
                success: function (result) {
                    if (result === "1") {
                        $('.pwd_container').append('<p style="color: red;">用户不存在</p>');
                    } else if (result === "2") {
                        $('.pwd_container').append('<p style="color: red;">密码错误</p>');
                    } else if (result.substring(0, 1) === "3") {
                        window.location.href = result.substring(1);
                    }
                }
            });
        }

        $('.submit_btn').on("click", ajaxLogin)
    });

</script>
</html>