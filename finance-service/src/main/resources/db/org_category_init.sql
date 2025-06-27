-- 组织分类表初始化SQL

-- 创建组织分类表（如果不存在）
CREATE TABLE IF NOT EXISTS `eb_org_category` (
  `id` varchar(32) NOT NULL COMMENT 'ID',
  `code` varchar(20) NOT NULL COMMENT '分类代码',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `parent_code` varchar(20) DEFAULT NULL COMMENT '父级分类代码',
  `level` tinyint(1) NOT NULL DEFAULT '1' COMMENT '层级（1:一级 2:二级 3:三级）',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（0:禁用 1:启用）',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_code` (`parent_code`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_delete_flag` (`delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织分类表';

-- 清空现有数据（如果需要重新初始化）
DELETE FROM `eb_org_category` WHERE `code` LIKE '0103%';

-- 插入组织分类初始化数据
INSERT INTO `eb_org_category` (`id`, `code`, `name`, `parent_code`, `level`, `sort_order`, `status`, `remark`, `create_time`, `update_time`, `delete_flag`) VALUES
('org_cat_01030101', '01030101', '养殖场', '010301', 3, 1, 1, '从事畜禽养殖的场所', NOW(), NOW(), 0),
('org_cat_01030102', '01030102', '动物交易市场', '010301', 3, 2, 1, '进行动物买卖交易的市场', NOW(), NOW(), 0),
('org_cat_01030103', '01030103', '屠宰场', '010301', 3, 3, 1, '对畜禽进行屠宰加工的场所', NOW(), NOW(), 0),
('org_cat_01030104', '01030104', '动物产品交易市场', '010301', 3, 4, 1, '进行动物产品买卖交易的市场', NOW(), NOW(), 0),
('org_cat_01030105', '01030105', '动物隔离场', '010301', 3, 5, 1, '对动物进行隔离检疫的场所', NOW(), NOW(), 0),
('org_cat_01030106', '01030106', '无害化处理场', '010301', 3, 6, 1, '对病死动物进行无害化处理的场所', NOW(), NOW(), 0),
('org_cat_01030107', '01030107', '兽药生产企业', '010301', 3, 7, 1, '生产兽药的企业', NOW(), NOW(), 0),
('org_cat_01030108', '01030108', '兽药经营企业', '010301', 3, 8, 1, '经营销售兽药的企业', NOW(), NOW(), 0),
('org_cat_01030109', '01030109', '饲料生产企业', '010301', 3, 9, 1, '生产动物饲料的企业', NOW(), NOW(), 0),
('org_cat_01030110', '01030110', '生物安全实验室', '010301', 3, 10, 1, '进行生物安全相关研究的实验室', NOW(), NOW(), 0),
('org_cat_01030111', '01030111', '动物诊疗机构', '010301', 3, 11, 1, '为动物提供诊疗服务的机构', NOW(), NOW(), 0),
('org_cat_01030112', '01030112', '动物及动物产品经纪人', '010301', 3, 12, 1, '从事动物及动物产品中介服务的个人或机构', NOW(), NOW(), 0);

-- 查询验证数据
SELECT * FROM `eb_org_category` WHERE `code` LIKE '0103%' ORDER BY `sort_order`; 