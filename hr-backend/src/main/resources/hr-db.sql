-- 创建数据库 hr-db
CREATE
DATABASE /*!32312 IF NOT EXISTS*/`hr-db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE
`hr-db`;

-- 部门表
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `name`     varchar(32)  DEFAULT NULL COMMENT '部门名称',
    `parentId` int(11) DEFAULT NULL,
    `depPath`  varchar(255) DEFAULT NULL,
    `enabled`  tinyint(1) DEFAULT '1',
    `isParent` tinyint(1) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8;

INSERT INTO `department`(`id`, `name`, `parentId`, `depPath`, `enabled`, `isParent`)
VALUES (1, '股东会', -1, '.1', 1, 1),
       (4, '董事会', 1, '.1.4', 1, 1),
       (5, '总办', 4, '.1.4.5', 1, 1),
       (8, '财务部', 5, '.1.4.5.8', 1, 0),
       (78, '市场部', 5, '.1.4.5.78', 1, 1),
       (81, '华北市场部', 78, '.1.4.5.78.81', 1, 1),
       (82, '华南市场部', 78, '.1.4.5.78.82', 1, 0),
       (85, '石家庄市场部', 81, '.1.4.5.78.81.85', 1, 0),
       (86, '西北市场部', 78, '.1.4.5.78.86', 1, 1),
       (87, '西安市场', 86, '.1.4.5.78.86.87', 1, 1),
       (89, '莲湖区市场', 87, '.1.4.5.78.86.87.89', 1, 0),
       (91, '技术部', 5, '.1.4.5.91', 1, 0),
       (92, '运维部', 5, '.1.4.5.92', 1, 1),
       (93, '运维1部', 92, '.1.4.5.92.93', 1, 0),
       (94, '运维2部', 92, '.1.4.5.92.94', 1, 0),
       (96, 'bbb', 1, '.1.96', 1, 1),
       (104, '111', 96, '.1.96.104', 1, 0);

-- 民族表
DROP TABLE IF EXISTS `nation`;
CREATE TABLE `nation`
(
    `id`   int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

INSERT INTO `nation`(`id`, `name`)
VALUES (1, '汉族'),
       (2, '蒙古族'),
       (3, '回族'),
       (4, '藏族'),
       (5, '维吾尔族'),
       (6, '苗族'),
       (7, '彝族'),
       (8, '壮族'),
       (9, '布依族'),
       (10, '朝鲜族'),
       (11, '满族'),
       (12, '侗族'),
       (13, '瑶族'),
       (14, '白族'),
       (15, '土家族'),
       (16, '哈尼族'),
       (17, '哈萨克族'),
       (18, '傣族'),
       (19, '黎族'),
       (20, '傈僳族'),
       (21, '佤族'),
       (22, '畲族'),
       (23, '高山族'),
       (24, '拉祜族'),
       (25, '水族'),
       (26, '东乡族'),
       (27, '纳西族'),
       (28, '景颇族'),
       (29, '柯尔克孜族'),
       (30, '土族'),
       (31, '达斡尔族'),
       (32, '仫佬族'),
       (33, '羌族'),
       (34, '布朗族'),
       (35, '撒拉族'),
       (36, '毛难族'),
       (37, '仡佬族'),
       (38, '锡伯族'),
       (39, '阿昌族'),
       (40, '普米族'),
       (41, '塔吉克族'),
       (42, '怒族'),
       (43, '乌孜别克族'),
       (44, '俄罗斯族'),
       (45, '鄂温克族'),
       (46, '崩龙族'),
       (47, '保安族'),
       (48, '裕固族'),
       (49, '京族'),
       (50, '塔塔尔族'),
       (51, '独龙族'),
       (52, '鄂伦春族'),
       (53, '赫哲族'),
       (54, '门巴族'),
       (55, '珞巴族'),
       (56, '基诺族');

-- 政治面貌表
DROP TABLE IF EXISTS `politicsstatus`;
CREATE TABLE `politicsstatus`
(
    `id`   int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

INSERT INTO `politicsstatus`(`id`, `name`)
VALUES (1, '中共党员'),
       (2, '中共预备党员'),
       (3, '共青团员'),
       (4, '民革党员'),
       (5, '民盟盟员'),
       (6, '民建会员'),
       (7, '民进会员'),
       (8, '农工党党员'),
       (9, '致公党党员'),
       (10, '九三学社社员'),
       (11, '台盟盟员'),
       (12, '无党派民主人士'),
       (13, '普通公民');

-- 职称表
DROP TABLE IF EXISTS `joblevel`;
CREATE TABLE `joblevel`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `name`       varchar(32) DEFAULT NULL COMMENT '职称名称',
    `titleLevel` enum('正高级','副高级','中级','初级','员级') DEFAULT NULL,
    `createDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `enabled`    tinyint(1) DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;

