package com.zbkj.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 商品同步响应
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
@ApiModel(value = "ProductSyncResponse", description = "商品同步响应")
public class ProductSyncResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "同步商品总数")
    private Integer totalCount;

    @ApiModelProperty(value = "成功同步数量")
    private Integer successCount;

    @ApiModelProperty(value = "失败同步数量")
    private Integer failedCount;

    @ApiModelProperty(value = "同步消息")
    private String message;

    @ApiModelProperty(value = "同步状态")
    private Boolean success;
} 