-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: fly
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sys_log`
--

DROP TABLE IF EXISTS `sys_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '请求方法',
  `params` varchar(5000) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT '请求参数',
  `time` bigint NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `user_id_index` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC COMMENT='系统日志';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_log`
--

LOCK TABLES `sys_log` WRITE;
/*!40000 ALTER TABLE `sys_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='菜单管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'系统管理','','',0,'system',7),(2,1,'管理员列表','sys/user','',1,'admin',0),(3,1,'角色管理','sys/role','',1,'role',0),(4,1,'菜单管理','sys/menu','',1,'menu',0),(6,1,'定时任务','sys/schedule','',1,'job',0),(7,6,'查看',NULL,'sys:schedule:page,sys:schedule:info',2,NULL,0),(8,6,'新增',NULL,'sys:schedule:save',2,NULL,0),(9,6,'修改',NULL,'sys:schedule:update',2,NULL,0),(10,6,'删除',NULL,'sys:schedule:delete',2,NULL,0),(11,6,'暂停',NULL,'sys:schedule:pause',2,NULL,0),(12,6,'恢复',NULL,'sys:schedule:resume',2,NULL,0),(13,6,'立即执行',NULL,'sys:schedule:run',2,NULL,0),(14,6,'日志列表',NULL,'sys:schedule:log',2,NULL,0),(15,2,'查看',NULL,'sys:user:page,sys:user:info,sys:log:page,pay:zfb',2,NULL,0),(16,2,'新增','','sys:user:save,sys:role:list',2,'',0),(17,2,'修改','','sys:user:update,sys:role:list',2,'',0),(18,2,'删除','','sys:user:delete',2,'',0),(19,3,'查看',NULL,'sys:role:page,sys:role:info',2,NULL,0),(20,3,'新增',NULL,'sys:role:save,sys:menu:list',2,NULL,0),(21,3,'修改',NULL,'sys:role:update,sys:menu:list',2,NULL,0),(22,3,'删除',NULL,'sys:role:delete',2,NULL,0),(23,4,'查看',NULL,'sys:menu:list,sys:menu:info',2,NULL,0),(24,4,'新增',NULL,'sys:menu:save,sys:menu:select',2,NULL,0),(25,4,'修改',NULL,'sys:menu:update,sys:menu:select',2,NULL,0),(26,4,'删除',NULL,'sys:menu:delete',2,NULL,0),(27,1,'参数管理','sys/config','',1,'config',0),(29,1,'系统日志','sys/log','',1,'log',0),(30,0,'客机管理',NULL,NULL,0,'plane',0),(31,30,'客机管理','plane   ',NULL,1,NULL,0),(32,31,'查看',NULL,'plane:getPlaneInfo',2,NULL,0),(33,31,'新增',NULL,'plane:addPlaneInfo',2,NULL,0),(34,31,'修改',NULL,'plane:updatePlaneInfo',2,NULL,0),(35,31,'删除',NULL,'plane:deletePlaneInfo',2,NULL,0),(36,0,'航班管理',NULL,NULL,0,'flight',0),(37,36,'航班管理','flight',NULL,1,NULL,0),(38,37,'查看',NULL,'flight:getFlight',2,NULL,0),(39,37,'新增',NULL,'flight:addFlight',2,NULL,0),(40,37,'修改',NULL,'plane:updateFlight',2,NULL,0),(41,37,'删除',NULL,'flight:deleteFlight',2,NULL,0),(42,0,'城市管理',NULL,NULL,0,'city',0),(43,42,'城市管理','addr',NULL,1,NULL,0),(44,43,'查看',NULL,'addr:listCity',2,NULL,0),(45,43,'新增',NULL,'addr:addCity',2,NULL,0),(46,43,'修改',NULL,'addr:updateCity',2,NULL,0),(47,43,'删除',NULL,'addr:deleteCity',2,NULL,0),(48,0,'机组人员管理','','',0,'crew',0),(49,48,'机组人员管理','crew','',1,'',0),(50,49,'查看',NULL,'crew:listCrew',2,NULL,0),(51,49,'新增',NULL,'crew:addCrew',2,NULL,0),(52,49,'修改',NULL,'crew:updateCrew',2,NULL,0),(53,49,'删除',NULL,'crew:deleteCrew',2,NULL,0),(54,0,'机组人员分组','','',0,'crewGroup',0),(55,54,'机组人员分组','crewGroup','',1,'',0),(56,55,'查看',NULL,'crewGroup:list',2,NULL,0),(57,55,'新增',NULL,'crewGroup:add',2,NULL,0),(58,55,'修改',NULL,'crewGroup:update',2,NULL,0),(59,55,'删除',NULL,'crewGroup:delete',2,NULL,0),(60,0,'机组人员排班','',NULL,0,'crewSchedule',0),(61,60,'机组人员排班','crewSchedule',NULL,1,NULL,0),(62,61,'查看',NULL,'crewSchedule:list',2,NULL,0),(63,61,'新增',NULL,'crewSchedule:add',2,NULL,0),(64,61,'修改',NULL,'crewSchedule:update',2,NULL,0),(65,61,'删除',NULL,'crewSchedule:delete',2,NULL,0),(67,0,'航班客舱价格','',NULL,0,'flightPrice',0),(68,67,'航班客舱价格','flightPrice',NULL,1,NULL,0),(69,68,'查看',NULL,'flightPrice:list',2,NULL,0),(70,68,'新增',NULL,'flightPrice:add',2,NULL,0),(71,68,'修改',NULL,'flightPrice:update',2,NULL,0),(72,68,'删除',NULL,'flightPrice:delete',2,NULL,0),(73,0,'乘客信息管理','',NULL,0,'passenger',0),(74,73,'乘客信息管理','passenger',NULL,1,NULL,0),(75,74,'查看',NULL,'passenger:list',2,NULL,0),(76,74,'新增',NULL,'passenger:add',2,NULL,0),(77,74,'修改',NULL,'passenger:update',2,NULL,0),(78,74,'删除',NULL,'passenger:delete',2,NULL,0),(79,0,'订单管理','',NULL,0,'订单',0),(80,79,'订单管理','passenger',NULL,1,NULL,0),(81,80,'查看',NULL,'order:list',2,NULL,0),(82,80,'新增',NULL,'order:add',2,NULL,0),(83,80,'修改',NULL,'order:update',2,NULL,0),(84,80,'删除',NULL,'order:delete',2,NULL,0),(85,0,'优惠券管理','',NULL,0,'优惠券',0),(86,85,'优惠券管理','coupon',NULL,1,NULL,0),(87,86,'查看',NULL,'coupon:list',2,NULL,0),(88,86,'新增',NULL,'coupon:add',2,NULL,0),(89,86,'修改',NULL,'coupon:update',2,NULL,0),(90,86,'删除',NULL,'coupon:delete',2,NULL,0),(91,0,'航班预定管理','',NULL,0,'航班预定',0),(92,91,'航班预定管理','coupon',NULL,1,NULL,0),(93,92,'查看',NULL,'booking:list',2,NULL,0),(94,92,'新增',NULL,'booking:add',2,NULL,0),(95,92,'修改',NULL,'booking:update',2,NULL,0),(96,92,'删除',NULL,'booking:delete',2,NULL,0);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='角色';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'admin','超级管理员',1,'2021-11-30 14:08:46'),(2,'server','客服',1,'2019-08-02 16:46:49'),(3,'monitor','监控员',1,'2026-03-21 14:29:06');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=301 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='角色与菜单对应关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (194,1,1),(195,1,2),(196,1,3),(197,1,4),(198,1,6),(199,1,7),(200,1,8),(201,1,9),(202,1,10),(203,1,11),(204,1,12),(205,1,13),(206,1,14),(207,1,15),(208,1,16),(209,1,17),(210,1,18),(211,1,19),(212,1,20),(213,1,21),(214,1,22),(215,1,23),(216,1,24),(217,1,25),(218,1,26),(219,1,27),(220,1,29),(221,1,30),(222,1,31),(223,1,32),(224,1,33),(225,1,34),(226,1,35),(227,1,36),(228,1,37),(229,1,38),(230,1,39),(231,1,40),(232,1,41),(235,2,32),(236,2,38),(237,2,1),(238,2,2),(239,2,3),(240,2,4),(241,2,6),(242,2,7),(243,2,15),(244,2,19),(245,2,23),(246,1,42),(247,1,43),(248,1,44),(249,1,45),(250,1,46),(251,1,47),(252,1,48),(253,1,49),(254,1,50),(255,1,51),(256,1,52),(257,1,53),(258,1,54),(259,1,55),(260,1,56),(261,1,57),(262,1,58),(263,1,59),(264,1,60),(265,1,61),(266,1,62),(267,1,63),(268,1,64),(269,1,65),(270,1,66),(271,1,67),(272,1,68),(273,1,69),(274,1,70),(275,1,71),(276,1,72),(277,1,73),(278,1,74),(279,1,75),(280,1,76),(281,1,77),(282,1,78),(283,1,79),(284,1,80),(285,1,81),(286,1,82),(287,1,83),(288,1,84),(289,1,85),(290,1,86),(291,1,87),(292,1,88),(293,1,89),(294,1,90),(295,1,91),(296,1,92),(297,1,93),(298,1,94),(299,1,95),(300,1,96);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '密码',
  `email` varchar(100) COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(100) COLLATE utf8mb3_unicode_ci DEFAULT NULL COMMENT '手机号',
  `status` tinyint DEFAULT NULL COMMENT '状态  0：禁用   1：正常',
  `create_user_id` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='系统用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$W4AhmPj4sB0EdKVLkXQhs.B0poFkxM4ikKbcHsOSXPKVcu0XLLi.i','1467281460@qq.com','13098734222',1,1,'2016-11-11 11:11:11'),(2,'peter','$2a$10$OLdPw3Smh8tiyL6zaMsLmOLtWLqfGt/FD7v8EwV6FZu9T294BRR9i','123@qq.com','11111111111',1,1,'2019-08-03 09:05:55'),(3,'jack','$2a$10$iX8uJ83ML1Zy2b02y.rUKuUNWpSygSjGz7/dGkLFUs6XcN2zD5dym','77556@qq.com','15671659999',1,1,'2021-12-07 11:45:20'),(8,'rose','$2a$10$wq1Ed67GT7HBhAAXAKHyV.Rgi6qOnvkn/iZA2fnGrMraUbv3t1Ivy','mg0iuc18@yeah.net','17319173708',1,1,'2026-09-02 20:18:27');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='用户与角色对应关系';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1,1),(2,2,2),(3,3,2),(4,8,1);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_city`
--

DROP TABLE IF EXISTS `tb_city`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_city` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '城市编号',
  `city_code` varchar(10) NOT NULL COMMENT '城市三字码（IATA），如 PEK、SHA、CAN',
  `city_name` varchar(50) NOT NULL COMMENT '城市名称',
  `airport_name` varchar(100) NOT NULL COMMENT '机场名称',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0：停用 1：运营中',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_city_code` (`city_code`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航空公司入驻城市表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_city`