INSERT INTO `joblevel`(`id`, `name`, `titleLevel`, `createDate`, `enabled`)
VALUES (9, '教授', '正高级', '2018-01-11 00:00:00', 1),
       (10, '副教授', '副高级', '2018-01-11 21:19:20', 1),
       (12, '助教', '初级', '2018-01-11 21:35:39', 1),
       (13, '讲师', '中级', '2018-01-11 00:00:00', 0),
       (14, '初级工程师', '初级', '2018-01-14 00:00:00', 1),
       (15, '中级工程师66', '中级', '2018-01-14 00:00:00', 1),
       (16, '高级工程师', '副高级', '2018-01-14 16:19:14', 1),
       (17, '骨灰级工程师', '正高级', '2018-01-14 16:19:24', 1);

-- 职位表
DROP TABLE IF EXISTS `position`;
CREATE TABLE `position`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `name`       varchar(32) DEFAULT NULL COMMENT '职位',
    `createDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `enabled`    tinyint(1) DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;

INSERT INTO `position`(`id`, `name`, `createDate`, `enabled`)
VALUES (29, '技术总监', '2018-01-11 21:13:42', 1),
       (30, '运营总监', '2018-01-11 21:13:48', 1),
       (31, '市场总监', '2018-01-11 00:00:00', 1),
       (33, '研发工程师', '2018-01-14 00:00:00', 0),
       (34, '运维工程师', '2018-01-14 16:11:41', 1),
       (36, 'Java研发经理', '2019-10-01 00:00:00', 1);

-- 2. 员工主表 (依赖 department, nation, politicsstatus, joblevel, position)
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`
(
    `id`             int(11) NOT NULL AUTO_INCREMENT COMMENT '员工编号',
    `name`           varchar(10) DEFAULT NULL COMMENT '员工姓名',
    `gender`         char(4)     DEFAULT NULL COMMENT '性别',
    `birthday`       date        DEFAULT NULL COMMENT '出生日期',
    `idCard`         char(18)    DEFAULT NULL COMMENT '身份证号',
    `wedlock`        enum('已婚','未婚','离异') DEFAULT NULL COMMENT '婚姻状况',
    `nationId`       int(8) DEFAULT NULL COMMENT '民族',
    `nativePlace`    varchar(20) DEFAULT NULL COMMENT '籍贯',
    `politicId`      int(8) DEFAULT NULL COMMENT '政治面貌',
    `email`          varchar(20) DEFAULT NULL COMMENT '邮箱',
    `phone`          varchar(11) DEFAULT NULL COMMENT '电话号码',
    `address`        varchar(64) DEFAULT NULL COMMENT '联系地址',
    `departmentId`   int(11) DEFAULT NULL COMMENT '所属部门',
    `jobLevelId`     int(11) DEFAULT NULL COMMENT '职称ID',
    `posId`          int(11) DEFAULT NULL COMMENT '职位ID',
    `engageForm`     varchar(8)  DEFAULT NULL COMMENT '聘用形式',
    `tiptopDegree`   enum('博士','硕士','本科','大专','高中','初中','小学','其他') DEFAULT NULL COMMENT '最高学历',
    `specialty`      varchar(32) DEFAULT NULL COMMENT '所属专业',
    `school`         varchar(32) DEFAULT NULL COMMENT '毕业院校',
    `beginDate`      date        DEFAULT NULL COMMENT '入职日期',
    `workState`      enum('在职','离职') DEFAULT '在职' COMMENT '在职状态',
    `workID`         char(8)     DEFAULT NULL COMMENT '工号',
    `contractTerm` double DEFAULT NULL COMMENT '合同期限',
    `conversionTime` date        DEFAULT NULL COMMENT '转正日期',
    `notWorkDate`    date        DEFAULT NULL COMMENT '离职日期',
    `beginContract`  date        DEFAULT NULL COMMENT '合同起始日期',
    `endContract`    date        DEFAULT NULL COMMENT '合同终止日期',
    `workAge`        int(11) DEFAULT NULL COMMENT '工龄',
    PRIMARY KEY (`id`),
    KEY              `departmentId` (`departmentId`),
    KEY              `jobId` (`jobLevelId`),
    KEY              `dutyId` (`posId`),
    KEY              `nationId` (`nationId`),
    KEY              `politicId` (`politicId`),
    KEY              `workID_key` (`workID`),
    CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`departmentId`) REFERENCES `department` (`id`),
    CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`jobLevelId`) REFERENCES `joblevel` (`id`),
    CONSTRAINT `employee_ibfk_3` FOREIGN KEY (`posId`) REFERENCES `position` (`id`),
    CONSTRAINT `employee_ibfk_4` FOREIGN KEY (`nationId`) REFERENCES `nation` (`id`),
    CONSTRAINT `employee_ibfk_5` FOREIGN KEY (`politicId`) REFERENCES `politicsstatus` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1942 DEFAULT CHARSET=utf8;


