package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户响应对象
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
@ApiModel(value = "UserResponse对象", description = "用户响应对象")
public class UserResponse {

    @ApiModelProperty(value = "用户id")
    private Integer id;

    @ApiModelProperty(value = "标签Ids")
    private String tagId;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "国家，中国CN，其他OTHER")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "1为正常，0为禁止")
    private Boolean status;

    @ApiModelProperty(value = "注册类型：public-公众号，mini-小程序，H5-H5")
    private String registerType;

    @ApiModelProperty(value = "是否关联公众号")
    private Boolean isWechatPublic;

    @ApiModelProperty(value = "是否关联小程序")
    private Boolean isWechatRoutine;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "用户积分")
    private Integer integral;

    @ApiModelProperty(value = "用户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后一次登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "上级推广员id")
    private Integer spreadUid;

    @ApiModelProperty(value = "上级推广员昵称")
    private String spreadName;

    @ApiModelProperty(value = "是否注销")
    private Boolean isLogoff;

    @ApiModelProperty(value = "用户类型：1:管理员,2:员工")
    private Integer userType;

    @ApiModelProperty(value = "养殖类型：0:游客，1:种植户/种植企业，2:合作社，3:经纪人，4:供应商，5:平台")
    private Integer farmType;

    @ApiModelProperty(value = "养殖场名称")
    private String farmName;

    @ApiModelProperty(value = "养殖机构分类代码")
    private String orgCategoryCode;

    @ApiModelProperty(value = "养殖机构分类名称")
    private String orgCategoryName;

    // 授信相关字段
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
