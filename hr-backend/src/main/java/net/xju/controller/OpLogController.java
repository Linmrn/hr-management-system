package net.xju.controller;

import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.service.OpLogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OpLogController {

    private final OpLogService opLogService = new OpLogService();

    @RequestMapping("/system/log/list.do")
    public String list(HttpServletRequest request, HttpServletResponse response) {
        try {
            int page = parseInt(request.getParameter("page"), 1);
            int size = parseInt(request.getParameter("size"), 10);
            String keyword = request.getParameter("keyword");
            return "json:" + JSONUtil.toJson(Result.success(opLogService.getByPage(page, size, keyword)));
        } catch (Exception e) {
            e.printStackTrace();
            return "json:" + JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    private int parseInt(String value, int defaultValue) {
        if (value == null || value.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