-- Business and account seed data omitted for public distribution.

-- 3. 员工相关明细表 (依赖 employee)

-- 调薪表
DROP TABLE IF EXISTS `adjustsalary`;
CREATE TABLE `adjustsalary`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `eid`          int(11) DEFAULT NULL,
    `asDate`       date         DEFAULT NULL COMMENT '调薪日期',
    `beforeSalary` int(11) DEFAULT NULL COMMENT '调前薪资',
    `afterSalary`  int(11) DEFAULT NULL COMMENT '调后薪资',
    `reason`       varchar(255) DEFAULT NULL COMMENT '调薪原因',
    `remark`       varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY            `pid` (`eid`),
    CONSTRAINT `adjustsalary_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 考评表
DROP TABLE IF EXISTS `appraise`;
CREATE TABLE `appraise`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `eid`        int(11) DEFAULT NULL,
    `appDate`    date         DEFAULT NULL COMMENT '考评日期',
    `appResult`  varchar(32)  DEFAULT NULL COMMENT '考评结果',
    `appContent` varchar(255) DEFAULT NULL COMMENT '考评内容',
    `remark`     varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY          `pid` (`eid`),
    CONSTRAINT `appraise_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 奖罚表
DROP TABLE IF EXISTS `employeeec`;
CREATE TABLE `employeeec`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `eid`      int(11) DEFAULT NULL COMMENT '员工编号',
    `ecDate`   date         DEFAULT NULL COMMENT '奖罚日期',
    `ecReason` varchar(255) DEFAULT NULL COMMENT '奖罚原因',
    `ecPoint`  int(11) DEFAULT NULL COMMENT '奖罚分',
    `ecType`   int(11) DEFAULT NULL COMMENT '奖罚类别，0：奖，1：罚',
    `remark`   varchar(255) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY        `pid` (`eid`),
    CONSTRAINT `employeeec_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 调动表
DROP TABLE IF EXISTS `employeeremove`;
CREATE TABLE `employeeremove`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `eid`        int(11) DEFAULT NULL,
    `afterDepId` int(11) DEFAULT NULL COMMENT '调动后部门',
    `afterJobId` int(11) DEFAULT NULL COMMENT '调动后职位',
    `removeDate` date         DEFAULT NULL COMMENT '调动日期',
    `reason`     varchar(255) DEFAULT NULL COMMENT '调动原因',
    `remark`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY          `pid` (`eid`),
    CONSTRAINT `employeeremove_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 培训表
