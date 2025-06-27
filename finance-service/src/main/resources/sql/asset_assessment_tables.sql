-- 资产评估表
CREATE TABLE `eb_asset_assessment` (
    `id` VARCHAR(64) NOT NULL COMMENT '评估ID',
    `user_id` INT(11) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(255) DEFAULT NULL COMMENT '用户名称',
    `farm_institution_id` INT(11) DEFAULT NULL COMMENT '养殖场机构ID',
    `farm_name` VARCHAR(255) DEFAULT NULL COMMENT '养殖场名称',
    `manager_phone` VARCHAR(20) DEFAULT NULL COMMENT '养殖场负责人电话',
    `manager_name` VARCHAR(100) DEFAULT NULL COMMENT '养殖场负责人姓名',
    `id_number` VARCHAR(100) DEFAULT NULL COMMENT '身份证号/统一社会信用代码',
    `land_area` DECIMAL(18,2) DEFAULT NULL COMMENT '土地面积（平方米）',
    `building_area` DECIMAL(18,2) DEFAULT NULL COMMENT '建筑面积（平方米）',
    `equipment_value` DECIMAL(18,2) DEFAULT NULL COMMENT '设备价值（元）',
    `inventory_value` DECIMAL(18,2) DEFAULT NULL COMMENT '库存价值（元）',
    `other_assets_value` DECIMAL(18,2) DEFAULT NULL COMMENT '其他资产价值（元）',
    `total_assessment_value` DECIMAL(18,2) DEFAULT NULL COMMENT '评估总价值（元）',
    `remark` TEXT DEFAULT NULL COMMENT '备注',
    `attachment_paths` TEXT DEFAULT NULL COMMENT '附件文件路径，多个路径用逗号分隔',
    `assessment_status` INT(11) DEFAULT 0 COMMENT '评估状态：0-草稿，1-已提交',
    `is_used` INT(11) DEFAULT 0 COMMENT '是否已用于申请：0-未使用，1-已使用',
    `document_path` VARCHAR(500) DEFAULT NULL COMMENT 'Word文档路径',
    `oss_document_url` VARCHAR(500) DEFAULT NULL COMMENT '阿里云OSS文档URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `delete_flag` INT(11) DEFAULT 0 COMMENT '删除标志：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_farm_institution_id` (`farm_institution_id`),
    KEY `idx_assessment_status` (`assessment_status`),
    KEY `idx_is_used` (`is_used`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产评估表';

-- 资产评估养殖品种关联表
CREATE TABLE `eb_asset_assessment_breeding` (
    `id` VARCHAR(64) NOT NULL COMMENT 'ID',
    `assessment_id` VARCHAR(64) NOT NULL COMMENT '资产评估ID',
    `farm_code` VARCHAR(100) DEFAULT NULL COMMENT '养殖场code',
    `farm_name` VARCHAR(255) DEFAULT NULL COMMENT '养殖场名称',
    `breeding_product_id` VARCHAR(64) NOT NULL COMMENT '养殖品种ID',
    `breeding_product_name` VARCHAR(255) DEFAULT NULL COMMENT '养殖品种名称',
    `breeding_product_type` VARCHAR(100) DEFAULT NULL COMMENT '养殖品种类型',
    `animal_number` INT(11) DEFAULT NULL COMMENT '存栏数量',
    `breeding_product_scale` VARCHAR(100) DEFAULT NULL COMMENT '养殖规模',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_assessment_id` (`assessment_id`),
    KEY `idx_breeding_product_id` (`breeding_product_id`),
    KEY `idx_farm_code` (`farm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产评估养殖品种关联表';

-- 为现有的授信申请表添加新字段
ALTER TABLE `eb_credit_application` ADD COLUMN `assessment_id` VARCHAR(64) DEFAULT NULL COMMENT '资产评估ID' AFTER `apply_type`;
ALTER TABLE `eb_credit_application` ADD COLUMN `apply_purpose` VARCHAR(500) DEFAULT NULL COMMENT '申请用途' AFTER `apply_amount`;

-- 添加索引
ALTER TABLE `eb_credit_application` ADD KEY `idx_assessment_id` (`assessment_id`); 