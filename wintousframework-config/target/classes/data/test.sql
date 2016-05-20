/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50610
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50610
File Encoding         : 65001

Date: 2016-05-20 13:29:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_id_table`
-- ----------------------------
DROP TABLE IF EXISTS `t_id_table`;
CREATE TABLE `t_id_table` (
  `tid` int(11) NOT NULL AUTO_INCREMENT,
  `tablename` varchar(255) DEFAULT NULL,
  `pkid` int(11) DEFAULT NULL,
  PRIMARY KEY (`tid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_id_table
-- ----------------------------
INSERT INTO `t_id_table` VALUES ('1', 'yun_app', '6');
INSERT INTO `t_id_table` VALUES ('2', 't_test_student', '11');

-- ----------------------------
-- Table structure for `t_test_student`
-- ----------------------------
DROP TABLE IF EXISTS `t_test_student`;
CREATE TABLE `t_test_student` (
  `sid` int(11) NOT NULL DEFAULT '0',
  `name` varchar(255) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`sid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_test_student
-- ----------------------------
INSERT INTO `t_test_student` VALUES ('7', 'zhanghs', '20');
INSERT INTO `t_test_student` VALUES ('8', 'zhanghs', '20');
INSERT INTO `t_test_student` VALUES ('9', 'zhanghs', '20');
INSERT INTO `t_test_student` VALUES ('10', 'zhanghs', '20');
