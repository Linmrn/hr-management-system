package net.xju.common.utils;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JSONUtil {
    public static void printByJSON(HttpServletResponse response, Object obj) {
        String jsonStr = "";
        if (obj != null && !"".equals(obj)) {
            jsonStr = JSON.toJSONString(obj);
        }
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json;charset=UTF-8");
            out = response.getWriter();
            out.print(jsonStr);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    public static String toJson(Object obj) {
        if (obj == null) {
            return "";
        }
        return JSON.toJSONString(obj);
    }
}
