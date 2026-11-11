package net.xju.common.framework.simplemvc;

import net.xju.common.utils.JSONUtil;
import net.xju.common.utils.Result;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
/*
这个类同学们直接拷贝，不要写，在视频中如果有修改，也是直接拷贝！
 */
public class DispatcherServlet extends HttpServlet {
    private String scanPackageTxt;// "com,net"
    private String viewPathTxt; //   "/page"
    private String suffixTxt;  //     "jsp"
    private Map<String, Object[]> map = new HashMap<>();
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        Object[] classAndMethod = map.get(requestURI);
        if(classAndMethod!=null){
            Class clazz= (Class) classAndMethod[0];
            Method method = (Method) classAndMethod[1];
            if (!java.util.Arrays.asList(method.getAnnotation(RequestMapping.class).methods()).contains(request.getMethod())) {
                response.setHeader("Allow", String.join(", ", method.getAnnotation(RequestMapping.class).methods()));
                response.sendError(405); return;
            }
            if (!"GET".equals(request.getMethod()) && !"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.sendError(403); return;
            }
            try {
                //new  UserAction();
                Object obj =  clazz.getConstructor().newInstance();
                Object viewTxt;
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0) {
                    viewTxt = method.invoke(obj);
                } else if (parameterTypes.length == 1) {
                    viewTxt = method.invoke(obj, request);
                } else if (parameterTypes.length == 2) {
                    viewTxt = method.invoke(obj, request, response);
                } else {
                    throw new SimpleMVCException("不支持的方法参数数量: " + parameterTypes.length + ", method=" + method.getName());
                }
                if(viewTxt!=null){
                    String view = ((String) viewTxt).trim();
                    if (view.startsWith("json:")) {

                        String json = view.substring(5);
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().write(json);
                    } else if(view.startsWith("s:")){
                        view = view.substring(view.indexOf(":") + 1);
                        sendRedirect(view, response);
                    } else {
                        forward(view, request, response);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // 修改：向前端返回JSON格式的错误信息，避免前端收到HTML错误页面
                try {
                    response.setContentType("application/json;charset=UTF-8");
                    String errorJson = JSONUtil.toJson(Result.error("服务器内部错误: " + e.getMessage()));
                    response.getWriter().write(errorJson);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }else{
            try {
                response.setContentType("application/json;charset=UTF-8");
                String errorJson = JSONUtil.toJson(Result.error("找不到对应的路径:" + requestURI, 404));
                response.getWriter().write(errorJson);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void init() throws ServletException {
        //读取配置参数 scan-package , view-path ,suffix
        this.readInitparam();
        this.scanPackage(scanPackageTxt);
    }

    private void scanPackage(String packages) {
        try {
            String[] packs = packages.split(",");
            for (int i = 0; i < packs.length; i++) {
                String eachPack = packs[i].trim();// "com.my"  ----> "com/my"
                String eachPackPath = "/" + eachPack.replace('.', '/') + "/";
                //记住这行:获取类路径的真实路径
                String classDirPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//                修复bug：修复代码的硬盘路径包含空格的问题
                classDirPath = URLDecoder.decode(classDirPath,"UTF-8");
                File file = new File(classDirPath + eachPackPath);//
                if (file.exists()) {
                    String[] classSimpleNames = file.list();
                    for (String simpleName : classSimpleNames) {
                        // UserAction.class
                        String className = eachPack + "." + simpleName.substring(0, simpleName.lastIndexOf("."));
                        Class cla = Class.forName(className);
                        String nameSpace = "";
                        Annotation annotation4Class = cla.getAnnotation(RequestMapping.class);
                        if (annotation4Class != null) {
                            RequestMapping rp = (RequestMapping) annotation4Class;
                            nameSpace = rp.value();
                        }

                        Method[] methods = cla.getMethods();
                        for (Method method : methods) {
                            RequestMapping annotation4Method = method.getAnnotation(RequestMapping.class);
                            if (annotation4Method != null) {
                                String path = annotation4Method.value();
                                String uri = getServletContext().getContextPath()+nameSpace+path;
                                if(map.get(uri)!=null){
                                    throw new  SimpleMVCException("路径重复:"+uri);
                                }else{
                                    map.put(uri,new Object[]{cla,method});
                                }
                            }
                        }

                    }
                } else {
                    throw new SimpleMVCException("包不存在:" + eachPack);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获得视图字符串之后，根据配置参数 跳转 到对应的页面！
     */
    private void forward(String view, HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = viewPathTxt + "/" + view + "." + suffixTxt;
            request.getRequestDispatcher(path).forward(request, response);
        } catch (Exception e) {
            throw new SimpleMVCException(e);
        }
    }

    /**
     * 重定向:
     * @param view
     * @param response
     */
    private void sendRedirect(String view,  HttpServletResponse response) {
        try {
            String path = viewPathTxt + "/" + view + "." + suffixTxt;
            response.sendRedirect(this.getServletContext().getContextPath()+path);
        } catch (Exception e) {
            throw new SimpleMVCException(e);
        }
    }


    /**
     * 读取 前端控制器的配置参数: scan-package , view-path ,suffix
     */

    private void readInitparam() {
        scanPackageTxt = this.getServletConfig().getInitParameter("scan-package");
        if (scanPackageTxt == null) {
            throw new SimpleMVCException("DispatcherServlet 配置缺少参数:" + "scan-package");
        }
        viewPathTxt = this.getServletConfig().getInitParameter("view-path");
        if (viewPathTxt == null) {
            throw new SimpleMVCException("DispatcherServlet 配置缺少参数:" + "view-path");
        }
        suffixTxt = this.getServletConfig().getInitParameter("suffix");
        if (suffixTxt == null) {
            throw new SimpleMVCException("DispatcherServlet 配置缺少参数:" + "suffix");
        }
        scanPackageTxt = scanPackageTxt.trim();
        viewPathTxt = viewPathTxt.trim();
        suffixTxt = suffixTxt.trim();
    }
}