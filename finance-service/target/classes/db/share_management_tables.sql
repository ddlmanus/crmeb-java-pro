-- 股份分红管理系统数据库表结构

-- 1. 股份管理表
CREATE TABLE `eb_share_management` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `member_name` varchar(100) NOT NULL COMMENT '社员名称',
  `holding_ratio` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '持有比例',
  `share_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '股份数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_name` (`member_name`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股份管理表';

-- 2. 分红管理表
CREATE TABLE `eb_dividend_management` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `dividend_title` varchar(200) NOT NULL COMMENT '分红标题',
  `total_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '分红总金额',
  `dividend_date` datetime NOT NULL COMMENT '分红日期',
  `create_user_id` int(11) NOT NULL COMMENT '创建用户ID',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态（0:草稿 1:已发布 2:已完成）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_create_user_id` (`create_user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_dividend_date` (`dividend_date`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分红管理表';

-- 3. 分红明细表
CREATE TABLE `eb_dividend_detail` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `dividend_id` varchar(32) NOT NULL COMMENT '分红管理ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `member_name` varchar(100) NOT NULL COMMENT '社员名称',
  `holding_ratio` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '持有比例',
  `dividend_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '分红金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_dividend_id` (`dividend_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_name` (`member_name`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分红明细表';

-- 4. 股份变更记录表
CREATE TABLE `eb_share_change_record` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `member_name` varchar(100) NOT NULL COMMENT '社员名称',
  `before_ratio` decimal(10,4) DEFAULT '0.0000' COMMENT '变更前持有比例',
  `after_ratio` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '变更后持有比例',
  `before_quantity` int(11) DEFAULT '0' COMMENT '变更前股份数量',
  `after_quantity` int(11) NOT NULL DEFAULT '0' COMMENT '变更后股份数量',
  `change_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '变更类型（1:增加 2:减少 3:转让）',
  `change_reason` varchar(500) DEFAULT '' COMMENT '变更原因',
  `change_date` datetime NOT NULL COMMENT '变更日期',
  `operator_id` int(11) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) NOT NULL COMMENT '操作人姓名',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_member_name` (`member_name`),
  KEY `idx_change_date` (`change_date`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='股份变更记录表';

-- 插入测试数据
INSERT INTO `eb_share_management` (`id`, `user_id`, `member_name`, `holding_ratio`, `share_quantity`, `create_time`, `update_time`, `delete_flag`) VALUES
('1001', 1001, '赵建国', 10.0000, 1000, NOW(), NOW(), 0),
('1002', 1002, '陈明远', 10.0000, 600, NOW(), NOW(), 0),
('1003', 1003, '林美玲', 10.0000, 400, NOW(), NOW(), 0),
('1004', 1004, '王雨华', 5.0000, 100, NOW(), NOW(), 0),
('1005', 1005, '张伟明', 5.0000, 100, NOW(), NOW(), 0); 