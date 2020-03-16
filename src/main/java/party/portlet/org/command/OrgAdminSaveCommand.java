package party.portlet.org.command;

import com.alibaba.fastjson.JSON;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import hg.party.dao.org.OrgDao;
import hg.util.result.ResultUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.util.StringUtils;
import party.constants.PartyPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.IOException;
import java.io.PrintWriter;


@Component(
		immediate = true,
		property = {
				"javax.portlet.name=" + PartyPortletKeys.OrgAdmin,
				"mvc.command.name=/org/adminSave"
	    },
	    service = MVCResourceCommand.class
)
public class OrgAdminSaveCommand implements MVCResourceCommand {
	@Reference
	private OrgDao orgDao;

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse) throws PortletException{
		String orgId = ParamUtil.getString(resourceRequest, "orgId");
		orgId = HtmlUtil.escape(orgId);
		String adminStr = ParamUtil.getString(resourceRequest, "admin");
		adminStr = HtmlUtil.escape(adminStr);
		PrintWriter printWriter = null;
		try {
			printWriter = resourceResponse.getWriter();
			if(!StringUtils.isEmpty(orgId)){
				boolean suc;
				if (StringUtils.isEmpty(adminStr)){
					suc = orgDao.changeAdmin(orgId );
				}else {
					String[] admins = adminStr.split(",");
					suc = orgDao.changeAdmin(orgId, admins);
				}
				if (suc){
					printWriter.write(JSON.toJSONString(ResultUtil.success("保存成功！")));
				}else {
					printWriter.write(JSON.toJSONString(ResultUtil.success("保存失败..")));
				}

			}else{
				printWriter.write(JSON.toJSONString(ResultUtil.fail("组织orgId不能为空！")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
