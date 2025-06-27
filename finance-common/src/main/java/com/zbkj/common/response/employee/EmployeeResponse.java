package com.zbkj.common.response.employee;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工响应类
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "EmployeeResponse对象", description = "员工响应类")
public class EmployeeResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID")
    private Integer id;

    @ApiModelProperty(value = "关联用户ID")
    private Integer userId;

    @ApiModelProperty(value = "员工工号")
    private String employeeNo;

    @ApiModelProperty(value = "员工姓名")
    private String name;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @ApiModelProperty(value = "性别：0未知，1男，2女")
    private Integer gender;

    @ApiModelProperty(value = "性别描述")
    private String genderText;

    @ApiModelProperty(value = "员工状态：0离职，1在职，2试用期")
    private Integer status;

    @ApiModelProperty(value = "状态描述")
    private String statusText;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    // 用户登录相关信息
    @ApiModelProperty(value = "登录用户名")
    private String account;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户状态：0禁用，1正常")
    private Boolean userStatus;

    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    // 养殖机构相关信息
    @ApiModelProperty(value = "养殖类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;

    @ApiModelProperty(value = "养殖类型描述")
    private String farmTypeText;

    @ApiModelProperty(value = "机构ID")
    private Integer farmId;

    @ApiModelProperty(value = "养殖场code")
    private String farmCode;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "负责人姓名")
    private String responsiblePerson;

    @ApiModelProperty(value = "授信系数")
    private java.math.BigDecimal creditCoefficient;

    @ApiModelProperty(value = "授信额度")
    private java.math.BigDecimal creditLimit;

    @ApiModelProperty(value = "已消费金额")
    private java.math.BigDecimal consumedAmount;

    @ApiModelProperty(value = "评估金额")
    private java.math.BigDecimal assessmentAmount;

    @ApiModelProperty(value = "总存栏量")
    private Integer totalStockQuantity;

    @ApiModelProperty(value = "养殖品种名称（拼接）")
    private String breedingVarietyNames;

    @ApiModelProperty(value = "养殖品种类型（拼接）")
    private String breedingVarietyTypes;

    // 补充缺失的授信相关字段
    @ApiModelProperty(value = "剩余授信额度")
    private java.math.BigDecimal remainingCredit;

    @ApiModelProperty(value = "已使用授信额度")
    private java.math.BigDecimal usedCredit;

    @ApiModelProperty(value = "待还款额度")
    private java.math.BigDecimal pendingRepayment;

    @ApiModelProperty(value = "授信状态：0-未授信，1-已授信，2-授信冻结，3-授信失效")
    private Integer creditStatus;

    @ApiModelProperty(value = "授信开始时间")
    private Date creditStartTime;

    @ApiModelProperty(value = "授信结束时间")
    private Date creditEndTime;

    @ApiModelProperty(value = "上级授信用户ID")
    private Integer parentCreditUserId;

    @ApiModelProperty(value = "授信层级：1-平台，2-合作社，3-员工")
    private Integer creditLevel;

    @ApiModelProperty(value = "最近授信审核时间")
    private Date lastCreditAuditTime;

    @ApiModelProperty(value = "最近授信审核人ID")
    private Integer lastCreditAuditUserId;

    @ApiModelProperty(value = "授信审核备注")
    private String creditAuditRemark;
} 