--

LOCK TABLES `tb_city` WRITE;
/*!40000 ALTER TABLE `tb_city` DISABLE KEYS */;
INSERT INTO `tb_city` VALUES (1,'PEK','北京','北京首都国际机场',1,'2026-08-01 16:46:39',0),(2,'SHA','上海','上海虹桥国际机场',1,'2026-08-01 16:46:39',0),(3,'CAN','广州','广州白云国际机场',1,'2026-08-01 16:46:39',0),(4,'SZX','深圳','深圳宝安国际机场',1,'2026-08-01 16:46:39',0),(5,'CTU','成都','成都天府国际机场',1,'2026-08-01 16:46:39',0),(6,'CKG','重庆','重庆江北国际机场',1,'2026-08-01 16:46:39',0);
/*!40000 ALTER TABLE `tb_city` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_coupon`
--

DROP TABLE IF EXISTS `tb_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_name` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '优惠券名称',
  `coupon_code` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '优惠券编码',
  `coupon_type` int DEFAULT NULL COMMENT '优惠券类型',
  `coupon_price` double DEFAULT NULL COMMENT '优惠券金额',
  `status` int DEFAULT NULL COMMENT '状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_coupon_code` (`coupon_code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_coupon`
--

LOCK TABLES `tb_coupon` WRITE;
/*!40000 ALTER TABLE `tb_coupon` DISABLE KEYS */;
INSERT INTO `tb_coupon` VALUES (1,'三元立减金','CPN001',1,3,0,'2026-09-28 16:42:28','2026-10-09 10:54:35',0),(2,'十元立减金','CPN002',1,10,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(3,'二十元立减金','CPN003',1,20,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(4,'五十元立减金','CPN004',1,50,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(5,'满100减10','CPN005',2,10,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(6,'满200减20','CPN006',2,20,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(7,'满300减30','CPN007',2,30,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(8,'满500减50','CPN008',2,50,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(9,'满1000减100','CPN009',2,100,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(10,'95折扣券','CPN010',3,0.95,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(11,'9折扣券','CPN011',3,0.9,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(12,'88折扣券','CPN012',3,0.88,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(13,'85折扣券','CPN013',3,0.85,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(14,'8折扣券','CPN014',3,0.8,0,'2026-09-28 16:42:28','2026-09-28 16:42:28',0),(15,'75折扣券','CPN015',3,0.75,1,'2026-09-28 16:42:28','2026-09-28 16:42:28',0);
/*!40000 ALTER TABLE `tb_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_crew`
--

DROP TABLE IF EXISTS `tb_crew`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_crew` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '人员编号',
  `crew_no` varchar(20) NOT NULL COMMENT '工号',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `gender` tinyint NOT NULL COMMENT '性别 0：女 1：男',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `position_type` varchar(50) DEFAULT NULL COMMENT '职位类型',
  `group_id` bigint DEFAULT NULL COMMENT '所属分组ID',
  `base_city_id` bigint DEFAULT NULL COMMENT '基地城市ID',
  `license_no` varchar(50) DEFAULT NULL COMMENT '执照/资质证书编号',
  `flight_hours` int NOT NULL DEFAULT '0' COMMENT '累计飞行时长（小时）',
  `hire_date` date NOT NULL COMMENT '入职日期',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0：离职 1：在职 2：休假',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_crew_no` (`crew_no`),
  KEY `idx_crew_group` (`group_id`),
  KEY `idx_crew_city` (`base_city_id`),
  KEY `idx_crew_status` (`status`),
  CONSTRAINT `fk_crew_city` FOREIGN KEY (`base_city_id`) REFERENCES `tb_city` (`id`),
  CONSTRAINT `fk_crew_group` FOREIGN KEY (`group_id`) REFERENCES `tb_crew_group` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机组人员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_crew`
--

LOCK TABLES `tb_crew` WRITE;
/*!40000 ALTER TABLE `tb_crew` DISABLE KEYS */;
INSERT INTO `tb_crew` VALUES (1,'JL20200001','张伟',1,'13800001001','PILOT',1,1,'LIC-CAPT-001',8500,'2015-03-10',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(2,'JL20200002','李强',1,'13800001002','COPILOT',1,1,'LIC-FO-002',3200,'2019-07-22',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(3,'JL20200003','王芳',0,'13800001003','FLIGHT_ATTENDANT_LEADER',3,1,'LIC-PURS-003',5600,'2017-05-15',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(4,'JL20200004','刘洋',1,'13800001004','PILOT',2,2,'LIC-CAPT-004',9200,'2013-11-08',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(5,'JL20200005','陈静',0,'13800001005','FLIGHT_ATTENDANT',3,1,NULL,1800,'2022-01-20',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(6,'JL20200006','赵磊',1,'13800001006','COPILOT',2,2,'LIC-FO-006',2700,'2020-09-01',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(7,'JL20200007','孙丽',0,'13800001007','FLIGHT_ATTENDANT_LEADER',4,3,NULL,900,'2023-06-12',1,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(8,'JL20200008','周涛',1,'13800001008','COPILOT',1,1,'LIC-ENG-008',6100,'2016-04-18',0,'2026-08-01 16:46:39','2026-09-03 17:25:12',0),(13,'JL20200009','赵小丁',1,'13800001001','PILOT',5,4,'LIC-CAPT-001',8500,'2015-03-10',1,'2026-09-07 14:06:17','2026-09-07 14:06:17',0),(14,'JL20200010','李小兵',1,'061 9352 8756','COPILOT',1,1,'ea ullamco',9,'1986-01-03',1,'2026-09-09 14:00:54','2007-02-16 16:46:30',0),(18,'JL20200011','刘小兰',0,'18273337212',NULL,NULL,NULL,'ea ullkkaa',100,'2026-09-16',1,'2026-09-11 16:48:36','2026-09-11 16:48:36',0);
/*!40000 ALTER TABLE `tb_crew` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_crew_group`
--

DROP TABLE IF EXISTS `tb_crew_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_crew_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分组编号',
  `group_code` varchar(20) NOT NULL COMMENT '分组编码',
  `group_name` varchar(50) NOT NULL COMMENT '分组名称',
  `leader_id` bigint DEFAULT NULL COMMENT '组长（机组人员ID）',
  `base_city_id` bigint NOT NULL COMMENT '所属基地城市ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0：解散 1：正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_code` (`group_code`),
  KEY `fk_group_city` (`base_city_id`),
  CONSTRAINT `fk_group_city` FOREIGN KEY (`base_city_id`) REFERENCES `tb_city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机组人员分组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_crew_group`
--

LOCK TABLES `tb_crew_group` WRITE;
/*!40000 ALTER TABLE `tb_crew_group` DISABLE KEYS */;
INSERT INTO `tb_crew_group` VALUES (1,'GRP-A01','飞行一大队',1,1,1,'2026-08-01 16:46:39','2026-09-03 17:24:37',0),(2,'GRP-A02','飞行二大队',4,2,1,'2026-08-01 16:46:39','2026-09-03 17:24:37',0),(3,'GRP-B01','客舱一大队',3,1,1,'2026-08-01 16:46:39','2026-09-03 17:24:37',0),(4,'GRP-B02','客舱二大队',7,3,1,'2026-08-01 16:46:39','2026-09-03 17:24:37',0),(5,'GRP-A03','飞行三大队',13,4,1,'2026-09-07 11:46:30','2026-09-07 11:46:30',0),(6,'GRP-A04','飞行四大队',NULL,5,1,'2026-09-08 14:23:30','2026-09-08 14:23:30',0),(7,'GRP-B03','客舱三大队',18,6,0,'2026-09-11 16:52:08','2026-09-11 16:52:08',0);
/*!40000 ALTER TABLE `tb_crew_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_crew_schedule`
--

DROP TABLE IF EXISTS `tb_crew_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_crew_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排班编号',
  `pilot_id` bigint NOT NULL COMMENT '机组人员ID',
  `flight_id` char(7) NOT NULL COMMENT '航班批次编号（对应 tb_flight.flight_id）',
  `duty_type` tinyint NOT NULL DEFAULT '1' COMMENT '职责类型 1：执飞 2：备份',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0：取消 1：已排班 2：已完成',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `assistant_id` int NOT NULL COMMENT '乘务长id',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pilot_flight` (`pilot_id`,`flight_id`),
  KEY `idx_schedule_flight` (`flight_id`),
  KEY `idx_schedule_status` (`status`),
  KEY `idx_schedule_pilot` (`pilot_id`),
  CONSTRAINT `fk_schedule_flight` FOREIGN KEY (`flight_id`) REFERENCES `tb_flight` (`flight_id`),
  CONSTRAINT `fk_schedule_pilot` FOREIGN KEY (`pilot_id`) REFERENCES `tb_crew` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 COMMENT='机组人员航班日程安排表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_crew_schedule`
--

LOCK TABLES `tb_crew_schedule` WRITE;
/*!40000 ALTER TABLE `tb_crew_schedule` DISABLE KEYS */;
INSERT INTO `tb_crew_schedule` VALUES (1,1,'B3N8PQ5',2,1,'2026-08-01 17:04:27','2026-09-18 15:19:38',3,0),(2,2,'D5V1JXL',1,2,'2026-08-01 17:04:27','2026-09-03 17:25:07',3,0),(3,3,'F8Z4K3N',1,2,'2026-08-01 17:04:27','2026-09-03 17:25:07',7,0),(4,4,'HY6R2WT',1,2,'2026-08-01 17:04:27','2026-09-03 17:25:07',0,0),(5,6,'J2K6M4C',1,2,'2026-08-01 17:04:27','2026-09-03 17:25:07',7,0),(6,1,'KA7X92M',1,1,'2026-08-01 17:04:27','2026-09-03 17:25:07',0,0),(7,2,'L4H8S6D',1,1,'2026-08-01 17:04:27','2026-09-03 17:25:07',0,0),(8,4,'M6T5X1A',1,1,'2026-08-01 17:04:27','2026-09-03 17:25:07',7,0),(9,5,'P1F2V5B',1,1,'2026-08-01 17:04:27','2026-09-03 17:25:07',0,0),(10,7,'Q9E3Y7U',1,1,'2026-08-01 17:04:27','2026-09-03 17:25:07',3,0),(11,8,'T8R1E5Z',2,2,'2026-08-01 17:04:27','2026-09-03 17:25:07',0,0);
/*!40000 ALTER TABLE `tb_crew_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_flight`
--

DROP TABLE IF EXISTS `tb_flight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_flight` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `flight_id` char(7) NOT NULL COMMENT '航班批次编号',
  `plane_id` bigint NOT NULL COMMENT '客机编号',
  `plane_type` varchar(50) NOT NULL COMMENT '客机类型',
  `departure_time` datetime NOT NULL COMMENT '起飞时间',
  `arrival_time` datetime NOT NULL COMMENT '到达时间',
  `departure_city` varchar(50) NOT NULL COMMENT '出发城市',
  `arrival_city` varchar(50) NOT NULL COMMENT '到达城市',
  `total_seats` int NOT NULL COMMENT '总座位数',
  `real_seats` int NOT NULL COMMENT '实际入座数',
  `status` tinyint NOT NULL COMMENT '状态 0：停飞 1：正常',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flight_id` (`flight_id`),
  KEY `plane_id_index` (`plane_id`),
  KEY `idx_flight_id` (`flight_id`),
  CONSTRAINT `fk_flight_plane_id` FOREIGN KEY (`plane_id`) REFERENCES `tb_plane` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='航班行程';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_flight`
--

LOCK TABLES `tb_flight` WRITE;
/*!40000 ALTER TABLE `tb_flight` DISABLE KEYS */;
INSERT INTO `tb_flight` VALUES (1,'B3N8PQ5',1003,'波音787-9','2025-08-01 09:15:00','2025-08-01 12:15:00','广州','上海',130,0,1,0),(2,'D5V1JXL',1004,'空客A330-300','2025-08-01 11:30:00','2025-08-01 14:45:00','深圳','上海',130,0,1,0),(3,'F8Z4K3N',1005,'波音737-800','2025-08-01 13:00:00','2025-08-01 16:00:00','成都','广州',130,0,1,0),(4,'HY6R2WT',1003,'波音787-9','2025-08-01 10:00:00','2025-08-01 13:00:00','上海','成都',130,0,1,0),(5,'J2K6M4C',1012,'空客A330-300','2025-08-03 11:15:00','2025-08-03 14:00:00','上海','深圳',130,0,1,0),(6,'KA7X92M',1001,'波音737-800','2025-08-01 08:00:00','2025-08-01 10:30:00','北京','上海',130,0,1,0),(7,'L4H8S6D',1009,'波音737 MAX 8','2025-08-02 16:20:00','2025-08-02 19:00:00','广州','上海',130,0,1,0),(8,'M6T5X1A',1007,'波音777-300ER','2025-08-02 08:45:00','2025-08-02 12:15:00','上海','广州',130,0,1,0),(9,'P1F2V5B',1010,'空客A320','2025-08-03 06:50:00','2025-08-03 09:40:00','成都','北京',130,0,1,0),(10,'Q9E3Y7U',1008,'空客A350-900','2025-08-02 14:00:00','2025-08-02 17:30:00','北京','成都',130,0,1,0),(11,'T8R1E5Z',1013,'波音737-800','2025-08-03 15:30:00','2025-08-03 18:30:00','广州','成都',130,0,0,0),(12,'W2C7G9R',1006,'空客A320neo','2025-08-02 07:30:00','2025-08-02 10:00:00','北京','深圳',130,0,1,0),(13,'X7W3N9G',1011,'波音787-9','2025-08-03 09:00:00','2025-08-03 12:20:00','深圳','重庆',130,0,1,0),(14,'t9jm3eW',1002,'空客A320','2026-07-30 02:00:00','2026-07-30 03:00:00','重庆','彭水',130,0,1,0),(15,'SKCLS7G',1007,'波音777-300ER','2026-09-28 11:05:31','2026-09-28 13:06:33','重庆','拉萨',130,0,1,0);
/*!40000 ALTER TABLE `tb_flight` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_flight_price`
--

DROP TABLE IF EXISTS `tb_flight_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_flight_price` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '票价ID',
  `flight_id` char(7) NOT NULL COMMENT '航班批次编号',
  `seat_type` varchar(50) NOT NULL COMMENT '座位类型',
  `price` decimal(10,2) NOT NULL COMMENT '票价',
  `status` tinyint NOT NULL COMMENT '状态 0：停用 1：正常',
  `seat_num` int NOT NULL COMMENT '座位数',
  `sale_num` int NOT NULL COMMENT '已售座位数',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_flightId_seat_type` (`flight_id`,`seat_type`),
  CONSTRAINT `fk_flight_price_flight_id` FOREIGN KEY (`flight_id`) REFERENCES `tb_flight` (`flight_id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC COMMENT='航班票价';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_flight_price`
--

LOCK TABLES `tb_flight_price` WRITE;
/*!40000 ALTER TABLE `tb_flight_price` DISABLE KEYS */;
INSERT INTO `tb_flight_price` VALUES (1,'KA7X92M','ECONOMY',999.00,1,100,0,0),(2,'KA7X92M','BUSINESS',2680.00,1,20,0,0),(3,'KA7X92M','FIRST',4500.00,1,10,0,0),(4,'B3N8PQ5','ECONOMY',1280.00,1,100,0,0),(5,'B3N8PQ5','BUSINESS',3200.00,1,20,0,0),(6,'B3N8PQ5','FIRST',5100.00,1,10,0,0),(7,'HY6R2WT','ECONOMY',1560.00,1,100,0,0),(8,'HY6R2WT','BUSINESS',3980.00,1,20,0,0),(9,'HY6R2WT','FIRST',6200.00,1,10,0,0),(10,'D5V1JXL','ECONOMY',1120.00,1,100,0,0),(11,'D5V1JXL','BUSINESS',2980.00,1,20,0,0),(12,'D5V1JXL','FIRST',4800.00,1,10,0,0),(13,'F8Z4K3N','ECONOMY',1080.00,1,100,0,0),(14,'F8Z4K3N','BUSINESS',2780.00,1,20,0,0),(15,'W2C7G9R','ECONOMY',1350.00,1,10,0,0),(16,'W2C7G9R','BUSINESS',3500.00,1,100,0,0),(17,'W2C7G9R','FIRST',5600.00,1,20,0,0),(18,'M6T5X1A','ECONOMY',1420.00,1,10,0,0),(19,'M6T5X1A','BUSINESS',3680.00,1,100,0,0),(20,'M6T5X1A','FIRST',5900.00,1,20,0,0),(21,'Q9E3Y7U','ECONOMY',1680.00,1,10,0,0),(22,'Q9E3Y7U','BUSINESS',4200.00,1,100,0,0),(23,'Q9E3Y7U','FIRST',6800.00,1,20,0,0),(24,'L4H8S6D','ECONOMY',960.00,1,10,0,0),(25,'L4H8S6D','BUSINESS',2580.00,1,100,0,0),(26,'P1F2V5B','ECONOMY',1220.00,1,20,0,0),(27,'P1F2V5B','BUSINESS',3100.00,1,10,0,0),(28,'P1F2V5B','FIRST',4980.00,1,100,0,0),(29,'X7W3N9G','ECONOMY',1580.00,1,20,0,0),(30,'X7W3N9G','BUSINESS',4050.00,1,10,0,0),(31,'X7W3N9G','FIRST',6500.00,1,100,0,0),(32,'J2K6M4C','ECONOMY',1100.00,1,20,0,0),(33,'J2K6M4C','BUSINESS',2880.00,1,10,0,0),(34,'J2K6M4C','FIRST',4600.00,1,100,0,0),(35,'T8R1E5Z','ECONOMY',1150.00,1,20,0,0),(36,'T8R1E5Z','BUSINESS',2900.00,1,10,0,0),(37,'T8R1E5Z','BUSINESS',2900.00,1,100,0,0);
/*!40000 ALTER TABLE `tb_flight_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_order`
--

DROP TABLE IF EXISTS `tb_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_code` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号',
  `booking_number` bigint NOT NULL COMMENT '预订编号',
  `passenger_id` bigint NOT NULL COMMENT '乘客ID',
  `passenger_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '乘客姓名',
  `discount` double NOT NULL COMMENT '折扣',
  `coupon_code` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '优惠券编号',
  `payment_method` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '支付方式',
  `comment` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `status` int DEFAULT '0' COMMENT '订单状态',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `fk_booking_number` (`booking_number`),
  CONSTRAINT `fk_booking_number` FOREIGN KEY (`booking_number`) REFERENCES `tb_record` (`booking_number`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_order`
--

LOCK TABLES `tb_order` WRITE;
/*!40000 ALTER TABLE `tb_order` DISABLE KEYS */;
INSERT INTO `tb_order` VALUES (1,'ORD20250615001',1,1,'张三',0.85,'CPN001','ALIPAY','经济舱','2025-06-15 10:00:00','2025-06-15 10:05:00',0,0),(2,'ORD20250616002',2,2,'李四',0.9,'CPN002','WECHAT','商务舱','2025-06-16 14:30:00','2025-06-18 08:00:00',0,0),(3,'ORD20250617003',3,3,'王五',1,NULL,'CREDIT','行程取消','2025-06-17 09:15:00','2025-06-18 16:00:00',0,0),(4,'ORD20250618004',4,4,'赵六',0.8,'CPN003','ALIPAY','经济舱早鸟','2025-06-18 07:45:00','2025-06-18 07:50:00',0,0),(5,'ORD20250619005',5,5,'孙七',0.95,'CPN004','WECHAT','头等舱','2025-06-19 11:20:00','2025-06-22 09:30:00',0,0),(6,'ORD20250620006',6,6,'周八',0.88,'CPN005','ALIPAY','经济舱','2025-06-20 16:00:00','2025-06-20 16:08:00',0,0),(7,'ORD20250621007',7,7,'吴九',0.9,'CPN006','CREDIT','头等舱','2025-06-21 08:30:00','2025-06-23 12:00:00',0,0),(8,'ORD20250622008',8,8,'郑十',0.92,'CPN007','WECHAT','经济舱','2025-06-22 13:10:00','2025-06-22 13:15:00',0,0),(9,'ORD20250623009',9,9,'钱十一',0.85,NULL,'ALIPAY','航班延误取消','2025-06-23 10:40:00','2025-06-24 09:00:00',0,0),(10,'ORD20250624010',10,10,'孙十二',0.88,'CPN008','WECHAT','经济舱','2025-06-24 15:55:00','2025-06-24 16:02:00',0,0),(11,'ORD20250624011',23,1,'张三',0.88,NULL,'ALIPAY','经济舱','2026-09-28 11:42:36','2026-10-14 14:37:18',3,0);
/*!40000 ALTER TABLE `tb_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_passenger`
--

DROP TABLE IF EXISTS `tb_passenger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_passenger` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '乘客ID',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '姓名',
  `age` int NOT NULL COMMENT '年龄',
  `sex` varchar(10) COLLATE utf8mb4_general_ci NOT NULL COMMENT '性别',
  `phone` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '手机号',
  `email` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '邮箱',
  `certificate` varchar(18) COLLATE utf8mb4_general_ci NOT NULL COMMENT '证件号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `username` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_certificate` (`certificate`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `username_index` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_passenger`
--

LOCK TABLES `tb_passenger` WRITE;
/*!40000 ALTER TABLE `tb_passenger` DISABLE KEYS */;
INSERT INTO `tb_passenger` VALUES (1,'张三',32,'男','13800138001','zhangwei@example.com','110101199301011234','2025-01-15 09:30:00','2025-06-20 14:22:00','zhangwei','e10adc3949ba59abbe56e',0),(2,'李四',28,'女','13900139002','lina@example.com','310102199605152345','2025-02-03 11:15:00','2025-07-01 08:45:00','lina','e10adc3949ba59abbe56e',0),(3,'王五',45,'男','13700137003','wangqiang@example.com','440103198101203456','2025-01-20 16:40:00','2025-05-18 10:30:00','wangqiang','e10adc3949ba59abbe56e',0),(4,'赵六',24,'女','13600136004','zhaomin@example.com','510104200207284567','2025-03-10 08:20:00','2025-08-05 17:10:00','zhaomin','e10adc3949ba59abbe56e',0),(5,'孙七',36,'男','13500135005','liuyang@example.com','320105198908115678','2025-02-18 13:55:00','2025-06-30 09:00:00','liuyang','e10adc3949ba59abbe56e',0),(6,'周八',30,'女','13400134006','chenjing@example.com','420106199412256789','2025-04-01 10:00:00','2025-07-22 15:33:00','chenjing','e10adc3949ba59abbe56e',0),(7,'吴九',52,'男','13300133007','yanglei@example.com','610107197303077890','2025-01-08 07:45:00','2025-04-12 11:20:00','yanglei','e10adc3949ba59abbe56e',0),(8,'郑十',22,'女','13200132008','huangli@example.com','330108200409189012','2025-05-12 14:30:00','2025-09-01 08:15:00','huangli','e10adc3949ba59abbe56e',0),(9,'钱十一',41,'男','13100131009','zhoujun@example.com','500109198502041234','2025-03-25 09:10:00','2025-08-15 13:40:00','zhoujun','e10adc3949ba59abbe56e',0),(10,'孙十二',27,'女','15000150010','wufang@example.com','210110199708233456','2025-04-20 16:25:00','2025-07-30 10:55:00','wufang','e10adc3949ba59abbe56e',0),(11,'刘锋',38,'男','15100151011','xuming@example.com','120111198706154567','2025-02-28 11:50:00','2025-06-10 14:08:00','xuming','e10adc3949ba59abbe56e',0),(12,'大为',26,'女','15200152012','sunting@example.com','230112199809076789','2025-05-05 08:35:00','2025-09-10 16:22:00','sunting','e10adc3949ba59abbe56e',0),(13,'李白',33,'男','15300153013','machao@example.com','340113199204288901','2025-01-30 15:15:00','2025-05-25 09:48:00','machao','e10adc3949ba59abbe56e',0),(14,'赵晓东',29,'女','15500155014','zhuying@example.com','430114199511129012','2025-06-01 10:40:00','2025-09-18 12:30:00','zhuying','e10adc3949ba59abbe56e',0),(15,'刘波',48,'男','15600156015','hubo@example.com','650115197707051234','2025-03-15 13:05:00','2025-08-02 07:55:00','hubo','e10adc3949ba59abbe56e',0),(16,'林雪',21,'女','15800158016','linxue@example.com','350116200503203456','2025-07-08 09:20:00','2025-09-20 11:10:00','linxue','e10adc3949ba59abbe56e',0),(17,'何刚',55,'男','15900159017','hegang@example.com','220117197009124567','2025-01-02 17:30:00','2025-04-08 15:45:00','hegang','e10adc3949ba59abbe56e',0),(18,'郭琳',31,'女','18600186018','guolin@example.com','410118199407086789','2025-04-15 12:00:00','2025-08-28 10:20:00','guolin','e10adc3949ba59abbe56e',0),(19,'罗勇',39,'男','18700187019','luoyong@example.com','520119198603017890','2025-02-10 08:50:00','2025-07-15 14:35:00','luoyong','e10adc3949ba59abbe56e',0),(20,'谢瑶',23,'女','18800188020','xieyao@example.com','130120200106259012','2025-06-20 15:10:00','2025-09-15 09:40:00','xieyao','e10adc3949ba59abbe56e',0),(21,'刘波一',35,'男','19283318221','liuliu@qq.comm','50023777128172121','2026-10-09 15:37:01','2026-10-09 15:46:12','liubo','d41d8cd98f00b204e9800998ecf8427e',0);
/*!40000 ALTER TABLE `tb_passenger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_plane`
--

DROP TABLE IF EXISTS `tb_plane`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_plane` (
  `id` bigint NOT NULL COMMENT '客机编号',
  `plane_type` varchar(50) NOT NULL COMMENT '客机类型',
  `total_seats` int NOT NULL COMMENT '总座位数',
  `status` tinyint NOT NULL COMMENT '状态 0：维修中 1：使用中',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plane_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客机信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_plane`
--

LOCK TABLES `tb_plane` WRITE;
/*!40000 ALTER TABLE `tb_plane` DISABLE KEYS */;
INSERT INTO `tb_plane` VALUES (1001,'波音737-800',130,1,0),(1002,'空客A320',130,1,0),(1003,'波音787-9',130,1,0),(1004,'空客A330-300',130,1,0),(1005,'波音737-800',130,1,0),(1006,'空客A320neo',130,1,0),(1007,'波音777-300ER',130,1,0),(1008,'空客A350-900',130,1,0),(1009,'波音737 MAX 8',130,1,0),(1010,'空客A320',130,0,0),(1011,'波音787-9',130,1,0),(1012,'空客A330-300',130,0,0),(1013,'波音737-800',130,1,0);
/*!40000 ALTER TABLE `tb_plane` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tb_record`
--

DROP TABLE IF EXISTS `tb_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tb_record` (
  `booking_number` bigint NOT NULL AUTO_INCREMENT COMMENT '预订编号（自增主键）',
  `passenger_id` bigint DEFAULT NULL COMMENT '乘客ID',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `date` datetime DEFAULT NULL COMMENT '预订日期',
  `booking_status` varchar(20) DEFAULT NULL COMMENT '预订状态：CONFIRMED-已确认, COMPLETED-已完成, CANCELLED-已取消',
  `from` varchar(100) DEFAULT NULL COMMENT '出发地',
  `to` varchar(100) DEFAULT NULL COMMENT '目的地',
  `booking_class` varchar(20) DEFAULT NULL COMMENT '舱位等级：ECONOMY-经济舱, PREMIUM_ECONOMY-超级经济舱, BUSINESS-商务舱',
  `flight_id` char(7) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL COMMENT '航班批次编号',
  `certificate_substring` char(6) DEFAULT NULL COMMENT '证件号后6位数字',
  `is_deleted` int NOT NULL COMMENT '是否删除',
  PRIMARY KEY (`booking_number`),
  KEY `fk_record_flight_id` (`flight_id`),
  KEY `fk_record_passenger_id` (`passenger_id`),
  CONSTRAINT `fk_record_flight_id` FOREIGN KEY (`flight_id`) REFERENCES `tb_flight` (`flight_id`),
  CONSTRAINT `fk_record_passenger_id` FOREIGN KEY (`passenger_id`) REFERENCES `tb_passenger` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='航班预订记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tb_record`
--

LOCK TABLES `tb_record` WRITE;
/*!40000 ALTER TABLE `tb_record` DISABLE KEYS */;
INSERT INTO `tb_record` VALUES (1,1,'张三','2025-01-15 08:00:00','COMPLETED','上海','成都','ECONOMY','B3N8PQ5','011234',0),(2,2,'李四','2025-01-20 00:00:00','COMPLETED','广州','深圳','BUSINESS','D5V1JXL','154567',0),(3,3,'王五','2025-02-01 00:00:00','CANCELLED','成都','重庆','ECONOMY','F8Z4K3N','259012',0),(4,4,'赵六','2025-02-10 00:00:00','CANCELLED','杭州','南京','BUSINESS','HY6R2WT','233456',0),(5,5,'孙七','2025-02-15 00:00:00','CONFIRMED','西安','兰州','ECONOMY','J2K6M4C','124567',0),(6,6,'周八','2025-03-01 00:00:00','COMPLETED','武汉','长沙','BUSINESS','KA7X92M','076789',0),(7,7,'吴九','2025-03-05 00:00:00','CONFIRMED','天津','青岛','ECONOMY','L4H8S6D','152345',0),(8,8,'郑十','2025-03-10 00:00:00','CONFIRMED','大连','沈阳','ECONOMY','M6T5X1A','115678',0),(9,9,'钱十一','2025-03-15 00:00:00','CANCELLED','厦门','福州','ECONOMY','P1F2V5B','189012',0),(10,10,'孙十二','2025-03-20 00:00:00','CANCELLED','昆明','贵阳','BUSINESS','Q9E3Y7U','288901',0),(14,14,'刘锋','2026-08-22 00:00:00','CANCELLED','重庆','上海','ECONOMY','T8R1E5Z','203456',0),(15,15,'大为','2026-08-22 00:00:00','CANCELLED','重庆','上海','ECONOMY','W2C7G9R','086789',0),(16,16,'李白','2026-08-22 00:00:00','CANCELLED','重庆','上海','ECONOMY','X7W3N9G','256789',0),(18,18,'赵晓东','2025-08-01 09:15:00','CONFIRMED','广州','北京','FIRST','B3N8PQ5','129012',0),(19,19,'刘波','2025-08-01 09:15:00','CONFIRMED','广州','北京','FIRST','B3N8PQ5','203456',0),(23,1,'张三','2026-09-28 11:40:53','CONFIRMED','重庆','拉萨','ECONOMY','T8R1E5Z','011234',0);
/*!40000 ALTER TABLE `tb_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-15 10:32:24
