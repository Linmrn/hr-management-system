# HR 人事管理系统

基于 Vue 3 与 Java Servlet 的前后端分离人事管理系统，提供员工档案、人事事务、薪资管理、统计分析和系统配置等功能。后端使用自定义 SimpleMVC 框架处理请求，通过 JDBC 访问 MySQL，使用 Maven 构建 WAR 包并部署到 Tomcat。

## 功能概览

| 模块           | 主要功能                                             |
| -------------- | ---------------------------------------------------- |
| 账号与个人中心 | 登录、注册、退出、个人资料维护、密码修改、头像上传   |
| 员工管理       | 员工信息查询、分页、增删改、Excel 导入导出、员工档案 |
| 人事管理       | 奖惩、培训、考评、调薪、调动记录维护                 |
| 薪资管理       | 工资账套、员工账套配置、工资表维护与导出、工资查询   |
| 统计分析       | 人事统计、积分统计与记录、生日及转正信息             |
| 系统管理       | 部门、职位、职称、操作员、角色菜单配置、操作日志     |

员工高级资料页中的培训、考评、调薪、调动标签仍有“开发中”占位内容；部分记录可通过独立的人事管理页面维护，员工详情页可汇总查看历史记录。考评组件及接口已存在，但默认菜单与静态路由尚未提供独立考评页面入口。

## 技术栈

| 层次       | 技术                                                    |
| ---------- | ------------------------------------------------------- |
| 前端       | Vue 3、Vite 8、Vue Router 4、Pinia 3                    |
| 界面与图表 | Element Plus、ECharts、Sass                             |
| HTTP 请求  | Axios，开发环境通过 Vite 代理访问后端                   |
| 后端       | Java 8 编译目标、Servlet 4.0、自定义 SimpleMVC          |
| 数据访问   | MySQL Connector/J 8.0.33、Druid、Apache Commons DbUtils |
| 数据处理   | Jackson、Fastjson、Apache POI、jBCrypt                  |
| 构建与部署 | Maven、WAR、Tomcat                                      |

## 项目结构

前后端为两个独立目录，以下启动说明按同级目录组织：

```text
hr-backend/
├── pom.xml
├── scripts/                         # 安全与工资记录回归脚本
├── SECURITY-VERIFICATION.md          # 安全验证与部署说明
├── src/test/java/                    # Java 安全回归入口
├── src/main/java/net/xju/
│   ├── common/framework/simplemvc/  # 请求映射与分发
│   ├── common/utils/                # 数据库、JSON、Excel 等工具
│   ├── controller/                  # HTTP 接口
│   ├── service/                     # 业务处理
│   ├── dao/                         # SQL 与数据访问
│   ├── entity/                      # 实体类
│   └── filter/                      # 用户上下文、跨域处理
├── src/main/resources/
│   ├── db.properties                # 数据库连接配置
│   └── hr-db.sql                    # 建表与初始化数据
└── src/main/webapp/WEB-INF/web.xml   # Servlet 与过滤器配置

hr-frontend/
├── package.json
├── vite.config.js                   # 开发端口与接口代理
└── src/
    ├── views/                       # 业务页面
    ├── components/                  # 页面组件
    ├── router/                      # 路由配置
    ├── store/                       # 状态管理
    ├── api/                         # 接口封装
    └── utils/                       # 请求与菜单等工具
```

## 本地运行

### 1. 准备环境

- JDK 8 或兼容的更高版本（Maven 编译目标为 Java 8）。
- Maven 3.x。
- MySQL 8.0。
- Tomcat 9（项目使用 `javax.servlet` API）。
- Node.js `^20.19.0 || >=22.12.0`，以及 npm；此版本要求来自当前 Vite 包。

### 2. 初始化数据库

在后端目录打开终端，登录 MySQL：

```sh
mysql -u root -p
```

在 MySQL 客户端执行，路径替换为实际项目路径：

```sql
SOURCE D:/HP/IdeaProjects/hr-backend/src/main/resources/hr-db.sql;
```

脚本会创建并使用 `hr-db`，写入部门、民族、政治面貌、职位、职称、角色与菜单等基础配置，不包含登录账号、员工和工资业务数据。**脚本包含 `DROP TABLE`，重新导入会覆盖已有同名表，请仅在准备好的开发数据库中执行。**

数据库连接由 `src/main/resources/db.properties` 配置，支持以下环境变量覆盖：

