package net.xju.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // 允许的来源（开发阶段用前端地址）

        // 直接放行预检请求，不进入后续逻辑
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(200);
            return;
        }

        // 继续执行下一个过滤器或目标 Servlet
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}