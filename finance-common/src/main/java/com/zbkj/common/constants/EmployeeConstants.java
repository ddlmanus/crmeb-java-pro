package com.zbkj.common.constants;

/**
 * 员工常量类
 *
 * @author zbkj
 * @since 2024-12-19
 */
public class EmployeeConstants {

    /** 员工状态 */
    public static final class Status {
        /** 离职 */
        public static final Integer RESIGNED = 0;
        /** 在职 */
        public static final Integer ACTIVE = 1;
        /** 试用期 */
        public static final Integer PROBATION = 2;
    }

    /** 性别 */
    public static final class Gender {
        /** 未知 */
        public static final Integer UNKNOWN = 0;
        /** 男 */
        public static final Integer MALE = 1;
        /** 女 */
        public static final Integer FEMALE = 2;
    }

    /** 养殖类型 */
    public static final class FarmType {
        /** 游客 */
        public static final Integer VISITOR = 0;
        /** 种植户/种植企业 */
        public static final Integer PLANTER = 1;
        /** 合作社 */
        public static final Integer COOPERATIVE = 2;
        /** 经纪人 */
        public static final Integer BROKER = 3;
        /** 供应商 */
        public static final Integer SUPPLIER = 4;
        /** 平台 */
        public static final Integer PLATFORM = 5;
    }

    /** 用户类型 */
    public static final class UserType {
        /** 游客 */
        public static final Integer VISITOR = 0;
        /** 管理员 */
        public static final Integer ADMIN = 1;
        /** 员工 */
        public static final Integer EMPLOYEE = 2;
    }

    /** 状态描述映射 */
    public static String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "离职";
            case 1: return "在职";
            case 2: return "试用期";
            default: return "未知";
        }
    }

    /** 性别描述映射 */
    public static String getGenderText(Integer gender) {
        if (gender == null) return "未知";
        switch (gender) {
            case 0: return "未知";
            case 1: return "男";
            case 2: return "女";
            default: return "未知";
        }
    }

    /** 养殖类型描述映射 */
    public static String getFarmTypeText(Integer farmType) {
        if (farmType == null) return "未知";
        switch (farmType) {
            case 0: return "游客";
            case 1: return "种植户/种植企业";
            case 2: return "合作社";
            case 3: return "经纪人";
            case 4: return "供应商";
            case 5: return "平台";
            default: return "未知";
        }
    }
} 