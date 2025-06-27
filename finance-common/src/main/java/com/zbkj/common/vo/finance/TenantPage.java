package com.zbkj.common.vo.finance;

import com.zbkj.common.request.PageParamRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TenantPage extends PageParamRequest {

    @ApiModelProperty(value  = "企业名称")
    private String enterpriseName;

    /**
     * 租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）
     */
    @ApiModelProperty(value  = "租户类型（1：种植企业 2：合作社 3：服务供应商 4：商品供应商 5：种植户）")
    private Integer tenantType;

    /**
     * 审核状态（2：已通过 1：被驳回 0：待审核）
     */
    @ApiModelProperty(value  = "审核状态（2：已通过 1：被驳回 0：待审核）")
    private Integer auditStatus;
}
