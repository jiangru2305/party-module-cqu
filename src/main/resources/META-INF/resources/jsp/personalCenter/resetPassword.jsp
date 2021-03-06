
<%@ include file="/init.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<portlet:resourceURL id="/password/reset" var="reset"/>

<html>
	<head>
	<style type="text/css">
	    input#button1 {
		    float: right;
		}
		textarea {
		    width: 100%;
		    height: 100px;
		}
		
		input#button2 {
        float: left;
       }
       
       /* 常用人员弹窗样式 */
        #commonStaff thead {
            border-bottom: 1px solid #d8d8d8;
            background: #f5f5f5;
        }
        
        #commonStaff .modal-body {
            max-height: 400px;
        }
        
        #commonStaff .common_list {
            border-right: none;
        }
        
        #commonStaff .common_list tr td {
            cursor: pointer;
        }
        
        #commonStaff .common_list tr td:nth-child(1) {
            cursor: default;
        }
        
        #commonStaff table td {
            font-size: 13px;
        }
        
        #commonStaff th,
        #commonStaff .common_list td {
            border: none;
        }
        
        #commonStaff .modal-body>div {
            padding: 0;
        }
        
        #commonStaff .modal-body>div table {
            width: 100%;
        }
        
        #commonStaff .add_btn {
            height: auto;
            background: #f5f5f5;
            border: 1px solid #d8d8d8;
            border-top: none;
        }
        
        #commonStaff .add_btn button {
            margin: 15px;
        }
        
        #commonStaff .add_btn img {
            margin-right: 5px;
        }
        
        .table_container {
            height: 200px;
            overflow-y: auto;
            border: 1px solid #d8d8d8;
        }
        
        .common_list_container .table_container {
            border-right: none;
        }
        
        #commonStaff .table_title {
            text-align: center;
        }
        
        .add_btn form {
            width: 80%;
            background: #fff;
            border: 1px solid #d8d8d8;
            border-radius: 6px;
            padding: 10px 5px;
            padding-bottom: 0;
        }
        
        #commonStaff .add_btn form {
            margin: 0 0 10px 10px;
            display: none;
        }
        
        #commonStaff .add_btn form .btn {
            margin: 0;
        }
        
        #commonStaff .form-group {
            margin-bottom: 0;
        }
        
        .form_btn {
            text-align: right;
        }
        
        .common_member_list tr td:nth-child(1) {
            border-left: none;
        }
        
        .common_member_list tr td:nth-child(2) {
            border-right: none;
        }
	</style>

        <link rel="stylesheet" href="${basePath}/css/assign.css" />
        <script type="text/javascript" src="${basePath}/cqu/js/jquery.md5.js"></script>

		<script type="text/javascript" >
            $(function() {
                $('#send').on('click', function(){
                    var original = $('[name=original]').val().trim();
                    var newPass = $('[name=newPass]').val().trim();
                    var repeat = $('[name=repeat]').val().trim();
                    if (!newPass || !repeat || !original){
                        layuiModal.alert("密码不能为空");
                        return;
                    }

                    if (repeat === newPass){
                        $.post('${reset}', {original: $.md5(original).toUpperCase(), newPass: $.md5(repeat).toUpperCase()}, function (res) {
                            if (res.result){
                                layuiModal.alert("修改成功", function () {
                                    window.location.href = '/home';
                                })
                            }else {
                                layuiModal.alert(res.message)
                            }
                        })
                    }
                });
            });
		</script>
	</head>
	<body>
    <div class="table_form_content">
        <!-- 右侧盒子内容 -->
        <div class="activity_manage_page">
            <div class="breadcrumb_group" style="margin-bottom: 20px;">
                当前位置：
                <span class="layui-breadcrumb" lay-separator=">">
					<a href="javascript:;">修改密码</a>
				</span>
            </div>
            <div class="bg_white_container">
            <div class="content_form" style="padding: 20px 0;">
                <iframe name="forUpload" style="display: none;"></iframe>
                <form class="form-horizontal" role="form" action="${reset }" method="post" target="forUpload" enctype="multipart/form-data">
                    <div id="hg-form-container" class="form-group">
                        <div class="col-sm-12 col-xs-12" style="margin-bottom: 20px;">
                            <div class="col-sm-6 col-xs-12">
                                <div class="col-sm-3 col-xs-3 ">
                                    <span class="control-label form-label-required">原密码：</span>
                                </div>
                                <div class="col-sm-9 col-xs-9">
                                    <input class="form-control" name="original" type="password" style="text-indent: inherit;">
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12 col-xs-12" style="margin-bottom: 20px;">
                            <div class="col-sm-6 col-xs-12">
                                <div class="col-sm-3 col-xs-3 ">
                                    <span class="control-label form-label-required">新密码：</span>
                                </div>
                                <div class="col-sm-9 col-xs-9">
                                    <input class="form-control" name="newPass" type="password" style="text-indent: inherit;">
                                </div>
                            </div>
                        </div>
                        <div class="col-sm-12 col-xs-12" style="margin-bottom: 20px;">
                            <div class="col-sm-6 col-xs-12">
                                <div class="col-sm-3 col-xs-3 ">
                                    <span class="control-label form-label-required">重复密码：</span>
                                </div>
                                <div class="col-sm-9 col-xs-9">
                                    <input class="form-control" name="repeat" type="password" style="text-indent: inherit;">
                                </div>
                            </div>
                        </div>
                        <input id="taskId" type="hidden" name="taskId法国 v 个·" value="${taskId}"/>
                        <input id="submit" type="submit" style="display:none;"/>
                        <input id="formId" type="hidden" name="formId" value="${formId}"/>

                        <div class="layui-inline btn_group" style="width: calc(50% - 120px);margin: 0;margin-top: 10px;">
                            <label class="layui-form-label"></label>
                            <div class="layui-input-inline">
                                <button id="send" type="button" class="layui-btn" lay-submit="" lay-filter="partyMemForm" style="padding: 0 20px;font-size: 16px;height: 40px;line-height: 40px;background-color: #FFAB33;border-radius: 4px;">
                                    确定
                                </button>
                                <button id="cancel" onclick="window.history.back();" type="button" class="layui-btn layui-btn-primary" style="background-color: transparent;color: #666;padding: 0 20px;font-size: 16px;height: 40px;line-height: 40px;border-radius: 4px;">
                                    取消
                                </button>
                            </div>
                        </div>
<%--                        <button id="send" type="button" class="btn btn-default col-sm-2 col-xs-4" style="margin-left: 12%; ">上报 </button>--%>
<%--                        <button id="cancel" type="button" class="btn btn-default col-sm-2 col-xs-4" style="margin-left: 45%;">取消 </button>--%>
                    </div>
                </form>
            </div>
            </div>
        </div>

    </div>
	</body>
</html>