| 环境变量         | 对应配置项 | 仓库默认值                          |
| ---------------- | ---------- | ----------------------------------- |
| `HR_DB_URL`      | `url`      | `jdbc:mysql://localhost:3306/hr-db` |
| `HR_DB_USERNAME` | `username` | 空，启动前必须配置                  |
| `HR_DB_PASSWORD` | `password` | 空，按本地数据库账号配置            |

为 **Tomcat 进程**设置这些变量；通过 IDEA 启动时，在 Tomcat 运行配置中设置。修改其他终端的变量不会影响已经启动的 Tomcat。不要把实际凭据写进 Git。以下仅说明资源配置键：

```properties
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/hr-db
username=你的数据库用户名
password=你的数据库密码
initialSize=10
maxActive=20
```

### 3. 构建并部署后端

在 `hr-backend` 目录执行：

```sh
mvn clean package
```

构建产物为 `target/hr-backend-1.0-SNAPSHOT.war`。将其部署到 Tomcat 9，HTTP 端口设为 `8080`，应用上下文设为 `/`。

- 使用 IntelliJ IDEA：配置本地 Tomcat，添加该项目的 WAR 或 exploded artifact，设置 Application context 为 `/`。
- 使用独立 Tomcat：将 WAR 命名为 `ROOT.war` 放入 `webapps`，先妥善处理已有 ROOT 应用，再启动 Tomcat。

前端默认将接口转发到 `http://localhost:8080`，因此根上下文配置很重要。后端通过 Servlet 容器启动，不使用 `java -jar`。

### 4. 启动前端

在 `hr-frontend` 目录执行：

```sh
npm ci
npm run dev
```

浏览器打开终端显示的地址，默认是 `http://localhost:5173`。如果端口已占用，以 Vite 实际输出为准。

首次访问 `http://localhost:5173/#/register`（或在登录页切换注册），提交自己的账号。新账号默认禁用且未分配角色，没有默认管理员账号或密码。由数据库管理员连接 `hr-db`，完成一次性管理员初始化，将下方两处用户名替换为刚注册的用户名：

```sql
START TRANSACTION;
UPDATE hr SET enabled = 1 WHERE username = '替换为刚注册的用户名';
INSERT INTO hr_role (hrid, rid)
SELECT h.id, r.id FROM hr h CROSS JOIN role r
WHERE h.username = '替换为刚注册的用户名' AND r.name = 'ROLE_admin'
AND NOT EXISTS (SELECT 1 FROM hr_role x WHERE x.hrid = h.id AND x.rid = r.id);
COMMIT;
```

其余账号由管理员在操作员管理页面启用并分配角色。不要为公开部署创建固定演示密码。

首次登录后，可先核对部门、职位和职称，再添加员工、维护人事记录，最后建立工资账套并为员工配置账套和工资记录。

### 5. 构建前端

```sh
npm run build
```

输出目录为 `hr-frontend/dist`。`npm run preview` 可预览构建产物；完整业务联调仍需后端与代理。正式部署时由 Web 服务器托管静态资源，将 `*.do` 请求（含查询参数）反向代理到 Tomcat，使前端页面与接口保持同源。`vite.config.js` 中的 `server.proxy` 仅用于开发服务器，不会打包进静态文件。前端采用 Hash 路由，页面地址形如 `/#/home/dashboard`。

头像保存在后端部署目录下的 `uploads/`，通过受登录保护的 `/file/image.do` 读取。部署环境需允许写入该目录，并在重新部署前备份需要保留的上传文件。

## 验证与回归

后端现有回归脚本需要 JDK、Maven 和 Python 3。在后端目录使用 PowerShell 执行：

```powershell
# 首次运行先下载并缓存 Maven 依赖
mvn package
./scripts/test-security.ps1
```

脚本使用 `mvn -o package -q` 离线打包，随后显式执行 `SecurityRegressionTest` 和 `scripts/test_salary_guards.py`。Java 回归使用替身 DAO 与 Servlet 请求；工资回归从实际 DAO 提取 SQL，在 SQLite 内存表中验证已结算记录的更新、删除保护。两者均不访问业务数据库，不能替代 MySQL、Tomcat 环境中的联调与并发测试。仅运行 `mvn test` 不会自动执行该 Java 回归入口。

