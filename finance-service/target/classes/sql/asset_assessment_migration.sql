-- 资产评估相关表字段迁移脚本
-- 如果表已存在但缺少字段，请执行以下SQL

-- 为eb_asset_assessment_breeding表添加farm_code和farm_name字段（如果不存在）
ALTER TABLE `eb_asset_assessment_breeding` 
ADD COLUMN `farm_code` VARCHAR(100) DEFAULT NULL COMMENT '养殖场code' AFTER `assessment_id`,
ADD COLUMN `farm_name` VARCHAR(255) DEFAULT NULL COMMENT '养殖场名称' AFTER `farm_code`;

-- 添加farm_code索引（如果不存在）
ALTER TABLE `eb_asset_assessment_breeding` ADD KEY `idx_farm_code` (`farm_code`); 