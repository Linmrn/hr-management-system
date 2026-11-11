package net.xju.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.xju.common.framework.simplemvc.RequestMapping;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Position;
import net.xju.service.OpLogService;
import net.xju.service.PositionService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class PositionController {

    private final PositionService positionService = new PositionService();
    private final OpLogService opLogService = new OpLogService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping("/system/basic/pos/home.do")
    public String home() {
        return "s:index";
    }

    @RequestMapping("/system/basic/pos/list.do")
    public String getAllPositions() {
        try {
            List<Position> list = positionService.getAllPositions();
            return "json:" + JSONUtil.toJson(Result.success(list));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtil.toJson(Result.error("查询失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/pos/add.do", methods={"POST", "PUT", "DELETE"})
    public String addPosition(HttpServletRequest request) {
        try {
            Position position = objectMapper.readValue(request.getInputStream(), Position.class);
            if (positionService.addPosition(position) == 1) {
                opLogService.log("新增职位：" + position.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "添加成功"));
            }
            return JSONUtil.toJson(Result.fail("添加失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtil.toJson(Result.error("添加失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/pos/update.do", methods={"POST", "PUT", "DELETE"})
    public String updatePositions(HttpServletRequest request) {
        try {
            Position position = objectMapper.readValue(request.getInputStream(), Position.class);
            if (positionService.updatePositions(position) == 1) {
                opLogService.log("更新职位：" + position.getName());
                return "json:" + JSONUtil.toJson(Result.success(null, "更新成功"));
            }
            return JSONUtil.toJson(Result.fail("更新失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtil.toJson(Result.error("更新失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/pos/delete.do", methods={"POST", "PUT", "DELETE"})
    public String deletePositionById(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (positionService.deletePositionById(id) == 1) {
                opLogService.log("删除职位ID：" + id);
                return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
            }
            return JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }

    @RequestMapping(value="/system/basic/pos/deleteBatch.do", methods={"POST", "PUT", "DELETE"})
    public String deletePositionsByIds(HttpServletRequest request) {
        try {
            String[] idStrs = request.getParameterValues("ids");
            if (idStrs == null || idStrs.length == 0) {
                return JSONUtil.toJson(Result.fail("请选择要删除的职位"));
            }
            Integer[] ids = Arrays.stream(idStrs).map(Integer::parseInt).toArray(Integer[]::new);
            int deleted = positionService.deletePositionsByIds(ids);
            if (deleted == ids.length) {
                opLogService.log("批量删除职位数量：" + ids.length);
                return "json:" + JSONUtil.toJson(Result.success(null, "删除成功"));
            }
            return JSONUtil.toJson(Result.fail("删除失败"));
        } catch (Exception e) {
            e.printStackTrace();
            return JSONUtil.toJson(Result.error("删除失败: " + e.getMessage()));
        }
    }
}
