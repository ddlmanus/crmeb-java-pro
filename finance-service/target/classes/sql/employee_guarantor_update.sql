-- 为担保人表添加员工相关字段
ALTER TABLE `eb_guarantee_info` ADD COLUMN `employee_id` INT(11) DEFAULT NULL COMMENT '员工ID（用于员工担保人管理）' AFTER `application_id`;
ALTER TABLE `eb_guarantee_info` ADD COLUMN `user_id` INT(11) DEFAULT NULL COMMENT '用户ID（关联的用户）' AFTER `employee_id`;
ALTER TABLE `eb_guarantee_info` ADD COLUMN `guarantor_type` INT(11) DEFAULT 1 COMMENT '担保人类型：1-授信申请担保人，2-员工担保人' AFTER `user_id`;

-- 添加时间戳字段
ALTER TABLE `eb_guarantee_info` ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `remark`;
ALTER TABLE `eb_guarantee_info` ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`;

-- 添加索引
ALTER TABLE `eb_guarantee_info` ADD KEY `idx_employee_id` (`employee_id`);
ALTER TABLE `eb_guarantee_info` ADD KEY `idx_user_id` (`user_id`);
ALTER TABLE `eb_guarantee_info` ADD KEY `idx_guarantor_type` (`guarantor_type`);
ALTER TABLE `eb_guarantee_info` ADD KEY `idx_create_time` (`create_time`);

-- 为现有数据设置默认担保人类型
UPDATE `eb_guarantee_info` SET `guarantor_type` = 1 WHERE `guarantor_type` IS NULL AND `application_id` IS NOT NULL; 