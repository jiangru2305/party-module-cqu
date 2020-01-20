package party.portlet.report.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import dt.session.SessionManager;
import hg.party.dao.org.OrgDao;
import hg.party.entity.organization.Organization;
import hg.party.unity.ResourceProperties;
import hg.party.unity.WordUtils;
import hg.util.ConstantsKey;
import hg.util.ExcelUtil;
import hg.util.TransactionUtil;
import org.apache.log4j.Logger;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import party.constants.PartyPortletKeys;
import party.portlet.report.entity.view.ExcelHandler;
import party.portlet.report.dao.ReportDao;
import party.portlet.report.dao.ReportTaskDao;
import party.portlet.report.dao.ReportTaskOrgDao;
import party.portlet.report.entity.ReportOrgTask;
import party.portlet.report.entity.ReportTask;
import party.portlet.report.entity.view.FileView;
import party.portlet.report.entity.view.WordHandler;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@Component(immediate = true,
        property = {
            "javax.portlet.name=" + PartyPortletKeys.SecondaryNewTaskPortlet,
            "mvc.command.name=/secondary/task/add"
        },
        service = MVCActionCommand.class
)
public class AddReportTaskActionCommand implements MVCActionCommand {
    Logger log = Logger.getLogger(AddReportTaskActionCommand.class);

    @Reference
    private ReportDao reportDao;

    @Reference
    private ReportTaskDao reportTaskDao;

    @Reference
    private ReportTaskOrgDao reportTaskOrgDao;

    @Reference
    TransactionUtil transactionUtil;
    @Reference
    private OrgDao orgDao;
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    @Override
    @Transactional
    public boolean processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException {

        String formId = ParamUtil.getString(actionRequest, "formId");
        String orgId = (String) SessionManager.getAttribute(actionRequest.getRequestedSessionId(), "department");
        String userName = (String) SessionManager.getAttribute(actionRequest.getRequestedSessionId(), "userName");
        Organization organization = orgDao.findByOrgId(orgId);
        String theme = ParamUtil.getString(actionRequest, "theme");
        String description = ParamUtil.getString(actionRequest, "description");
        String content = ParamUtil.getString(actionRequest, "task_content");
        Integer status =  ParamUtil.getInteger(actionRequest, "status");
        String toOrg = ParamUtil.getString(actionRequest, "publicObject");
        String taskId = ParamUtil.getString(actionRequest, "taskId");
        ReportTask reportTask;
        if (StringUtils.isEmpty(taskId)) {
            taskId = UUID.randomUUID().toString();
            reportTask = new ReportTask();
            reportTask.setTask_id(taskId);
        }else {
            reportTask = reportTaskDao.findByTaskId(taskId);
        }
        String[] toOrgs = toOrg.split(",");

        synchronized (PortalUtil.getHttpServletRequest(actionRequest).getSession()) {
            String originalFormId = (String) SessionManager.getAttribute(actionRequest.getRequestedSessionId(), "formId-report-task");
            if (formId.equals(originalFormId)) {
                transactionUtil.startTransaction();
                try {
                    reportTask.setContent(content);
                    reportTask.setDescription(description);
                    reportTask.setPublisher(orgId);
                    reportTask.setTheme(theme);
                    reportTask.setPublish_time(new Timestamp(System.currentTimeMillis()));
                    reportTask.setStatus(status);
                    reportTask.setTask_id(taskId);
                    reportTask.setReceivers(toOrg);
                    UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);
                    List<FileView> fileViewList = saveAttachment(uploadPortletRequest, taskId);
                    String type;
                    if (fileViewList.size() > 0){
                        type = fileViewList.get(0).getType();
                        reportTask.setType(type);
                    }else {
                        throw new Exception();
                    }
                    if (type.equalsIgnoreCase(FileView.EXCEL)) {
                        List<ExcelHandler> excels = new ArrayList<>();
                        for (FileView fileView : fileViewList) {
                            Map<String, List<Map<String, Object>>> excelData = ExcelUtil.importAllSheet(fileView.getFilename(), new FileInputStream(fileView.getPath()));
                            ExcelHandler excelHandler = new ExcelHandler(fileView.getFilename(), fileView.getPath(), excelData);
                            excels.add(excelHandler);
                        }
                        reportTask.setFiles(gson.toJson(excels));
                    }else if (type.equalsIgnoreCase(FileView.WORD)){
                        List<WordHandler> words = new ArrayList<>();
                        for (FileView fileView : fileViewList) {
                            List<String> paragraph = new ArrayList<>();
                            String wordContent = WordUtils.importWordData(fileView.getFilename(), new FileInputStream(fileView.getPath()));
                            paragraph.add(wordContent);
                            WordHandler wordHandler = new WordHandler(fileView.getFilename(), fileView.getPath(), paragraph);
                            words.add(wordHandler);
                        }
                        reportTask.setFiles(gson.toJson(words));
                    }

                    reportTaskDao.saveOrUpdate(reportTask);
                    if (status.equals(ConstantsKey.PUBLISHED)) {
                        // 新任务
                        List<ReportOrgTask> orgTasks = new ArrayList<>();
                        for (String org : toOrgs) {
                            ReportOrgTask reportOrgTask = new ReportOrgTask();
                            reportOrgTask.setOrg_id(org);
                            reportOrgTask.setStatus(ConstantsKey.UNREPORTED);
                            reportOrgTask.setTask_id(taskId);
                            orgTasks.add(reportOrgTask);
                        }
                        reportTaskOrgDao.saveAll(orgTasks);
                    }

                    transactionUtil.commit();
                    SessionManager.setAttribute(actionRequest.getRequestedSessionId(), "formId-report-task", "null");
                    if (status.equals(ConstantsKey.PUBLISHED)){
                        actionResponse.sendRedirect("/secondary_report");
                    }else {
                        actionResponse.sendRedirect("/task_draft");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    transactionUtil.rollback();
                }
            }
        }
        return false;
    }
    public List<FileView> saveAttachment(UploadPortletRequest uploadPortletRequest, String taskId) {
        File[] uploadFiles = uploadPortletRequest.getFiles("files");
        String[] filenames = uploadPortletRequest.getFileNames("files");
        List<FileView> fileViewList = new ArrayList<>();

        if (uploadFiles != null) {
            int i = 0;
            for (File file : uploadFiles) {
                FileView fileView = new FileView();
                String sourceFileName = filenames[i++];
                //保存文件最终路径
                ResourceProperties resourceProperties = new ResourceProperties();
                Properties properties = resourceProperties.getResourceProperties();//获取配置文件
                String uploadPath = properties.getProperty("uploadPath");
                File folder = new File(uploadPath);
                String fileUrl = "/ajaxFileName/" + taskId + "/" + sourceFileName;
                File filePath = new File(folder.getAbsolutePath() + fileUrl);
                //保存文件到物理路径
                try {
                    FileUtil.copyFile(file, filePath);
                    fileView.setFilename(sourceFileName);
                    fileView.setPath(filePath.getAbsolutePath());
                    String extension = sourceFileName.substring(sourceFileName.lastIndexOf("."));
                    if (extension.equalsIgnoreCase(".doc")
                            || extension.equalsIgnoreCase(".docx")){
                        fileView.setType(FileView.WORD);
                    }else if (extension.equalsIgnoreCase(".xls")
                            || extension.equalsIgnoreCase(".xlsx")){
                        fileView.setType(FileView.EXCEL);
                    }else{
                        fileView.setType("other");
                    }
                    fileViewList.add(fileView);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return fileViewList;
    }

}
