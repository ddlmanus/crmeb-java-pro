package com.zbkj.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * PC底部授权Vo对象
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
@ApiModel(value = "PcBottomAuthorizeVo", description = "PC底部授权Vo对象")
public class PcBottomAuthorizeVo implements Serializable {

    private static final long serialVersionUID = -3097800453211866415L;

    @ApiModelProperty(value = "联系电话")
//    @NotBlank(message = "联系电话不能为空")
    private String phone;

    @ApiModelProperty(value = "地址")
//    @NotBlank(message = "地址不能为空")
    private String address;

    @ApiModelProperty(value = "授权信息")
    @NotBlank(message = "授权信息不能为空")
    private String authInfo;

    @ApiModelProperty(value = "备案号")
    @NotBlank(message = "备案号不能为空")
    private String filingNum;
}
