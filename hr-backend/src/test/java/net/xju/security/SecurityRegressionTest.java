package net.xju.security;
import net.xju.filter.LoginFilter;
import net.xju.entity.*;
import net.xju.service.HrService;
import net.xju.common.utils.HrUtils;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;

/** Standalone regression suite; no database or business data is accessed. */
public class SecurityRegressionTest {
 static void check(boolean value, String name) { if (!value) throw new AssertionError(name); }
 static Hr user(boolean admin) { Hr h=new Hr();h.setId(42);h.setUsername("test");h.setEnabled(true);Role r=new Role();r.setName(admin?"ROLE_admin":"ROLE_personnel");h.setRoles(Arrays.asList(r));return h; }
 @SuppressWarnings("unchecked") static <T> T proxy(Class<T> type, InvocationHandler handler) { return (T)Proxy.newProxyInstance(type.getClassLoader(),new Class[]{type},handler); }
 static void filter(String path,Hr sessionUser,Hr current,int expected,boolean passed) throws Exception {
  Map<String,Object> session=new HashMap<>();session.put("loginUser",sessionUser);
  HttpSession hs=proxy(HttpSession.class,(p,m,a)->{if(m.getName().equals("getAttribute"))return session.get(a[0]);if(m.getName().equals("setAttribute"))session.put((String)a[0],a[1]);if(m.getName().equals("invalidate"))session.clear();return null;});
  HttpServletRequest req=proxy(HttpServletRequest.class,(p,m,a)->{switch(m.getName()){case "getMethod":return "GET";case "getRequestURI":return path;case "getContextPath":return "";case "getSession":return sessionUser==null?null:hs;default:return null;}});
  int[] status={200};boolean[] reached={false};StringWriter body=new StringWriter();
  HttpServletResponse res=proxy(HttpServletResponse.class,(p,m,a)->{if(m.getName().equals("setStatus"))status[0]=(int)a[0];if(m.getName().equals("getWriter"))return new PrintWriter(body);return null;});
  LoginFilter f=new LoginFilter(){protected Hr reloadUser(Hr h){return current;}};
  f.doFilter(req,res,(a,b)->reached[0]=true);
  check(status[0]==expected && reached[0]==passed,"filter "+path+" status="+status[0]);check(HrUtils.getCurrentHr()==null,"thread context cleanup");
 }
 public static class Endpoint {
  static boolean called;
  @net.xju.common.framework.simplemvc.RequestMapping(value="/delete.do", methods={"POST"})
  public String delete() { called=true; return null; }
 }
 static void methodGuard(String verb, String header, int expected, boolean called) throws Exception {
  net.xju.common.framework.simplemvc.DispatcherServlet servlet=new net.xju.common.framework.simplemvc.DispatcherServlet();
  Field field=servlet.getClass().getDeclaredField("map");field.setAccessible(true);
  Map<String,Object[]> routes=new HashMap<>();routes.put("/delete.do",new Object[]{Endpoint.class,Endpoint.class.getMethod("delete")});field.set(servlet,routes);
  HttpServletRequest req=proxy(HttpServletRequest.class,(p,m,a)->{switch(m.getName()){case "getMethod":return verb;case "getRequestURI":return "/delete.do";case "getHeader":return header;default:return null;}});
  int[] status={200}; HttpServletResponse res=proxy(HttpServletResponse.class,(p,m,a)->{if(m.getName().equals("sendError"))status[0]=(int)a[0];return null;});
  Method service=servlet.getClass().getDeclaredMethod("service",HttpServletRequest.class,HttpServletResponse.class);service.setAccessible(true);
  Endpoint.called=false;service.invoke(servlet,req,res);check(status[0]==expected && Endpoint.called==called,"method/CSRF guard");
 }
 public static void main(String[] args) throws Exception {
  filter("/salary/table/list.do",null,null,401,false);
  filter("/system/hr/role.do",user(false),user(false),403,false);
  filter("/system/basic/permiss/updateMenuRole.do",user(false),user(false),403,false);
  filter("/system/hr/role.do",user(true),user(true),200,true);
  filter("/system/config/menu.do",user(false),user(false),200,true);
  filter("/salary/table/list.do",user(false),user(false),200,true);
  filter("/doLogin.do",null,null,200,true);
  Hr disabled=user(true);disabled.setEnabled(false);filter("/salary/table/list.do",user(true),disabled,401,false);
  filter("/system/hr/role.do",user(true),user(false),403,false);
  Hr unassigned=user(false);unassigned.setRoles(Collections.emptyList());
  filter("/salary/table/list.do",unassigned,unassigned,403,false);
  filter("/employee/basic/list.do",unassigned,unassigned,403,false);
  methodGuard("GET", null, 405, false);
  methodGuard("POST", null, 403, false);
  methodGuard("POST", "XMLHttpRequest", 200, true);
  String hash=HrService.encodePassword("test-password");
  check(!hash.equals("test-password") && hash.startsWith("$2"),"hash storage");
  check(HrService.checkPassword("test-password",hash),"hash login");
  check(!HrService.checkPassword("wrong",hash),"wrong password");
  String longPassword=String.join("",Collections.nCopies(72,"a"));
  check(!HrService.checkPassword(longPassword+"b",HrService.encodePassword(longPassword)),"BCrypt truncation rejected");
  check(HrService.checkPassword("legacy","legacy"),"legacy transition");
  check(!HrService.checkPassword("wrong","legacy"),"legacy wrong password");
  check(!HrService.checkPassword("x","$2bad"),"malformed hash");
  Hr legacy=user(false); legacy.setPassword("legacy");
  final String[] saved={null};
  net.xju.dao.HrDao fakeDao=new net.xju.dao.HrDao(){
   public int upgradePassword(int id,String old,String hash){check(id==42 && old.equals("legacy"),"conditional upgrade");saved[0]=hash;return 1;}
   public boolean insert(Hr h){saved[0]=h.getPassword();return true;}
  };
  HrService service=new HrService();Field dao=HrService.class.getDeclaredField("hrDao");dao.setAccessible(true);dao.set(service,fakeDao);
  check(service.verifyAndUpgradePassword(legacy,"legacy") && HrService.checkPassword("legacy",saved[0]),"legacy upgrade persisted");
  saved[0]=null;check(!service.verifyAndUpgradePassword(legacy,"wrong") && saved[0]==null,"no upgrade on wrong password");
  legacy.setPassword("new-password");check(service.register(legacy) && HrService.checkPassword("new-password",saved[0]),"registration hashes exactly once");
  System.out.println("PASS: password upgrade and registration hashing");
  System.out.println("PASS: anonymous access, admin role, revocation, disabled account, context cleanup, BCrypt and legacy verification");
 }
}
