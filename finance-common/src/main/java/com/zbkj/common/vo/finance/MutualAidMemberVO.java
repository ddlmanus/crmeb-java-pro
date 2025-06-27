package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "社员互助金成员信息", description = "社员互助金成员信息")
public class MutualAidMemberVO {

    @ApiModelProperty(value = "用户ID")
    private Integer userId;

    @ApiModelProperty(value = "社员名称")
    private String name;

    @ApiModelProperty(value = "社员账号")
    private String account;

    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "养殖户类型 0:游客，1:养殖户/养殖企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;
    @ApiModelProperty(value = "用户类型：0:游客，1:管理员,2,员工")
    private Integer userType;
    @ApiModelProperty(value ="养殖场机构id")
    private Integer farmId;
    @ApiModelProperty(value ="养殖场code")
    private String farmCode;
    @ApiModelProperty(value ="养殖场名称")
    private String farmName;
    @ApiModelProperty(value ="所属机构id")
    private String organizationId;
    @ApiModelProperty(value ="所属机构名称")
    private String organizationName;
    @ApiModelProperty(value ="所属机构code")
    private String organizationCode;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区县ID")
    private Integer districtId;
    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区")
    private String district;

    @ApiModelProperty(value = "总额度")
    private BigDecimal totalPrice;

    @ApiModelProperty(value = "剩余额度")
    private BigDecimal balance;

    @ApiModelProperty(value = "冻结金额")
    private BigDecimal freezePrice;

    @ApiModelProperty(value = "评估金额")
    private BigDecimal estimatedPrice;

    @ApiModelProperty(value = "预计收益")
    private BigDecimal estimatedYield;

    @ApiModelProperty(value = "审核状态")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核原因")
    private String auditReason;

    @ApiModelProperty(value = "是否可用")
    private Integer available;

    @ApiModelProperty(value = "步骤")
    private Integer step;

    @ApiModelProperty(value = "总支出")
    private BigDecimal totalExpense;

    @ApiModelProperty(value = "总充值")
    private BigDecimal totalRecharge;

    @ApiModelProperty(value = "还款期限（天）")
    private Integer termDays;

    @ApiModelProperty(value = "贷款利率")
    private BigDecimal premium;

    @ApiModelProperty(value = "卡号")
    private String cardNo;

    @ApiModelProperty(value = "预测金额")
    private BigDecimal predictedAmount;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间格式化")
    private String createTimeClone;
} 