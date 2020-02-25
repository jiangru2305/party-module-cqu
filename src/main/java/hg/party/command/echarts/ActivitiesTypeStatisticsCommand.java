package hg.party.command.echarts;


import com.alibaba.fastjson.JSON;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import hg.party.entity.party.BaseStatistics;
import hg.party.server.party.PartyOrgServer;
import hg.util.result.ResultUtil;
import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import party.constants.PartyPortletKeys;

import javax.portlet.PortletException;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import java.io.PrintWriter;
import java.util.List;


/**
 * 党活动分类统计
 */

@Component(
		immediate = true,
		property = {
			"javax.portlet.name=" + PartyPortletKeys.PartyEcharts,
			"mvc.command.name=/hg/part/activitiesTypeStatistic"
	    },
	    service = MVCResourceCommand.class
)
public class ActivitiesTypeStatisticsCommand implements MVCResourceCommand{
	
	Logger logger = Logger.getLogger(ActivitiesTypeStatisticsCommand.class);
	@Reference
	private PartyOrgServer partyOrgServer;

	@Override
	public boolean serveResource(ResourceRequest resourceRequest, ResourceResponse resourceResponse)
			throws PortletException {
		int year = ParamUtil.getInteger(resourceRequest, "year");
		int month = ParamUtil.getInteger(resourceRequest, "month");
		try {
			PrintWriter printWriter=resourceResponse.getWriter();
			logger.info("查询党活动分类年月统计");
			if(month >= 0 && year >= 0){
				List<BaseStatistics> activitiesTypeStatistic = partyOrgServer.activitiesTypeStatistic(year,month);
				printWriter.write(JSON.toJSONString(ResultUtil.success(activitiesTypeStatistic)));
			}else{
				printWriter.write(JSON.toJSONString(ResultUtil.fail("年月参数数据错误！")));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}


}