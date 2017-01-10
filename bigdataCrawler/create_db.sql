/*
Navicat MySQL Data Transfer

Source Server         : 10.10.64.173
Source Server Version : 50527
Source Host           : 10.10.64.173:3306
Source Database       : xgovcn_news

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2012-10-09 11:34:07
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `linkdb`
-- ----------------------------
DROP TABLE IF EXISTS `crawler_linkdb`;
CREATE TABLE `crawler_linkdb` (
  `id` char(32) NOT NULL DEFAULT '' COMMENT 'url的MD5',
  `url` varchar(1024) NOT NULL DEFAULT '',
  `normalizeUrl` varchar(1024) NOT NULL DEFAULT '' COMMENT '归一化url',
  `fetchtime` bigint(20) NOT NULL DEFAULT '0' COMMENT '采集时间',
  `state` mediumint(9) NOT NULL DEFAULT '0' COMMENT 'link状态，0=正常，-1=黑名单',
  `depth` mediumint(9) NOT NULL DEFAULT '0',
  `extend` text COMMENT '扩展字段，以{key1=value1,key2=value2}形式存储',
  PRIMARY KEY (`id`),
  KEY `dep_state` (`depth`,`state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of linkdb
-- ----------------------------

-- ----------------------------
-- Table structure for `taskdb`
-- ----------------------------
DROP TABLE IF EXISTS `crawler_taskdb`;
CREATE TABLE `crawler_taskdb` (
  `id` varchar(255) NOT NULL DEFAULT '' COMMENT 'task id',
  `xml` longtext NOT NULL COMMENT 'task定义，xml格式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of taskdb
-- ----------------------------

-- ----------------------------
-- Table structure for `taskseed_sample`
-- ----------------------------
DROP TABLE IF EXISTS `crawler_taskseed_sample`;
CREATE TABLE `crawler_taskseed_sample` (
  `id`  char(32) NOT NULL DEFAULT '' COMMENT 'task seed id，url+taskid的MD5',
  `urlid` varchar(1024) NOT NULL COMMENT 'url的MD5',
  `taskid` varchar(255) NOT NULL DEFAULT '' COMMENT 'seed所属task的id',
  `url` varchar(1024) NOT NULL DEFAULT '' COMMENT 'seed url',
  `normalizeUrl` varchar(1024) NOT NULL DEFAULT '' COMMENT '归一化url',
  `level` mediumint(9) NOT NULL DEFAULT '0' COMMENT 'url所属层级',
  `state` mediumint(9) NOT NULL DEFAULT '0' COMMENT 'seed状态，0=没采集，1=待更新，2=已完成，-n=失败n次',
  `depth` mediumint(9) NOT NULL DEFAULT '0' COMMENT '页面深度，相对于初始种子页而言',
  `extend` text COMMENT '扩展字段，以{key1=value1,key2=value2}形式存储',
  PRIMARY KEY (`id`),
  KEY `dep_state_lv` (`depth`,`state`,`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of taskseed_sample
-- ----------------------------

-- ----------------------------
-- Table structure for `webdb`
-- ----------------------------
DROP TABLE IF EXISTS `crawler_webdb`;
CREATE TABLE `crawler_webdb` (
  `id` char(32) NOT NULL DEFAULT '' COMMENT 'url的MD5',
  `url` varchar(1024) NOT NULL DEFAULT '',
  `normalizeUrl` varchar(1024) NOT NULL DEFAULT '' COMMENT '归一化url',
  `html` longblob NOT NULL,
  `fetchtime` bigint(20) NOT NULL DEFAULT '0' COMMENT '采集时间',
  `parsestate` tinyint(4) NOT NULL DEFAULT '0' COMMENT '解析状态',
  `extend` text COMMENT '扩展字段，以{key1=value1,key2=value2}形式存储',
  PRIMARY KEY (`id`),
  KEY `parse_time` (`parsestate`,`fetchtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of webdb
-- ----------------------------

-- ----------------------------
-- Table structure for `webdb_increment`
-- ----------------------------
DROP TABLE IF EXISTS `crawler_webdb_increment`;
CREATE TABLE `crawler_webdb_increment` (
  `id` char(32) NOT NULL DEFAULT '' COMMENT 'url的MD5',
  `url` varchar(1024) NOT NULL DEFAULT '',
  `normalizeUrl` varchar(1024) NOT NULL DEFAULT '' COMMENT '归一化url',
  `html` longblob NOT NULL,
  `fetchtime` bigint(20) NOT NULL DEFAULT '0' COMMENT '采集时间',
  `parsestate` tinyint(4) NOT NULL DEFAULT '0' COMMENT '解析状态',
  `extend` text COMMENT '扩展字段，以{key1=value1,key2=value2}形式存储',
  PRIMARY KEY (`id`),
  KEY `parse_time` (`parsestate`,`fetchtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of webdb_increment
-- ----------------------------
