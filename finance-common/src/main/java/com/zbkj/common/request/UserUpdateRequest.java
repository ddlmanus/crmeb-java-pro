package com.zbkj.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户更新请求对象
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
@ApiModel(value="UserUpdateRequest", description="用户更新请求对象")
public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "uid")
    @NotNull(message = "用户uid不能为空")
    private Integer id;

    @ApiModelProperty(value = "真实姓名")
    @Length(max = 20, message = "真实姓名长度不能超过20个字符")
    private String realName;

    @ApiModelProperty(value = "用户昵称")
    @Length(max = 50, message = "用户昵称长度不能超过50个字符")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    private String phone;

    @ApiModelProperty(value = "身份证号码")
    @Length(max = 18, message = "身份证号码长度不能超过18个字符")
    private String identityCardNo;

    @ApiModelProperty(value = "用户类型：1:管理员,2:员工")
    private Integer userType;

    @ApiModelProperty(value = "养殖场名称")
    @Length(max = 100, message = "养殖场名称长度不能超过100个字符")
    private String farmName;

    @ApiModelProperty(value = "养殖场机构id")
    private Integer farmId;

    @ApiModelProperty(value = "养殖场code")
    private String farmCode;

    @ApiModelProperty(value = "所属机构id")
    private String organizationId;

    @ApiModelProperty(value = "所属机构名称")
    private String organizationName;

    @ApiModelProperty(value = "所属机构code")
    private String organizationCode;

    @ApiModelProperty(value = "省份")
    @Length(max = 20, message = "省份长度不能超过20个字符")
    private String province;

    @ApiModelProperty(value = "城市")
    @Length(max = 20, message = "城市长度不能超过20个字符")
    private String city;

    @ApiModelProperty(value = "区")
    @Length(max = 20, message = "区长度不能超过20个字符")
    private String district;

    @ApiModelProperty(value = "详细地址")
    @Length(max = 200, message = "详细地址长度不能超过200个字符")
    private String address;

    @ApiModelProperty(value = "性别，0未知，1男，2女，3保密")
    private Integer sex;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "标签Ids")
    private String tagId;

    @ApiModelProperty(value = "生日")
    private String birthday;

    @ApiModelProperty(value = "用户备注")
    @Length(max = 200, message = "备注长度不能超过200个字符")
    private String mark;

    @ApiModelProperty(value = "状态是否正常， 0 = 禁止， 1 = 正常")
    private Boolean status;

    @ApiModelProperty(value = "是否为推广员")
    private Boolean isPromoter;

    @ApiModelProperty(value = "省份ID")
    private Integer provinceId;

    @ApiModelProperty(value = "城市ID")
    private Integer cityId;

    @ApiModelProperty(value = "区县ID")
    private Integer districtId;

    @ApiModelProperty(value = "授信利率")
    private BigDecimal creditRatio;

    @ApiModelProperty(value = "还款期限（天）")
    private Integer repaymentDays;

    @ApiModelProperty(value = "卡号")
    @Length(max = 50, message = "卡号长度不能超过50个字符")
    private String cardNumber;
}
