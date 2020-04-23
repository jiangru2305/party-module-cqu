package hg.party.command.party;


import com.google.gson.Gson;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import dt.session.SessionManager;
import hg.party.entity.partyMembers.JsonPageResponse;
import hg.party.server.party.PartyMeetingPlanInfoService;
import hg.util.postgres.PostgresqlPageResult;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.util.StringUtils;
import party.constants.PartyPortletKeys;

import javax.portlet.*;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 查询下一级组织发布的计划
 */
@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + PartyPortletKeys.PartyApprovalPlan,
				"javax.portlet.name=" + PartyPortletKeys.PartyApprovalBranch,
				"mvc.command.name=/part/meeting/page"
		},
		service = MVCResourceCommand.class
	)

public class MeetingPlanPageCommand implements MVCResourceCommand {
	@Reference
	PartyMeetingPlanInfoService partyMeetingPlanInfoService;
	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		HttpServletResponse res = PortalUtil.getHttpServletResponse(resourceResponse);
		int page = ParamUtil.getInteger(resourceRequest, "page");
		int size = ParamUtil.getInteger(resourceRequest, "limit");
		String keyword = ParamUtil.getString(resourceRequest, "keyword");
		Object orgId = SessionManager.getAttribute(resourceRequest.getRequestedSessionId(), "department");
		try {
			PostgresqlPageResult<Map<String, Object>> data = new PostgresqlPageResult(null, 0,0);
			if(orgId!=null && !StringUtils.isEmpty(String.valueOf(orgId))){
				if (StringUtils.isEmpty(keyword)){
					data = partyMeetingPlanInfoService.searchPage(page, size,String.valueOf(orgId),null);
				}else {
					data = partyMeetingPlanInfoService.searchPage(page, size, String.valueOf(orgId),keyword);
				}
			}
			Gson gson = new Gson();
			res.getWriter().write(gson.toJson(data.toJsonPageResponse()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
