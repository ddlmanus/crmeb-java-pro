package com.zbkj.common.model.employee;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 员工表
 * </p>
 *
 * @author zbkj
 * @since 2024-12-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_employee")
@ApiModel(value = "Employee对象", description = "员工表")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工ID")
    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(value = "员工状态：0离职，1在职，2试用期")
    private Integer status;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    private Integer createBy;

    @ApiModelProperty(value = "更新人")
    private Integer updateBy;

    @ApiModelProperty(value = "是否删除：0否，1是")
    private Boolean isDeleted;

    // 养殖机构相关信息
    @ApiModelProperty(value = "养殖类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;

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

    @ApiModelProperty(value = "所属机构id")
    private String organizationId;
} 