package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

/**
 * 个人中心响应对象
 *  +----------------------------------------------------------------------
 *  | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 *  +----------------------------------------------------------------------
 *  | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 *  +----------------------------------------------------------------------
 *  | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 *  +----------------------------------------------------------------------
 *  | Author: CRMEB Team <admin@crmeb.com>
 *  +----------------------------------------------------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserCenterResponse对象", description="个人中心响应对象")
public class UserCenterResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private Integer id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "用户剩余积分")
    private Integer integral;

    @ApiModelProperty(value = "用户剩余经验")
    private Integer experience;

    @ApiModelProperty(value = "等级")
    private Integer level;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "用户优惠券数量")
    private Integer couponCount;

    @ApiModelProperty(value = "是否会员")
    private Boolean isVip;

    @ApiModelProperty(value = "会员图标")
    private String vipIcon;

    @ApiModelProperty(value = "会员名称")
    private String vipName;

    @ApiModelProperty(value = "用户收藏数量")
    private Integer collectCount;

    @ApiModelProperty(value = "用户足迹数量")
    private Integer browseNum;

    @ApiModelProperty(value = "个人中心banner")
    private List<HashMap<String, Object>> centerBanner;

    @ApiModelProperty(value = "个人中心服务")
    private List<HashMap<String, Object>> centerMenu;

    @ApiModelProperty(value = "是否付费会员")
    private Boolean isPaidMember;

    @ApiModelProperty(value = "付费会员入口：1-开启，0-关闭")
    private Integer paidMemberPaidEntrance = 0;

    @ApiModelProperty(value = "移动端商家管理 - 当前最新token - 作用于移动端商家管理")
    private LoginResponse loginResponse;
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

    @ApiModelProperty(value = "用户类型：0:游客，1:管理员,2,员工")
    private Integer userType;
    @ApiModelProperty(value = "养殖户类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer  farmType;


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