DROP TABLE IF EXISTS `employeetrain`;
CREATE TABLE `employeetrain`
(
    `id`           int(11) NOT NULL AUTO_INCREMENT,
    `eid`          int(11) DEFAULT NULL COMMENT '员工编号',
    `trainDate`    date         DEFAULT NULL COMMENT '培训日期',
    `trainContent` varchar(255) DEFAULT NULL COMMENT '培训内容',
    `remark`       varchar(255) DEFAULT NULL COMMENT '备注',
    `picture`      varchar(500) DEFAULT NULL COMMENT '图片URL',
    PRIMARY KEY (`id`),
    KEY            `pid` (`eid`),
    CONSTRAINT `employeetrain_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 4. 工资账套表 (需在 empsalary 之前)

DROP TABLE IF EXISTS `salary`;
CREATE TABLE `salary`
(
    `id`                   int(11) NOT NULL AUTO_INCREMENT,
    `basicSalary`          int(11) DEFAULT NULL COMMENT '基本工资',
    `bonus`                int(11) DEFAULT NULL COMMENT '奖金',
    `lunchSalary`          int(11) DEFAULT NULL COMMENT '午餐补助',
    `trafficSalary`        int(11) DEFAULT NULL COMMENT '交通补助',
    `allSalary`            int(11) DEFAULT NULL COMMENT '应发工资',
    `pensionBase`          int(11) DEFAULT NULL COMMENT '养老金基数',
    `pensionPer`           float       DEFAULT NULL COMMENT '养老金比率',
    `createDate`           timestamp NULL DEFAULT NULL COMMENT '启用时间',
    `medicalBase`          int(11) DEFAULT NULL COMMENT '医疗基数',
    `medicalPer`           float       DEFAULT NULL COMMENT '医疗保险比率',
    `accumulationFundBase` int(11) DEFAULT NULL COMMENT '公积金基数',
    `accumulationFundPer`  float       DEFAULT NULL COMMENT '公积金比率',
    `name`                 varchar(32) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;


-- Business and account seed data omitted for public distribution.
DROP TABLE IF EXISTS `salary_record`;
CREATE TABLE salary_record (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               eid INT NOT NULL COMMENT '员工ID',
                               yearMonth VARCHAR(7) NOT NULL COMMENT '年月，格式：YYYY-MM',
                               basicSalary DECIMAL(10,2) DEFAULT 0,
                               bonus DECIMAL(10,2) DEFAULT 0,
                               lunchSalary DECIMAL(10,2) DEFAULT 0,
                               trafficSalary DECIMAL(10,2) DEFAULT 0,
                               pension DECIMAL(10,2) DEFAULT 0,
                               medical DECIMAL(10,2) DEFAULT 0,
                               accumulationFund DECIMAL(10,2) DEFAULT 0,
                               tax DECIMAL(10,2) DEFAULT 0,
                               allSalary DECIMAL(10,2) DEFAULT 0 COMMENT '应发合计',
                               actualSalary DECIMAL(10,2) DEFAULT 0 COMMENT '实发工资',
                               status TINYINT DEFAULT 0 COMMENT '0未结算 1已结算',
                               createTime DATETIME DEFAULT CURRENT_TIMESTAMP,
                               updateTime DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               FOREIGN KEY (eid) REFERENCES employee(id) ON DELETE CASCADE,
                               UNIQUE KEY uk_eid_month (eid, yearMonth)
);
-- Business and account seed data omitted for public distribution.
-- 员工工资关联表 (依赖 employee, salary)
DROP TABLE IF EXISTS `empsalary`;
CREATE TABLE `empsalary`
(
    `id`  int(11) NOT NULL AUTO_INCREMENT,
    `eid` int(11) DEFAULT NULL,
    `sid` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `eid` (`eid`),
    KEY   `empsalary_ibfk_2` (`sid`),
    CONSTRAINT `empsalary_ibfk_1` FOREIGN KEY (`eid`) REFERENCES `employee` (`id`),
    CONSTRAINT `empsalary_ibfk_2` FOREIGN KEY (`sid`) REFERENCES `salary` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;

-- Business and account seed data omitted for public distribution.


-- 5. 用户与权限相关

-- HR操作员表
DROP TABLE IF EXISTS `hr`;
CREATE TABLE `hr`
(
    `id`        int(11) NOT NULL AUTO_INCREMENT COMMENT 'hrID',
    `name`      varchar(32)  DEFAULT NULL COMMENT '姓名',
    `phone`     char(11)     DEFAULT NULL COMMENT '手机号码',
    `telephone` varchar(16)  DEFAULT NULL COMMENT '住宅电话',
    `address`   varchar(64)  DEFAULT NULL COMMENT '联系地址',
    `enabled`   tinyint(1) DEFAULT '1',
    `username`  varchar(255) DEFAULT NULL COMMENT '用户名',
    `password`  varchar(255) DEFAULT NULL COMMENT '密码',
    `userface`  varchar(255) DEFAULT NULL,
    `remark`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- Business and account seed data omitted for public distribution.

-- 角色表 (需在 hr_role 和 menu_role 之前)
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`     int(11) NOT NULL AUTO_INCREMENT,
    `name`   varchar(64) DEFAULT NULL,
    `nameZh` varchar(64) DEFAULT NULL COMMENT '角色名称',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

INSERT INTO `role`(`id`, `name`, `nameZh`)
VALUES (1, 'ROLE_manager', '部门经理'),
       (2, 'ROLE_personnel', '人事专员'),
       (3, 'ROLE_recruiter', '招聘主管'),
       (4, 'ROLE_train', '培训主管'),
       (5, 'ROLE_performance', '薪酬绩效主管'),
       (6, 'ROLE_admin', '系统管理员'),
       (13, 'ROLE_test2', '测试角色2'),
       (14, 'ROLE_test1', '测试角色1'),
       (17, 'ROLE_test3', '测试角色3'),
       (18, 'ROLE_test4', '测试角色4'),
       (19, 'ROLE_test4', '测试角色4'),
       (20, 'ROLE_test5', '测试角色5'),
       (21, 'ROLE_test6', '测试角色6');

-- HR角色关联表 (依赖 hr, role)
DROP TABLE IF EXISTS `hr_role`;
CREATE TABLE `hr_role`
(
    `id`   int(11) NOT NULL AUTO_INCREMENT,
    `hrid` int(11) DEFAULT NULL,
    `rid`  int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY    `rid` (`rid`),
    KEY    `hr_role_ibfk_1` (`hrid`),
    CONSTRAINT `hr_role_ibfk_1` FOREIGN KEY (`hrid`) REFERENCES `hr` (`id`) ON DELETE CASCADE,
    CONSTRAINT `hr_role_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;

-- Business and account seed data omitted for public distribution.

-- 菜单表 (自引用)
DROP TABLE IF EXISTS `menu`;
CREATE TABLE `menu`
(
    `id`          int(11) NOT NULL AUTO_INCREMENT,
    `url`         varchar(64) DEFAULT NULL,
    `path`        varchar(64) DEFAULT NULL,
    `component`   varchar(64) DEFAULT NULL,
    `name`        varchar(64) DEFAULT NULL,
    `iconCls`     varchar(64) DEFAULT NULL,
    `keepAlive`   tinyint(1) DEFAULT NULL,
    `requireAuth` tinyint(1) DEFAULT NULL,
    `parentId`    int(11) DEFAULT NULL,
    `enabled`     tinyint(1) DEFAULT '1',
    PRIMARY KEY (`id`),
    KEY           `parentId` (`parentId`),
    CONSTRAINT `menu_ibfk_1` FOREIGN KEY (`parentId`) REFERENCES `menu` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8;

INSERT INTO `menu`(`id`, `url`, `path`, `component`, `name`, `iconCls`, `keepAlive`, `requireAuth`, `parentId`,
                   `enabled`)
VALUES (1, '/', NULL, NULL, '所有', NULL, NULL, NULL, NULL, 1),
       (2, '/', '/home', 'Home', '员工资料', 'fa fa-user-circle-o', NULL, 1, 1, 1),
       (3, '/', '/home', 'Home', '人事管理', 'fa fa-address-card-o', NULL, 1, 1, 1),
       (4, '/', '/home', 'Home', '薪资管理', 'fa fa-money', NULL, 1, 1, 1),
       (5, '/', '/home', 'Home', '统计管理', 'fa fa-bar-chart', NULL, 1, 1, 1),
       (6, '/', '/home', 'Home', '系统管理', 'fa fa-windows', NULL, 1, 1, 1),
       (7, '/employee/basic/**', '/emp/basic', 'EmpBasic', '基本资料', NULL, NULL, 1, 2, 1),
       (8, '/employee/advanced/**', '/emp/adv', 'EmpAdv', '高级资料', NULL, NULL, 1, 2, 1),
       (9, '/personnel/emp/**', '/per/emp', 'PerEmp', '员工资料', NULL, NULL, 1, 3, 1),
       (10, '/personnel/ec/**', '/per/ec', 'PerEc', '员工奖惩', NULL, NULL, 1, 3, 1),
       (11, '/personnel/train/**', '/per/train', 'PerTrain', '员工培训', NULL, NULL, 1, 3, 1),
       (12, '/personnel/salary/**', '/per/salary', 'PerSalary', '员工调薪', NULL, NULL, 1, 3, 1),
       (13, '/personnel/remove/**', '/per/mv', 'PerMv', '员工调动', NULL, NULL, 1, 3, 1),
       (14, '/salary/sob/**', '/sal/sob', 'SalSob', '工资账套管理', NULL, NULL, 1, 4, 1),
       (15, '/salary/sobcfg/**', '/sal/sobcfg', 'SalSobCfg', '员工账套设置', NULL, NULL, 1, 4, 1),
       (16, '/salary/table/**', '/sal/table', 'SalTable', '工资表管理', NULL, NULL, 1, 4, 1),
       (17, '/salary/month/**', '/sal/month', 'SalMonth', '月末处理', NULL, NULL, 1, 4, 1),
       (18, '/salary/search/**', '/sal/search', 'SalSearch', '工资表查询', NULL, NULL, 1, 4, 1),
       (19, '/statistics/all/**', '/sta/all', 'StaAll', '综合信息统计', NULL, NULL, 1, 5, 1),
       (20, '/statistics/score/**', '/sta/score', 'StaScore', '员工积分统计', NULL, NULL, 1, 5, 1),
       (21, '/statistics/personnel/**', '/sta/pers', 'StaPers', '人事信息统计', NULL, NULL, 1, 5, 1),
       (22, '/statistics/recored/**', '/sta/record', 'StaRecord', '人事记录统计', NULL, NULL, 1, 5, 1),
       (23, '/system/basic/**', '/sys/basic', 'SysBasic', '基础信息设置', NULL, NULL, 1, 6, 1),
       (24, '/system/cfg/**', '/sys/cfg', 'SysCfg', '系统管理', NULL, NULL, 1, 6, 1),
       (25, '/system/log/**', '/sys/log', 'SysLog', '操作日志管理', NULL, NULL, 1, 6, 1),
       (26, '/system/hr/**', '/sys/hr', 'SysHr', '操作员管理', NULL, NULL, 1, 6, 1),
       (27, '/system/data/**', '/sys/data', 'SysData', '备份恢复数据库', NULL, NULL, 1, 6, 1),
       (28, '/system/init/**', '/sys/init', 'SysInit', '初始化数据库', NULL, NULL, 1, 6, 1);

-- 菜单角色关联表 (依赖 menu, role)
DROP TABLE IF EXISTS `menu_role`;
CREATE TABLE `menu_role`
(
    `id`  int(11) NOT NULL AUTO_INCREMENT,
    `mid` int(11) DEFAULT NULL,
    `rid` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY   `mid` (`mid`),
    KEY   `rid` (`rid`),
    CONSTRAINT `menu_role_ibfk_1` FOREIGN KEY (`mid`) REFERENCES `menu` (`id`),
    CONSTRAINT `menu_role_ibfk_2` FOREIGN KEY (`rid`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8;

INSERT INTO `menu_role`(`id`, `mid`, `rid`)
VALUES (161, 7, 3),
       (162, 7, 6),
       (163, 9, 6),
       (164, 10, 6),
       (165, 11, 6),
       (166, 12, 6),
       (167, 13, 6),
       (168, 14, 6),
       (169, 15, 6),
       (170, 16, 6),
       (171, 17, 6),
       (172, 18, 6),
       (173, 19, 6),
       (174, 20, 6),
       (175, 21, 6),
       (176, 22, 6),
       (177, 23, 6),
       (178, 25, 6),
       (179, 26, 6),
       (180, 27, 6),
       (181, 28, 6),
       (182, 24, 6),
       (247, 7, 4),
       (248, 8, 4),
       (249, 11, 4),
       (250, 7, 2),
       (251, 8, 2),
       (252, 9, 2),
       (253, 10, 2),
       (254, 12, 2),
       (255, 13, 2),
       (256, 7, 1),
       (257, 8, 1),
       (258, 9, 1),
       (259, 10, 1),
       (260, 11, 1),
       (261, 12, 1),
       (262, 13, 1),
       (263, 14, 1),
       (264, 15, 1),
       (265, 16, 1),
       (266, 17, 1),
       (267, 18, 1),
       (268, 19, 1),
       (269, 20, 1),
       (270, 21, 1),
       (271, 22, 1),
       (272, 23, 1),
       (273, 24, 1),
       (274, 25, 1),
       (275, 26, 1),
       (276, 27, 1),
       (277, 28, 1),
       (280, 7, 14),
       (281, 8, 14),
       (282, 9, 14);

-- 消息内容表
DROP TABLE IF EXISTS `msgcontent`;
CREATE TABLE `msgcontent`
(
    `id`         int(11) NOT NULL AUTO_INCREMENT,
    `title`      varchar(64)        DEFAULT NULL,
    `message`    varchar(255)       DEFAULT NULL,
    `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- Business and account seed data omitted for public distribution.

-- 操作日志表 (依赖 hr)
DROP TABLE IF EXISTS `oplog`;
CREATE TABLE `oplog`
(
    `id`      int(11) NOT NULL AUTO_INCREMENT,
    `addDate` date         DEFAULT NULL COMMENT '添加日期',
    `operate` varchar(255) DEFAULT NULL COMMENT '操作内容',
    `hrid`    int(11) DEFAULT NULL COMMENT '操作员ID',
    PRIMARY KEY (`id`),
    KEY       `hrid` (`hrid`),
    CONSTRAINT `oplog_ibfk_1` FOREIGN KEY (`hrid`) REFERENCES `hr` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 系统消息表 (依赖 msgcontent, hr)
DROP TABLE IF EXISTS `sysmsg`;
CREATE TABLE `sysmsg`
(
    `id`    int(11) NOT NULL AUTO_INCREMENT,
    `mid`   int(11) DEFAULT NULL COMMENT '消息id',
    `type`  int(11) DEFAULT '0' COMMENT '0表示群发消息',
    `hrid`  int(11) DEFAULT NULL COMMENT '这条消息是给谁的',
    `state` int(11) DEFAULT '0' COMMENT '0 未读 1 已读',
    PRIMARY KEY (`id`),
    KEY     `hrid` (`hrid`),
    KEY     `sysmsg_ibfk_1` (`mid`),
    CONSTRAINT `sysmsg_ibfk_1` FOREIGN KEY (`mid`) REFERENCES `msgcontent` (`id`),
    CONSTRAINT `sysmsg_ibfk_2` FOREIGN KEY (`hrid`) REFERENCES `hr` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;

-- Business and account seed data omitted for public distribution.
-- 员工奖惩数据 (employeeec)  共 22 条

-- Business and account seed data omitted for public distribution.

-- 员工培训数据 (employeetrain)  共 22 条

-- Business and account seed data omitted for public distribution.

-- 员工考评数据 (appraise)  共 20 条

-- Business and account seed data omitted for public distribution.


-- 员工调薪数据 (adjustsalary)  共 21 条

-- Business and account seed data omitted for public distribution.


-- 员工调动数据 (employeeremove)  共 20 条

-- Business and account seed data omitted for public distribution.
-- 存储过程 (去除 DEFINER)

DROP PROCEDURE IF EXISTS `addDep`;
DELIMITER
$$
CREATE PROCEDURE `addDep`(in depName varchar (32), in parentId int, in enabled boolean, out result int, out result2 int)
begin
  declare
did int;
  declare
pDepPath varchar(64);
insert into department
set name=depName, parentId=parentId, enabled=enabled;
select row_count()
into result;
select last_insert_id()
into did;
set
result2=did;
select depPath
into pDepPath
from department
where id = parentId;
update department
set depPath=concat(pDepPath, '.', did)
where id = did;
update department
set isParent= true
where id = parentId;
end $$
DELIMITER ;

DROP PROCEDURE IF EXISTS `deleteDep`;
DELIMITER
$$
CREATE PROCEDURE `deleteDep`(in did int, out result int)
begin
  declare
ecount int;
  declare
pid int;
  declare
pcount int;
  declare
a int;
select count(*)
into a
from department
where id = did
  and isParent = false;
if
a=0 then set result=-2;
else
select count(*)
into ecount
from employee
where departmentId = did;
if
ecount>0 then set result=-1;
else
select parentId
into pid
from department
where id = did;
delete
from department
where id = did
  and isParent = false;
select row_count()
into result;
select count(*)
into pcount
from department
where parentId = pid;
if
pcount=0 then
update department
set isParent= false
where id = pid;
end if;
end if;
end if;
end $$
DELIMITER ;