前端使用 `npm run build` 检查生产构建，目前没有配置独立的 `test` 或 `lint` 脚本。其他部署验证步骤见 [安全验证说明](SECURITY-VERIFICATION.md)。

## 接口与开发约定

后端在 `web.xml` 中将 `*.do` 请求交给 `DispatcherServlet`，扫描 `net.xju.controller` 下的 `@RequestMapping`。常用入口如下：

| 接口                        | 常用方法 | 用途                                      |
| --------------------------- | -------- | ----------------------------------------- |
| `/doLogin.do`               | POST     | 登录，提交表单字段 `username`、`password` |
| `/doRegister.do`            | POST     | 注册，提交表单字段 `username`、`password` |
| `/logout.do`                | POST     | 退出登录                                  |
| `/hr/info.do`               | GET      | 获取当前账号资料                          |
| `/system/config/menu.do`    | GET      | 获取当前账号菜单                          |
| `/employee/basic/list.do`   | GET      | 分页查询员工                              |
| `/employee/basic/import.do` | POST     | 导入员工                                  |
| `/employee/basic/export.do` | GET      | 导出员工                                  |
| `/salary/sob/list.do`       | GET      | 查询工资账套                              |
| `/salary/table/list.do`     | GET      | 查询工资表                                |
| `/system/log/list.do`       | GET      | 查询操作日志（管理员）                    |

登录成功后使用 HTTP Session 保存用户，前端 Axios 开启 `withCredentials`。普通业务接口使用 `Result` 返回 JSON，常见字段包括 `success`、`data`、`message`、`code`，具体以接口实现为准；文件导出接口直接返回文件。客户端应同时检查 HTTP 状态与 `success` 字段，部分业务失败仍以 HTTP 200 返回。

新增业务通常依次补充实体、DAO、Service 和 Controller，再在前端增加页面与接口调用。数据库连接配置修改后，需要重新构建或更新部署资源。

## 常见问题

| 现象                     | 排查方向                                                     |
| ------------------------ | ------------------------------------------------------------ |
| 接口返回 404             | 确认 WAR 已部署、Tomcat 端口为 8080、应用上下文为 `/`，并检查请求是否带 `.do` |
| 数据库连接失败           | 检查 MySQL 服务、`hr-db` 是否存在、数据库用户名和密码，以及 Tomcat 日志 |
| 登录失败                 | 确认账号已注册、已启用且密码正确；本项目无预置演示账号       |
| 登录后业务接口返回 403   | 确认管理员已分配角色；访问系统管理需要 `ROLE_admin`          |
| POST 请求返回 403 或 405 | 检查是否携带 `X-Requested-With: XMLHttpRequest`，以及请求方法是否符合接口声明 |
| 前端安装或启动失败       | 检查 Node.js 是否满足 Vite 的版本要求，并在前端目录执行命令  |
| 页面可访问但接口失败     | 检查后端是否启动，以及 `vite.config.js` 的代理目标           |
| 上传失败                 | `web.xml` 将单文件与整个 multipart 请求上限均设为 2 MiB，检查文件及请求大小、上传目录写权限 |

## 当前实现说明

所有业务请求需要有效会话，系统管理仅管理员可用，员工、薪资和统计接口要求至少一个已分配角色。现有业务角色共享业务权限，不提供按部门的数据隔离。

管理员身份依据数据库中的 `ROLE_admin` 角色名。受保护请求会重新加载账号状态和角色，因此禁用账号或撤销权限会在后续请求生效。新注册和修改密码使用 BCrypt；旧账号的明文密码在成功登录时尝试升级。

写接口拒绝 GET，要求 `X-Requested-With: XMLHttpRequest`。服务端不开放跨域访问；前后端应通过同源代理部署。此自定义请求头与禁止跨域的组合用于阻止跨站表单写请求，上传组件也必须携带该头。

调动页面仅为历史记录登记；新增、编辑或删除历史记录不改变当前任职状态。实际调动在员工基本资料中修改部门和岗位。

已结算工资记录由后端和 DAO SQL 阻止更新、删除；发生状态冲突时接口返回冲突结果。

本文依据仓库源码和配置整理，启动步骤仍需在实际 JDK、MySQL 与 Tomcat 环境中完成联调验证。

## 许可证

项目采用 [MIT License](LICENSE)。第三方依赖及原始来源代码应保留各自的版权和授权声明。

