package com.zbkj.common.request;

import com.zbkj.common.constants.RegularConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 后台管理员修改对象
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
@ApiModel(value="SystemAdminUpdateRequest对象", description="后台管理员修改对象")
public class SystemAdminUpdateRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "后台管理员表ID")
    @NotNull(message = "管理员id不能为空")
    private Integer id;

    @ApiModelProperty(value = "后台管理员账号", required = true)
    @NotBlank(message = "后台管理员账号不能为空")
    @Length(max = 32, message = "账号长度不能超过32个字符")
    private String account;

    @ApiModelProperty(value = "后台管理员姓名", required = true)
    @NotBlank(message = "管理姓名不能为空")
    @Length(max = 16, message = "姓名长度不能超过16个字符")
    private String realName;

    @ApiModelProperty(value = "后台管理员权限(menus_id)")
    @NotBlank(message = "后台管理员权限不能为空")
    @Length(max = 128, message = "角色组合长度不能超过128个字符")
    private String roles;

    @ApiModelProperty(value = "后台管理员状态 1有效0无效")
    @NotNull(message = "status 字段不能为空")
    @Range(min = 0, max = 1, message = "未知的状态")
    private Boolean status;

    @ApiModelProperty(value = "手机号码")
//    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = RegularConstants.PHONE_TWO, message = "请填写正确的手机号")
    private String phone;
}
