<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/init.jsp" %>
<portlet:resourceURL id="/PartyPassCommand" var="PartyPass" />
<portlet:resourceURL id="/PartyRejectedCommand" var="PartyRejected" />
<html>
	<head>
		 <link rel="stylesheet" href="${basePath }/css/details.css" />
		 <style type="text/css">
		 	.details_content_title >p{
		 		text-align:left!important;
		 	}
			#rejectModal .layui-form-item .layui-input-inline{
				width:200px
			}
			#rejectModal .layui-form-label{
				width:140px;
				font-weight:bold;
			}
			#rejectModal .layui-form-label-text{
				float: left;
				display: block;
				padding: 0 10px;
				width: 200px;
				font-weight: 400;
				line-height: 40px;
				font-size: 16px;
				text-align: left;
			}
		 </style>
	</head>
	<body>
	<div class="table_form_content">
		<%--    <div class="table_form_content activity_manage_container">--%>
		<div class="activity_manage_page">
			<div class="breadcrumb_group" style="margin-bottom: 20px;">
				当前位置：
				<span class="layui-breadcrumb" lay-separator=">">
                        <a href="javascript:;">组织生活管理</a>
					   <c:if test="${orgType == 'organization'}" >
						   <a href="javascript:;" onclick="window.location.href='/approvalplanone'">审批计划</a>
					   </c:if>
						<c:if test="${orgType == 'secondary'}" >
							<a href="javascript:;" onclick="window.location.href='/approvalplantwo'">审批计划</a>
						</c:if>
                        <a href="javascript:;">计划详情</a>
                    </span>
			</div>
			<div class="bg_white_container">
				<div class="details_container">
					<p class="details_title">${org.org_name}--${meetingTheme }</p>
					<div class="details_content">
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>会议类型:</span>${type }</p>
							<p class="col-sm-6 col-xs-12"><span>开展时间:</span>${time }</p>
						</div>
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>开展校区：</span>${meetingPlan.campus}</p>
							<p class="col-sm-6 col-xs-12"><span>开展地点：</span>${meetingPlan.placeName }</p>
						</div>
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>参会人员：</span>${meetingUserName }</p>
							<p class="col-sm-6 col-xs-12"><span>主持人：</span>${meetingPlan.hostName }</p>
						</div>
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>联系人：</span>${meetingPlan.contactName }</p>
							<p class="col-sm-6 col-xs-12"><span>联系人电话：</span>${meetingPlan.contact_phone }</p>
						</div>
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>列席人员：</span>${meetingPlan.sit }</p>
							<p class="col-sm-6 col-xs-12"></p>
						</div>
						<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
							<p class="col-sm-6 col-xs-12"><span>详细内容：</span></p>
							<p class="col-sm-6 col-xs-12"></p>
						</div>
						<div class="details_content_info" style="word-wrap:break-word">
							${content }
						</div>
						<c:if test="${hasNote}">
							<div class="details_content_title" style="border-bottom: 0px solid #e1e1e1;">
								<p class="col-sm-6 col-xs-12"><span>实到人员：</span>${attendances }</p>
								<p class="col-sm-6 col-xs-12"></p>
							</div>
							<div class="details_content_title">
								<p class="col-sm-6 col-xs-12"><span>会议纪要：</span></p>
								<p class="col-sm-6 col-xs-12"></p>
							</div>
							<div class="details_content_info" style="word-wrap:break-word">
								${note.attachment }
							</div>
						</c:if>
						<div class="col-sm-12 col-xs-12">
							<div class="layui-inline btn_group"
								 style="width: calc(50% - 120px);margin: 0;margin-top: 10px;">
								<label class="layui-form-label"></label>
								<div class="layui-input-inline">
									<input value="${meetingPlan.task_status == '1'}" type="hidden">
									<c:if test="${meetingPlan.task_status == '1' && hasCheckPermission}">
										<button type="button" id="pass" class="layui-btn" style="padding: 0 20px;font-size: 16px;height: 40px;line-height: 40px;background-color: #FFAB33;border-radius: 4px;">
											通过
										</button>
										<button type="button" id="reject" class="layui-btn layui-btn-primary" style="background-color: transparent;color: #666;padding: 0 20px;font-size: 16px;height: 40px;line-height: 40px;border-radius: 4px;">
											驳回
										</button>
									</c:if>
									<button type="button" onclick="window.history.back();"
											class="layui-btn layui-btn-primary" style="background-color: transparent;color: #666;padding: 0 20px;font-size: 16px;height: 40px;line-height: 40px;border-radius: 4px;">
										返回
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- 弹窗 -->
			<div style="display: none" id="rejectModal">
				<form class="layui-form" action="">
					<input type="hidden" class="layui-layer-input"  name="meetingId" value="1">
					<div class="layui-form-item">
						<div class="layui-inline">
							<label class="layui-form-label layui-required">驳回理由</label>
							<div class="layui-input-inline">
								<select name="rejectReason" lay-verify="select" >
									<option value="">-请选择-</option>
									<c:forEach var="reason" items="${reasonList }">
										<option value="${reason.resources_value}">${reason.resources_value}</option>
									</c:forEach>
								</select>
							</div>
						</div>
					</div>

					<div class="layui-layer-btn layui-layer-btn-">
						<a class="layui-layer-btn0" type="button"  lay-submit="" lay-filter="rejectForm">确定</a>
						<a class="layui-layer-btn1">取消</a>
					</div>
				</form>
			</div>
		</div>
		<script>
			layui.use(['layer','form'], function() {
				var layer = layui.layer,
						form = layui.form;
				form.on('submit(rejectForm)', function (data) {
					var url = "${PartyRejected}";
					$.ajax({
						url:url,
						data:{meeting_id2:"${meetingPlan.meeting_id}",should_:data.field.rejectReason},
						dataType:'json',
						async:false,
						success:function(res){
							if(res){
								layer.msg("驳回成功。");
								window.history.back();
							}
						}
					});

				})
				form.verify({
					select: function (value, item) {
						if (value == '' || value == null) {
							return "请选择必填项。";
						}
					}
				});
				$("#pass").click(function(){
					layer.confirm('确认通过？', {
						btn: ['确定','取消'] //按钮
					}, function(){
						$.ajax({
							url:"${PartyPass}",
							data:{ meetingId:"${meetingPlan.meeting_id}"},
							dataType:'json',
							success:function(res){
								if(res.code == 200){
									layer.msg("审核通过");
									window.history.back();
								}
							}
						});
					});
				});
				$("#reject").click(function(){
					layer.prompt({
						type: 1,
						btn: 0,
						content: $("#rejectModal")
					});
				});
			})
		</script>
	</div>
	</body>
</html>