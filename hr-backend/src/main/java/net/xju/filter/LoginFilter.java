package net.xju.filter;

import net.xju.common.utils.HrUtils;
import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;
import net.xju.entity.Hr;
import net.xju.service.HrService;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoginFilter implements Filter {
    private static final Set<String> PUBLIC_PATHS = new HashSet<>(Arrays.asList(
        "/login.do", "/doLogin.do", "/doRegister.do"));
    public void init(FilterConfig config) {}
    protected Hr reloadUser(Hr user) throws SQLException {
        return new HrService().loadUserByUsername(user.getUsername());
    }
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HrUtils.clear();
        try {
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) { resp.setStatus(204); return; }
            String path = req.getRequestURI().substring(req.getContextPath().length());
            if (PUBLIC_PATHS.contains(path)) { chain.doFilter(request, response); return; }
            HttpSession session = req.getSession(false);
            Hr user = session == null ? null : (Hr) session.getAttribute("loginUser");
            if (user == null) { reject(resp, 401, "请先登录"); return; }
            Hr current = reloadUser(user);
            if (current == null || !current.getId().equals(user.getId()) || !Boolean.TRUE.equals(current.getEnabled())) {
                session.invalidate(); reject(resp, 401, "登录已失效"); return;
            }
            current.setPassword(null);
            session.setAttribute("loginUser", current);
            if (path.startsWith("/system/") && !path.equals("/system/config/menu.do") && !HrUtils.isAdmin(current)) {
                reject(resp, 403, "需要管理员权限"); return;
            }
            if ((path.startsWith("/employee/") || path.startsWith("/salary/") || path.startsWith("/statistics/"))
                    && (current.getRoles() == null || current.getRoles().isEmpty())) {
                reject(resp, 403, "请联系管理员分配业务角色"); return;
            }
            HrUtils.setCurrentHr(current);
            chain.doFilter(request, response);
        } catch (SQLException e) {
            reject(resp, 503, "身份验证暂不可用");
        } finally { HrUtils.clear(); }
    }
    private void reject(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJson(Result.error(message, code)));
    }
    public void destroy() {}
}
