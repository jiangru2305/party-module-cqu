package party.portlet.report.dao;

import com.dt.springjdbc.dao.impl.PostgresqlDaoImpl;
import com.dt.springjdbc.dao.impl.PostgresqlQueryResult;
import org.osgi.service.component.annotations.Component;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import party.portlet.report.entity.ReportOrgTask;

import java.util.List;
import java.util.Map;

@Component(immediate = true,service = ReportTaskOrgDao.class)
public class ReportTaskOrgDao extends PostgresqlDaoImpl<ReportOrgTask> {
    public void saveAll(List<ReportOrgTask> contents) {
        contents.forEach(this::saveOrUpdate);
    }

    public List<ReportOrgTask> findByTaskId(String id){
        String sql = "select * from hg_party_report_task_org where task_id = ?";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(ReportOrgTask.class), id);
    }

    public PostgresqlQueryResult<Map<String, Object>> findPageByTaskId(String taskId, int page) {
        String sql = "select * from hg_party_report_task_org where task_id = ?";
        return postGresqlFindBySql(page, 10, sql, taskId);
    }

    public PostgresqlQueryResult<Map<String, Object>> findPage(String orgId, int page) {
        String sql = "select t.theme as theme, t.description as description, t.publish_time as publish_time," +
                " t.files as templateFiles, t.word_files as wordTemplateFiles, o.status as status, t.task_id, " +
                " r.files as uploadFiles, r.word_files as wordUploadFiles, r.status as report_status" +
                " from hg_party_report_task_org o inner join hg_party_report_task t on o.task_id = t.task_id" +
                " left join hg_party_report r on o.task_id = r.task_id and o.org_id = r.org_id" +
                " where o.org_id = ? order by o.status asc, t.publish_time desc";
        return postGresqlFindBySql(page, 10, sql, orgId);
    }

    public ReportOrgTask findByTaskIdAndOrgId(String taskId, String department) {
        String sql = "select * from hg_party_report_task_org where task_id = ? and org_id = ?";
        return jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(ReportOrgTask.class), taskId, department);
    }

    public int countByTaskId(String id) {
        String sql = "select count(id) from hg_party_report_task_org where task_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, id);
    }

    public List<String> findOrgNameByTaskId(String taskId) {
        String sql = "select org.org_name from hg_party_report_task_org task " +
                "left join hg_party_org org on task.org_id = org.org_id and org.historic = false " +
                "where task_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, taskId);
    }
}
