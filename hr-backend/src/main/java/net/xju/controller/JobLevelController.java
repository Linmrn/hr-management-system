package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.JobLevel;
import net.xju.service.JobLevelService;
import net.xju.service.OpLogService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class JobLevelController {

    private final JobLevelService jobLevelService = new JobLevelService();
    private final OpLogService opLogService = new OpLogService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/system/basic/joblevel/list.do")
    public String getAllJobLevels() {
        try {
            List<JobLevel> list = jobLevelService.getAllJobLevels();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/joblevel/add.do", methods={"POST", "PUT", "DELETE"})
    public String addJobLevel(HttpServletRequest request) {
        try {
            JobLevel jobLevel = objectMapper.readValue(request.getInputStream(), JobLevel.class);
            if (jobLevelService.addJobLevel(jobLevel) == 1) {
                opLogService.log("新增职称：" + jobLevel.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "添加成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/joblevel/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateJobLevelById(HttpServletRequest request) {
        try {
            JobLevel jobLevel = objectMapper.readValue(request.getInputStream(), JobLevel.class);
            if (jobLevelService.updateJobLevelById(jobLevel) == 1) {
                opLogService.log("更新职称：" + jobLevel.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/joblevel/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deleteJobLevelById(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (jobLevelService.deleteJobLevelById(id) == 1) {
                opLogService.log("删除职称ID：" + id);
                return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/joblevel/deleteBatch.do", methods={"POST", "PUT", "DELETE"})
    public String deleteJobLevelsByIds(HttpServletRequest request) {
        try {
            String[] idStrs = request.getParameterValues("ids");
            if (idStrs == null || idStrs.length == 0) {
                return "json:" + JSONUtil.toJson(Result.fail("请选择要删除的职称"));
            }
            Integer[] ids = Arrays.stream(idStrs).map(Integer::parseInt).toArray(Integer[]::new);
            int deleted = jobLevelService.deleteJobLevelsByIds(ids);
            if (deleted == ids.length) {
                opLogService.log("批量删除职称数量：" + ids.length);
                return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("部分删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }
}
