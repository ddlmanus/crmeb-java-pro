package com.zbkj.common.utils;

import cn.hutool.core.util.StrUtil;
import com.zbkj.common.constants.EmployeeConstants;
import com.zbkj.common.exception.CrmebException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * 员工数据验证工具类
 *
 * @author zbkj
 * @since 2024-12-19
 */
public class EmployeeValidationUtil {

    /** 手机号正则表达式 */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /** 邮箱正则表达式 */
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");

    /** 身份证号正则表达式 */
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$");

    /** 员工工号正则表达式 */
    private static final Pattern EMPLOYEE_NO_PATTERN = Pattern.compile("^[A-Z0-9]{3,20}$");

    /**
     * 验证员工工号格式
     */
    public static void validateEmployeeNo(String employeeNo) {
        if (StrUtil.isBlank(employeeNo)) {
            throw new CrmebException("员工工号不能为空");
        }
        if (!EMPLOYEE_NO_PATTERN.matcher(employeeNo).matches()) {
            throw new CrmebException("员工工号格式不正确，应为3-20位大写字母和数字组合");
        }
    }

    /**
     * 验证手机号格式
     */
    public static void validatePhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            throw new CrmebException("手机号不能为空");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new CrmebException("手机号格式不正确");
        }
    }

    /**
     * 验证邮箱格式
     */
    public static void validateEmail(String email) {
        if (StrUtil.isNotBlank(email) && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CrmebException("邮箱格式不正确");
        }
    }

    /**
     * 验证身份证号格式
     */
    public static void validateIdCard(String idCard) {
        if (StrUtil.isNotBlank(idCard) && !ID_CARD_PATTERN.matcher(idCard).matches()) {
            throw new CrmebException("身份证号格式不正确");
        }
    }

    /**
     * 验证员工姓名
     */
    public static void validateName(String name) {
        if (StrUtil.isBlank(name)) {
            throw new CrmebException("员工姓名不能为空");
        }
        if (name.length() > 50) {
            throw new CrmebException("员工姓名长度不能超过50个字符");
        }
    }

    /**
     * 验证性别
     */
    public static void validateGender(Integer gender) {
        if (gender != null && (gender < 0 || gender > 2)) {
            throw new CrmebException("性别参数不正确");
        }
    }

    /**
     * 验证员工状态
     */
    public static void validateStatus(Integer status) {
        if (status == null) {
            throw new CrmebException("员工状态不能为空");
        }
        if (status < 0 || status > 2) {
            throw new CrmebException("员工状态参数不正确");
        }
    }

    /**
     * 验证养殖类型
     */
    public static void validateFarmType(Integer farmType) {
        if (farmType != null && (farmType < 0 || farmType > 5)) {
            throw new CrmebException("养殖类型参数不正确");
        }
    }

    /**
     * 验证授信额度
     */
    public static void validateCreditLimit(BigDecimal creditLimit) {
        if (creditLimit != null) {
            if (creditLimit.compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("授信额度不能为负数");
            }
            if (creditLimit.compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new CrmebException("授信额度不能超过99999999.99");
            }
        }
    }

    /**
     * 验证授信系数
     */
    public static void validateCreditCoefficient(BigDecimal creditCoefficient) {
        if (creditCoefficient != null) {
            if (creditCoefficient.compareTo(BigDecimal.ZERO) < 0) {
                throw new CrmebException("授信系数不能为负数");
            }
            if (creditCoefficient.compareTo(BigDecimal.ONE) > 0) {
                throw new CrmebException("授信系数不能超过1");
            }
        }
    }

    /**
     * 验证存栏量
     */
    public static void validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity != null && stockQuantity < 0) {
            throw new CrmebException("存栏量不能为负数");
        }
    }

    /**
     * 验证用户账号
     */
    public static void validateAccount(String account) {
        if (StrUtil.isBlank(account)) {
            throw new CrmebException("用户账号不能为空");
        }
        if (account.length() < 3 || account.length() > 20) {
            throw new CrmebException("用户账号长度应为3-20个字符");
        }
        if (!Pattern.matches("^[a-zA-Z0-9_]{3,20}$", account)) {
            throw new CrmebException("用户账号只能包含字母、数字和下划线");
        }
    }

    /**
     * 验证密码
     */
    public static void validatePassword(String password) {
        if (StrUtil.isBlank(password)) {
            throw new CrmebException("密码不能为空");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new CrmebException("密码长度应为6-20个字符");
        }
    }
} 