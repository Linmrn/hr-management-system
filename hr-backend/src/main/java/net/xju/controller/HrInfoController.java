package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.HrUtils;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Hr;
import net.xju.service.HrService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class HrInfoController {

    private HrService hrService = new HrService();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERFACE_DIR = "/uploads/userface/";

    @RequestMapping("/hr/info.do")
    public String getCurrentHr(HttpServletRequest request) {
        Hr currentHr = HrUtils.getCurrentHr();
        if (currentHr == null) {
            currentHr = (Hr) request.getSession().getAttribute("loginUser");
        }
        if (currentHr == null) {
            return "json:" + JSONUtil.toJson(Result.error("用户未登录", 401));
        }
        currentHr.setPassword(null);
        return "json:" + JSONUtil.toJson(Result.success(currentHr));
    }

    @RequestMapping(value="/hr/info/update.do", methods={"POST", "PUT", "DELETE"})
    public String updateHr(HttpServletRequest request) {
        try {
            Hr hr = parseJsonBody(request, Hr.class);
            Hr currentHr = HrUtils.getCurrentHr();
            if (currentHr == null) {
                currentHr = (Hr) request.getSession().getAttribute("loginUser");
            }
            if (currentHr == null) {
                return "json:" + JSONUtil.toJson(Result.error("用户未登录", 401));
            }
            if (!currentHr.getId().equals(hr.getId())) {
                return "json:" + JSONUtil.toJson(Result.error("无权修改其他用户", 403));
            }
            // Personal profile updates must not change account identity or enabled state.
            hr.setUsername(currentHr.getUsername());
            hr.setEnabled(currentHr.getEnabled());
            if (hrService.updateHr(hr) == 1) {
                Hr updatedHr = hrService.getHrById(hr.getId());
                updatedHr.setPassword(null);
                request.getSession().setAttribute("loginUser", updatedHr);
                HrUtils.setCurrentHr(updatedHr);
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/hr/pass.do", methods={"POST", "PUT", "DELETE"})
    public String updateHrPasswd(HttpServletRequest request) {
        try {
            Map<String, Object> info = parseJsonBody(request, Map.class);
            String oldpass = (String) info.get("oldpass");
            String pass = (String) info.get("pass");
            Integer hrid = info.get("hrid") == null ? null : Integer.valueOf(String.valueOf(info.get("hrid")));
            if (hrid == null) {
                return "json:" + JSONUtil.toJson(Result.error("参数错误"));
            }
            if (HrUtils.getCurrentHr() == null || !hrid.equals(HrUtils.getCurrentHr().getId())) {
                return "json:" + JSONUtil.toJson(Result.error("无权修改其他用户", 403));
            }
            if (hrService.updateHrPasswd(oldpass, pass, hrid)) {
                return "json:" + JSONUtil.toJson(Result.success("更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("旧密码错误"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/hr/userface.do", methods={"POST", "PUT", "DELETE"})
    public String updateHrUserface(HttpServletRequest request) {
        try {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                return "json:" + JSONUtil.toJson(Result.error("未选择文件"));
            }
            String idParam = request.getParameter("id");
            if (idParam == null) {
                return "json:" + JSONUtil.toJson(Result.error("缺少用户ID"));
            }
            Integer id = Integer.parseInt(idParam);
            if (HrUtils.getCurrentHr() == null || !id.equals(HrUtils.getCurrentHr().getId())) {
                return "json:" + JSONUtil.toJson(Result.error("无权修改其他用户", 403));
            }
            String realPath = request.getServletContext().getRealPath(USERFACE_DIR);
            File dir = new File(realPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String originalName = filePart.getSubmittedFileName();
            String suffix = originalName == null || originalName.lastIndexOf('.') < 0 ? "" : originalName.substring(originalName.lastIndexOf('.')).toLowerCase(java.util.Locale.ROOT);
            if (!(".jpg".equals(suffix) || ".jpeg".equals(suffix) || ".png".equals(suffix))) {
                return "json:" + JSONUtil.toJson(Result.fail("只允许JPG或PNG图片"));
            }
            String newFileName = UUID.randomUUID().toString() + suffix;
            filePart.write(realPath + File.separator + newFileName);
            String url = request.getContextPath() + USERFACE_DIR + newFileName;
            if (hrService.updateUserface(url, id) == 1) {
                Hr currentHr = HrUtils.getCurrentHr();
                if (currentHr != null && currentHr.getId().equals(id)) {
                    currentHr.setUserface(url);
                    request.getSession().setAttribute("loginUser", currentHr);
                    HrUtils.setCurrentHr(currentHr);
                }
                return "json:" + JSONUtil.toJson(Result.success(url, "更新成功"));
            }
            return "json:" + JSONUtil.toJson(Result.error("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("上传失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/logout.do", methods={"POST", "PUT", "DELETE"})
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        HrUtils.clear();
        return "json:" + JSONUtil.toJson(Result.success("注销成功"));
    }

    @RequestMapping(value="/hr/logout.do", methods={"POST", "PUT", "DELETE"})
    public String logoutWithHrPrefix(HttpServletRequest request) {
        return logout(request);
    }

    private <T> T parseJsonBody(HttpServletRequest request, Class<T> clazz) throws IOException {
        return objectMapper.readValue(request.getInputStream(), clazz);
    }
}