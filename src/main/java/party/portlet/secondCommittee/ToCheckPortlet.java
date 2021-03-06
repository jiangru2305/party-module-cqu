package party.portlet.secondCommittee;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ParamUtil;
import dt.session.SessionManager;
import hg.party.dao.secondCommittee.MeetingPlanDao;
import hg.party.server.secondCommittee.SecondCommitteeService;
import hg.util.ConstantsKey;
import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import party.constants.PartyPortletKeys;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名称： party<br>
 * 内容摘要： @TODO<br>
 * 创建人 　： Mingxiang Duan<br>
 * 创建日期： 2017年12月26日上午10:54:05<br>
 * 版本号　 ： v1.0.0<br>
 * 公司　　 : <br>
 * 修改记录1 <br>
 * 修改日期：<br>
 * 版本号 　：<br>
 * 修改人 　：<br>
 * 修改内容： <br>
 */


@Component(
		immediate = true,
		property = {
			"com.liferay.portlet.display-category=category.sample",
			"com.liferay.portlet.instanceable=true",
			"javax.portlet.display-name=支部、二级党委会议记录列表",
			"javax.portlet.init-param.template-path=/",
			
			"com.liferay.portlet.requires-namespaced-parameters=false",
			"javax.portlet.portlet-mode=text/html;view,edit",
			"javax.portlet.init-param.view-template=/jsp/secondCommittee/toNote.jsp",
			"javax.portlet.name=" + PartyPortletKeys.ToCheckPortlet,
			"javax.portlet.resource-bundle=content.Language",
			"javax.portlet.security-role-ref=power-user,user"
		},
		service = Portlet.class
	)
public class ToCheckPortlet extends MVCPortlet {
	@Reference
	private MeetingPlanDao meetingPlanDao;
	Logger logger = Logger.getLogger(ToCheckPortlet.class);


	@Override
	@Transactional
	public void doView(RenderRequest request, RenderResponse response)
			throws IOException, PortletException {
		try {
			String sessionId=request.getRequestedSessionId();
			String orgId = SessionManager.getAttribute(sessionId, "department").toString();
			String search = ParamUtil.getString(request, "search");
			int pageNo = ParamUtil.getInteger(request, "pageNo");
			int totalPage = ParamUtil.getInteger(request, "total_page_");//总页码
			if(pageNo <= 0){
				pageNo = 1;//默认当前页为1
			}else if(pageNo > totalPage){
				pageNo = totalPage;
			}
			
			Map<String, Object> informMeetingList = meetingPlanDao.queryInformMeetingsByOrgId(orgId, search, pageNo);
			logger.info("informMeetingList :" + informMeetingList);

			request.setAttribute("search", search);
			request.setAttribute("pageNo", informMeetingList.get("pageNow"));
			request.setAttribute("pages", informMeetingList.get("totalPage"));
			request.setAttribute("meetingStates", ConstantsKey.MEETING_STATES);
			request.setAttribute("informMeetingList", informMeetingList.get("list"));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.doView(request, response);
	}
}
