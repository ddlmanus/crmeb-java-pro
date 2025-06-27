package com.zbkj.common.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户个人资料响应对象
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "UserInfoResponse对象", description = "用户个人资料响应对象")
public class UserInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "用户类型：0:游客，1:管理员,2,员工")
    private Integer userType;
    @ApiModelProperty(value = "养殖户类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;
    
    @ApiModelProperty(value = "养殖场机构id")
    private Integer farmId;
    
    @ApiModelProperty(value = "养殖场code")
    private String farmCode;
    
    @ApiModelProperty(value = "养殖场名称")
    private String farmName;
    
    @ApiModelProperty(value = "所属机构id")
    private String organizationId;
    
    @ApiModelProperty(value = "所属机构名称")
    private String organizationName;
    
    @ApiModelProperty(value = "所属机构code")
    private String organizationCode;
    
    @ApiModelProperty(value = "真实姓名")
    private String realName;
    
    @ApiModelProperty(value = "身份证号码")
    private String identityCardNo;

    // 授信相关字段
    @ApiModelProperty(value = "申请金额")
    private BigDecimal applyAmount;

    @ApiModelProperty(value = "授信额度")
    private BigDecimal creditLimit;

    @ApiModelProperty(value = "评估额度")
    private BigDecimal assessmentAmount;

    @ApiModelProperty(value = "剩余授信额度")
    private BigDecimal remainingCredit;

    @ApiModelProperty(value = "已使用授信额度")
    private BigDecimal usedCredit;

    @ApiModelProperty(value = "待还款额度")
    private BigDecimal pendingRepayment;

    @ApiModelProperty(value = "授信状态：0-未授信，1-已授信，2-授信冻结，3-授信失效")
    private Integer creditStatus;

    @ApiModelProperty(value = "授信开始时间")
    private Date creditStartTime;

    @ApiModelProperty(value = "授信结束时间")
    private Date creditEndTime;

    @ApiModelProperty(value = "授信系数")
    private BigDecimal creditCoefficient;

    @ApiModelProperty(value = "授信利率")
    private BigDecimal creditRatio;

    @ApiModelProperty(value = "还款期限（天）")
    private Integer repaymentDays;

    @ApiModelProperty(value = "卡号")
    private String cardNumber;

    @ApiModelProperty(value = "总存栏量")
    private Integer totalStockQuantity;

    @ApiModelProperty(value = "养殖品种名称（拼接）")
    private String breedingVarietyNames;

    @ApiModelProperty(value = "养殖品种类型（拼接）")
    private String breedingVarietyTypes;

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
