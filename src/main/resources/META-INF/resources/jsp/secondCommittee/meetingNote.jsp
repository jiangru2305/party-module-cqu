<%@ include file="/init.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<!-- 保存新增站点 -->
<%-- <portlet:actionURL name="${submitCommand }" var="submitForm"/> --%>
<portlet:actionURL name="/hg/postSubmissions" var="submitForm"/>
<portlet:actionURL name="/hg/meetingNote/submit" var="uploadMeetingNotesUrl"/>
<!-- 附件下载 -->
<portlet:resourceURL id="/PartyImageDownCommand" var="download">
</portlet:resourceURL>

<portlet:resourceURL id="/hg/getPublicObject" var="getPublicObject"/>

<portlet:resourceURL id="/hg/getGroup" var="getGroup"/>
<portlet:resourceURL id="/form/uploadImage" var="uploadimageUrl"/>
<!-- 视频上传 -->
<portlet:resourceURL id="/form/uploadVideo" var="uploadvideoUrl"/>
<!-- 附件上传 -->
<portlet:resourceURL id="/form/uploadFile" var="uploadfileUrl"/>
<html>
<head>
    <style type="text/css">
    </style>
    <script type="text/javascript" src="${basePath}/js/utf8-jsp/ueditor.config-thumb.js?v=4"></script>
    <script type="text/javascript" src="${basePath}/js/utf8-jsp/ueditor.all.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ basePath }/js/utf8-jsp/lang/zh-cn/zh-cn.js"></script>
    <link rel="stylesheet" href="${basePath }/js/utf8-jsp/third-party/codemirror/codemirror.css"/>
    <link rel="stylesheet" href="${basePath }/js/utf8-jsp/themes/iframe.css"/>
    <link rel="stylesheet" href="${basePath }/js/utf8-jsp/themes/default/css/ueditor.css"/>
    <script type="text/javascript" charset="utf-8"
            src="${ basePath }/js/utf8-jsp/third-party/codemirror/codemirror.js"></script>
    <script type="text/javascript" charset="utf-8"
            src="${ basePath }/js/utf8-jsp/third-party/zeroclipboard/ZeroClipboard.js"></script>
    <link rel="stylesheet" type="text/css" href="${basePath}/cqu/css/release-event.min.css" />

    <portlet:resourceURL id="/hg/taskCheckReplyState" var="taskCheckReplyState"/>
    <portlet:resourceURL id="/hg/checkMeetingTime" var="checkMeetingTimeUrl"/>

</head>
<body>
<div class="table_form_content release_event_container">
    <!-- 右侧盒子内容 -->
    <div class="release_event_page">
        <div class="breadcrumb_group">
            当前位置：
            <span class="layui-breadcrumb" lay-separator=">">
                        <a href="javascript:;">组织生活管理</a>
                        <a href="javascript:;">提交会议纪要</a>
                    </span>
        </div>
        <div class="form_container">
            <form class="layui-form custom_form" id="activityForm" action="${uploadMeetingNotesUrl}" enctype="multipart/form-data" method="post">
                <div class="layui-inline">
                    <input type="hidden" name="formId" value="${formId}">
                    <input type="hidden" name="meetingId" value="${meeting.meeting_id}">
                    <label class="layui-form-label">党组织：</label>
                    <div class="layui-input-inline">
                        <input type="text" disabled maxlength="20"
                               name="activityName" autocomplete="off" class="layui-input"
                               value="${meeting.sname}">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">会议类型：</label>
                    <div class="layui-input-inline">
                        <input type="text" disabled maxlength="20" name="activityName"
                               autocomplete="off" class="layui-input" value="${meeting.meeting_type}">
                    </div>
                </div>
                <div class="layui-inline">
                    <label class="layui-form-label">会议主题：</label>
                    <div class="layui-input-inline">
                        <input type="text" disabled maxlength="20" name="activityName"
                               autocomplete="off" class="layui-input" value="${meeting.meeting_theme}">
                    </div>
                </div>
                <div class="layui-inline ueditor_container">
                    <label class="layui-form-label">会议考勤：</label>
                    <input type="hidden" id="attendances" name="attendances">
                    <div class="layui-input-inline">
                        <div id="attendance"></div>
                    </div>
                </div>
                <div class="layui-inline ueditor_container">
                    <label class="layui-form-label">会议纪要：</label>
                    <div class="layui-input-inline">
                        <script id="meeting_content" name="meeting_content"  type="text/plain"></script>
                    </div>
                </div>
                <div class="layui-inline btn_group">
                    <label class="layui-form-label"></label>
                    <div class="layui-input-inline">
                        <button type="button" class="layui-btn" lay-submit="" lay-filter="activityForm">确定</button>
                        <button type="button" class="layui-btn layui-btn-primary">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</div>
<script type="text/javascript">
    var ueObj = UE.getEditor("meeting_content", { initialFrameWidth:821, initialFrameHeight: 250});
    var uploadUrls = {
        file: '${uploadfileUrl}',
        image: '${uploadimageUrl}',
        video: '${uploadvideoUrl}'
    };
    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
    UE.Editor.prototype.getActionUrl = function(action) {
        if (action === 'uploadimage' || action === 'uploadscrawl' || action === 'uploadimage') {
            return uploadUrls.image;
        } else if (action === 'uploadvideo') {//视频
            return uploadUrls.video;
        } else if (action === 'uploadfile') {//附件
            return uploadUrls.file;
        } else {
            return this._bkGetActionUrl.call(this, action);
        }
    };
    $(function () {
        layui.use('element', function(){
            var element = layui.element;
        });
        layui.use('form', function(){
            var form = layui.form;
            //表单提交
            form.on('submit(activityForm)', function(data){

                $('#attendances').val(transfer.getData('attendance')
                    .map(function(att){return att.value}).join(","));
                // layer.alert(JSON.stringify(data.field), {
                //     title: '最终的提交信息'
                // });
                $('#activityForm').submit();
                return true;
            });
        });
        var transfer;
        layui.use('transfer', function(){
            transfer = layui.transfer;
            var participants = ${participants};
            participants = participants.map(function (p) {
                return {value: p.participant_id, title: p.member_name}
            });
            transfer.render({
                elem: '#attendance',
                title: ['应到人员', '实到人员'],
                height: 210,
                id: 'attendance',
                data: participants
            });
        });
        //渲染
    });
</script>
</body>


</html>